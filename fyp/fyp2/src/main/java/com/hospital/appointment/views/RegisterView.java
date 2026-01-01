package com.hospital.appointment.views;

import com.hospital.appointment.dao.UserDAO;
import com.hospital.appointment.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RegisterView {
    private VBox view;

    public RegisterView() {
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        // Consistent Deep Navy background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // Registration Form Card
        VBox registerForm = new VBox(12); // Slightly tighter spacing for more fields
        registerForm.setAlignment(Pos.CENTER_LEFT); // Align labels to left for readability
        registerForm.setMaxWidth(450);
        registerForm.setPadding(new Insets(35));
        registerForm.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; -fx-border-color: #cbd5e0; -fx-border-width: 1;");
    
        // Title Section
        Label titleLabel = new Label("Patient Registration");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#2d3748"));
        
        Label subHeader = new Label("Create your secure diagnostic account");
        subHeader.setStyle("-fx-font-size: 13px; -fx-text-fill: #718096;");
    
        // Shared styling for labels and fields
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-font-size: 13px;";
        String fieldStyle = "-fx-background-color: #f7fafc; -fx-border-color: #edf2f7; -fx-border-radius: 4; -fx-padding: 8;";
    
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle(labelStyle);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a unique username");
        usernameField.setStyle(fieldStyle);
    
        Label passwordLabel = new Label("Security Password");
        passwordLabel.setStyle(labelStyle);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Minimum 6 characters");
        passwordField.setStyle(fieldStyle);
    
        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle(labelStyle);
        TextField emailField = new TextField();
        emailField.setPromptText("example@hospital.com");
        emailField.setStyle(fieldStyle);
    
        Label phoneLabel = new Label("Phone Number");
        phoneLabel.setStyle(labelStyle);
        TextField phoneField = new TextField();
        phoneField.setPromptText("(+60)123456789");
        phoneField.setStyle(fieldStyle);
    
        // Clinical Green for "Create" actions to differentiate from Login blue
        Button registerButton = new Button("COMPLETE REGISTRATION");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle("-fx-background-color: #38a169; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 4; -fx-cursor: hand;");
    
        // Back Button (Ghost style)
        Button backButton = new Button("Return to Login");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-font-size: 12px; -fx-underline: true; -fx-cursor: hand;");
    
        // Logic remains identical to your original code
        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
    
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please fill all required fields");
                return;
            }
    
            if (password.length() < 6) {
                showAlert(Alert.AlertType.WARNING, "Password must be at least 6 characters long");
                return;
            }
    
            UserDAO userDAO = new UserDAO();
            if (userDAO.usernameExists(username)) {
                showAlert(Alert.AlertType.ERROR, "Username already exists.");
                return;
            }
    
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Registration");
            confirmAlert.setHeaderText("Create Patient Account");
            confirmAlert.setContentText("Verify details before proceeding?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        User user = new User();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setEmail(email);
                        user.setPhone(phone);
                        user.setRole(User.Role.PATIENT);
                        user.setStatus(User.Status.ACTIVE);
    
                        int userId = userDAO.createUser(user);
                        if (userId != -1) {
                            PatientInfoView patientInfoView = new PatientInfoView(userId);
                            view.getScene().setRoot(patientInfoView.getView());
                        }
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
                    }
                }
            });
        });
    
        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView();
            view.getScene().setRoot(loginView.getView());
        });
    
        // Building the UI
        VBox titleBox = new VBox(2, titleLabel, subHeader);
        titleBox.setAlignment(Pos.CENTER);
    
        registerForm.getChildren().addAll(
            titleBox, new Separator(),
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            emailLabel, emailField,
            phoneLabel, phoneField,
            new Label(""), // Spacer
            registerButton, backButton
        );
    
        view.getChildren().add(registerForm);
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

