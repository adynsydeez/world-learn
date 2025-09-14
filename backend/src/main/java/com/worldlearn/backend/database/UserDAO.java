package com.worldlearn.backend.database;

import com.worldlearn.backend.models.Student;
import com.worldlearn.backend.models.Teacher;
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
        final String sql = "SELECT user_id, email, password, first_name, last_name, user_role FROM users WHERE user_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(id));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String user_id      = rs.getString("user_id");   // use getInt + String.valueOf if you prefer
                    String email    = rs.getString("email");
                    String password = rs.getString("password");
                    String first    = rs.getString("first_name");
                    String last     = rs.getString("last_name");
                    String roleRaw  = rs.getString("user_role");
                    String role     = roleRaw == null ? "" : roleRaw.trim().toLowerCase();

                    int int_id = Integer.parseInt(user_id);
                    switch (role) {
                        case "student":
                            User student = new Student(email, password, first, last, role);
                            student.setId(int_id);
                            return student;
                        case "teacher":
                            User teacher = new Teacher(email, password, first, last, role);
                            teacher.setId(int_id);
                            return teacher;
                        default:
                            throw new IllegalArgumentException("Unknown user_role: " + roleRaw);
                    }
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
        final String sql = "SELECT * FROM users";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String user_id      = rs.getString("user_id");
                String email    = rs.getString("email");
                String password = rs.getString("password");
                String first    = rs.getString("first_name");
                String last     = rs.getString("last_name");
                String roleRaw  = rs.getString("user_role");
                String role     = roleRaw == null ? "" : roleRaw.trim().toLowerCase();

                int int_id = Integer.parseInt(user_id);
                User u;
                switch (role) {
                    case "student":
                        u = new Student(email, password, first, last, role);
                        u.setId(int_id);
                        users.add(u);
                        break; // Added missing break
                    case "teacher":
                        u = new Teacher(email, password, first, last, role);
                        u.setId(int_id);
                        users.add(u);
                        break; // Added missing break
                    default:
                        throw new IllegalArgumentException("Unknown user_role: " + roleRaw);
                }
            }
        }
        return users;
    }

    public User getUserByEmailAndPassword(String email, String password) throws SQLException {
        String sql = "SELECT user_id, first_name, last_name, email, password, user_role FROM users WHERE email = ? AND password = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String roleRaw  = rs.getString("user_role");
                    String role     = roleRaw == null ? "" : roleRaw.trim().toLowerCase();
                    int id          = rs.getInt("user_id");
                    String first    = rs.getString("first_name");
                    String last     = rs.getString("last_name");
                    String userEmail = rs.getString("email");
                    String userPassword = rs.getString("password");

                    User u;
                    switch (role) {
                        case "student" -> u = new Student(userEmail, userPassword, first, last, role);
                        case "teacher" -> u = new Teacher(userEmail, userPassword, first, last, role);
                        default -> throw new IllegalArgumentException("Unknown user_role: " + roleRaw);
                    }
                    u.setId(id);
                    return u;
                }
            }
        }
        return null; // invalid credentials
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