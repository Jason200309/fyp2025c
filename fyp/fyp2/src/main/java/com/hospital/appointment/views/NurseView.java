package com.hospital.appointment.views;

import com.hospital.appointment.dao.AppointmentDAO;
import com.hospital.appointment.dao.PatientDAO;
import com.hospital.appointment.models.Appointment;
import com.hospital.appointment.models.Patient;
import com.hospital.appointment.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;


public class NurseView {
    private VBox view;
    private User user;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private ObservableList<Appointment> appointments;
    private FilteredList<Appointment> filteredAppointments;
    private TableView<Appointment> appointmentsTable;
    private TextField searchField;

    public NurseView(User user) {
        this.user = user;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.appointments = FXCollections.observableArrayList();
        this.filteredAppointments = new FilteredList<>(appointments, p -> true);
        createView();
    }

    private void createView() {
        view = new VBox(0);
        // Deep Navy Medical Background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // --- Header ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        // Blue header for consistency
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
    
        Label titleLabel = new Label("APPOINTMENT MANAGEMENT: " + user.getUsername());
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
    
        // --- Main Content: Management Section ---
        VBox appointmentsBox = new VBox(20);
        appointmentsBox.setPadding(new Insets(30));
        // Slate Card for management
        appointmentsBox.setStyle("-fx-background-color: #2d3748; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 0);");
    
        HBox textHeader = new HBox(15);
        textHeader.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(5);
        Label appointmentsTitle = new Label("PATIENT BOOKING REQUESTS");
        appointmentsTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 18px; -fx-text-fill: white;");
        Label subtitle = new Label("Review, approve, or finalize patient appointments.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #a0aec0;");
        titleBox.getChildren().addAll(appointmentsTitle, subtitle);
        
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
        // White background table with black text
        appointmentsTable.setStyle("-fx-background-color: white; -fx-control-inner-background: white; -fx-table-cell-border-color: #ddd;");
        appointmentsTable.setPrefHeight(400);
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
    
        // --- Action Toolbar ---
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));
    
        String btnBase = "-fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-cursor: hand;";
    
        Button approveButton = new Button("APPROVE");
        approveButton.setStyle(btnBase + "-fx-background-color: #38a169;");
        approveButton.setOnAction(e -> updateSelectedAppointmentStatus(Appointment.Status.APPROVED));
    
        Button completeButton = new Button("MARK COMPLETED");
        completeButton.setStyle(btnBase + "-fx-background-color: #3182ce;");
        completeButton.setOnAction(e -> updateSelectedAppointmentStatus(Appointment.Status.COMPLETED));
    
        Region btnSpacer = new Region();
        HBox.setHgrow(btnSpacer, Priority.ALWAYS);
    
        Button rejectButton = new Button("REJECT REQUEST");
        rejectButton.setStyle(btnBase + "-fx-background-color: #e53e3e;");
        rejectButton.setOnAction(e -> updateSelectedAppointmentStatus(Appointment.Status.REJECTED));
    
        actionButtons.getChildren().addAll(approveButton, completeButton, btnSpacer, rejectButton);
    
        appointmentsBox.getChildren().addAll(textHeader, new Separator(), appointmentsTable, actionButtons);
    
        VBox contentWrapper = new VBox();
        contentWrapper.setPadding(new Insets(30));
        contentWrapper.getChildren().add(appointmentsBox);
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);
    
        view.getChildren().addAll(header, contentWrapper);
        loadAppointments();
    }

    private void loadAppointments() {
        appointments.clear();
        appointments.addAll(appointmentDAO.findAll());
    }

    private void updateSelectedAppointmentStatus(Appointment.Status status) {
        Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an appointment");
            return;
        }

        // Confirmation dialog
        String actionText = status == Appointment.Status.APPROVED ? "approve" : 
                           status == Appointment.Status.REJECTED ? "reject" : "mark as complete";
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Action");
        confirmAlert.setHeaderText("Confirm Action");
        confirmAlert.setContentText("Are you sure you want to " + actionText + " this appointment?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                appointmentDAO.updateStatus(selected.getAppointmentId(), status);
                showAlert(Alert.AlertType.INFORMATION, "Appointment status updated to " + status);
                loadAppointments();
            }
        });
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
