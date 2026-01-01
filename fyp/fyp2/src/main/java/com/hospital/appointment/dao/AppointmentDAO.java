package com.hospital.appointment.dao;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.models.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentDAO {
    private static final Logger logger = Logger.getLogger(AppointmentDAO.class.getName());

    /**
     * Create new appointment
     * @return Generated appointment_id, or -1 if failed
     */
    public int create(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setDate(2, Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setTime(3, Time.valueOf(appointment.getAppointmentTime()));
            pstmt.setString(4, appointment.getStatus().name());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating appointment", e);
        }
        return -1;
    }

    /**
     * Find all appointments
     */
    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all appointments", e);
        }
        return appointments;
    }

    /**
     * Find appointments by patient_id
     */
    public List<Appointment> findByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC, appointment_time DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding appointments by patient id", e);
        }
        return appointments;
    }

    /**
     * Find appointments by status
     */
    public List<Appointment> findByStatus(Appointment.Status status) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE status = ? ORDER BY appointment_date DESC, appointment_time DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding appointments by status", e);
        }
        return appointments;
    }

    /**
     * Find appointment by appointment_id
     */
    public Appointment findById(int appointmentId) {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appointmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding appointment by id", e);
        }
        return null;
    }

    /**
     * Update appointment status
     */
    public void updateStatus(int appointmentId, Appointment.Status status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating appointment status", e);
        }
    }

    /**
     * Find appointments by patient_id where is_seen = false
     */
    public List<Appointment> findUnseenByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? AND is_seen = 0 ORDER BY appointment_date DESC, appointment_time DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding unseen appointments by patient id", e);
        }
        return appointments;
    }

    /**
     * Find appointments by patient_id where is_seen = false AND status = APPROVED
     */
    public List<Appointment> findUnseenApprovedByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? AND is_seen = 0 AND status = 'APPROVED' ORDER BY appointment_date DESC, appointment_time DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding unseen approved appointments by patient id", e);
        }
        return appointments;
    }

    /**
     * Update is_seen to true for multiple appointments
     */
    public void markAsSeen(List<Integer> appointmentIds) {
        if (appointmentIds == null || appointmentIds.isEmpty()) {
            return;
        }
        
        String sql = "UPDATE appointments SET is_seen = 1 WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (Integer appointmentId : appointmentIds) {
                pstmt.setInt(1, appointmentId);
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error marking appointments as seen", e);
        }
    }

    /**
     * Map ResultSet to Appointment object
     */
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        appointment.setStatus(Appointment.Status.valueOf(rs.getString("status")));
        // Handle is_seen field - use getBoolean and default to false if column doesn't exist
        try {
            appointment.setSeen(rs.getBoolean("is_seen"));
        } catch (SQLException e) {
            // Column might not exist in older database schemas, default to false
            appointment.setSeen(false);
        }
        return appointment;
    }
}
