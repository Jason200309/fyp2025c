# Hospital Appointment System

A JavaFX-based hospital appointment management system with AI-powered X-ray analysis integration.

## Features

### Patient Portal
- Book appointments with date, time, and notes
- View personal appointment history
- Track appointment status

### Nurse Portal
- View and manage all appointments
- Confirm or complete appointments
- Upload X-ray images for appointments
- Automatic AI analysis of uploaded X-ray images

### Doctor Portal
- View appointments with X-ray images
- View AI analysis results including:
  - Diagnosis (PNEUMONIA or NORMAL)
  - Confidence level and percentage
  - AI recommendations
  - Visual display of X-ray images

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Python 3.8+ (for running the AI API)
- FastAPI server running (from chest-xray-pneumonia-detection-ai/api)

## Setup Instructions

### 1. Start the AI API Server

First, start the FastAPI server for pneumonia detection:

```bash
cd chest-xray-pneumonia-detection-ai/api
pip install -r requirements.txt
python main.py
```

The API should be running on `http://localhost:7860`

### 2. Build and Run the JavaFX Application

**Important**: Always use the Maven JavaFX plugin to run the application. Do not run the main class directly from the IDE without proper JavaFX module configuration.

#### Using Maven (Recommended):

```bash
cd fyp
mvn clean compile
mvn javafx:run
```

Or use the provided scripts:
- Windows: `run.bat`
- Linux/Mac: `chmod +x run.sh && ./run.sh`

#### If running from IDE:

If you need to run from your IDE (IntelliJ IDEA, Eclipse, etc.), you must configure the run configuration to include JavaFX modules:

**For IntelliJ IDEA:**
1. Go to Run → Edit Configurations
2. Add VM options: `--module-path %USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\21\javafx-controls-21.jar --add-modules javafx.controls,javafx.fxml`
3. Or better: Use "Run with Maven" and select `javafx:run` goal

**For Eclipse:**
1. Right-click project → Run As → Maven Build
2. Goals: `javafx:run`

**Alternative - Use Maven Exec Plugin:**
```bash
mvn exec:java -Dexec.mainClass="com.hospital.appointment.HospitalApp"
```

## Default Login Credentials

- **Doctor**: username: `doctor1`, password: `password`
- **Nurse**: username: `nurse1`, password: `password`
- **Patient**: username: `patient1`, password: `password`

## Project Structure

```
fyp/
├── src/main/java/com/hospital/appointment/
│   ├── HospitalApp.java              # Main application entry point
│   ├── models/                        # Data models
│   │   ├── User.java
│   │   ├── Appointment.java
│   │   └── XrayImage.java
│   ├── dao/                           # Data Access Objects
│   │   ├── UserDAO.java
│   │   ├── AppointmentDAO.java
│   │   └── XrayImageDAO.java
│   ├── database/                      # Database management
│   │   └── DatabaseManager.java
│   ├── api/                           # API integration
│   │   └── PneumoniaDetectionAPI.java
│   └── views/                         # JavaFX views
│       ├── LoginView.java
│       ├── PatientView.java
│       ├── NurseView.java
│       └── DoctorView.java
├── pom.xml                            # Maven configuration
└── README.md
```

## Database

The application uses SQLite database (`hospital_appointments.db`) which is automatically created on first run. The database includes:

- **users**: User accounts (patients, nurses, doctors)
- **appointments**: Appointment records
- **xray_images**: X-ray image metadata and AI analysis results

## API Integration

The application integrates with the FastAPI pneumonia detection service:
- Endpoint: `http://localhost:7860/predict`
- Accepts: Image files (JPG, PNG, DICOM)
- Returns: JSON with diagnosis, confidence, and recommendations

## Workflow

1. **Patient** books an appointment
2. **Nurse** confirms the appointment
3. **Nurse** uploads X-ray image (automatically analyzed by AI)
4. **Doctor** views the appointment and AI analysis results

## Notes

- X-ray images are stored in the `uploads/` directory
- The AI API must be running for X-ray analysis to work
- All passwords are stored in plain text (for demo purposes only - use proper hashing in production)

## Troubleshooting

### API Connection Issues
- Ensure the FastAPI server is running on port 7860
- Check firewall settings
- Verify the API URL in `PneumoniaDetectionAPI.java`

### Database Issues
- Delete `hospital_appointments.db` to reset the database
- Check file permissions in the project directory

### Image Upload Issues
- Ensure the `uploads/` directory is writable
- Check file size limits
- Verify image file format is supported

## License

This project is for educational purposes.


