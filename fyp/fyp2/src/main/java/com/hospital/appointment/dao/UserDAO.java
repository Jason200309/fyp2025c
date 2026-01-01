package com.hospital.appointment.dao;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.models.User;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    /**
     * Authenticate user by username and password
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 'ACTIVE'";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setRole(User.Role.valueOf(rs.getString("role")));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setStatus(User.Status.valueOf(rs.getString("status")));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error authenticating user", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }

    /**
     * Find user by user_id
     */
    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setRole(User.Role.valueOf(rs.getString("role")));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setStatus(User.Status.valueOf(rs.getString("status")));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding user by id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }

    /**
     * Check if username already exists
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking username existence", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return false;
    }

    /**
     * Create new user account
     * Default role: PATIENT (if not specified), Default status: ACTIVE
     * @return Generated user_id, or -1 if failed
     */
    public int createUser(User user) {
        String sql = "INSERT INTO users (username, password, role, email, phone, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                // Use role from user object, default to PATIENT if not set
                String role = (user.getRole() != null) ? user.getRole().name() : "PATIENT";
                pstmt.setString(3, role);
                pstmt.setString(4, user.getEmail());
                pstmt.setString(5, user.getPhone());
                // Default status to ACTIVE
                String status = (user.getStatus() != null) ? user.getStatus().name() : "ACTIVE";
                pstmt.setString(6, status);
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating user", e);
            e.printStackTrace(); // Print stack trace for debugging
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return -1;
    }
}
