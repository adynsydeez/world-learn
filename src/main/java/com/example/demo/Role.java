package com.example.demo;

public enum Role {
    STUDENT,
    TEACHER;

    @Override
    public String toString() {
        // Pretty-print for UI (instead of "STUDENT")
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}

