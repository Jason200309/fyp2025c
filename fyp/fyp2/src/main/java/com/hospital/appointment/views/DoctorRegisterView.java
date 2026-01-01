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

public class DoctorRegisterView {
    private VBox view;
    private User adminUser;

    public DoctorRegisterView() {
        this(null);
    }

    public DoctorRegisterView(User adminUser) {
        this.adminUser = adminUser;
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        // Consistent Clinical Navy Background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // Registration Card Container
        VBox registerForm = new VBox(15);
        registerForm.setAlignment(Pos.CENTER_LEFT); // Left-aligned labels for better scanning
        registerForm.setMaxWidth(450);
        registerForm.setPadding(new Insets(40));
        // Professional white card with crisp borders
        registerForm.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #cbd5e0; -fx-border-width: 1;");
    
        // Header Section
        VBox headerText = new VBox(5);
        headerText.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Add New Medical Staff");
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px; -fx-text-fill: #2d3748;");
        
        Label subTitle = new Label("Registering a new Doctor account");
        subTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #718096;");
        headerText.getChildren().addAll(titleLabel, subTitle);
    
        // Shared styling strings
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-font-size: 13px;";
        String fieldStyle = "-fx-background-color: #f7fafc; -fx-border-color: #edf2f7; -fx-border-radius: 4; -fx-padding: 10;";
    
        Label usernameLabel = new Label("Account Username");
        usernameLabel.setStyle(labelStyle);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter unique username");
        usernameField.setStyle(fieldStyle);
    
        Label passwordLabel = new Label("Security Password");
        passwordLabel.setStyle(labelStyle);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Minimum 6 characters");
        passwordField.setStyle(fieldStyle);
    
        Label emailLabel = new Label("Professional Email");
        emailLabel.setStyle(labelStyle);
        TextField emailField = new TextField();
        emailField.setPromptText("doctor@hospital.com");
        emailField.setStyle(fieldStyle);
    
        Label phoneLabel = new Label("Contact Number");
        phoneLabel.setStyle(labelStyle);
        TextField phoneField = new TextField();
        phoneField.setPromptText("Primary phone number");
        phoneField.setStyle(fieldStyle);
    
        // Primary Action Button (Medical Blue)
        Button registerButton = new Button("CREATE ACCOUNT");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 4; -fx-cursor: hand;");
    
        // Navigation Button (Secondary Slate style)
        Button backButton = new Button("Return to Admin Dashboard");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-font-size: 12px; -fx-underline: true; -fx-cursor: hand;");
    
        // Logic remains identical
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
            confirmAlert.setHeaderText("Create Doctor Account");
            confirmAlert.setContentText("Assign account to the staff directory?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        User user = new User();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setEmail(email);
                        user.setPhone(phone);
                        user.setRole(User.Role.DOCTOR);
                        user.setStatus(User.Status.ACTIVE);
    
                        int userId = userDAO.createUser(user);
                        if (userId != -1) {
                            DoctorInfoView doctorInfoView = new DoctorInfoView(userId, adminUser);
                            view.getScene().setRoot(doctorInfoView.getView());
                        }
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Failed: " + ex.getMessage());
                    }
                }
            });
        });
    
        backButton.setOnAction(e -> {
            if (adminUser != null) {
                AdminView adminView = new AdminView(adminUser);
                view.getScene().setRoot(adminView.getView());
            } else {
                LoginView loginView = new LoginView();
                view.getScene().setRoot(loginView.getView());
            }
        });
    
        registerForm.getChildren().addAll(
            headerText, new Separator(),
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

