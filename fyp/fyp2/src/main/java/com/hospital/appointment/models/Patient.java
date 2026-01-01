package com.hospital.appointment.models;

import java.time.LocalDate;

public class Patient {
    private int patientId;
    private int userId;
    private String fullName;
    private String icNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public Patient() {
    }

    public Patient(int patientId, int userId, String fullName, String icNumber, 
                   LocalDate dateOfBirth, Gender gender, String address) {
        this.patientId = patientId;
        this.userId = userId;
        this.fullName = fullName;
        this.icNumber = icNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
    }

    // Getters and Setters
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

