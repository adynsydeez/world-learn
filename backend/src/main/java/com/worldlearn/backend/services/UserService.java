package com.worldlearn.backend.services;

import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.dto.UpdatePasswordRequest;
import com.worldlearn.backend.dto.UpdateUserProfileRequest;
import com.worldlearn.backend.models.User;
import java.sql.SQLException;
import java.util.List;

/**
 * User service
 */
public class UserService {
    private final UserDAO userDAO;

    /**
     * Constructs service
     * @param userDAO
     */
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Logs in user
     * @param email
     * @param password
     * @return user
     * @throws SQLException
     */
    public User logIn(String email, String password) throws SQLException {
        return userDAO.getUserByEmailAndPassword(email, password);
    }

    /**
     * Gets user by id
     * @param id
     * @return user
     * @throws SQLException
     * @throws IllegalArgumentException
     */
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

    /**
     * Creates user
     * @param user
     * @return created user
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public User createUser(User user) throws SQLException {
        validateUser(user);
        return userDAO.createUser(user);
    }

    /**
     * Gets all users
     * @return users
     * @throws SQLException
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    /**
     * Updates user
     * @param id
     * @param user
     * @return updated user
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public User updateUser(int id, UpdateUserProfileRequest user) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userDAO.updateUser(id, user);
    }

    /**
     * Deletes user
     * @param id
     * @return true if deleted
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public boolean deleteUser(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userDAO.deleteUser(id);
    }

    /**
     * Updates password
     * @param id
     * @param user
     * @return updated user
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public User updatePassword(int id, UpdatePasswordRequest user) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userDAO.updatePassword(id, user);
    }

    /**
     * Validates user
     * @param user
     * @throws IllegalArgumentException
     */
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

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Checks email format
     * @param email
     * @return true if valid
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
