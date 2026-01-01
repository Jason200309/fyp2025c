package com.hospital.appointment.views;

import com.hospital.appointment.dao.NurseDAO;
import com.hospital.appointment.models.Nurse;
import com.hospital.appointment.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class NurseInfoView {
    private VBox view;
    private int userId;
    private User adminUser;

    public NurseInfoView(int userId) {
        this(userId, null);
    }

    public NurseInfoView(int userId, User adminUser) {
        this.userId = userId;
        this.adminUser = adminUser;
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        // Consistent Deep Navy background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // Nurse Information Form Card
        VBox infoForm = new VBox(25);
        infoForm.setAlignment(Pos.CENTER);
        infoForm.setMaxWidth(550);
        infoForm.setPadding(new Insets(40));
        // White card with subtle border
        infoForm.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; -fx-border-color: #cbd5e0; -fx-border-width: 1;");
    
        // Title Section
        VBox headerText = new VBox(5);
        headerText.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Staff Placement");
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px; -fx-text-fill: #2d3748;");
        
        Label subTitle = new Label("Assign the registered nurse to a hospital department");
        subTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #718096;");
        headerText.getChildren().addAll(titleLabel, subTitle);
    
        // Form Layout
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setPadding(new Insets(10, 0, 10, 0));
    
        Label departmentLabel = new Label("Assigned Department:");
        departmentLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-font-size: 13px;");
        
        TextField departmentField = new TextField();
        departmentField.setPromptText("e.g., Radiology / Outpatient");
        departmentField.setStyle("-fx-background-color: #f7fafc; -fx-border-color: #edf2f7; -fx-border-radius: 4; -fx-padding: 10;");
    
        formGrid.add(departmentLabel, 0, 0);
        formGrid.add(departmentField, 1, 0);
    
        // Column constraints for clean alignment
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(160);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(300);
        formGrid.getColumnConstraints().addAll(col1, col2);
    
        // Submit Button (Clinical Green to match Nurse theme)
        Button submitButton = new Button("COMPLETE ASSIGNMENT");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setStyle("-fx-background-color: #38a169; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 4; -fx-cursor: hand;");
        
        submitButton.setOnAction(e -> {
            String department = departmentField.getText().trim();
    
            if (department.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter the department.");
                return;
            }
    
            try {
                Nurse nurse = new Nurse();
                nurse.setUserId(userId);
                nurse.setDepartment(department);
    
                NurseDAO nurseDAO = new NurseDAO();
                int nurseId = nurseDAO.createNurse(nurse);
                
                if (nurseId != -1) {
                    showAlert(Alert.AlertType.INFORMATION, "Nurse registration completed successfully!");
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
    
        infoForm.getChildren().addAll(
            headerText, new Separator(),
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

