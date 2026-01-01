package com.hospital.appointment.dao;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.models.Patient;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientDAO {
    private static final Logger logger = Logger.getLogger(PatientDAO.class.getName());

    /**
     * Create new patient record
     * @return Generated patient_id, or -1 if failed
     */
    public int createPatient(Patient patient) {
        String sql = "INSERT INTO patients (user_id, full_name, ic_number, date_of_birth, gender, address) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, patient.getUserId());
                pstmt.setString(2, patient.getFullName());
                pstmt.setString(3, patient.getIcNumber());
                if (patient.getDateOfBirth() != null) {
                    pstmt.setDate(4, Date.valueOf(patient.getDateOfBirth()));
                } else {
                    pstmt.setDate(4, null);
                }
                if (patient.getGender() != null) {
                    pstmt.setString(5, patient.getGender().name());
                } else {
                    pstmt.setString(5, null);
                }
                pstmt.setString(6, patient.getAddress());
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating patient", e);
            e.printStackTrace(); // Print stack trace for debugging
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return -1;
    }

    /**
     * Find patient by user_id
     */
    public Patient findByUserId(int userId) {
        String sql = "SELECT * FROM patients WHERE user_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Patient patient = new Patient();
                        patient.setPatientId(rs.getInt("patient_id"));
                        patient.setUserId(rs.getInt("user_id"));
                        patient.setFullName(rs.getString("full_name"));
                        patient.setIcNumber(rs.getString("ic_number"));
                        Date dob = rs.getDate("date_of_birth");
                        if (dob != null) {
                            patient.setDateOfBirth(dob.toLocalDate());
                        }
                        String genderStr = rs.getString("gender");
                        if (genderStr != null) {
                            patient.setGender(Patient.Gender.valueOf(genderStr));
                        }
                        patient.setAddress(rs.getString("address"));
                        return patient;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding patient by user_id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }

    /**
     * Find patient by patient_id
     */
    public Patient findByPatientId(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, patientId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Patient patient = new Patient();
                        patient.setPatientId(rs.getInt("patient_id"));
                        patient.setUserId(rs.getInt("user_id"));
                        patient.setFullName(rs.getString("full_name"));
                        patient.setIcNumber(rs.getString("ic_number"));
                        Date dob = rs.getDate("date_of_birth");
                        if (dob != null) {
                            patient.setDateOfBirth(dob.toLocalDate());
                        }
                        String genderStr = rs.getString("gender");
                        if (genderStr != null) {
                            patient.setGender(Patient.Gender.valueOf(genderStr));
                        }
                        patient.setAddress(rs.getString("address"));
                        return patient;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding patient by patient_id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }
}

