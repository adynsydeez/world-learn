package com.worldlearn.backend.services;

import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.models.User;
import java.sql.SQLException;
import java.util.List;


public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User getUserById(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }

        return userDAO.getUserById(id);
    }

    public User createUser(User user) throws SQLException {
        // Validation
        validateUser(user);

        // Business logic (e.g., check for duplicate emails)
        // You could add email uniqueness check here

        return userDAO.createUser(user);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public User updateUser(int id, User user) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        validateUser(user);

        return userDAO.updateUser(id, user);
    }

    public boolean deleteUser(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userDAO.deleteUser(id);
    }

    private void validateUser(User user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("User role is required");
        }

        // Email format validation
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}