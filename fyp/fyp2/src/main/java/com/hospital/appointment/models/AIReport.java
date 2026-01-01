package com.hospital.appointment.models;

import java.time.LocalDateTime;

public class AIReport {
    private int reportId;
    private int imageId;
    private String prediction;
    private Double confidenceScore;
    private LocalDateTime generatedAt;
    private boolean isVisible;

    public AIReport() {
    }

    public AIReport(int reportId, int imageId, String prediction, Double confidenceScore, LocalDateTime generatedAt) {
        this.reportId = reportId;
        this.imageId = imageId;
        this.prediction = prediction;
        this.confidenceScore = confidenceScore;
        this.generatedAt = generatedAt;
        this.isVisible = false;
    }

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}

