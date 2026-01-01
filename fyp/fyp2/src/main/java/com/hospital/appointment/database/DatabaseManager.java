package com.hospital.appointment.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DatabaseManager - Manages MySQL database connections
 * 
 * Connects to MySQL database at jdbc:mysql://localhost:3306/hms_fyp
 * Assumes database and tables are already created.
 */
public class DatabaseManager {
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;
    private Connection connection;
    
    // MySQL database connection URL
    // Add connection parameters to prevent premature closing and timeout issues
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hms_fyp?useSSL=false&serverTimezone=UTC&autoReconnect=true&failOverReadOnly=false&maxReconnects=10&initialTimeout=1";
    private static final String DB_USER = "root";  // Update if your MySQL user is different
    private static final String DB_PASSWORD = "";  // Update with your MySQL password

    private DatabaseManager() {
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initializes database connection to MySQL.
     * Assumes database and tables are already created.
     */
    public void initializeDatabase() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to MySQL database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Successfully connected to MySQL database: " + DB_URL);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to MySQL database", e);
            logger.log(Level.SEVERE, "Please ensure MySQL is running and database 'hms_fyp' exists", e);
        }
    }

    /**
     * Gets a valid database connection, reconnecting if necessary.
     * @return Connection object
     * @throws SQLException if connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        // Check if connection is null, closed, or invalid
        try {
            boolean needsReconnect = false;
            if (connection == null) {
                needsReconnect = true;
            } else {
                try {
                    if (connection.isClosed() || !connection.isValid(1)) {
                        needsReconnect = true;
                    }
                } catch (SQLException e) {
                    // Connection is invalid, need to reconnect
                    needsReconnect = true;
                }
            }
            
            if (needsReconnect) {
                // Reconnect if connection is not valid
                logger.info("Connection invalid or closed, reconnecting...");
                try {
                    // Close old connection if it exists
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            // Ignore errors when closing old connection
                        }
                    }
                } catch (Exception e) {
                    // Ignore any errors when closing old connection
                }
                
                // Create new connection
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                logger.info("Successfully reconnected to MySQL database: " + DB_URL);
            }
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get database connection", e);
            throw e;
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error closing database connection", e);
        }
    }
}


