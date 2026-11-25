package com.interviewhub.dao;

import com.interviewhub.database.DBConnection;
import com.interviewhub.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object (DAO)
 * Handles CRUD operations on the 'users' table using PreparedStatement and try-with-resources.
 * Provides parameterized query execution for secure user entity management.
 */
public class UserDAO {

    public int createUser(User user, Connection externalConn) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, password_salt, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        boolean manageConnection = (externalConn == null);
        Connection conn = manageConnection ? DBConnection.getConnection() : externalConn;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getPasswordSalt());
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getStatus() != null ? user.getStatus() : "ACTIVE");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    user.setUserId(id);
                    return id;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } finally {
            if (manageConnection && conn != null) {
                conn.close();
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractUserFromResultSet(rs));
            }
        }
        return list;
    }

    public boolean updateUserStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int userId, String newHash, String newSalt) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, password_salt = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);
            stmt.setString(2, newSalt);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        // Instantiate anonymous concrete implementation to hold base User columns
        int id = rs.getInt("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String hash = rs.getString("password_hash");
        String salt = rs.getString("password_salt");
        String role = rs.getString("role");
        String status = rs.getString("status");
        Timestamp createdAt = rs.getTimestamp("created_at");

        return new User(id, name, email, hash, salt, role, status, createdAt) {
            @Override
            public String getRoleDetails() {
                return "User Role: " + getRole();
            }
        };
    }
}
