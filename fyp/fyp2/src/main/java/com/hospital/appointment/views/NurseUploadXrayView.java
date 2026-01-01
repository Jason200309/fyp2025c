package com.hospital.appointment.views;

import com.hospital.appointment.api.PneumoniaDetectionAPI;
import com.hospital.appointment.dao.AIReportDAO;
import com.hospital.appointment.dao.AppointmentDAO;
import com.hospital.appointment.dao.NurseDAO;
import com.hospital.appointment.dao.PatientDAO;
import com.hospital.appointment.dao.XrayImageDAO;
import com.hospital.appointment.models.AIReport;
import com.hospital.appointment.models.Appointment;
import com.hospital.appointment.models.Patient;
import com.hospital.appointment.models.User;
import com.hospital.appointment.models.XrayImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class NurseUploadXrayView {
    private static final Logger logger = Logger.getLogger(NurseUploadXrayView.class.getName());
    private VBox view;
    private User user;
    private Integer nurseId;
    private AppointmentDAO appointmentDAO;
    private XrayImageDAO xrayImageDAO;
    private AIReportDAO aiReportDAO;
    private PatientDAO patientDAO;
    private ObservableList<Appointment> appointments;
    private FilteredList<Appointment> filteredAppointments;
    private TableView<Appointment> appointmentsTable;
    private TextField searchField;

    public NurseUploadXrayView(User user) {
        this.user = user;
        this.appointmentDAO = new AppointmentDAO();
        this.xrayImageDAO = new XrayImageDAO();
        this.aiReportDAO = new AIReportDAO();
        this.patientDAO = new PatientDAO();
        this.appointments = FXCollections.observableArrayList();
        this.filteredAppointments = new FilteredList<>(appointments, p -> true);
        // Get nurse_id from user_id
        NurseDAO nurseDAO = new NurseDAO();
        this.nurseId = nurseDAO.findNurseIdByUserId(user.getUserId());
        createView();
    }

    private void createView() {
        view = new VBox(0); // Spacing handled by content wrapper
        // Deep Navy Medical Background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // --- Header ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        // Blue header for consistency
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
    
        Label titleLabel = new Label("RADIOGRAPHY UPLOAD PORTAL: " + user.getUsername());
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");
    
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
    
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> {
            NurseHomeView nurseHomeView = new NurseHomeView(user);
            view.getScene().setRoot(nurseHomeView.getView());
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
    
        // --- Main Content Area ---
        VBox appointmentsBox = new VBox(20);
        appointmentsBox.setPadding(new Insets(30));
        // Darker Slate Card for the worklist
        appointmentsBox.setStyle("-fx-background-color: #2d3748; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 0);");
    
        HBox textHeader = new HBox(15);
        textHeader.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(5);
        Label appointmentsTitle = new Label("PENDING RADIOLOGY UPLOADS");
        appointmentsTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 18px; -fx-text-fill: white;");
        Label appointmentsSubTitle = new Label("Select a completed appointment to link a new X-ray scan.");
        appointmentsSubTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #a0aec0;");
        titleBox.getChildren().addAll(appointmentsTitle, appointmentsSubTitle);
        
        Region searchSpacer = new Region();
        HBox.setHgrow(searchSpacer, Priority.ALWAYS);
        
        Label searchLabel = new Label("Search by Report ID:");
        searchLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
        searchField = new TextField();
        searchField.setPromptText("Enter Report ID...");
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-font-size: 13px; -fx-padding: 5;");
        
        // Add listener to filter appointments based on search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAppointments.setPredicate(appointment -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String reportId = String.valueOf(appointment.getAppointmentId());
                return reportId.toLowerCase().contains(newValue.toLowerCase());
            });
        });
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        textHeader.getChildren().addAll(titleBox, searchSpacer, searchBox);
    
        appointmentsTable = new TableView<>();
        // Styling the table for a modern dark look
        appointmentsTable.setStyle("-fx-background-color: white; -fx-control-inner-background: white; -fx-table-cell-border-color: #ddd;");
        appointmentsTable.setPrefHeight(450);
        appointmentsTable.setItems(filteredAppointments);
        
        TableColumn<Appointment, String> reportIdCol = new TableColumn<>("Report ID");
        reportIdCol.setPrefWidth(100);
        reportIdCol.setCellValueFactory(cellData -> {
            int appointmentId = cellData.getValue().getAppointmentId();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(appointmentId));
        });
        
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setPrefWidth(200);
        patientCol.setCellValueFactory(cellData -> {
            int patientId = cellData.getValue().getPatientId();
            Patient patient = patientDAO.findByPatientId(patientId);
            String name = patient != null ? patient.getFullName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(120);
        dateCol.setCellValueFactory(cellData -> {
            java.time.LocalDate date = cellData.getValue().getAppointmentDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.toString() : "");
        });
        
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setPrefWidth(100);
        timeCol.setCellValueFactory(cellData -> {
            java.time.LocalTime time = cellData.getValue().getAppointmentTime();
            return new javafx.beans.property.SimpleStringProperty(time != null ? time.toString() : "");
        });
        
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(120);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        appointmentsTable.getColumns().add(reportIdCol);
        appointmentsTable.getColumns().add(patientCol);
        appointmentsTable.getColumns().add(dateCol);
        appointmentsTable.getColumns().add(timeCol);
        appointmentsTable.getColumns().add(statusCol);
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    
        // Action Button (Medical Orange/Amber to signify "Attention Required")
        Button uploadXrayButton = new Button("PROCEED TO IMAGE SELECTION");
        uploadXrayButton.setMaxWidth(Double.MAX_VALUE);
        uploadXrayButton.setStyle(
            "-fx-background-color: #ed8936; " + 
            "-fx-text-fill: white; " + 
            "-fx-font-weight: bold; " + 
            "-fx-font-size: 15px; " + 
            "-fx-padding: 15; " + 
            "-fx-background-radius: 5; " + 
            "-fx-cursor: hand;"
        );
    
        uploadXrayButton.setOnAction(e -> {
            Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a completed appointment first");
                return;
            }
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm X-Ray Image Upload");
            confirmAlert.setHeaderText("Initialize Radiography Link");
            confirmAlert.setContentText("Are you sure you want to upload a new X-ray for " + selected.getPatientId() + "?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    uploadXrayImage();
                }
            });
        });
    
        appointmentsBox.getChildren().addAll(textHeader, new Separator(), appointmentsTable, uploadXrayButton);
        
        VBox contentWrapper = new VBox();
        contentWrapper.setPadding(new Insets(30));
        contentWrapper.getChildren().add(appointmentsBox);
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);
    
        view.getChildren().addAll(header, contentWrapper);
        loadCompletedAppointments();
    }

    private void loadCompletedAppointments() {
        appointments.clear();
        // Load both COMPLETED and UPLOADED appointments so all reports are always visible
        appointments.addAll(appointmentDAO.findByStatus(Appointment.Status.COMPLETED));
        appointments.addAll(appointmentDAO.findByStatus(Appointment.Status.UPLOADED));
    }

    private void uploadXrayImage() {
        Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a completed appointment first");
            return;
        }

        if (nurseId == null) {
            showAlert(Alert.AlertType.ERROR, "Nurse record not found. Please contact administrator.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select X-Ray Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.dcm")
        );

        Stage stage = (Stage) view.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Create uploads directory if it doesn't exist
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs();
                }

                // Copy file to uploads directory
                String fileName = selected.getAppointmentId() + "_" + System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(uploadsDir, fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Save to xray_images table
                XrayImage xrayImage = new XrayImage();
                xrayImage.setAppointmentId(selected.getAppointmentId());
                xrayImage.setUploadedBy(nurseId);
                xrayImage.setImagePath(destFile.getAbsolutePath());
                xrayImage.setUploadDate(LocalDateTime.now());

                int imageId = xrayImageDAO.create(xrayImage);
                if (imageId == -1) {
                    showAlert(Alert.AlertType.ERROR, "Failed to save X-ray image to database.");
                    return;
                }

                // Analyze with AI API and save to ai_reports table
                boolean analysisSuccess = analyzeXrayWithAI(destFile, imageId);
                
                if (!analysisSuccess) {
                    // API failed - delete the xray image from database
                    xrayImageDAO.delete(imageId);
                    // Also delete the file
                    try {
                        Files.deleteIfExists(destFile.toPath());
                    } catch (Exception ex) {
                        logger.warning("Failed to delete image file: " + ex.getMessage());
                    }
                    showAlert(Alert.AlertType.WARNING, 
                        "AI service is not available. The X-ray image has been removed.\n" +
                        "Please try again when the AI service is ready.");
                    return;
                }

                // Update appointment status to UPLOADED after successful upload
                appointmentDAO.updateStatus(selected.getAppointmentId(), Appointment.Status.UPLOADED);
                
                showAlert(Alert.AlertType.INFORMATION, "X-Ray image uploaded successfully!");
                // Refresh the appointments list
                loadCompletedAppointments();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error uploading image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Analyzes X-ray image using the FastAPI AI service and saves to ai_reports table.
     * @return true if analysis successful, false if API is not available
     */
    private boolean analyzeXrayWithAI(File imageFile, int imageId) {
        try {
            // Step 1: Initialize API client and send image
            PneumoniaDetectionAPI api = new PneumoniaDetectionAPI();
            PneumoniaDetectionAPI.PneumoniaResult result = api.analyzeXray(imageFile);

            // Step 2: Save AI report to ai_reports table
            AIReport aiReport = new AIReport();
            aiReport.setImageId(imageId);
            aiReport.setPrediction(result.getDiagnosis());
            // Convert percentage back to 0.0-1.0 for database
            aiReport.setConfidenceScore(result.getRawScore());
            aiReport.setGeneratedAt(LocalDateTime.now());

            int reportId = aiReportDAO.create(aiReport);
            if (reportId == -1) {
                logger.warning("AI analysis completed but failed to save report to database.");
                return false;
            }
            
            return true;
                
        } catch (IOException e) {
            // API is not available - return false so caller can delete the image
            logger.warning("AI API unavailable: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // Unexpected error - also return false
            logger.severe("Unexpected error during AI analysis: " + e.getMessage());
            e.printStackTrace();
            return false;
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

