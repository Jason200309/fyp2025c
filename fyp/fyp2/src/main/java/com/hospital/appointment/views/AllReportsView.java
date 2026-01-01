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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AllReportsView {
    private Stage stage;
    private User user;
    private AIReportDAO aiReportDAO;
    private DoctorDiagnosisDAO doctorDiagnosisDAO;
    private AppointmentDAO appointmentDAO;
    private XrayImageDAO xrayImageDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;

    public AllReportsView(User user) {
        this.user = user;
        this.aiReportDAO = new AIReportDAO();
        this.doctorDiagnosisDAO = new DoctorDiagnosisDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.xrayImageDAO = new XrayImageDAO();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
    }

    public void show() {
        stage = new Stage();
        stage.setTitle("All Reports - AI and Doctor Diagnosis");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setWidth(1100); // Slightly wider for better column spacing
        stage.setHeight(800);
    
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        // Using the established Deep Navy background
        root.setStyle("-fx-background-color: #1a202c;");
    
        // --- Header ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        
        Label titleLabel = new Label("DIAGNOSTIC ARCHIVE: " + user.getUsername());
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Button headerCloseButton = new Button("Back and Close");
        headerCloseButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4;");
        headerCloseButton.setOnAction(e -> stage.close());
        
        header.getChildren().addAll(titleLabel, headerSpacer, headerCloseButton);
    
        // --- Reports Table Styling ---
        TableView<ReportData> reportsTable = new TableView<>();
        reportsTable.setStyle(
            "-fx-background-color: white; " +
            "-fx-control-inner-background: white; " +
            "-fx-table-cell-border-color: #ddd; " +
            "-fx-table-header-border-color: transparent;"
        );
        
        // Injecting CSS for the table headers and selection color
        // Note: In a real app, this is better in a .css file, but keeping it inline as requested:
        reportsTable.setFixedCellSize(40);
    
        ObservableList<ReportData> reports = FXCollections.observableArrayList();
    
        TableColumn<ReportData, Integer> reportIdCol = new TableColumn<>("ID");
        reportIdCol.setPrefWidth(60);
        reportIdCol.setCellValueFactory(new PropertyValueFactory<>("reportId"));
    
        TableColumn<ReportData, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setPrefWidth(180);
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
    
        TableColumn<ReportData, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
    
        TableColumn<ReportData, LocalTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
    
        TableColumn<ReportData, String> aiPredictionCol = new TableColumn<>("AI Result");
        aiPredictionCol.setPrefWidth(150);
        aiPredictionCol.setCellValueFactory(new PropertyValueFactory<>("aiPrediction"));
    
        TableColumn<ReportData, String> aiConfidenceCol = new TableColumn<>("AI Conf.");
        aiConfidenceCol.setCellValueFactory(new PropertyValueFactory<>("aiConfidence"));
    
        TableColumn<ReportData, String> doctorDiagnosisCol = new TableColumn<>("Doctor Diagnosis");
        doctorDiagnosisCol.setCellValueFactory(cellData -> {
            String diagnosis = cellData.getValue().getDoctorDiagnosis();
            if (diagnosis != null && diagnosis.length() > 50) {
                diagnosis = diagnosis.substring(0, 47) + "...";
            }
            return new javafx.beans.property.SimpleStringProperty(diagnosis);
        });
    
        reportsTable.getColumns().addAll(reportIdCol, patientCol, dateCol, timeCol, 
                                         aiPredictionCol, aiConfidenceCol, doctorDiagnosisCol);
        reportsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    
        loadAllReports(reports);
        reportsTable.setItems(reports);
    
        // --- Details Area ---
        VBox detailsContainer = new VBox(10);
        detailsContainer.setPadding(new Insets(20));
        detailsContainer.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #3182ce; " + 
            "-fx-border-width: 0 0 0 5;" // Left blue accent border
        );
    
        Label detailsTitle = new Label("Selected Report Details");
        detailsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748;");
        
        Label detailsLabel = new Label("Please select a diagnostic entry from the table above to view full AI results and doctor notes.");
        detailsLabel.setStyle("-fx-text-fill: #718096; -fx-font-style: italic;");
        detailsLabel.setWrapText(true);
    
        detailsContainer.getChildren().addAll(detailsTitle, detailsLabel);
    
        ScrollPane detailsScrollPane = new ScrollPane(detailsContainer);
        detailsScrollPane.setFitToWidth(true);
        detailsScrollPane.setPrefHeight(250);
        detailsScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
    
        // Handle row selection
        reportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                detailsLabel.setStyle("-fx-text-fill: #2d3748; -fx-font-style: normal;");
                displayReportDetails(newSelection, detailsContainer);
            }
        });
    
        // --- Footer Button ---
        Button closeButton = new Button("Back and Close");
        closeButton.setStyle(
            "-fx-background-color: #4a5568; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " + 
            "-fx-padding: 12 40; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> stage.close());
    
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);
    
        root.getChildren().addAll(header, reportsTable, detailsScrollPane, buttonBox);
        VBox.setVgrow(reportsTable, Priority.ALWAYS);
    
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        // Optional: Add a subtle glow to the stage
        stage.show();
    }

    private void loadAllReports(ObservableList<ReportData> reports) {
        List<AIReport> aiReports = new java.util.ArrayList<>();
        
        // Get all appointments with X-ray images
        for (Appointment apt : appointmentDAO.findAll()) {
            List<XrayImage> images = xrayImageDAO.findByAppointmentId(apt.getAppointmentId());
            if (!images.isEmpty()) {
                // Get AI reports for this appointment
                List<AIReport> aptReports = aiReportDAO.findByAppointmentId(apt.getAppointmentId());
                aiReports.addAll(aptReports);
            }
        }

        // Create report data entries
        for (AIReport aiReport : aiReports) {
            // Find appointment through X-ray image
            XrayImage xrayImage = xrayImageDAO.findById(aiReport.getImageId());
            if (xrayImage != null) {
                Appointment appointment = appointmentDAO.findById(xrayImage.getAppointmentId());
                if (appointment != null) {
                    Patient patient = patientDAO.findByPatientId(appointment.getPatientId());
                    String patientName = patient != null ? patient.getFullName() : "Unknown";

                    // Get doctor diagnosis if exists
                    DoctorDiagnosis doctorDiagnosis = doctorDiagnosisDAO.findByReportId(aiReport.getReportId());
                    String doctorDiagnosisText = doctorDiagnosis != null ? doctorDiagnosis.getComments() : "Not available";

                    double confidence = aiReport.getConfidenceScore() != null ? aiReport.getConfidenceScore() * 100.0 : 0.0;
                    String confidenceText = String.format("%.2f%%", confidence);

                    ReportData reportData = new ReportData(
                        aiReport.getReportId(),
                        patientName,
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime(),
                        aiReport.getPrediction(),
                        confidenceText,
                        doctorDiagnosisText,
                        aiReport,
                        doctorDiagnosis,
                        xrayImage
                    );

                    reports.add(reportData);
                }
            }
        }
    }

    private void displayReportDetails(ReportData reportData, VBox detailsBox) {
        detailsBox.getChildren().clear();

        Label detailsTitle = new Label("Report Details");
        detailsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // X-Ray Image
        if (reportData.getXrayImage() != null) {
            File imageFile = new File(reportData.getXrayImage().getImagePath());
            if (imageFile.exists()) {
                VBox imageBox = new VBox(5);
                imageBox.setPadding(new Insets(10));
                imageBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 5;");
                
                Label imageTitle = new Label("X-Ray Image:");
                imageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                
                ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(400);
                
                imageBox.getChildren().addAll(imageTitle, imageView);
                detailsBox.getChildren().add(imageBox);
            }
        }

        // AI Report Details
        VBox aiDetails = new VBox(5);
        aiDetails.setPadding(new Insets(10));
        aiDetails.setStyle("-fx-background-color: #e8f4f8; -fx-background-radius: 5;");
        Label aiTitle = new Label("AI Analysis:");
        aiTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        aiTitle.setStyle("-fx-text-fill: black;");
        Label aiPrediction = new Label("Prediction: " + reportData.getAiPrediction());
        aiPrediction.setStyle("-fx-text-fill: black;");
        Label aiConfidence = new Label("Confidence: " + reportData.getAiConfidence());
        aiConfidence.setStyle("-fx-text-fill: black;");
        aiDetails.getChildren().addAll(aiTitle, aiPrediction, aiConfidence);

        // Doctor Diagnosis Details
        VBox doctorDetails = new VBox(5);
        doctorDetails.setPadding(new Insets(10));
        doctorDetails.setStyle("-fx-background-color: #fff4e6; -fx-background-radius: 5;");
        Label doctorTitle = new Label("Doctor Diagnosis:");
        doctorTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        doctorTitle.setStyle("-fx-text-fill: black;");
        Label doctorText = new Label(reportData.getDoctorDiagnosis());
        doctorText.setWrapText(true);
        doctorText.setStyle("-fx-text-fill: black;");
        doctorDetails.getChildren().addAll(doctorTitle, doctorText);

        // Check if report file already uploaded
        DoctorDiagnosis currentDiagnosis = doctorDiagnosisDAO.findByReportId(reportData.getReportId());
        boolean hasReportFile = currentDiagnosis != null && currentDiagnosis.getReportFilePath() != null 
                               && !currentDiagnosis.getReportFilePath().trim().isEmpty();
        
        // Upload/Delete e-report Button
        Button eReportButton;
        if (hasReportFile) {
            eReportButton = new Button("Delete e-report");
            eReportButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
            eReportButton.setOnAction(e -> deleteEReport(reportData.getReportId(), detailsBox, reportData));
        } else {
            eReportButton = new Button("Upload e-report");
            eReportButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
            eReportButton.setOnAction(e -> uploadEReport(reportData.getReportId(), detailsBox, reportData));
        }
        
        // Send Report to Patient Button
        // Reload AI report to get current visibility status
        AIReport currentReport = aiReportDAO.findById(reportData.getReportId());
        boolean isVisible = currentReport != null && currentReport.isVisible();
        
        Button sendToPatientButton = new Button("Send Report to Patient");
        sendToPatientButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(eReportButton, sendToPatientButton);
        
        VBox buttonContainer = new VBox(5);
        buttonContainer.getChildren().add(buttonBox);
        
        // Handle send to patient button state
        if (isVisible) {
            Label alreadySentLabel = new Label("✓ Report already sent to patient");
            alreadySentLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
            sendToPatientButton.setDisable(true);
            buttonContainer.getChildren().add(alreadySentLabel);
        } else {
            sendToPatientButton.setOnAction(e -> {
                // Confirmation dialog
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Send Report");
                confirmAlert.setHeaderText("Confirm Send Report to Patient");
                confirmAlert.setContentText("Are you sure you want to send this report to the patient?");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        if (sendReportToPatient(reportData.getReportId())) {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("Report has been sent to the patient successfully.");
                            successAlert.showAndWait();
                            // Refresh the report details to reflect the change
                            displayReportDetails(reportData, detailsBox);
                        } else {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText(null);
                            errorAlert.setContentText("Failed to send report to patient. Please try again.");
                            errorAlert.showAndWait();
                        }
                    }
                });
            });
        }
        
        detailsBox.getChildren().addAll(detailsTitle, aiDetails, doctorDetails, buttonContainer);
    }

    private boolean sendReportToPatient(int reportId) {
        return aiReportDAO.updateIsVisible(reportId, true);
    }

    private void deleteEReport(int reportId, VBox detailsBox, ReportData reportData) {
        // Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete e-report");
        confirmAlert.setHeaderText("Confirm Delete e-report");
        confirmAlert.setContentText("Are you sure you want to delete this e-report file? This action cannot be undone.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    // Get current diagnosis to get file path
                    DoctorDiagnosis diagnosis = doctorDiagnosisDAO.findByReportId(reportId);
                    if (diagnosis == null || diagnosis.getReportFilePath() == null || diagnosis.getReportFilePath().trim().isEmpty()) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("No e-report file found to delete.");
                        errorAlert.showAndWait();
                        return;
                    }
                    
                    String filePath = diagnosis.getReportFilePath();
                    File reportFile = new File(filePath);
                    
                    // Delete file from filesystem
                    if (reportFile.exists()) {
                        try {
                            Files.deleteIfExists(reportFile.toPath());
                        } catch (Exception ex) {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText(null);
                            errorAlert.setContentText("Failed to delete file from filesystem: " + ex.getMessage());
                            errorAlert.showAndWait();
                            return;
                        }
                    }
                    
                    // Update database to set report_file_path to null
                    if (doctorDiagnosisDAO.updateReportFilePath(reportId, null)) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("E-report deleted successfully!");
                        successAlert.showAndWait();
                        // Refresh the report details
                        displayReportDetails(reportData, detailsBox);
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Failed to update database. File may have been deleted but database was not updated.");
                        errorAlert.showAndWait();
                    }
                } catch (Exception ex) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to delete e-report: " + ex.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }

    private void uploadEReport(int reportId, VBox detailsBox, ReportData reportData) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select E-Report PDF File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                // Create /reports/ directory if it doesn't exist
                File reportsDir = new File("reports");
                if (!reportsDir.exists()) {
                    reportsDir.mkdirs();
                }

                // Copy file to /reports/ directory
                String fileName = reportId + "_" + System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(reportsDir, fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Store only the file path in doctor_diagnosis table
                String filePath = destFile.getAbsolutePath();
                
                // Get doctor_id from user
                Integer doctorId = doctorDAO.findDoctorIdByUserId(user.getUserId());
                if (doctorId == null) {
                    // If update fails, delete the copied file
                    try {
                        Files.deleteIfExists(destFile.toPath());
                    } catch (Exception ex) {
                        // Ignore deletion errors
                    }
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to find doctor information. Please try again.");
                    errorAlert.showAndWait();
                    return;
                }
                
                if (doctorDiagnosisDAO.updateOrInsertReportFilePath(reportId, doctorId, filePath)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("E-report uploaded successfully!");
                    successAlert.showAndWait();
                    // Refresh the report details
                    displayReportDetails(reportData, detailsBox);
                } else {
                    // If update fails, delete the copied file
                    try {
                        Files.deleteIfExists(destFile.toPath());
                    } catch (Exception ex) {
                        // Ignore deletion errors
                    }
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to save e-report file path to database.");
                    errorAlert.showAndWait();
                }
            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to upload e-report: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    // Helper class to hold report data for table display
    public static class ReportData {
        private int reportId;
        private String patientName;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String aiPrediction;
        private String aiConfidence;
        private String doctorDiagnosis;
        private AIReport aiReport;
        private DoctorDiagnosis doctorDiagnosisObj;
        private XrayImage xrayImage;

        public ReportData(int reportId, String patientName, LocalDate appointmentDate, 
                         LocalTime appointmentTime, String aiPrediction, String aiConfidence,
                         String doctorDiagnosis, AIReport aiReport, DoctorDiagnosis doctorDiagnosisObj,
                         XrayImage xrayImage) {
            this.reportId = reportId;
            this.patientName = patientName;
            this.appointmentDate = appointmentDate;
            this.appointmentTime = appointmentTime;
            this.aiPrediction = aiPrediction;
            this.aiConfidence = aiConfidence;
            this.doctorDiagnosis = doctorDiagnosis;
            this.aiReport = aiReport;
            this.doctorDiagnosisObj = doctorDiagnosisObj;
            this.xrayImage = xrayImage;
        }

        // Getters
        public int getReportId() { return reportId; }
        public String getPatientName() { return patientName; }
        public LocalDate getAppointmentDate() { return appointmentDate; }
        public LocalTime getAppointmentTime() { return appointmentTime; }
        public String getAiPrediction() { return aiPrediction; }
        public String getAiConfidence() { return aiConfidence; }
        public String getDoctorDiagnosis() { return doctorDiagnosis; }
        public AIReport getAiReport() { return aiReport; }
        public DoctorDiagnosis getDoctorDiagnosisObj() { return doctorDiagnosisObj; }
        public XrayImage getXrayImage() { return xrayImage; }
    }
}

