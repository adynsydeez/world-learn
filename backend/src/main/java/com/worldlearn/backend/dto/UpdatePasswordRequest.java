package com.worldlearn.backend.dto;

public class UpdatePasswordRequest {
    private String password;

    public UpdatePasswordRequest() {}

    public UpdatePasswordRequest(String password) {
        this.password = password;
    }

    // Getters and setters
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}