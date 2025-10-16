package com.worldlearn.backend.config;

public class ApiConfig {
    private static final String API_BASE_URL = "http://localhost:7000/api";

    public static String getApiBaseUrl() {
        return API_BASE_URL;
    }
}