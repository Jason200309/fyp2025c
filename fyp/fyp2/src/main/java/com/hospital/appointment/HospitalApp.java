package com.hospital.appointment;

import com.hospital.appointment.database.DatabaseManager;
import com.hospital.appointment.views.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HospitalApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize database
        DatabaseManager.getInstance().initializeDatabase();
        
        // Show login screen
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getView(), 800, 600);
        
        primaryStage.setTitle("Hospital Appointment System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    @Override
    public void stop() {
        DatabaseManager.getInstance().closeConnection();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

