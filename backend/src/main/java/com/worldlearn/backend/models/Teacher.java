package com.worldlearn.backend.models;

public class Teacher extends User {

    public Teacher(String email, String password) {
        super(email, password);
        setRole("teacher");
    }

    public Teacher(String email, String password, String first, String last, String role) {
        super(email, password, first, last, role);
        this.setRole("teacher");
    }

    public Teacher() {
        super();
    }

    @Override
    public String getUserRole(){
        return "teacher";
    }
}
