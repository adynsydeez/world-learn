package com.worldlearn.backend.models;

public class Teacher extends User {

    public Teacher(String email, String password, String first, String last, String role) {
        super(email, password, first, last, role);
        this.setRole("teacher");
    }
}
