package com.interviewhub.dao;

import com.interviewhub.database.DBConnection;
import com.interviewhub.model.Interviewer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interviewer DAO
 * Manages database persistence for Interviewers (joined with Users table).
 * Implements CRUD operations, profile retrieval, and analytical statistics queries.
 */
public class InterviewerDAO {

    public int createInterviewer(Interviewer interviewer, Connection externalConn) throws SQLException {
        String sql = "INSERT INTO interviewers (user_id, expertise, experience_years, specialization, organization) " +
                "VALUES (?, ?, ?, ?, ?)";
        boolean manageConnection = (externalConn == null);
        Connection conn = manageConnection ? DBConnection.getConnection() : externalConn;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, interviewer.getUserId());
            stmt.setString(2, interviewer.getExpertise());
            stmt.setInt(3, interviewer.getExperienceYears());
            stmt.setString(4, interviewer.getSpecialization());
            stmt.setString(5, interviewer.getOrganization());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating interviewer record failed.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    interviewer.setInterviewerId(id);
                    return id;
                } else {
                    throw new SQLException("Creating interviewer record failed, no generated ID.");
                }
            }
        } finally {
            if (manageConnection && conn != null) {
                conn.close();
            }
        }
    }

    public Interviewer getInterviewerByUserId(int userId) throws SQLException {
        String sql = "SELECT i.*, u.name, u.email, u.password_hash, u.password_salt, u.status, u.created_at " +
                "FROM interviewers i JOIN users u ON i.user_id = u.user_id WHERE i.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractInterviewer(rs);
                }
            }
        }
        return null;
    }

    public Interviewer getInterviewerById(int interviewerId) throws SQLException {
        String sql = "SELECT i.*, u.name, u.email, u.password_hash, u.password_salt, u.status, u.created_at " +
                "FROM interviewers i JOIN users u ON i.user_id = u.user_id WHERE i.interviewer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, interviewerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractInterviewer(rs);
                }
            }
        }
        return null;
    }

    public List<Interviewer> getAllInterviewers() throws SQLException {
        List<Interviewer> list = new ArrayList<>();
        String sql = "SELECT i.*, u.name, u.email, u.password_hash, u.password_salt, u.status, u.created_at " +
                "FROM interviewers i JOIN users u ON i.user_id = u.user_id ORDER BY i.interviewer_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractInterviewer(rs));
            }
        }
        return list;
    }

    public List<Interviewer> getInterviewersBySpecialization(String specialization) throws SQLException {
        List<Interviewer> list = new ArrayList<>();
        String sql = "SELECT i.*, u.name, u.email, u.password_hash, u.password_salt, u.status, u.created_at " +
                "FROM interviewers i JOIN users u ON i.user_id = u.user_id " +
                "WHERE LOWER(i.specialization) LIKE ? OR LOWER(i.expertise) LIKE ? " +
                "ORDER BY i.interviewer_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + specialization.trim().toLowerCase() + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractInterviewer(rs));
                }
            }
        }
        return list;
    }

    public boolean updateInterviewer(Interviewer interviewer) throws SQLException {
        String updateIntSql = "UPDATE interviewers SET expertise = ?, experience_years = ?, specialization = ?, organization = ? WHERE interviewer_id = ?";
        String updateUserSql = "UPDATE users SET name = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement iStmt = conn.prepareStatement(updateIntSql)) {
                    iStmt.setString(1, interviewer.getExpertise());
                    iStmt.setInt(2, interviewer.getExperienceYears());
                    iStmt.setString(3, interviewer.getSpecialization());
                    iStmt.setString(4, interviewer.getOrganization());
                    iStmt.setInt(5, interviewer.getInterviewerId());
                    iStmt.executeUpdate();
                }

                try (PreparedStatement uStmt = conn.prepareStatement(updateUserSql)) {
                    uStmt.setString(1, interviewer.getName());
                    uStmt.setInt(2, interviewer.getUserId());
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

    private Interviewer extractInterviewer(ResultSet rs) throws SQLException {
        return new Interviewer(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("password_salt"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getInt("interviewer_id"),
                rs.getString("expertise"),
                rs.getInt("experience_years"),
                rs.getString("specialization"),
                rs.getString("organization")
        );
    }
}
