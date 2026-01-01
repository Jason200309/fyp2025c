package com.hospital.appointment.views;

import com.hospital.appointment.dao.AppointmentDAO;
import com.hospital.appointment.dao.PatientDAO;
import com.hospital.appointment.models.Appointment;
import com.hospital.appointment.models.Patient;
import com.hospital.appointment.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PatientNotificationView {
    private Stage stage;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private User user;
    private Patient patient;
    private List<Appointment> unseenAppointments;

    public PatientNotificationView(User user) {
        this.user = user;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.patient = patientDAO.findByUserId(user.getUserId());
        this.unseenAppointments = new ArrayList<>();
    }

    public void show() {
        showAndWait();
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Notification Center");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setWidth(750);
        stage.setHeight(550);
    
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        // Consistent Deep Navy background for the modal
        root.setStyle("-fx-background-color: #1a202c;");
    
        // --- Header Section ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        // Soft Blue-Grey for notification context
        header.setStyle("-fx-background-color: #2b6cb0; -fx-background-radius: 8 8 0 0;");
    
        VBox titleBox = new VBox(2);
        String displayName = patient != null ? patient.getFullName() : user.getUsername();
        Label titleLabel = new Label("NEW UPDATES");
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");
        
        Label userSubLabel = new Label("Account: " + displayName);
        userSubLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ebf8ff;");
        titleBox.getChildren().addAll(titleLabel, userSubLabel);
    
        header.getChildren().add(titleBox);
    
        // --- Table Container ---
        VBox appointmentsBox = new VBox(15);
        appointmentsBox.setPadding(new Insets(20));
        // Darker slate background for the "unread" content
        appointmentsBox.setStyle("-fx-background-color: #2d3748; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
    
        Label appointmentsTitle = new Label("Unseen Approved Appointments");
        appointmentsTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 15px; -fx-text-fill: #38a169;"); // Clinical Green for Approved
    
        TableView<Appointment> appointmentsTable = new TableView<>();
        // Dark mode table styling
        appointmentsTable.setStyle("-fx-background-color: white; -fx-control-inner-background: white; -fx-table-cell-border-color: #ddd;");
        
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        appointmentsTable.setItems(appointments);
    
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Appointment Date");
        dateCol.setPrefWidth(200);
        dateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getAppointmentDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.toString() : "");
        });
    
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Scheduled Time");
        timeCol.setPrefWidth(150);
        timeCol.setCellValueFactory(cellData -> {
            LocalTime time = cellData.getValue().getAppointmentTime();
            return new javafx.beans.property.SimpleStringProperty(time != null ? time.toString() : "");
        });
    
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Current Status");
        statusCol.setPrefWidth(150);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    
        appointmentsTable.getColumns().addAll(dateCol, timeCol, statusCol);
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    
        // Logic for loading appointments
        if (patient != null) {
            unseenAppointments = appointmentDAO.findUnseenApprovedByPatientId(patient.getPatientId());
            appointments.addAll(unseenAppointments);
        }
    
        appointmentsBox.getChildren().addAll(appointmentsTitle, new Separator(), appointmentsTable);
    
        // --- Close Button (Clinical Blue) ---
        Button closeButton = new Button("ACKNOWLEDGE AND CLOSE");
        closeButton.setStyle(
            "-fx-background-color: #3182ce; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12 40; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
    
        closeButton.setOnAction(e -> {
            markAppointmentsAsSeen();
            stage.close();
        });
    
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);
    
        root.getChildren().addAll(header, appointmentsBox, buttonBox);
        VBox.setVgrow(appointmentsBox, Priority.ALWAYS);
    
        // Window close event logic
        stage.setOnCloseRequest(e -> markAppointmentsAsSeen());
    
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    // Private helper to keep the UI code clean
    private void markAppointmentsAsSeen() {
        if (!unseenAppointments.isEmpty()) {
            List<Integer> appointmentIds = new ArrayList<>();
            for (Appointment apt : unseenAppointments) {
                appointmentIds.add(apt.getAppointmentId());
            }
            appointmentDAO.markAsSeen(appointmentIds);
        }
    }
}

