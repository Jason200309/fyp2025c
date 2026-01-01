package com.hospital.appointment.views;

import com.hospital.appointment.dao.AppointmentDAO;
import com.hospital.appointment.dao.PatientDAO;
import com.hospital.appointment.models.Patient;
import com.hospital.appointment.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Separator;

public class PatientHomeView {
    private AnchorPane view;
    private final User user;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private StackPane buttonContainer;
    private StackPane badgePane;

    public PatientHomeView(User user) {
        this.user = user;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        createView();
    }

    private void createView() {
        VBox content = new VBox(0);
        // Deep Navy Medical Background
        content.setStyle("-fx-background-color: #1a202c;");
    
        // --- Header ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
    
        Label titleLabel = new Label("PATIENT PORTAL: " + user.getUsername());
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white; -fx-letter-spacing: 1px;");
    
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
    
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4;");
        backButton.setOnAction(e -> {
            // Already on dashboard, no action needed
        });
        backButton.setDisable(true);
        backButton.setOpacity(0.5);
    
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
    
        // --- Main Menu Section ---
        VBox menu = new VBox(30);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(40));
    
        Label menuTitle = new Label("Patient Services");
        menuTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 26px; -fx-text-fill: #e2e8f0;");
        
        Label menuSubtitle = new Label("Access your medical records and appointments");
        menuSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #a0aec0;");
    
        // Container for Cards (Side by Side)
        HBox cardContainer = new HBox(40);
        cardContainer.setAlignment(Pos.CENTER);
    
        // Card 1: Make Appointment
        VBox bookAppointmentBtn = createPatientCard(
            "BOOK APPOINTMENT", 
            "Schedule a new X-ray scan or consultation.",
            "#3182ce" // Medical Blue
        );
        bookAppointmentBtn.setOnMouseClicked(e -> {
            AppointmentView appointmentView = new AppointmentView(user);
            view.getScene().setRoot(appointmentView.getView());
        });
    
        // Card 2: View Report
        VBox viewStatusBtn = createPatientCard(
            "VIEW RADIOLOGY REPORTS", 
            "Access your AI-analyzed X-ray results and doctor notes.",
            "#38a169" // Clinical Green
        );
        viewStatusBtn.setOnMouseClicked(e -> {
            PatientReportView patientReportView = new PatientReportView(user);
            view.getScene().setRoot(patientReportView.getView());
        });
    
        cardContainer.getChildren().addAll(bookAppointmentBtn, viewStatusBtn);
    
        VBox titleGroup = new VBox(5, menuTitle, menuSubtitle);
        titleGroup.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(titleGroup, new Separator(), cardContainer);
    
        // Vertical Spacing
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
    
        content.getChildren().addAll(header, topSpacer, menu, bottomSpacer);
    
        // --- Notification Button (FAB) ---
        buttonContainer = new StackPane();
        Button notificationButton = new Button("🔔");
        notificationButton.setPrefSize(65, 65);
        notificationButton.setStyle(
            "-fx-background-color: #3182ce; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 24px; " +
            "-fx-background-radius: 50; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5); " +
            "-fx-cursor: hand;"
        );
        
        buttonContainer.getChildren().add(notificationButton);
        updateBadge(); // Assuming this logic adds a badge to the StackPane
        
        notificationButton.setOnAction(e -> {
            PatientNotificationView notificationView = new PatientNotificationView(user);
            notificationView.showAndWait();
            updateBadge();
        });
    
        // --- Final Assembly ---
        view = new AnchorPane();
        view.getChildren().add(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        
        view.getChildren().add(buttonContainer);
        AnchorPane.setBottomAnchor(buttonContainer, 30.0);
        AnchorPane.setRightAnchor(buttonContainer, 30.0);
    }
    
    // Helper for Patient Cards
    private VBox createPatientCard(String title, String desc, String color) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(350, 220);
        card.setPadding(new Insets(30));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle(
            "-fx-background-color: #2d3748; " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: " + color + "; " +
            "-fx-border-width: 0 0 5 0; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"
        );
    
        Label t = new Label(title);
        t.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label d = new Label(desc);
        d.setWrapText(true);
        d.setAlignment(Pos.CENTER);
        d.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 14px;");
    
        card.getChildren().addAll(t, d);
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-background-color: #4a5568; -fx-translate-y: -5;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-background-color: #4a5568; -fx-translate-y: -5;", "-fx-background-color: #2d3748;")));
        return card;
    }

    private void updateBadge() {
        // Remove existing badge if present
        if (badgePane != null && buttonContainer.getChildren().contains(badgePane)) {
            buttonContainer.getChildren().remove(badgePane);
            badgePane = null;
        }
        
        // Check for unseen APPROVED appointments and show badge if any
        Patient patient = patientDAO.findByUserId(user.getUserId());
        if (patient != null) {
            int unseenCount = appointmentDAO.findUnseenApprovedByPatientId(patient.getPatientId()).size();
            if (unseenCount > 0) {
                // Add red badge circle
                Circle badge = new Circle(12);
                badge.setFill(Color.RED);
                Label badgeLabel = new Label(String.valueOf(unseenCount > 9 ? "9+" : unseenCount));
                badgeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                badgeLabel.setTextFill(Color.WHITE);
                badgePane = new StackPane(badge, badgeLabel);
                badgePane.setTranslateX(20);
                badgePane.setTranslateY(-20);
                buttonContainer.getChildren().add(badgePane);
            }
        }
    }

    public AnchorPane getView() {
        return view;
    }
}