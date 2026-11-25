package com.interviewhub.dao;

import com.interviewhub.database.DBConnection;
import com.interviewhub.model.Candidate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Candidate DAO
 * Manages database persistence for Candidates (joined with Users table).
 * Implements parameterized JDBC queries and safe auto-generated key extraction.
 */
public class CandidateDAO {

    public int createCandidate(Candidate candidate, Connection externalConn) throws SQLException {
        String sql = "INSERT INTO candidates (user_id, student_id, department, cgpa, graduation_year, specialization) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        boolean manageConnection = (externalConn == null);
        Connection conn = manageConnection ? DBConnection.getConnection() : externalConn;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, candidate.getUserId());
            stmt.setString(2, candidate.getStudentId());
            stmt.setString(3, candidate.getDepartment());
            stmt.setDouble(4, candidate.getCgpa());
            stmt.setInt(5, candidate.getGraduationYear());
            stmt.setString(6, candidate.getSpecialization());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating candidate record failed.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    candidate.setCandidateId(id);
                    return id;
                } else {
                    throw new SQLException("Creating candidate record failed, no generated ID.");
                }
            }
        } finally {
            if (manageConnection && conn != null) {
                conn.close();
            }
        }
    }

    public Candidate getCandidateByUserId(int userId) throws SQLException {
        String sql = "SELECT c.*, u.name, u.email, u.password_hash, u.password_salt, u.status, u.created_at " +
                "FROM candidates c JOIN users u ON c.user_id = u.user_id WHERE c.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCandidate(rs);
                }
            }
        }
        return null;
    }

    public Candidate getCandidateById(int candidateId) throws SQLException {
        String sql = "SELECT c.*, u.name, u.email, u.password_hash, u.password_salt, u.status, u.created_at " +
                "FROM candidates c JOIN users u ON c.user_id = u.user_id WHERE c.candidate_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, candidateId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCandidate(rs);
                }
            }
        }
        return null;
    }

    public List<Candidate> getAllCandidates() throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT c.*, u.name, u.email, u.password_hash, u.password_salt, u.status, u.created_at " +
                "FROM candidates c JOIN users u ON c.user_id = u.user_id ORDER BY c.candidate_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractCandidate(rs));
            }
        }
        return list;
    }

    public boolean updateCandidate(Candidate candidate) throws SQLException {
        String updateCandSql = "UPDATE candidates SET department = ?, cgpa = ?, graduation_year = ?, specialization = ? WHERE candidate_id = ?";
        String updateUserSql = "UPDATE users SET name = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement cStmt = conn.prepareStatement(updateCandSql)) {
                    cStmt.setString(1, candidate.getDepartment());
                    cStmt.setDouble(2, candidate.getCgpa());
                    cStmt.setInt(3, candidate.getGraduationYear());
                    cStmt.setString(4, candidate.getSpecialization());
                    cStmt.setInt(5, candidate.getCandidateId());
                    cStmt.executeUpdate();
                }

                try (PreparedStatement uStmt = conn.prepareStatement(updateUserSql)) {
                    uStmt.setString(1, candidate.getName());
                    uStmt.setInt(2, candidate.getUserId());
                    uStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private Candidate extractCandidate(ResultSet rs) throws SQLException {
        return new Candidate(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("password_salt"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getInt("candidate_id"),
                rs.getString("student_id"),
                rs.getString("department"),
                rs.getDouble("cgpa"),
                rs.getInt("graduation_year"),
                rs.getString("specialization")
        );
    }
}
