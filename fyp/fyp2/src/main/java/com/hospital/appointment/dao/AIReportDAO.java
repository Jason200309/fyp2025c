package com.hospital.appointment.dao;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.models.AIReport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AIReportDAO {
    private static final Logger logger = Logger.getLogger(AIReportDAO.class.getName());

    /**
     * Create new AI report
     * @return Generated report_id, or -1 if failed
     */
    public int create(AIReport report) {
        String sql = "INSERT INTO ai_reports (image_id, prediction, confidence_score, generated_at) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, report.getImageId());
                pstmt.setString(2, report.getPrediction());
                pstmt.setObject(3, report.getConfidenceScore());
                pstmt.setTimestamp(4, Timestamp.valueOf(report.getGeneratedAt()));
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating AI report", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return -1;
    }

    /**
     * Find AI report by image_id
     */
    public AIReport findByImageId(int imageId) {
        String sql = "SELECT * FROM ai_reports WHERE image_id = ? ORDER BY generated_at DESC LIMIT 1";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, imageId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToAIReport(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding AI report by image id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }

    /**
     * Find AI report by report_id
     */
    public AIReport findById(int reportId) {
        String sql = "SELECT * FROM ai_reports WHERE report_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, reportId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToAIReport(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding AI report by id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }

    /**
     * Find all AI reports for appointments
     */
    public List<AIReport> findByAppointmentId(int appointmentId) {
        List<AIReport> reports = new ArrayList<>();
        String sql = "SELECT ar.* FROM ai_reports ar " +
                     "INNER JOIN xray_images xi ON ar.image_id = xi.image_id " +
                     "WHERE xi.appointment_id = ? " +
                     "ORDER BY ar.generated_at DESC";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, appointmentId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        reports.add(mapResultSetToAIReport(rs));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding AI reports by appointment id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return reports;
    }

    /**
     * Find all visible AI reports for appointments (for patients)
     */
    public List<AIReport> findVisibleByAppointmentId(int appointmentId) {
        List<AIReport> reports = new ArrayList<>();
        String sql = "SELECT ar.* FROM ai_reports ar " +
                     "INNER JOIN xray_images xi ON ar.image_id = xi.image_id " +
                     "WHERE xi.appointment_id = ? AND ar.is_visible = 1 " +
                     "ORDER BY ar.generated_at DESC";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, appointmentId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        reports.add(mapResultSetToAIReport(rs));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding visible AI reports by appointment id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return reports;
    }

    /**
     * Update is_visible status for a report
     */
    public boolean updateIsVisible(int reportId, boolean isVisible) {
        String sql = "UPDATE ai_reports SET is_visible = ? WHERE report_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBoolean(1, isVisible);
                pstmt.setInt(2, reportId);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating is_visible status", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return false;
    }

    /**
     * Map ResultSet to AIReport object
     */
    private AIReport mapResultSetToAIReport(ResultSet rs) throws SQLException {
        AIReport report = new AIReport();
        report.setReportId(rs.getInt("report_id"));
        report.setImageId(rs.getInt("image_id"));
        report.setPrediction(rs.getString("prediction"));
        Double confidence = rs.getObject("confidence_score") != null ? rs.getDouble("confidence_score") : null;
        report.setConfidenceScore(confidence);
        Timestamp generatedAt = rs.getTimestamp("generated_at");
        if (generatedAt != null) {
            report.setGeneratedAt(generatedAt.toLocalDateTime());
        }
        // Handle is_visible field - use getBoolean and default to false if column doesn't exist
        try {
            report.setVisible(rs.getBoolean("is_visible"));
        } catch (SQLException e) {
            // Column might not exist in older database schemas, default to false
            report.setVisible(false);
        }
        return report;
    }
}

