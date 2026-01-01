package com.hospital.appointment.dao;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.models.XrayImage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XrayImageDAO {
    private static final Logger logger = Logger.getLogger(XrayImageDAO.class.getName());

    /**
     * Create new X-ray image record
     * @return Generated image_id, or -1 if failed
     */
    public int create(XrayImage xrayImage) {
        String sql = "INSERT INTO xray_images (appointment_id, uploaded_by, image_path, upload_date) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, xrayImage.getAppointmentId());
                pstmt.setInt(2, xrayImage.getUploadedBy());
                pstmt.setString(3, xrayImage.getImagePath());
                pstmt.setTimestamp(4, Timestamp.valueOf(xrayImage.getUploadDate()));
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating xray image", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return -1;
    }

    /**
     * Find X-ray images by appointment_id
     */
    public List<XrayImage> findByAppointmentId(int appointmentId) {
        List<XrayImage> images = new ArrayList<>();
        String sql = "SELECT * FROM xray_images WHERE appointment_id = ? ORDER BY upload_date DESC";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, appointmentId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        images.add(mapResultSetToXrayImage(rs));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding xray images by appointment id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return images;
    }

    /**
     * Find X-ray image by image_id
     */
    public XrayImage findById(int imageId) {
        String sql = "SELECT * FROM xray_images WHERE image_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, imageId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToXrayImage(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding xray image by id", e);
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return null;
    }

    /**
     * Delete X-ray image by image_id
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(int imageId) {
        String sql = "DELETE FROM xray_images WHERE image_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, imageId);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting xray image", e);
            e.printStackTrace();
        }
        // Note: We don't close the connection here as it's managed by DatabaseManager
        return false;
    }

    /**
     * Map ResultSet to XrayImage object
     */
    private XrayImage mapResultSetToXrayImage(ResultSet rs) throws SQLException {
        XrayImage xrayImage = new XrayImage();
        xrayImage.setImageId(rs.getInt("image_id"));
        xrayImage.setAppointmentId(rs.getInt("appointment_id"));
        xrayImage.setUploadedBy(rs.getInt("uploaded_by"));
        xrayImage.setImagePath(rs.getString("image_path"));
        Timestamp uploadDate = rs.getTimestamp("upload_date");
        if (uploadDate != null) {
            xrayImage.setUploadDate(uploadDate.toLocalDateTime());
        }
        return xrayImage;
    }
}
