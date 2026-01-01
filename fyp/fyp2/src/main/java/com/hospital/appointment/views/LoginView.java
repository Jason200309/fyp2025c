package com.hospital.appointment.views;

import com.hospital.appointment.dao.UserDAO;
import com.hospital.appointment.models.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {

    private VBox view;



    public LoginView() {

        createView();

    }



    private void createView() {
        view = new VBox(0);
        // Professional Medical Background (Deep Navy/Slate)
        view.setStyle("-fx-background-color: #1a202c;");
        
        // Header with Exit Button
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #2b6cb0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 4; -fx-padding: 8 20;");
        exitButton.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Exit");
            confirmAlert.setHeaderText("Exit System");
            confirmAlert.setContentText("Are you sure you want to exit the system?");
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    Platform.exit();
                }
            });
        });
        
        header.getChildren().addAll(headerSpacer, exitButton);
    
        // Main Content Area (Centered)
        VBox contentArea = new VBox(20);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(40));
        VBox.setVgrow(contentArea, Priority.ALWAYS);
    
        // Login Form Card
        VBox loginForm = new VBox(15);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setMaxWidth(400);
        loginForm.setPadding(new Insets(40));
        // White card with subtle border to look like a medical tablet/interface
        loginForm.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; -fx-border-color: #cbd5e0; -fx-border-width: 1;");
    
        // Title (Changed to a professional Sans-Serif and Navy color)
        Label titleLabel = new Label("AI-RAY DIAGNOSTICS");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#2d3748"));
    
        Label usernameLabel = new Label("Staff/Patient ID");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter ID");
        // Styling the text field to look modern and flat
        usernameField.setStyle("-fx-background-color: #f7fafc; -fx-border-color: #edf2f7; -fx-border-radius: 4; -fx-padding: 10;");
    
        Label passwordLabel = new Label("Security Password");
        passwordLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setStyle("-fx-background-color: #f7fafc; -fx-border-color: #edf2f7; -fx-border-radius: 4; -fx-padding: 10;");
    
        // Primary Action Button (Medical Blue)
        Button loginButton = new Button("ACCESS SYSTEM");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12; -fx-background-radius: 4; -fx-cursor: hand;");
        
        // Secondary Action Button (Neutral/Ghost style)
        Button registerButton = new Button("Register New Patient");
        registerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-font-size: 12px; -fx-underline: true; -fx-cursor: hand;");
    
        // Add everything back to your original containers
        loginForm.getChildren().addAll(
            titleLabel, new Separator(),
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            loginButton, registerButton
        );
    
        contentArea.getChildren().add(loginForm);
        view.getChildren().addAll(header, contentArea);
        
        // Wire up your original logic
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter both username and password");
                return;
            }
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                navigateToRoleView(user);
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid username or password");
            }
        });
    
        registerButton.setOnAction(e -> {
            RegisterView registerView = new RegisterView();
            view.getScene().setRoot(registerView.getView());
        });
    }



    private void navigateToRoleView(User user) {

        switch (user.getRole()) {

            case PATIENT:

                PatientHomeView patientHomeView = new PatientHomeView(user);

                view.getScene().setRoot(patientHomeView.getView());

                break;

            case NURSE:

                NurseHomeView nurseHomeView = new NurseHomeView(user);

                view.getScene().setRoot(nurseHomeView.getView());

                break;

            case DOCTOR:

                DoctorHomeView doctorHomeView = new DoctorHomeView(user);

                view.getScene().setRoot(doctorHomeView.getView());

                break;

            case ADMIN:

                AdminView adminView = new AdminView(user);

                view.getScene().setRoot(adminView.getView());

                break;

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
