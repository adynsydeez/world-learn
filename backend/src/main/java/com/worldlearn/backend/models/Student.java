package com.worldlearn.backend.models;

/**
 * Student model
 */
public class Student extends User {

    /**
     * Constructs a Student with email and password
     * @param email
     * @param password
     */
    public Student(String email, String password) {
        super(email, password);
        setRole("student");
    }

    /**
     * Constructs a Student with full details
     * @param email
     * @param password
     * @param first
     * @param last
     * @param student
     */
    public Student(String email, String password, String first, String last, String student) {
        super(email, password, first, last, student);
    }

    /**
     * No-arg constructor
     */
    public Student() {
        super();
    }

    /**
     * Gets user role
     * @return role
     */
    @Override
    public String getUserRole(){
        return "student";
    }
}
