package com.hospital.appointment.dao;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.models.DoctorDiagnosis;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorDiagnosisDAO {
    private static final Logger logger = Logger.getLogger(DoctorDiagnosisDAO.class.getName());

    /**
     * Create new doctor diagnosis
     * @return Generated diagnosis_id, or -1 if failed
     */
    public int create(DoctorDiagnosis diagnosis) {
        String sql = "INSERT INTO doctor_diagnosis (report_id, doctor_id, diagnosis_result, comments, diagnosis_date) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, diagnosis.getReportId());
                pstmt.setInt(2, diagnosis.getDoctorId());
                pstmt.setString(3, diagnosis.getDiagnosisResult());
                pstmt.setString(4, diagnosis.getComments());
                pstmt.setTimestamp(5, Timestamp.valueOf(diagnosis.getDiagnosisDate()));
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating doctor diagnosis", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return -1;
    }

    /**
     * Find doctor diagnosis by report_id
     */
    public DoctorDiagnosis findByReportId(int reportId) {
        String sql = "SELECT * FROM doctor_diagnosis WHERE report_id = ? ORDER BY diagnosis_date DESC LIMIT 1";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, reportId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToDoctorDiagnosis(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding doctor diagnosis by report id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }

    /**
     * Find all doctor diagnoses
     */
    public List<DoctorDiagnosis> findAll() {
        List<DoctorDiagnosis> diagnoses = new ArrayList<>();
        String sql = "SELECT * FROM doctor_diagnosis ORDER BY diagnosis_date DESC";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        diagnoses.add(mapResultSetToDoctorDiagnosis(rs));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all doctor diagnoses", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return diagnoses;
    }

    /**
     * Update report_file_path for a diagnosis
     * If no diagnosis record exists, creates one with the file path
     */
    public boolean updateReportFilePath(int reportId, String filePath) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            // First, check if a record exists
            DoctorDiagnosis existing = findByReportId(reportId);
            
            if (existing != null) {
                // Update existing record
                String sql = "UPDATE doctor_diagnosis SET report_file_path = ? WHERE report_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, filePath);
                    pstmt.setInt(2, reportId);
                    
                    int rowsAffected = pstmt.executeUpdate();
                    return rowsAffected > 0;
                }
            } else {
                // Record doesn't exist - need doctor_id to create one
                // For now, we can't create without doctor_id, so return false
                // This should be handled by the caller or we need to get doctor_id
                logger.log(Level.WARNING, "Cannot update file path: No doctor_diagnosis record exists for report_id: " + reportId);
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating report file path", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return false;
    }
    
    /**
     * Update or insert report_file_path for a diagnosis (creates record if needed)
     */
    public boolean updateOrInsertReportFilePath(int reportId, int doctorId, String filePath) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            // First, check if a record exists
            DoctorDiagnosis existing = findByReportId(reportId);
            
            if (existing != null) {
                // Update existing record
                String sql = "UPDATE doctor_diagnosis SET report_file_path = ? WHERE report_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, filePath);
                    pstmt.setInt(2, reportId);
                    
                    int rowsAffected = pstmt.executeUpdate();
                    return rowsAffected > 0;
                }
            } else {
                // Create new record with just report_id, doctor_id, and report_file_path
                String sql = "INSERT INTO doctor_diagnosis (report_id, doctor_id, report_file_path, diagnosis_date) VALUES (?, ?, ?, NOW())";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, reportId);
                    pstmt.setInt(2, doctorId);
                    pstmt.setString(3, filePath);
                    
                    int rowsAffected = pstmt.executeUpdate();
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating or inserting report file path", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return false;
    }

    /**
     * Map ResultSet to DoctorDiagnosis object
     */
    private DoctorDiagnosis mapResultSetToDoctorDiagnosis(ResultSet rs) throws SQLException {
        DoctorDiagnosis diagnosis = new DoctorDiagnosis();
        diagnosis.setDiagnosisId(rs.getInt("diagnosis_id"));
        diagnosis.setReportId(rs.getInt("report_id"));
        diagnosis.setDoctorId(rs.getInt("doctor_id"));
        diagnosis.setDiagnosisResult(rs.getString("diagnosis_result"));
        diagnosis.setComments(rs.getString("comments"));
        Timestamp diagnosisDate = rs.getTimestamp("diagnosis_date");
        if (diagnosisDate != null) {
            diagnosis.setDiagnosisDate(diagnosisDate.toLocalDateTime());
        }
        // Handle report_file_path field
        try {
            diagnosis.setReportFilePath(rs.getString("report_file_path"));
        } catch (SQLException e) {
            // Column might not exist in older database schemas
            diagnosis.setReportFilePath(null);
        }
        return diagnosis;
    }
}



