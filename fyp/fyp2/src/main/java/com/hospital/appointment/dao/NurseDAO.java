package com.hospital.appointment.dao;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.models.Nurse;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NurseDAO {
    private static final Logger logger = Logger.getLogger(NurseDAO.class.getName());

    /**
     * Create new nurse record
     * @return Generated nurse_id, or -1 if failed
     */
    public int createNurse(Nurse nurse) {
        String sql = "INSERT INTO nurses (user_id, department) VALUES (?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, nurse.getUserId());
                pstmt.setString(2, nurse.getDepartment());
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating nurse", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return -1;
    }

    /**
     * Find nurse_id by user_id
     */
    public Integer findNurseIdByUserId(int userId) {
        String sql = "SELECT nurse_id FROM nurses WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("nurse_id");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding nurse_id by user_id", e);
        }
        return null;
    }
}

