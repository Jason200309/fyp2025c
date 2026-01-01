package com.hospital.appointment.views;

import com.hospital.appointment.dao.PatientDAO;
import com.hospital.appointment.models.Patient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class PatientInfoView {
    private VBox view;
    private int userId;

    public PatientInfoView(int userId) {
        this.userId = userId;
        createView();
    }

    private void createView() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        // Consistent Clinical Navy Background
        view.setStyle("-fx-background-color: #1a202c;");
    
        // Profile Form Card
        VBox infoForm = new VBox(20);
        infoForm.setAlignment(Pos.CENTER);
        infoForm.setMaxWidth(600);
        infoForm.setPadding(new Insets(40));
        // Clean White Card with subtle shadow
        infoForm.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0);"
        );
    
        // Title Section
        VBox headerText = new VBox(5);
        headerText.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Personal Medical Record");
        titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px; -fx-text-fill: #2d3748;");
        
        Label subTitle = new Label("Please complete your profile to enable AI diagnostic services");
        subTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #718096;");
        headerText.getChildren().addAll(titleLabel, subTitle);
    
        // --- Form Layout (GridPane) ---
        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
    
        // Shared styling strings
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-font-size: 13px;";
        String fieldStyle = "-fx-background-color: #f7fafc; -fx-border-color: #edf2f7; -fx-border-radius: 4; -fx-padding: 8;";
    
        Label fullNameLabel = new Label("Full Name *");
        fullNameLabel.setStyle(labelStyle);
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("As per Identification Card");
        fullNameField.setStyle(fieldStyle);
    
        Label icNumberLabel = new Label("IC / Passport No.");
        icNumberLabel.setStyle(labelStyle);
        TextField icNumberField = new TextField();
        icNumberField.setPromptText("Enter ID Number");
        icNumberField.setStyle(fieldStyle);
    
        Label dobLabel = new Label("Date of Birth");
        dobLabel.setStyle(labelStyle);
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Select Date");
        dobPicker.setPrefWidth(Double.MAX_VALUE);
        dobPicker.setStyle("-fx-background-color: #f7fafc;");
        
        // Applying your existing DOB restriction logic
        dobPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #fed7d7;");
                }
            }
        });
    
        Label genderLabel = new Label("Gender");
        genderLabel.setStyle(labelStyle);
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("MALE", "FEMALE", "OTHER");
        genderComboBox.setPromptText("Select Gender");
        genderComboBox.setPrefWidth(Double.MAX_VALUE);
        genderComboBox.setStyle("-fx-background-color: #f7fafc;");
    
        Label addressLabel = new Label("Residential Address");
        addressLabel.setStyle(labelStyle);
        TextArea addressField = new TextArea();
        addressField.setPromptText("Enter full home address");
        addressField.setPrefRowCount(3);
        addressField.setWrapText(true);
        addressField.setStyle("-fx-control-inner-background: #f7fafc; -fx-background-color: #f7fafc; -fx-border-color: #edf2f7;");
    
        formGrid.add(fullNameLabel, 0, 0);
        formGrid.add(fullNameField, 1, 0);
        formGrid.add(icNumberLabel, 0, 1);
        formGrid.add(icNumberField, 1, 1);
        formGrid.add(dobLabel, 0, 2);
        formGrid.add(dobPicker, 1, 2);
        formGrid.add(genderLabel, 0, 3);
        formGrid.add(genderComboBox, 1, 3);
        formGrid.add(addressLabel, 0, 4);
        formGrid.add(addressField, 1, 4);
    
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPrefWidth(140);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPrefWidth(320);
        formGrid.getColumnConstraints().addAll(col1, col2);
    
        // Submit Button (Medical Blue)
        Button submitButton = new Button("FINALIZE PROFILE");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 4; -fx-cursor: hand;");
    
        // Logic remains identical
        submitButton.setOnAction(e -> {
            String fullName = fullNameField.getText().trim();
            if (fullName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Full Name is required.");
                return;
            }
    
            try {
                Patient patient = new Patient();
                patient.setUserId(userId);
                patient.setFullName(fullName);
                patient.setIcNumber(icNumberField.getText().trim().isEmpty() ? null : icNumberField.getText().trim());
                patient.setDateOfBirth(dobPicker.getValue());
                if (genderComboBox.getValue() != null) {
                    patient.setGender(Patient.Gender.valueOf(genderComboBox.getValue()));
                }
                patient.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
    
                PatientDAO patientDAO = new PatientDAO();
                if (patientDAO.createPatient(patient) != -1) {
                    showAlert(Alert.AlertType.INFORMATION, "Profile saved. You may now login.");
                    LoginView loginView = new LoginView();
                    view.getScene().setRoot(loginView.getView());
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
            }
        });
    
        infoForm.getChildren().addAll(headerText, new Separator(), formGrid, new Label(""), submitButton);
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




