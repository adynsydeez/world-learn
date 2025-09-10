package com.worldlearn.backend.database;

import com.worldlearn.backend.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Database database;

    public UserDAO(Database database) {
        this.database = database;
    }

    public User getUserById(String id) throws SQLException {
        String sql = "SELECT user_id, email, password, first_name, last_name, user_role FROM users WHERE user_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(id));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("user_id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("user_role")
                    );
                }
            }
        }
        return null;
    }

    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password, first_name, last_name, user_role) VALUES (?, ?, ?, ?, ?::user_role_type)";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getRole());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return user;
                    }
                }
            }

            throw new SQLException("Failed to create user, no ID generated");
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, email, password, first_name, last_name, user_role FROM users";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("user_id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("user_role")
                );
                users.add(user);
            }
        }

        return users;
    }

    public User updateUser(int id, User user) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, password = ?, user_role = ?::user_role_type WHERE user_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRole());
            stmt.setInt(6, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                user.setId(id);
                return user;
            }

            return null;
        }
    }

    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}