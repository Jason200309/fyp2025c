package com.hospital.appointment.views;

import com.hospital.appointment.dao.AppointmentDAO;
import com.hospital.appointment.dao.PatientDAO;
import com.hospital.appointment.models.Appointment;
import com.hospital.appointment.models.Patient;
import com.hospital.appointment.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class AppointmentView {
    private VBox view;
    private User user;
    private Patient patient;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private ObservableList<Appointment> appointments;
    private TableView<Appointment> appointmentsTable;

    public AppointmentView(User user) {
        this.user = user;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        // Get patient record
        this.patient = patientDAO.findByUserId(user.getUserId());
        createView();
    }

    private void createView() {
        view = new VBox(0); // 0 spacing because we use padding in the wrapper
        view.setStyle("-fx-background-color: #1a202c;"); // Deep Navy Background
    
        // --- Header ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
    
        Label titleLabel = new Label("BOOK APPOINTMENT: " + user.getUsername());
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");
    
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
    
        // Header Buttons
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> {
            PatientHomeView patientHomeView = new PatientHomeView(user);
            view.getScene().setRoot(patientHomeView.getView());
        });
    
        Button logoutButton = new Button("Sign Out");
        logoutButton.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
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
    
        header.getChildren().addAll(titleLabel, spacer, backButton, logoutButton);
    
        // --- Booking Form Card ---
        VBox bookingForm = new VBox(20);
        bookingForm.setPadding(new Insets(25));
        bookingForm.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
    
        Label formTitle = new Label("Request New Diagnostic Appointment");
        formTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 18px; -fx-text-fill: #2d3748;");
    
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(1));
        datePicker.setPrefWidth(250);
        datePicker.setStyle("-fx-background-color: #f7fafc;");
    
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.getItems().addAll("09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30");
        timeComboBox.setValue("09:00");
        timeComboBox.setPrefWidth(250);
        timeComboBox.setStyle("-fx-background-color: #f7fafc;");
    
        Button bookButton = new Button("BOOK NOW");
        bookButton.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 40; -fx-cursor: hand; -fx-background-radius: 4;");
        bookButton.setOnAction(e -> {
            if (patient == null) {
                showAlert(Alert.AlertType.WARNING, "Patient profile not found. Please complete your profile first.");
                return;
            }
            
            LocalDate selectedDate = datePicker.getValue();
            String selectedTime = timeComboBox.getValue();
            
            if (selectedDate == null || selectedTime == null) {
                showAlert(Alert.AlertType.WARNING, "Please select both date and time.");
                return;
            }
            
            try {
                java.time.LocalTime appointmentTime = java.time.LocalTime.parse(selectedTime);
                
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Appointment");
                confirmAlert.setHeaderText("Book Appointment");
                confirmAlert.setContentText("Are you sure you want to book an appointment for " + selectedDate + " at " + selectedTime + "?");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        Appointment appointment = new Appointment();
                        appointment.setPatientId(patient.getPatientId());
                        appointment.setAppointmentDate(selectedDate);
                        appointment.setAppointmentTime(appointmentTime);
                        appointment.setStatus(Appointment.Status.PENDING);
                        
                        int appointmentId = appointmentDAO.create(appointment);
                        if (appointmentId != -1) {
                            showAlert(Alert.AlertType.INFORMATION, "Appointment booked successfully! Your appointment ID is " + appointmentId + ". Please wait for nurse approval.");
                            loadAppointments();
                            // Reset form
                            datePicker.setValue(LocalDate.now().plusDays(1));
                            timeComboBox.setValue("09:00");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Failed to book appointment. Please try again.");
                        }
                    }
                });
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
            }
        });
    
        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(15);
        
        Label dLabel = new Label("Preferred Date:");
        dLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        Label tLabel = new Label("Preferred Time:");
        tLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
    
        formGrid.add(dLabel, 0, 0);
        formGrid.add(datePicker, 1, 0);
        formGrid.add(tLabel, 0, 1);
        formGrid.add(timeComboBox, 1, 1);
    
        bookingForm.getChildren().addAll(formTitle, new Separator(), formGrid, bookButton);
    
        // --- Appointments Table Section ---
        VBox appointmentsBox = new VBox(15);
        appointmentsBox.setPadding(new Insets(25));
        appointmentsBox.setStyle("-fx-background-color: #2d3748; -fx-background-radius: 8;");
    
        Label appointmentsTitle = new Label("My Appointment History");
        appointmentsTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 18px; -fx-text-fill: white;");
    
        appointmentsTable = new TableView<>();
        appointmentsTable.setStyle("-fx-background-color: white; -fx-control-inner-background: white; -fx-table-cell-border-color: #ddd;");
        appointments = FXCollections.observableArrayList();
        appointmentsTable.setItems(appointments);
    
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(150);
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAppointmentDate().toString()));
    
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setPrefWidth(150);
        timeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAppointmentTime().toString()));
    
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(150);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    
        appointmentsTable.getColumns().addAll(dateCol, timeCol, statusCol);
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    
        appointmentsBox.getChildren().addAll(appointmentsTitle, appointmentsTable);
    
        // Content Wrapper
        VBox contentWrapper = new VBox(30);
        contentWrapper.setPadding(new Insets(40));
        contentWrapper.getChildren().addAll(bookingForm, appointmentsBox);
    
        view.getChildren().addAll(header, contentWrapper);
        loadAppointments();
    }

    private void loadAppointments() {
        if (patient == null) {
            return;
        }
        appointments.clear();
        appointments.addAll(appointmentDAO.findByPatientId(patient.getPatientId()));
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

