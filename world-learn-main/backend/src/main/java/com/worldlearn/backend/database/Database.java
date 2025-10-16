package com.worldlearn.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.worldlearn.backend.config.DatabaseConfig;

public class Database {
    private static final String URL = DatabaseConfig.DB_URL;
    private static final String USER = DatabaseConfig.DB_USER;
    private static final String PASSWORD = DatabaseConfig.DB_PASSWORD;

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to test database connection
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}