package com.lms.dao;

import com.lms.config.DatabaseConnection;
import java.sql.*;

public class AdminDAO {

    public AdminDAO() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS admins (
                    id       SERIAL PRIMARY KEY,
                    name     VARCHAR(100) NOT NULL,
                    email    VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL
                )
            """);

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM admins WHERE email = 'admin@gmail.com'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO admins (name, email, password) VALUES ('User Admin', 'admin@gmail.com', 'Rahull@5')");
            }

        } catch (SQLException e) {
            System.err.println("AdminDAO init error: " + e.getMessage());
        }
    }

    public boolean authenticateAdmin(String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM admins WHERE email=? AND password=?")) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Auth error: " + e.getMessage());
            return false;
        }
    }

    public void addJob(String title, String department) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO jobs (title, department, status) VALUES (?, ?, 'Open')")) {
            ps.setString(1, title);
            ps.setString(2, department);
            ps.executeUpdate();
            System.out.println("Job added: " + title);
        } catch (SQLException e) {
            System.err.println("addJob error: " + e.getMessage());
        }
    }

    public void addCourse(String title, String description) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO courses (title, description) VALUES (?, ?)")) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.executeUpdate();
            System.out.println("Course added: " + title);
        } catch (SQLException e) {
            System.err.println("addCourse error: " + e.getMessage());
        }
    }

    public void addInterviewer(String name, String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO interviewers (name, email) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.executeUpdate();
            System.out.println("Interviewer added: " + name);
        } catch (SQLException e) {
            System.err.println("addInterviewer error: " + e.getMessage());
        }
    }

    public void viewHiringPipeline() {
        String sql = """
            SELECT c.id, c.name, c.email, j.title AS job, c.status,
                   i.score, i.status AS interview_status
            FROM candidates c
            JOIN jobs j ON c.job_id = j.id
            LEFT JOIN interviews i ON i.candidate_id = c.id
            ORDER BY c.id
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%n%-4s %-18s %-24s %-18s %-22s %-6s %-12s%n",
                    "ID","Name","Email","Job","Status","Score","Interview");
            System.out.println("-".repeat(108));
            while (rs.next()) {
                System.out.printf("%-4d %-18s %-24s %-18s %-22s %-6s %-12s%n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("job"), rs.getString("status"),
                        rs.getObject("score") == null ? "-" : rs.getInt("score"),
                        rs.getString("interview_status") == null ? "Pending" : rs.getString("interview_status"));
            }
        } catch (SQLException e) {
            System.err.println("viewHiringPipeline error: " + e.getMessage());
        }
    }

    public void viewOnboardingSummary() {
        String sql = """
            SELECT c.name, o.status,
                   COUNT(cc.course_id) AS total,
                   SUM(CASE WHEN cc.status='Completed' THEN 1 ELSE 0 END) AS done
            FROM onboardings o
            JOIN candidates c ON c.id = o.candidate_id
            LEFT JOIN candidate_courses cc ON cc.candidate_id = o.candidate_id
            GROUP BY c.name, o.status
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%n%-20s %-18s %-14s %-10s%n", "Candidate","Status","Total Courses","Completed");
            System.out.println("-".repeat(65));
            while (rs.next()) {
                System.out.printf("%-20s %-18s %-14d %-10d%n",
                        rs.getString("name"), rs.getString("status"),
                        rs.getInt("total"), rs.getInt("done"));
            }
        } catch (SQLException e) {
            System.err.println("viewOnboardingSummary error: " + e.getMessage());
        }
    }
}