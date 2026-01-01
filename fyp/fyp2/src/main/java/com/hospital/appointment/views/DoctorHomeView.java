package com.hospital.appointment.views;

import com.hospital.appointment.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.control.Separator;

public class DoctorHomeView {
    private VBox view;
    private final User user;

    public DoctorHomeView(User user) {
        this.user = user;
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
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
    
        Label titleLabel = new Label("PHYSICIAN WORKSTATION: " + user.getUsername());
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
        logoutButton.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
        logoutButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Logout");
            confirmAlert.setHeaderText("End Session");
            confirmAlert.setContentText("Are you sure you want to logout of the diagnostic system?");
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    LoginView loginView = new LoginView();
                    view.getScene().setRoot(loginView.getView());
                }
            });
        });
    
        header.getChildren().addAll(titleLabel, headerSpacer, backButton, logoutButton);
    
        // --- Main Content Area ---
        VBox menu = new VBox(30);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(40));
    
        Label menuTitle = new Label("Clinical Decision Support");
        menuTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 26px; -fx-text-fill: #e2e8f0;");
        
        Label menuSubtitle = new Label("Select a diagnostic module to begin");
        menuSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #a0aec0;");
    
        // Container for Cards
        HBox cardContainer = new HBox(40);
        cardContainer.setAlignment(Pos.CENTER);
    
        // Card 1: AI Review
        VBox reviewAiReportBtn = createModuleCard(
            "AI X-RAY ANALYSIS", 
            "Process new chest X-rays through the AI diagnostic engine.",
            "#3182ce" // Medical Blue
        );
        reviewAiReportBtn.setOnMouseClicked(e -> {
            DoctorView doctorView = new DoctorView(user);
            view.getScene().setRoot(doctorView.getView());
        });
    
        // Card 2: All Reports
        VBox viewAllReportsBtn = createModuleCard(
            "PATIENT ARCHIVES", 
            "Access historical reports, AI findings, and patient history.",
            "#38a169" // Clinical Green
        );
        viewAllReportsBtn.setOnMouseClicked(e -> {
            AllReportsView allReportsView = new AllReportsView(user);
            allReportsView.show();
        });
    
        cardContainer.getChildren().addAll(reviewAiReportBtn, viewAllReportsBtn);
    
        // Build the layout
        VBox titleGroup = new VBox(5, menuTitle, menuSubtitle);
        titleGroup.setAlignment(Pos.CENTER);
        
        menu.getChildren().addAll(titleGroup, new Separator(), cardContainer);
    
        // Spacers to keep it centered
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
    
        view.getChildren().addAll(header, topSpacer, menu, bottomSpacer);
    }
    
    // Helper method for the "Module Cards"
    private VBox createModuleCard(String title, String desc, String accentColor) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(350, 220);
        card.setPadding(new Insets(30));
        card.setCursor(javafx.scene.Cursor.HAND);
        
        card.setStyle(
            "-fx-background-color: #2d3748; " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: " + accentColor + "; " +
            "-fx-border-width: 0 0 6 0; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 0);"
        );
    
        Label t = new Label(title);
        t.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        
        Label d = new Label(desc);
        d.setWrapText(true);
        d.setAlignment(Pos.CENTER);
        d.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 14px; -fx-line-spacing: 5;");
    
        card.getChildren().addAll(t, d);
        
        // Smooth Hover Effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-background-color: #4a5568; -fx-translate-y: -5;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-background-color: #4a5568; -fx-translate-y: -5;", "-fx-background-color: #2d3748;")));
        
        return card;
    }

    public VBox getView() {
        return view;
    }
}