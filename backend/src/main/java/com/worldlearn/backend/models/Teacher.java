package com.worldlearn.backend.models;

/**
 * Teacher model
 */
public class Teacher extends User {

    /**
     * Constructs a Teacher with email and password
     * @param email
     * @param password
     */
    public Teacher(String email, String password) {
        super(email, password);
        setRole("teacher");
    }

    /**
     * Constructs a Teacher with full details
     * @param email
     * @param password
     * @param first
     * @param last
     * @param role
     */
    public Teacher(String email, String password, String first, String last, String role) {
        super(email, password, first, last, role);
        this.setRole("teacher");
    }

    /**
     * No-arg constructor
     */
    public Teacher() {
        super();
    }

    /**
     * Gets user role
     * @return role
     */
    @Override
    public String getUserRole(){
        return "teacher";
    }
}
