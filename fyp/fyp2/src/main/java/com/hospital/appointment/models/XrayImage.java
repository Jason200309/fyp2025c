package com.hospital.appointment.models;

import java.time.LocalDateTime;

public class XrayImage {
    private int imageId;
    private int appointmentId;
    private int uploadedBy; // nurse_id
    private String imagePath;
    private LocalDateTime uploadDate;

    public XrayImage() {
    }

    public XrayImage(int imageId, int appointmentId, int uploadedBy, String imagePath, LocalDateTime uploadDate) {
        this.imageId = imageId;
        this.appointmentId = appointmentId;
        this.uploadedBy = uploadedBy;
        this.imagePath = imagePath;
        this.uploadDate = uploadDate;
    }

    // Getters and Setters
    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(int uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
}
