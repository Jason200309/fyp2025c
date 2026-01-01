package com.hospital.appointment.models;

public class User {
    private int userId;
    private String username;
    private String password;
    private Role role;
    private String email;
    private String phone;
    private Status status;

    public enum Role {
        PATIENT, NURSE, DOCTOR, ADMIN
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    public User() {
    }

    public User(int userId, String username, String password, Role role, String email, String phone, Status status) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
