package com.lms.dao;

import com.lms.config.DatabaseConnection;
import java.sql.*;

public class OnboardingDAO {

    public OnboardingDAO() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS onboardings (
                    id           SERIAL PRIMARY KEY,
                    candidate_id INT UNIQUE NOT NULL REFERENCES candidates(id),
                    start_date   DATE NOT NULL DEFAULT CURRENT_DATE,
                    status       VARCHAR(20) NOT NULL DEFAULT 'In Progress'
                        CHECK (status IN ('In Progress','Completed'))
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS courses (
                    id          SERIAL PRIMARY KEY,
                    title       VARCHAR(200) NOT NULL,
                    description TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS candidate_courses (
                    candidate_id INT NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
                    course_id    INT NOT NULL REFERENCES courses(id)    ON DELETE CASCADE,
                    status       VARCHAR(20) NOT NULL DEFAULT 'Not Started'
                        CHECK (status IN ('Not Started','In Progress','Completed')),
                    percentage   INT NOT NULL DEFAULT 0 CHECK (percentage BETWEEN 0 AND 100),
                    PRIMARY KEY (candidate_id, course_id)
                )
            """);

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_candidate_courses_candidate ON candidate_courses(candidate_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_onboardings_candidate ON onboardings(candidate_id)");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM courses");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO courses (title, description) VALUES ('Java Advanced JDBC Programming','JDBC and transactions')");
                stmt.execute("INSERT INTO courses (title, description) VALUES ('Enterprise Git & Version Control','Branching and CI/CD')");
                stmt.execute("INSERT INTO courses (title, description) VALUES ('Information Security & Compliance','Security principles')");
            }

        } catch (SQLException e) {
            System.err.println("OnboardingDAO init error: " + e.getMessage());
        }
    }

    public boolean hireCandidate(int candidateId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("SELECT status FROM candidates WHERE id=?")) {
                ps.setInt(1, candidateId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    System.out.println("Candidate not found.");
                    conn.rollback();
                    return false;
                }
                String currentStatus = rs.getString("status");
                if (currentStatus.equals("Hired")) {
                    System.out.println("Already hired.");
                    return false;
                }
                if (!currentStatus.equals("Interview Completed")) {
                    System.out.println("Candidate must pass interview first. Status: " + currentStatus);
                    return false;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE candidates SET status='Hired' WHERE id=?")) {
                ps.setInt(1, candidateId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO onboardings (candidate_id) VALUES (?)")) {
                ps.setInt(1, candidateId);
                ps.executeUpdate();
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id FROM courses");
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO candidate_courses (candidate_id, course_id) VALUES (?, ?)")) {
                while (rs.next()) {
                    ps.setInt(1, candidateId);
                    ps.setInt(2, rs.getInt("id"));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.println("Candidate hired and courses assigned!");
            return true;

        } catch (SQLException e) {
            System.err.println("hireCandidate error: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ex) {}
        }
    }

    public void updateCourseProgress(int candidateId, int courseId, String status, int percentage) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE candidate_courses SET status=?, percentage=? WHERE candidate_id=? AND course_id=?")) {
                ps.setString(1, status);
                ps.setInt(2, percentage);
                ps.setInt(3, candidateId);
                ps.setInt(4, courseId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM candidate_courses WHERE candidate_id=? AND status != 'Completed'")) {
                ps.setInt(1, candidateId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    try (PreparedStatement ps2 = conn.prepareStatement(
                            "UPDATE onboardings SET status='Completed' WHERE candidate_id=?")) {
                        ps2.setInt(1, candidateId);
                        ps2.executeUpdate();
                    }
                    System.out.println("All courses completed! Onboarding marked as Completed.");
                }
            }

            conn.commit();
            System.out.println("Progress updated: " + status + " (" + percentage + "%)");

        } catch (SQLException e) {
            System.err.println("updateCourseProgress error: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ex) {}
        }
    }

    public void viewCandidateCourses(int candidateId) {
        String sql = """
            SELECT co.id, co.title, cc.status, cc.percentage
            FROM candidate_courses cc
            JOIN courses co ON co.id = cc.course_id
            WHERE cc.candidate_id = ?
            ORDER BY co.id
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%n%-4s %-40s %-15s %-10s%n", "ID","Course","Status","Progress");
            System.out.println("-".repeat(72));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-4d %-40s %-15s %d%%%n",
                        rs.getInt("id"), rs.getString("title"),
                        rs.getString("status"), rs.getInt("percentage"));
            }
            if (!found) System.out.println("No courses found.");
        } catch (SQLException e) {
            System.err.println("viewCandidateCourses error: " + e.getMessage());
        }
    }
}