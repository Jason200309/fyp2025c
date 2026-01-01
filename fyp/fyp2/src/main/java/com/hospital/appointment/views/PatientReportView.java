package com.hospital.appointment.views;

import com.hospital.appointment.dao.AIReportDAO;
import com.hospital.appointment.dao.AppointmentDAO;
import com.hospital.appointment.dao.DoctorDiagnosisDAO;
import com.hospital.appointment.dao.PatientDAO;
import com.hospital.appointment.models.AIReport;
import com.hospital.appointment.models.Appointment;
import com.hospital.appointment.models.DoctorDiagnosis;
import com.hospital.appointment.models.Patient;
import com.hospital.appointment.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;

import java.io.File;
import java.util.List;

public class PatientReportView {
    private VBox view;
    private User user;
    private Patient patient;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private AIReportDAO aiReportDAO;
    private DoctorDiagnosisDAO doctorDiagnosisDAO;
    private ObservableList<Appointment> appointments;
    private TableView<Appointment> appointmentsTable;

    public PatientReportView(User user) {
        this.user = user;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.aiReportDAO = new AIReportDAO();
        this.doctorDiagnosisDAO = new DoctorDiagnosisDAO();
        // Get patient record
        this.patient = patientDAO.findByUserId(user.getUserId());
        createView();
    }

    private void createView() {
        view = new VBox(0); // Set spacing to 0 to manage layout via wrappers
        // Deep Navy Medical Background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // --- Header ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        // Medical Blue Header
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
    
        Label titleLabel = new Label("VIEW REPORTS: " + user.getUsername());
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");
    
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
    
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> {
            PatientHomeView patientHomeView = new PatientHomeView(user);
            view.getScene().setRoot(patientHomeView.getView());
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
        // Slate Card for medical records
        appointmentsBox.setStyle("-fx-background-color: #2d3748; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 0);");
    
        VBox textHeader = new VBox(5);
        Label appointmentsTitle = new Label("Personal Appointment History");
        appointmentsTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 18px; -fx-text-fill: white;");
        Label subTitle = new Label("Select an appointment to view AI findings and physician feedback.");
        subTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #a0aec0;");
        textHeader.getChildren().addAll(appointmentsTitle, subTitle);
    
        // --- Modern Dark Table ---
        appointmentsTable = new TableView<>();
        appointmentsTable.setStyle("-fx-background-color: white; -fx-control-inner-background: white; -fx-table-cell-border-color: #ddd;");
        appointmentsTable.setPrefHeight(400);
        appointments = FXCollections.observableArrayList();
        appointmentsTable.setItems(appointments);
        
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(150);
        dateCol.setCellValueFactory(cellData -> {
            java.time.LocalDate date = cellData.getValue().getAppointmentDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.toString() : "");
        });
        
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setPrefWidth(150);
        timeCol.setCellValueFactory(cellData -> {
            java.time.LocalTime time = cellData.getValue().getAppointmentTime();
            return new javafx.beans.property.SimpleStringProperty(time != null ? time.toString() : "");
        });
        
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(150);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        appointmentsTable.getColumns().add(dateCol);
        appointmentsTable.getColumns().add(timeCol);
        appointmentsTable.getColumns().add(statusCol);
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    
        // View Report Button (Action-Oriented Medical Blue)
        Button viewReportButton = new Button("ACCESS SECURE REPORT");
        viewReportButton.setMaxWidth(Double.MAX_VALUE);
        viewReportButton.setStyle(
            "-fx-background-color: #3182ce; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        viewReportButton.setOnAction(e -> viewSelectedAppointmentReport());
    
        appointmentsBox.getChildren().addAll(textHeader, new Separator(), appointmentsTable, viewReportButton);
    
        VBox contentWrapper = new VBox();
        contentWrapper.setPadding(new Insets(30));
        contentWrapper.getChildren().add(appointmentsBox);
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);
    
        view.getChildren().addAll(header, contentWrapper);
        loadAppointments();
    }

    private void loadAppointments() {
        if (patient == null) {
            return;
        }
        appointments.clear();
        // Get all appointments for the patient
        List<Appointment> allAppointments = appointmentDAO.findByPatientId(patient.getPatientId());
        
        // Filter to show only appointments that have a report file (non-null report_file_path)
        for (Appointment appointment : allAppointments) {
            // Get AI reports for this appointment
            List<AIReport> reports = aiReportDAO.findVisibleByAppointmentId(appointment.getAppointmentId());
            if (!reports.isEmpty()) {
                // Check if there's a doctor diagnosis with report_file_path for this report
                AIReport report = reports.get(0);
                DoctorDiagnosis doctorDiagnosis = doctorDiagnosisDAO.findByReportId(report.getReportId());
                if (doctorDiagnosis != null && doctorDiagnosis.getReportFilePath() != null 
                    && !doctorDiagnosis.getReportFilePath().trim().isEmpty()) {
                    // Validate file exists
                    File reportFile = new File(doctorDiagnosis.getReportFilePath());
                    if (reportFile.exists()) {
                        appointments.add(appointment);
                    }
                }
            }
        }
    }

    private void viewSelectedAppointmentReport() {
        Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an appointment to view the report");
            return;
        }

        // Get visible AI reports only (reports sent by doctor)
        List<AIReport> reports = aiReportDAO.findVisibleByAppointmentId(selected.getAppointmentId());
        if (reports.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No report available for this appointment yet. Please wait for the doctor's reply");
            return;
        }

        // Get doctor diagnosis using the report ID
        AIReport report = reports.get(0);
        DoctorDiagnosis doctorDiagnosis = doctorDiagnosisDAO.findByReportId(report.getReportId());
        
        if (doctorDiagnosis == null || doctorDiagnosis.getReportFilePath() == null || doctorDiagnosis.getReportFilePath().trim().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No e-report file available for this appointment yet.");
            return;
        }

        // Create a dialog to display the report
        Dialog<Void> reportDialog = new Dialog<>();
        reportDialog.setTitle("Medical Report - Appointment " + selected.getAppointmentId());
        reportDialog.setHeaderText("Medical Report");

        VBox reportContent = new VBox(15);
        reportContent.setPadding(new Insets(20));
        reportContent.setPrefWidth(600);

        // Display e-report file preview and download button
        VBox reportBox = new VBox(10);
        reportBox.setPadding(new Insets(15));
        reportBox.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        String filePath = doctorDiagnosis.getReportFilePath();
        java.io.File reportFile = new java.io.File(filePath);
        
        if (!reportFile.exists()) {
            Label errorLabel = new Label("Report file not found.");
            errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            errorLabel.setTextFill(Color.RED);
            reportBox.getChildren().add(errorLabel);
        } else {
            // Preview button - opens file externally
            Button previewButton = new Button("Preview");
            previewButton.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
            previewButton.setOnAction(e -> {
                try {
                    if (reportFile.exists()) {
                        java.awt.Desktop.getDesktop().open(reportFile);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Report file not found.");
                    }
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Failed to open file: " + ex.getMessage());
                }
            });
            
            // Download button - uses Files.copy to create a copy, never modifies original
            Button downloadButton = new Button("Download Report");
            downloadButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
            downloadButton.setOnAction(e -> {
                // Validate file exists before download
                if (!reportFile.exists()) {
                    showAlert(Alert.AlertType.ERROR, "Report file not found.");
                    return;
                }
                
                javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                fileChooser.setTitle("Save Report File");
                fileChooser.setInitialFileName(reportFile.getName());
                fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                    new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*")
                );
                
                javafx.stage.Window window = reportDialog.getDialogPane().getScene().getWindow();
                File saveFile = fileChooser.showSaveDialog(window);
                if (saveFile != null) {
                    try {
                        // Use Files.copy to create a copy of the original file (master copy remains unchanged)
                        java.nio.file.Files.copy(reportFile.toPath(), saveFile.toPath(), 
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Report file downloaded successfully!");
                        successAlert.showAndWait();
                    } catch (Exception ex) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Failed to download file: " + ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
            
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(previewButton, downloadButton);
            reportBox.getChildren().add(buttonBox);
        }
        
        reportContent.getChildren().add(reportBox);

        ScrollPane scrollPane = new ScrollPane(reportContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        reportDialog.getDialogPane().setContent(scrollPane);
        ButtonType backAndCloseButtonType = new ButtonType("Back and Close", ButtonBar.ButtonData.OK_DONE);
        reportDialog.getDialogPane().getButtonTypes().add(backAndCloseButtonType);
        reportDialog.showAndWait();
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

