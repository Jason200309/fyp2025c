package com.hospital.appointment.views;

import com.hospital.appointment.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.*; 
import javafx.scene.layout.*;  

public class AdminView {
    private VBox view;
    private final User user;

    public AdminView(User user) {
        this.user = user;
        createView();
    }

    private void createView() {
        view = new VBox(0); // Set spacing to 0 because we'll handle padding inside
        // Professional Deep Navy Background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // --- Header Section ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        // Medical Blue accent for the header bar
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
    
        Label titleLabel = new Label("SYSTEM ADMINISTRATION: " + user.getUsername());
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white; -fx-letter-spacing: 2px;");
    
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
    
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
            confirmAlert.setHeaderText("Sign Out");
            confirmAlert.setContentText("Are you sure you want to end the session?");
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    LoginView loginView = new LoginView();
                    view.getScene().setRoot(loginView.getView());
                }
            });
        });
    
        header.getChildren().addAll(titleLabel, spacer, backButton, logoutButton);
    
        // --- Main Dashboard Menu ---
        VBox contentArea = new VBox(30);
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.setPadding(new Insets(50, 40, 40, 40));
    
        Label menuTitle = new Label("Management Control Panel");
        menuTitle.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 24px; -fx-text-fill: #e2e8f0;");
    
        // Module Container (Horizontal layout for the "Cards")
        HBox moduleContainer = new HBox(30);
        moduleContainer.setAlignment(Pos.CENTER);
    
        // Doctor Registration Card
        VBox doctorCard = createAdminCard("Register Doctor", "Add medical practitioners to the system", "#3182ce");
        doctorCard.setOnMouseClicked(e -> {
            DoctorRegisterView doctorRegisterView = new DoctorRegisterView(user);
            view.getScene().setRoot(doctorRegisterView.getView());
        });
    
        // Nurse Registration Card
        VBox nurseCard = createAdminCard("Register Nurse", "Add nursing staff for appointment management", "#38a169");
        nurseCard.setOnMouseClicked(e -> {
            NurseRegisterView nurseRegisterView = new NurseRegisterView(user);
            view.getScene().setRoot(nurseRegisterView.getView());
        });
    
        moduleContainer.getChildren().addAll(doctorCard, nurseCard);
        contentArea.getChildren().addAll(menuTitle, new Separator(), moduleContainer);
    
        view.getChildren().addAll(header, contentArea);
    }
    
    // Helper method to keep your createView clean while maintaining the style
    private VBox createAdminCard(String title, String description, String colorHex) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(280, 180);
        card.setPadding(new Insets(20));
        card.setCursor(javafx.scene.Cursor.HAND);
        
        card.setStyle(
            "-fx-background-color: #2d3748; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: " + colorHex + "; " +
            "-fx-border-width: 0 0 5 0; " + // Color accent on bottom
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"
        );
    
        Label t = new Label(title.toUpperCase());
        t.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label d = new Label(description);
        d.setWrapText(true);
        d.setAlignment(Pos.CENTER);
        d.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12px;");
    
        card.getChildren().addAll(t, d);
        
        // Hover effects
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-background-color: #4a5568;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-background-color: #4a5568;", "-fx-background-color: #2d3748;")));
        
        return card;
    }

    public VBox getView() {
        return view;
    }
}

