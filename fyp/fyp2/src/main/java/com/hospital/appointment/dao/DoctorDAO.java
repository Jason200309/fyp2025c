package com.hospital.appointment.dao;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.models.Doctor;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorDAO {
    private static final Logger logger = Logger.getLogger(DoctorDAO.class.getName());

    /**
     * Create new doctor record
     * @return Generated doctor_id, or -1 if failed
     */
    public int createDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, specialization, license_number, department) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, doctor.getUserId());
                pstmt.setString(2, doctor.getSpecialization());
                pstmt.setString(3, doctor.getLicenseNumber());
                pstmt.setString(4, doctor.getDepartment());
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating doctor", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return -1;
    }

    /**
     * Find doctor_id by user_id
     */
    public Integer findDoctorIdByUserId(int userId) {
        String sql = "SELECT doctor_id FROM doctors WHERE user_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("doctor_id");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding doctor_id by user_id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }
}

