package com.hospital.appointment.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * PneumoniaDetectionAPI - JavaFX Integration with FastAPI AI Service
 * 
 * This class handles communication with the FastAPI pneumonia detection service.
 * It sends X-ray images via multipart/form-data POST requests and parses JSON responses.
 * 
 * API Endpoint: http://127.0.0.1:8000/predict
 * Response Format: {"prediction": "NORMAL" | "PNEUMONIA", "confidence": 0.0 - 1.0}
 * 
 * @author Hospital Appointment System
 * @version 1.0
 */
public class PneumoniaDetectionAPI {
    // FastAPI service endpoint configuration
    private static final String API_BASE_URL = "http://127.0.0.1:8000";
    private static final String PREDICT_ENDPOINT = API_BASE_URL + "/predict";
    
    // HTTP client with timeout configuration for reliable network communication
    private final OkHttpClient client;
    private final Gson gson;

    /**
     * Constructor initializes HTTP client and JSON parser.
     * Timeouts are set to handle slow network connections and large image files.
     */
    public PneumoniaDetectionAPI() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // Connection establishment timeout
            .readTimeout(60, TimeUnit.SECONDS)     // Response reading timeout
            .writeTimeout(60, TimeUnit.SECONDS)    // Request writing timeout
            .build();
        this.gson = new Gson();
    }

    /**
     * Analyzes an X-ray image by sending it to the FastAPI AI service.
     * 
     * Implementation Flow:
     * 1. Create multipart/form-data request body with image file
     * 2. Send HTTP POST request to AI API endpoint
     * 3. Parse JSON response containing prediction and confidence
     * 4. Convert confidence from 0.0-1.0 range to percentage (0-100)
     * 5. Return structured result object
     * 
     * @param imageFile The X-ray image file to analyze (JPG, PNG, DICOM)
     * @return PneumoniaResult containing diagnosis and confidence score
     * @throws IOException if network error, API unavailable (503), or invalid response
     */
    public PneumoniaResult analyzeXray(File imageFile) throws IOException {
        // Step 1: Create multipart/form-data request body
        // The API expects the file under the "file" form field name
        RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", imageFile.getName(),
                RequestBody.create(imageFile, MediaType.parse("image/*")))
            .build();

        // Step 2: Build HTTP POST request
        Request request = new Request.Builder()
            .url(PREDICT_ENDPOINT)
            .post(requestBody)
            .build();

        // Step 3: Execute request and handle response
        try (Response response = client.newCall(request).execute()) {
            // Handle HTTP error status codes
            if (!response.isSuccessful()) {
                int statusCode = response.code();
                String errorMessage = "API request failed with status code: " + statusCode;
                
                // Specific handling for service unavailable
                if (statusCode == 503) {
                    errorMessage = "AI service is temporarily unavailable. Please try again later.";
                } else if (statusCode == 400) {
                    errorMessage = "Invalid image file format. Please ensure the file is a valid X-ray image.";
                } else if (statusCode == 500) {
                    errorMessage = "AI service encountered an internal error. Please contact support.";
                }
                
                throw new IOException(errorMessage);
            }

            // Step 4: Parse JSON response
            String responseBody = response.body().string();
            
            // Debug: Log response for troubleshooting
            System.out.println("API Response Status: " + response.code());
            System.out.println("API Response Body: " + responseBody);
            
            // Check if response body is empty
            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new IOException("API returned empty response. Status code: " + response.code());
            }
            
            // Parse JSON with error handling
            JsonObject jsonResponse;
            try {
                jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            } catch (Exception e) {
                throw new IOException("Failed to parse JSON response. Response body: " + responseBody + 
                    "\nError: " + e.getMessage());
            }
            
            // Validate response structure - check for both possible field names
            boolean hasPrediction = jsonResponse.has("prediction");
            boolean hasDiagnosis = jsonResponse.has("diagnosis");
            boolean hasConfidence = jsonResponse.has("confidence");
            
            if ((!hasPrediction && !hasDiagnosis) || !hasConfidence) {
                String availableFields = jsonResponse.keySet().toString();
                throw new IOException("Invalid API response format.\n" +
                    "Expected 'prediction' (or 'diagnosis') and 'confidence' fields.\n" +
                    "Received fields: " + availableFields + "\n" +
                    "Response body: " + responseBody);
            }

            // Step 5: Extract and process response data
            PneumoniaResult result = new PneumoniaResult();
            
            // Get prediction: "NORMAL" or "PNEUMONIA" (support both "prediction" and "diagnosis" fields)
            String prediction;
            if (jsonResponse.has("prediction")) {
                prediction = jsonResponse.get("prediction").getAsString();
            } else if (jsonResponse.has("diagnosis")) {
                prediction = jsonResponse.get("diagnosis").getAsString();
            } else {
                throw new IOException("Response missing 'prediction' or 'diagnosis' field. Response: " + responseBody);
            }
            result.setDiagnosis(prediction);
            
            // Get confidence (0.0 - 1.0) and convert to percentage (0 - 100)
            double confidenceRaw;
            try {
                confidenceRaw = jsonResponse.get("confidence").getAsDouble();
            } catch (Exception e) {
                throw new IOException("Failed to parse confidence value. Response: " + responseBody + 
                    "\nError: " + e.getMessage());
            }
            
            // Convert to percentage if needed (if value is <= 1.0, assume it's 0.0-1.0 range)
            double confidencePercentage;
            if (confidenceRaw <= 1.0) {
                confidencePercentage = confidenceRaw * 100.0;
            } else {
                // Already in percentage format
                confidencePercentage = confidenceRaw;
            }
            
            result.setConfidence(confidencePercentage);
            result.setRawScore(confidenceRaw <= 1.0 ? confidenceRaw : confidenceRaw / 100.0); // Store original 0.0-1.0 value
            
            // Store full response for database persistence
            result.setFullResponse(responseBody);
            
            // Determine confidence level for UI display
            String confidenceLevel = determineConfidenceLevel(confidencePercentage);
            result.setConfidenceLevel(confidenceLevel);
            
            // Generate recommendation based on diagnosis
            String recommendation = generateRecommendation(prediction, confidencePercentage);
            result.setRecommendation(recommendation);

            return result;
        } catch (IOException e) {
            // Re-throw with context for better error messages
            throw new IOException("Failed to communicate with AI service: " + e.getMessage(), e);
        }
    }

    /**
     * Determines confidence level category based on percentage.
     * 
     * @param confidencePercentage Confidence value (0-100)
     * @return "High", "Moderate", or "Low"
     */
    private String determineConfidenceLevel(double confidencePercentage) {
        if (confidencePercentage >= 80) {
            return "High";
        } else if (confidencePercentage >= 60) {
            return "Moderate";
        } else {
            return "Low";
        }
    }

    /**
     * Generates medical recommendation based on diagnosis and confidence.
     * 
     * @param prediction "NORMAL" or "PNEUMONIA"
     * @param confidencePercentage Confidence value (0-100)
     * @return Human-readable recommendation text
     */
    private String generateRecommendation(String prediction, double confidencePercentage) {
        if ("PNEUMONIA".equals(prediction)) {
            if (confidencePercentage >= 80) {
                return "Strong indication of pneumonia. Recommend immediate medical attention.";
            } else if (confidencePercentage >= 60) {
                return "Moderate indication of pneumonia. Medical review recommended.";
            } else {
                return "Possible pneumonia detected. Further examination advised.";
            }
        } else { // NORMAL
            if (confidencePercentage >= 80) {
                return "No signs of pneumonia detected. Chest X-ray appears normal.";
            } else if (confidencePercentage >= 60) {
                return "Likely normal chest X-ray. Routine follow-up if symptoms persist.";
            } else {
                return "Unclear result. Manual review by radiologist recommended.";
            }
        }
    }

    /**
     * PneumoniaResult - Data transfer object for AI analysis results.
     * 
     * This class encapsulates the response from the FastAPI service,
     * providing structured access to diagnosis and confidence metrics.
     */
    public static class PneumoniaResult {
        private String diagnosis;          // "NORMAL" or "PNEUMONIA"
        private double confidence;        // Percentage (0-100)
        private String confidenceLevel;    // "High", "Moderate", or "Low"
        private String recommendation;     // Medical recommendation text
        private double rawScore;          // Original confidence (0.0-1.0)
        private String fullResponse;       // Complete JSON response for database storage

        // Getters and Setters
        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        /**
         * @return Confidence as percentage (0-100)
         */
        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public String getConfidenceLevel() {
            return confidenceLevel;
        }

        public void setConfidenceLevel(String confidenceLevel) {
            this.confidenceLevel = confidenceLevel;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }

        /**
         * @return Raw confidence score from API (0.0-1.0)
         */
        public double getRawScore() {
            return rawScore;
        }

        public void setRawScore(double rawScore) {
            this.rawScore = rawScore;
        }

        /**
         * @return Complete JSON response string for database persistence
         */
        public String getFullResponse() {
            return fullResponse;
        }

        public void setFullResponse(String fullResponse) {
            this.fullResponse = fullResponse;
        }
    }
}


