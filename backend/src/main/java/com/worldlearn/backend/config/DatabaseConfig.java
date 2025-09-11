package com.worldlearn.backend.config;

public class DatabaseConfig {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/worldlearn";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "worldlearnpw";
    public static final int SERVER_PORT = 7000;

    // You could load these from environment variables or config files
    public static String getDatabaseUrl() {
        return System.getenv("DATABASE_URL") != null
                ? System.getenv("DATABASE_URL")
                : DB_URL;
    }

    public static String getDatabaseUser() {
        return System.getenv("DATABASE_USER") != null
                ? System.getenv("DATABASE_USER")
                : DB_USER;
    }

    public static String getDatabasePassword() {
        return System.getenv("DATABASE_PASSWORD") != null
                ? System.getenv("DATABASE_PASSWORD")
                : DB_PASSWORD;
    }
}