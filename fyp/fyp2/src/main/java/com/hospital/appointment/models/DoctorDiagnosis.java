package com.hospital.appointment.models;

import java.time.LocalDateTime;

public class DoctorDiagnosis {
    private int diagnosisId;
    private int reportId;
    private int doctorId;
    private String diagnosisResult;
    private String comments;
    private LocalDateTime diagnosisDate;
    private String reportFilePath;

    public DoctorDiagnosis() {
    }

    public DoctorDiagnosis(int diagnosisId, int reportId, int doctorId, String diagnosisResult, 
                          String comments, LocalDateTime diagnosisDate) {
        this.diagnosisId = diagnosisId;
        this.reportId = reportId;
        this.doctorId = doctorId;
        this.diagnosisResult = diagnosisResult;
        this.comments = comments;
        this.diagnosisDate = diagnosisDate;
    }

    // Getters and Setters
    public int getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(int diagnosisId) {
        this.diagnosisId = diagnosisId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDiagnosisResult() {
        return diagnosisResult;
    }

    public void setDiagnosisResult(String diagnosisResult) {
        this.diagnosisResult = diagnosisResult;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDateTime diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }
}

