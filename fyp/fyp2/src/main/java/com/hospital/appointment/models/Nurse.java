package com.hospital.appointment.models;

public class Nurse {
    private int nurseId;
    private int userId;
    private String department;

    public Nurse() {
    }

    public Nurse(int nurseId, int userId, String department) {
        this.nurseId = nurseId;
        this.userId = userId;
        this.department = department;
    }

    // Getters and Setters
    public int getNurseId() {
        return nurseId;
    }

    public void setNurseId(int nurseId) {
        this.nurseId = nurseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}




