package com.hospital.appointment.views;

import com.hospital.appointment.dao.AIReportDAO;
import com.hospital.appointment.dao.AppointmentDAO;
import com.hospital.appointment.dao.DoctorDAO;
import com.hospital.appointment.dao.DoctorDiagnosisDAO;
import com.hospital.appointment.dao.PatientDAO;
import com.hospital.appointment.dao.XrayImageDAO;
import com.hospital.appointment.models.AIReport;
import com.hospital.appointment.models.Appointment;
import com.hospital.appointment.models.DoctorDiagnosis;
import com.hospital.appointment.models.Patient;
import com.hospital.appointment.models.User;
import com.hospital.appointment.models.XrayImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class DoctorView {
    private VBox view;
    private User user;
    private AppointmentDAO appointmentDAO;
    private XrayImageDAO xrayImageDAO;
    private AIReportDAO aiReportDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private DoctorDiagnosisDAO doctorDiagnosisDAO;
    private ObservableList<Appointment> appointments;
    private TableView<Appointment> appointmentsTable;
    private VBox resultsPane;
    private TextArea finalDiagnosisField;
    private Button submitDiagnosisButton;
    private Appointment selectedAppointment;

    public DoctorView(User user) {
        this.user = user;
        this.appointmentDAO = new AppointmentDAO();
        this.xrayImageDAO = new XrayImageDAO();
        this.aiReportDAO = new AIReportDAO();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        this.doctorDiagnosisDAO = new DoctorDiagnosisDAO();
        createView();
    }

    private void createView() {
        view = new VBox(0);
        view.setStyle("-fx-background-color: #1a202c;"); // Deep Navy background
    
        // --- Header ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
    
        Label titleLabel = new Label("DIAGNOSTIC WORKSTATION: " + user.getUsername());
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");
    
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
    
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> {
            DoctorHomeView doctorHomeView = new DoctorHomeView(user);
            view.getScene().setRoot(doctorHomeView.getView());
        });
    
        Button logoutButton = new Button("Sign Out");
        logoutButton.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4;");
        logoutButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Logout");
            confirmAlert.setHeaderText("Sign Out");
            confirmAlert.setContentText("Are you sure you want to logout?");
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    LoginView loginView = new LoginView();
                    view.getScene().setRoot(loginView.getView());
                }
            });
        });
    
        header.getChildren().addAll(titleLabel, headerSpacer, backButton, logoutButton);
    
        // --- Main Layout Split ---
        HBox mainContent = new HBox(25);
        mainContent.setPadding(new Insets(25));
        VBox.setVgrow(mainContent, Priority.ALWAYS);
    
        // --- Left Side: Worklist & Diagnosis Input ---
        VBox appointmentsBox = new VBox(15);
        appointmentsBox.setMinWidth(420);
        appointmentsBox.setPadding(new Insets(20));
        appointmentsBox.setStyle("-fx-background-color: #2d3748; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 0);");
    
        Label appointmentsTitle = new Label("PATIENT WORKLIST");
        appointmentsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white; -fx-letter-spacing: 1px;");
    
        appointmentsTable = new TableView<>();
        appointmentsTable.setStyle("-fx-background-color: white; -fx-control-inner-background: white; -fx-table-cell-border-color: #ddd;");
        appointmentsTable.setPrefHeight(300);
        appointments = FXCollections.observableArrayList();
        appointmentsTable.setItems(appointments);
        
        TableColumn<Appointment, String> reportIdCol = new TableColumn<>("Report ID");
        reportIdCol.setPrefWidth(100);
        reportIdCol.setCellValueFactory(cellData -> {
            int appointmentId = cellData.getValue().getAppointmentId();
            List<AIReport> reports = aiReportDAO.findByAppointmentId(appointmentId);
            if (!reports.isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty(String.valueOf(reports.get(0).getReportId()));
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setPrefWidth(150);
        patientCol.setCellValueFactory(cellData -> {
            int patientId = cellData.getValue().getPatientId();
            Patient patient = patientDAO.findByPatientId(patientId);
            String name = patient != null ? patient.getFullName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(100);
        dateCol.setCellValueFactory(cellData -> {
            java.time.LocalDate date = cellData.getValue().getAppointmentDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.toString() : "");
        });
        
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setPrefWidth(80);
        timeCol.setCellValueFactory(cellData -> {
            java.time.LocalTime time = cellData.getValue().getAppointmentTime();
            return new javafx.beans.property.SimpleStringProperty(time != null ? time.toString() : "");
        });
        
        appointmentsTable.getColumns().add(reportIdCol);
        appointmentsTable.getColumns().add(patientCol);
        appointmentsTable.getColumns().add(dateCol);
        appointmentsTable.getColumns().add(timeCol);
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        appointmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedAppointment = newSelection;
                loadXrayResults(newSelection.getAppointmentId());
                finalDiagnosisField.setDisable(false);
                submitDiagnosisButton.setDisable(false);
                finalDiagnosisField.clear();
            } else {
                selectedAppointment = null;
                finalDiagnosisField.setDisable(true);
                submitDiagnosisButton.setDisable(true);
                finalDiagnosisField.clear();
            }
        });
        
        // Styling the Diagnosis Area
        VBox diagnosisArea = new VBox(10);
        diagnosisArea.setPadding(new Insets(15));
        diagnosisArea.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 5; -fx-border-color: #4a5568; -fx-border-width: 1;");
    
        Label finalDiagnosisLabel = new Label("Final diagnosis");
        finalDiagnosisLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #a0aec0;");
        
        finalDiagnosisField = new TextArea();
        finalDiagnosisField.setPromptText("Click a patient above to begin review...");
        finalDiagnosisField.setPrefRowCount(4);
        finalDiagnosisField.setWrapText(true);
        finalDiagnosisField.setStyle("-fx-control-inner-background: #1a202c; -fx-text-fill: white; -fx-prompt-text-fill: #4a5568; -fx-background-color: #1a202c; -fx-border-radius: 4;");
        finalDiagnosisField.setDisable(true);
    
        submitDiagnosisButton = new Button("SUBMIT FINAL REPORT");
        submitDiagnosisButton.setMaxWidth(Double.MAX_VALUE);
        submitDiagnosisButton.setStyle("-fx-background-color: #38a169; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-cursor: hand;");
        submitDiagnosisButton.setDisable(true);
        submitDiagnosisButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Submission");
            confirmAlert.setHeaderText("Submit Final Diagnosis");
            confirmAlert.setContentText("Are you sure you want to submit the final diagnosis?");
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    submitFinalDiagnosis();
                }
            });
        });
    
        diagnosisArea.getChildren().addAll(finalDiagnosisLabel, finalDiagnosisField, submitDiagnosisButton);
        appointmentsBox.getChildren().addAll(appointmentsTitle, new Separator(), appointmentsTable, diagnosisArea);
    
        // --- Right Side: AI Viewer ---
        resultsPane = new VBox(15);
        resultsPane.setPadding(new Insets(20));
        resultsPane.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");
        HBox.setHgrow(resultsPane, Priority.ALWAYS);
    
        Label resultsTitle = new Label("AI DIAGNOSTIC ANALYSIS");
        resultsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748;");
    
        resultsPane.getChildren().add(resultsTitle);
    
        mainContent.getChildren().addAll(appointmentsBox, resultsPane);
        view.getChildren().addAll(header, mainContent);
    
        // Wire up your table logic here as you already have it
        loadAppointments();
    }

    private void loadAppointments() {
        appointments.clear();
        // Only show appointments that have X-ray images
        for (Appointment apt : appointmentDAO.findAll()) {
            List<XrayImage> images = xrayImageDAO.findByAppointmentId(apt.getAppointmentId());
            if (!images.isEmpty()) {
                appointments.add(apt);
            }
        }
    }

    private void loadXrayResults(int appointmentId) {
        resultsPane.getChildren().clear();

        Label resultsTitle = new Label("X-Ray Analysis Results");
        resultsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        resultsPane.getChildren().add(resultsTitle);

        // Get all AI reports for this appointment (ordered by generated_at DESC, so first is latest)
        List<AIReport> aiReports = aiReportDAO.findByAppointmentId(appointmentId);

        if (aiReports.isEmpty()) {
            Label noResultsLabel = new Label("No AI reports found for this appointment");
            resultsPane.getChildren().add(noResultsLabel);
            return;
        }

        // Get only the latest AI report (first in the list since it's ordered DESC)
        AIReport latestAiReport = aiReports.get(0);
        
        // Find the X-ray image associated with this AI report
        XrayImage xrayImage = xrayImageDAO.findById(latestAiReport.getImageId());
        
        if (xrayImage == null) {
            Label noImageLabel = new Label("X-Ray image not found for the latest AI report");
            resultsPane.getChildren().add(noImageLabel);
            return;
        }

        VBox imageResultBox = new VBox(10);
        imageResultBox.setPadding(new Insets(15));
        imageResultBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        // X-Ray Image
        File imageFile = new File(xrayImage.getImagePath());
        if (imageFile.exists()) {
            ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(400);
            imageResultBox.getChildren().add(imageView);
        }

        // AI Analysis Results from ai_reports table
        VBox aiResultsBox = new VBox(10);
        aiResultsBox.setPadding(new Insets(10));
        aiResultsBox.setStyle("-fx-background-color: #e8f4f8; -fx-background-radius: 5;");

        Label aiTitle = new Label("AI Analysis Results");
        aiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        String prediction = latestAiReport.getPrediction();
        Double confidenceRaw = latestAiReport.getConfidenceScore();
        
        // Convert 0.0-1.0 to percentage
        double confidence = confidenceRaw != null ? confidenceRaw * 100.0 : 0.0;
        
        // Determine confidence level
        String confidenceLevel = "N/A";
        if (confidence >= 80) {
            confidenceLevel = "High";
        } else if (confidence >= 60) {
            confidenceLevel = "Moderate";
        } else if (confidence > 0) {
            confidenceLevel = "Low";
        }
        
        // Generate recommendation
        String recommendation = "";
        if ("PNEUMONIA".equals(prediction)) {
            recommendation = confidence >= 80 ? 
                "Strong indication of pneumonia. Recommend immediate medical attention." :
                "Possible pneumonia detected. Further examination advised.";
        } else {
            recommendation = confidence >= 80 ?
                "No signs of pneumonia detected. Chest X-ray appears normal." :
                "Unclear result. Manual review by radiologist recommended.";
        }

        Label diagnosisLabel = new Label("Diagnosis: " + prediction);
        diagnosisLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        if (prediction.equals("PNEUMONIA")) {
            diagnosisLabel.setTextFill(Color.RED);
        } else if (prediction.equals("NORMAL")) {
            diagnosisLabel.setTextFill(Color.GREEN);
        } else {
            diagnosisLabel.setTextFill(Color.ORANGE);
        }

        Label confidenceLabel = new Label("Confidence: " + String.format("%.2f", confidence) + "% (" + confidenceLevel + ")");
        Label recommendationLabel = new Label("Recommendation: " + recommendation);
        recommendationLabel.setWrapText(true);

        // Progress bar for confidence
        ProgressBar confidenceBar = new ProgressBar(confidence / 100.0);
        confidenceBar.setPrefWidth(650);
        confidenceBar.setMaxWidth(Double.MAX_VALUE);

        aiResultsBox.getChildren().addAll(
            aiTitle, diagnosisLabel, confidenceLabel, confidenceBar, recommendationLabel
        );

        imageResultBox.getChildren().add(aiResultsBox);

        Label uploadedLabel = new Label("Uploaded: " + xrayImage.getUploadDate().toString());
        uploadedLabel.setFont(Font.font(10));
        uploadedLabel.setTextFill(Color.GRAY);
        imageResultBox.getChildren().add(uploadedLabel);

        resultsPane.getChildren().add(imageResultBox);
    }

    private void submitFinalDiagnosis() {
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an appointment first.");
            return;
        }

        String diagnosisText = finalDiagnosisField.getText().trim();
        if (diagnosisText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter a final diagnosis before submitting.");
            return;
        }

        // Get report_id for the selected appointment
        List<AIReport> reports = aiReportDAO.findByAppointmentId(selectedAppointment.getAppointmentId());
        if (reports.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No AI report found for this appointment. Cannot submit diagnosis.");
            return;
        }

        int reportId = reports.get(0).getReportId();
        Integer doctorId = doctorDAO.findDoctorIdByUserId(user.getUserId());
        if (doctorId == null) {
            showAlert(Alert.AlertType.ERROR, "Doctor ID not found. Please contact administrator.");
            return;
        }

        // Create doctor diagnosis
        DoctorDiagnosis diagnosis = new DoctorDiagnosis();
        diagnosis.setReportId(reportId);
        diagnosis.setDoctorId(doctorId);
        diagnosis.setDiagnosisResult("FINAL_DIAGNOSIS"); // You can change this based on your needs
        diagnosis.setComments(diagnosisText);
        diagnosis.setDiagnosisDate(LocalDateTime.now());

        int diagnosisId = doctorDiagnosisDAO.create(diagnosis);
        if (diagnosisId > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Final diagnosis submitted successfully!");
            finalDiagnosisField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to submit final diagnosis. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView() {
        return view;
    }
}
