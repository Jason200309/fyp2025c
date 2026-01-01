package com.hospital.appointment.models;

public class Doctor {
    private int doctorId;
    private int userId;
    private String specialization;
    private String licenseNumber;
    private String department;

    public Doctor() {
    }

    public Doctor(int doctorId, int userId, String specialization, String licenseNumber, String department) {
        this.doctorId = doctorId;
        this.userId = userId;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.department = department;
    }

    // Getters and Setters
    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}




