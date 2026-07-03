package com.lms.dao;

import com.lms.config.DatabaseConnection;
import java.sql.*;

public class CandidateDAO {

    public CandidateDAO() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS jobs (
                    id         SERIAL PRIMARY KEY,
                    title      VARCHAR(150) NOT NULL,
                    department VARCHAR(100) NOT NULL,
                    status     VARCHAR(20) NOT NULL DEFAULT 'Open'
                        CHECK (status IN ('Open','Closed'))
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS candidates (
                    id     SERIAL PRIMARY KEY,
                    name   VARCHAR(100) NOT NULL,
                    email  VARCHAR(100) UNIQUE NOT NULL,
                    phone  VARCHAR(20),
                    job_id INT NOT NULL REFERENCES jobs(id),
                    status VARCHAR(30) NOT NULL DEFAULT 'Applied'
                        CHECK (status IN ('Applied','Interview Scheduled','Interview Completed','Hired','Rejected'))
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS interviewers (
                    id    SERIAL PRIMARY KEY,
                    name  VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS interviews (
                    id             SERIAL PRIMARY KEY,
                    candidate_id   INT NOT NULL REFERENCES candidates(id),
                    interviewer_id INT NOT NULL REFERENCES interviewers(id),
                    scheduled_at   TIMESTAMP NOT NULL,
                    score          INT CHECK (score BETWEEN 0 AND 100),
                    feedback       TEXT,
                    status         VARCHAR(20) DEFAULT 'Scheduled'
                        CHECK (status IN ('Scheduled','Passed','Failed'))
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS candidate_skills (
                    candidate_id INT NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
                    skill        VARCHAR(100) NOT NULL,
                    PRIMARY KEY (candidate_id, skill)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS candidate_experience (
                    candidate_id INT NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
                    company_name VARCHAR(150) NOT NULL,
                    PRIMARY KEY (candidate_id, company_name)
                )
            """);

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_candidate_email ON candidates(email)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_candidate_status ON candidates(status)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_interviews_candidate ON interviews(candidate_id)");

        } catch (SQLException e) {
            System.err.println("CandidateDAO init error: " + e.getMessage());
        }
    }

    public boolean applyJob(String name, String email, String phone, int jobId, String[] skills, String[] companies) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int candidateId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO candidates (name, email, phone, job_id) VALUES (?, ?, ?, ?) RETURNING id")) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setInt(4, jobId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                candidateId = rs.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO candidate_skills (candidate_id, skill) VALUES (?, ?)")) {
                for (String skill : skills) {
                    ps.setInt(1, candidateId);
                    ps.setString(2, skill.trim());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO candidate_experience (candidate_id, company_name) VALUES (?, ?)")) {
                for (String company : companies) {
                    ps.setInt(1, candidateId);
                    ps.setString(2, company.trim());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.println("Application submitted! Candidate ID: " + candidateId);
            return true;

        } catch (SQLException e) {
            System.err.println("applyJob error: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ex) {}
        }
    }

    public void scheduleInterview(int candidateId, int interviewerId, String dateTimeStr) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO interviews (candidate_id, interviewer_id, scheduled_at) VALUES (?, ?, ?)")) {
                ps.setInt(1, candidateId);
                ps.setInt(2, interviewerId);
                ps.setTimestamp(3, Timestamp.valueOf(dateTimeStr));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE candidates SET status='Interview Scheduled' WHERE id=?")) {
                ps.setInt(1, candidateId);
                ps.executeUpdate();
            }
            System.out.println("Interview scheduled.");
        } catch (SQLException e) {
            System.err.println("scheduleInterview error: " + e.getMessage());
        }
    }

    public void submitInterviewScore(int interviewId, int score, String feedback, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE interviews SET score=?, feedback=?, status=? WHERE id=?")) {
                ps.setInt(1, score);
                ps.setString(2, feedback);
                ps.setString(3, status);
                ps.setInt(4, interviewId);
                ps.executeUpdate();
            }
            String candStatus = status.equals("Passed") ? "Interview Completed" : "Rejected";
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE candidates SET status=? WHERE id=(SELECT candidate_id FROM interviews WHERE id=?)")) {
                ps.setString(1, candStatus);
                ps.setInt(2, interviewId);
                ps.executeUpdate();
            }
            System.out.println("Feedback submitted. Score: " + score);
        } catch (SQLException e) {
            System.err.println("submitInterviewScore error: " + e.getMessage());
        }
    }

    public void listAllCandidates() {
        String sql = "SELECT c.id, c.name, c.email, j.title AS job, c.status FROM candidates c JOIN jobs j ON c.job_id=j.id ORDER BY c.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.printf("%n%-4s %-18s %-24s %-20s %-20s%n", "ID","Name","Email","Job","Status");
            System.out.println("-".repeat(90));
            while (rs.next()) {
                System.out.printf("%-4d %-18s %-24s %-20s %-20s%n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("job"), rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("listAllCandidates error: " + e.getMessage());
        }
    }

    public void listOpenJobs() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title, department FROM jobs WHERE status='Open'")) {
            System.out.printf("%n%-4s %-25s %-20s%n", "ID","Title","Department");
            System.out.println("-".repeat(52));
            while (rs.next()) {
                System.out.printf("%-4d %-25s %-20s%n",
                        rs.getInt("id"), rs.getString("title"), rs.getString("department"));
            }
        } catch (SQLException e) {
            System.err.println("listOpenJobs error: " + e.getMessage());
        }
    }

    public void listInterviewers() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, email FROM interviewers")) {
            System.out.printf("%n%-4s %-20s %-25s%n", "ID","Name","Email");
            System.out.println("-".repeat(52));
            while (rs.next()) {
                System.out.printf("%-4d %-20s %-25s%n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            }
        } catch (SQLException e) {
            System.err.println("listInterviewers error: " + e.getMessage());
        }
    }

    public void listInterviews() {
        String sql = """
            SELECT i.id, c.name AS candidate, iv.name AS interviewer,
                   i.scheduled_at, i.score, i.status
            FROM interviews i
            JOIN candidates c ON c.id = i.candidate_id
            JOIN interviewers iv ON iv.id = i.interviewer_id
            ORDER BY i.scheduled_at
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.printf("%n%-4s %-18s %-18s %-22s %-6s %-10s%n",
                    "ID","Candidate","Interviewer","Scheduled At","Score","Status");
            System.out.println("-".repeat(82));
            while (rs.next()) {
                System.out.printf("%-4d %-18s %-18s %-22s %-6s %-10s%n",
                        rs.getInt("id"), rs.getString("candidate"), rs.getString("interviewer"),
                        rs.getTimestamp("scheduled_at"),
                        rs.getObject("score") == null ? "-" : rs.getInt("score"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("listInterviews error: " + e.getMessage());
        }
    }

    public void viewCandidateDetails(int candidateId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT c.*, j.title AS job FROM candidates c JOIN jobs j ON c.job_id=j.id WHERE c.id=?")) {
                ps.setInt(1, candidateId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("\nID     : " + rs.getInt("id"));
                    System.out.println("Name   : " + rs.getString("name"));
                    System.out.println("Email  : " + rs.getString("email"));
                    System.out.println("Phone  : " + rs.getString("phone"));
                    System.out.println("Job    : " + rs.getString("job"));
                    System.out.println("Status : " + rs.getString("status"));
                } else {
                    System.out.println("Candidate not found.");
                    return;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT skill FROM candidate_skills WHERE candidate_id=?")) {
                ps.setInt(1, candidateId);
                ResultSet rs = ps.executeQuery();
                StringBuilder sb = new StringBuilder("Skills : ");
                while (rs.next()) sb.append(rs.getString("skill")).append(", ");
                System.out.println(sb.toString().replaceAll(", $", ""));
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT company_name FROM candidate_experience WHERE candidate_id=?")) {
                ps.setInt(1, candidateId);
                ResultSet rs = ps.executeQuery();
                StringBuilder sb = new StringBuilder("Exp.   : ");
                while (rs.next()) sb.append(rs.getString("company_name")).append(", ");
                System.out.println(sb.toString().replaceAll(", $", ""));
            }

        } catch (SQLException e) {
            System.err.println("viewCandidateDetails error: " + e.getMessage());
        }
    }
}