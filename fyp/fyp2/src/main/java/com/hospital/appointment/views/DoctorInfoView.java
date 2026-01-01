package com.hospital.appointment.views;

import com.hospital.appointment.dao.DoctorDAO;
import com.hospital.appointment.models.Doctor;
import com.hospital.appointment.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DoctorInfoView {
    private VBox view;
    private int userId;
    private User adminUser;

    public DoctorInfoView(int userId) {
        this(userId, null);
    }

    public DoctorInfoView(int userId, User adminUser) {
        this.userId = userId;
        this.adminUser = adminUser;
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        // Clinical Navy Background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // Profile Form Card
        VBox infoForm = new VBox(20);
        infoForm.setAlignment(Pos.CENTER);
        infoForm.setMaxWidth(550);
        infoForm.setPadding(new Insets(40));
        // Clean White Card
        infoForm.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; -fx-border-color: #cbd5e0; -fx-border-width: 1;");
    
        // Title Section
        Label titleLabel = new Label("Staff Credentialing");
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px; -fx-text-fill: #2d3748;");
        
        Label subTitle = new Label("Please complete the medical practitioner's professional profile");
        subTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #718096;");
    
        // --- Form Layout (GridPane) ---
        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setPadding(new Insets(10, 0, 10, 0));
    
        // Shared Styles
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-font-size: 13px;";
        String fieldStyle = "-fx-background-color: #f7fafc; -fx-border-color: #edf2f7; -fx-border-radius: 4; -fx-padding: 8;";
    
        Label specializationLabel = new Label("Clinical Specialization:");
        specializationLabel.setStyle(labelStyle);
        TextField specializationField = new TextField();
        specializationField.setPromptText("e.g., Radiology / Pulmonology");
        specializationField.setStyle(fieldStyle);
    
        Label licenseNumberLabel = new Label("Medical License No:");
        licenseNumberLabel.setStyle(labelStyle);
        TextField licenseNumberField = new TextField();
        licenseNumberField.setPromptText("Enter official registration ID");
        licenseNumberField.setStyle(fieldStyle);
    
        Label departmentLabel = new Label("Assigned Department:");
        departmentLabel.setStyle(labelStyle);
        TextField departmentField = new TextField();
        departmentField.setPromptText("e.g., Diagnostic Imaging");
        departmentField.setStyle(fieldStyle);
    
        formGrid.add(specializationLabel, 0, 0);
        formGrid.add(specializationField, 1, 0);
        formGrid.add(licenseNumberLabel, 0, 1);
        formGrid.add(licenseNumberField, 1, 1);
        formGrid.add(departmentLabel, 0, 2);
        formGrid.add(departmentField, 1, 2);
    
        // Column Constraints for perfect alignment
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(160);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(300);
        formGrid.getColumnConstraints().addAll(col1, col2);
    
        // Action Button (Medical Blue)
        Button submitButton = new Button("FINALIZE REGISTRATION");
        submitButton.setMaxWidth(Double.MAX_VALUE); // Make button wide
        submitButton.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 4; -fx-cursor: hand;");
    
        // Wiring up your original logic
        submitButton.setOnAction(e -> {
            String specialization = specializationField.getText().trim();
            String licenseNumber = licenseNumberField.getText().trim();
            String department = departmentField.getText().trim();
    
            if (specialization.isEmpty() || licenseNumber.isEmpty() || department.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please fill all required fields.");
                return;
            }
    
            try {
                Doctor doctor = new Doctor();
                doctor.setUserId(userId);
                doctor.setSpecialization(specialization);
                doctor.setLicenseNumber(licenseNumber);
                doctor.setDepartment(department);
    
                DoctorDAO doctorDAO = new DoctorDAO();
                int doctorId = doctorDAO.createDoctor(doctor);
                
                if (doctorId != -1) {
                    showAlert(Alert.AlertType.INFORMATION, "Registration Successful!");
                    if (adminUser != null) {
                        AdminView adminView = new AdminView(adminUser);
                        view.getScene().setRoot(adminView.getView());
                    } else {
                        LoginView loginView = new LoginView();
                        view.getScene().setRoot(loginView.getView());
                    }
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
            }
        });
    
        // Assembly
        VBox titleGroup = new VBox(5, titleLabel, subTitle);
        titleGroup.setAlignment(Pos.CENTER);
    
        infoForm.getChildren().addAll(
            titleGroup, new Separator(),
            formGrid,
            new Label(""), // Spacer
            submitButton
        );
    
        view.getChildren().add(infoForm);
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

