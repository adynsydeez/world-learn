package com.worldlearn.backend.database;

import com.worldlearn.backend.models.User;
import com.worldlearn.backend.models.WlClass;

import java.sql.*;

public class ClassDAO {
    private final Database database;

    public ClassDAO(Database database) {
        this.database = database;
    }

    // Simple class creation - just inserts into Classes table for testing
    public WlClass createClass(WlClass wlClass) throws SQLException {
        String sql = "INSERT INTO Classes (class_name, join_code) VALUES (?, ?) RETURNING class_id;";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, wlClass.getClassName());
            stmt.setInt(2, wlClass.getJoinCode());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int classId = rs.getInt("class_id");

                    // Create a new WlClass object with the generated ID
                    WlClass createdClass = new WlClass();
                    createdClass.setId(classId);
                    createdClass.setClassName(wlClass.getClassName());
                    createdClass.setJoinCode(wlClass.getJoinCode());

                    return createdClass;
                } else {
                    throw new SQLException("Creating class failed, no ID obtained.");
                }
            }
        }
    }

    // Future method: Create class and assign owner in a transaction
    // public WlClass createClassWithOwner(WlClass wlClass, User creator) throws SQLException {
    //     String sql1 = "INSERT INTO Classes (class_name, join_code) VALUES (?, ?) RETURNING class_id;";
    //     String sql2 = "INSERT INTO Teacher_Class (teacher_role, class_id, user_id) VALUES (?::teacher_role_type, ?, ?)";

    //     Connection conn = null;
    //     try {
    //         conn = database.getConnection();
    //         conn.setAutoCommit(false);

    //         int classId;

    //         // 1. Insert into Classes
    //         try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
    //             stmt1.setString(1, wlClass.getClassName());
    //             stmt1.setInt(2, wlClass.getJoinCode());

    //             try (ResultSet rs = stmt1.executeQuery()) {
    //                 if (rs.next()) {
    //                     classId = rs.getInt("class_id");
    //                 } else {
    //                     throw new SQLException("Creating class failed, no ID obtained.");
    //                 }
    //             }
    //         }

    //         // 2. Insert creator as owner
    //         try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
    //             stmt2.setString(1, "owner");
    //             stmt2.setInt(2, classId);
    //             stmt2.setInt(3, creator.getId());

    //             if (stmt2.executeUpdate() == 0) {
    //                 throw new SQLException("Creating Teacher_Class failed, no rows affected.");
    //             }
    //         }

    //         conn.commit();

    //         WlClass createdClass = new WlClass();
    //         createdClass.setId(classId);
    //         createdClass.setClassName(wlClass.getClassName());
    //         createdClass.setJoinCode(wlClass.getJoinCode());

    //         return createdClass;

    //     } catch (SQLException e) {
    //         if (conn != null) {
    //             try {
    //                 conn.rollback();
    //             } catch (SQLException rollbackEx) {
    //                 e.addSuppressed(rollbackEx);
    //             }
    //         }
    //         throw e;
    //     } finally {
    //         if (conn != null) {
    //             try {
    //                 conn.setAutoCommit(true);
    //                 conn.close();
    //             } catch (SQLException e) {
    //                 System.err.println("Error restoring auto-commit or closing connection: " + e.getMessage());
    //             }
    //         }
    //     }
    // }

    // Future method: Add user to existing class
    // public void addUserToClass(int classId, int userId, String role) throws SQLException {
    //     String sql = "INSERT INTO Teacher_Class (teacher_role, class_id, user_id) VALUES (?::teacher_role_type, ?, ?)";

    //     try (Connection conn = database.getConnection();
    //          PreparedStatement stmt = conn.prepareStatement(sql)) {

    //         stmt.setString(1, role);
    //         stmt.setInt(2, classId);
    //         stmt.setInt(3, userId);

    //         if (stmt.executeUpdate() == 0) {
    //             throw new SQLException("Adding user to class failed, no rows affected.");
    //         }
    //     }
    // }

    // Future method: Remove user from class
    // public void removeUserFromClass(int classId, int userId) throws SQLException {
    //     String sql = "DELETE FROM Teacher_Class WHERE class_id = ? AND user_id = ?";

    //     try (Connection conn = database.getConnection();
    //          PreparedStatement stmt = conn.prepareStatement(sql)) {

    //         stmt.setInt(1, classId);
    //         stmt.setInt(2, userId);

    //         if (stmt.executeUpdate() == 0) {
    //             throw new SQLException("Removing user from class failed, no rows affected.");
    //         }
    //     }
    // }

    // Future method: Update user role in class
    // public void updateUserRole(int classId, int userId, String newRole) throws SQLException {
    //     String sql = "UPDATE Teacher_Class SET teacher_role = ?::teacher_role_type WHERE class_id = ? AND user_id = ?";

    //     try (Connection conn = database.getConnection();
    //          PreparedStatement stmt = conn.prepareStatement(sql)) {

    //         stmt.setString(1, newRole);
    //         stmt.setInt(2, classId);
    //         stmt.setInt(3, userId);

    //         if (stmt.executeUpdate() == 0) {
    //             throw new SQLException("Updating user role failed, no rows affected.");
    //         }
    //     }
    // }
}