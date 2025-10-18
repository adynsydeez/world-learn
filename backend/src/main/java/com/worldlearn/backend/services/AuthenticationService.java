package com.worldlearn.backend.services;

import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.models.Student;
import com.worldlearn.backend.models.Teacher;
import com.worldlearn.backend.models.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Authentication service
 */
public class AuthenticationService {
    /**
     * In-memory users list
     */
    private final List<User> users = new CopyOnWriteArrayList<>();

    /**
     * User data access object
     */
    private final UserDAO userDAO;

    /**
     * Constructs AuthenticationService
     * @param userDAO
     */
    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;

        // Load existing users from DB
        try {
            users.addAll(userDAO.getAllUsers());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Signs up a user
     * @param email
     * @param password
     * @param role
     * @param firstName
     * @param lastName
     * @return created user
     * @throws IllegalArgumentException
     */
    public User signUp(String email, String password, String role, String firstName, String lastName) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password required");
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name required");

        // Check if email exists in memory or DB
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        User newUser;
        switch (role.toLowerCase()) {
            case "student" -> newUser = new Student(email, password, firstName, lastName, role);
            case "teacher" -> newUser = new Teacher(email, password, firstName, lastName, role);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        }

        try {
            // Insert into DB and get generated ID
            int generatedId = userDAO.createUser(newUser).getId();
            newUser.setId(generatedId);

            // Add to in-memory list
            users.add(newUser);
            return newUser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign up user: " + e.getMessage(), e);
        }
    }

    /**
     * Logs in a user
     * @param email
     * @param password
     * @return matching user
     * @throws IllegalArgumentException
     */
    public User logIn(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) &&
                    user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new IllegalArgumentException("Incorrect email or password");
    }
}
