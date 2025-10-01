package com.worldlearn.backend.database;

import com.worldlearn.backend.dto.CreateClassRequest;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.models.WlClass;
import com.worldlearn.backend.services.ClassService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {
    private final Database database;

    public ClassDAO(Database database) {
        this.database = database;
    }

    // Simple class creation - just inserts into Classes table for testing
    public WlClass createClass(WlClass wlClass) throws SQLException {
        String insertSql = "INSERT INTO Classes (class_name, join_code) VALUES (?, ?) RETURNING class_id;";
        String updateSql = "UPDATE Classes SET join_code = ? WHERE class_id = ?;";

        try (Connection conn = database.getConnection()) {
            conn.setAutoCommit(false);

            int classId;

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, wlClass.getClassName());
                insertStmt.setInt(2, 0); // temporary value

                try (ResultSet rs = insertStmt.executeQuery()) {
                    if (rs.next()) {
                        classId = rs.getInt("class_id");
                    } else {
                        conn.rollback();
                        throw new SQLException("Creating class failed, no ID obtained.");
                    }
                }
            }

            wlClass.setId(classId);
            int joinCode = ClassService.generateJoinCode(wlClass);
            wlClass.setJoinCode(joinCode);

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, joinCode);
                updateStmt.setInt(2, classId);
                updateStmt.executeUpdate();
            }

            conn.commit();
            return wlClass;

        } catch (SQLException e) {
            throw new SQLException("Error creating class: " + e.getMessage(), e);
        }
    }


    public List<WlClass> getAllClassesForUser(User user) throws SQLException {
        List<WlClass> classes = new ArrayList<>();
        String sql;

        if ("student".equalsIgnoreCase(user.getRole())) {
            sql = "SELECT c.class_id, c.class_name, c.join_code " +
                    "FROM classes c " +
                    "JOIN student_class sc ON c.class_id = sc.class_id " +
                    "WHERE sc.user_id = ?";
        } else if ("teacher".equalsIgnoreCase(user.getRole())) {
            sql = "SELECT c.class_id, c.class_name, c.join_code " +
                    "FROM classes c " +
                    "JOIN teacher_class tc ON c.class_id = tc.class_id " +
                    "WHERE tc.user_id = ?";
        } else {
            throw new IllegalArgumentException("Unknown role: " + user.getRole());
        }

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int class_id = rs.getInt("class_id");
                    String class_name = rs.getString("class_name");
                    int join_code = rs.getInt("join_code");

                    WlClass wlClass = new WlClass(class_id, class_name, join_code);
                    wlClass.setId(class_id);
                    classes.add(wlClass);
                }
            }
        }

        return classes;
    }

    public void assignStudentToClass(int classId, int userId) throws SQLException {
        String sql = "INSERT INTO student_class (class_id, user_id) VALUES (?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            stmt.setInt(2, userId);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Adding user to class failed, no rows affected.");
            }
        }
    }

    public int getClassIdByJoinCode(int joinCode) throws SQLException {
        String sql = "SELECT class_id FROM Classes WHERE join_code = ?";
        int classId = 0;
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, joinCode);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    classId = rs.getInt("class_id");
                }
            }
        }

        return classId;
    }

    public void saveTeacherToClass(int classId, int teacherId) throws SQLException {
        String sql = "INSERT INTO teacher_class(teacher_role, class_id, user_id) VALUES (?::teacher_role_type, ?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "creator");
            stmt.setInt(2, classId);
            stmt.setInt(3, teacherId);
            System.out.println("Saving teacherId=" + teacherId + " classId=" + classId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into teacher_class", e);
        }
    }

    public void saveLessonToClass(int classId, int lessonId) throws SQLException {
        String sql = "INSERT INTO class_lesson(class_id, lesson_id) VALUES (?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.setInt(2, lessonId);
            System.out.println("Saving lessonId=" + lessonId + " classId=" + classId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into class_lesson", e);
        }
    }

    public void saveStudentToClass(int classId, int studentId) throws SQLException {
        String sql = "INSERT INTO student_class(class_id, user_id) VALUES (?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.setInt(2, studentId);
            System.out.println("Saving studentId=" + studentId + " classId=" + classId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into student_class", e);
        }
    }


    // Future method: Remove user from class
    public void removeTeacherFromClass(int classId, int userId) throws SQLException {
         String sql = "DELETE FROM teacher_class WHERE class_id = ? AND user_id = ?";

         try (Connection conn = database.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setInt(1, classId);
             stmt.setInt(2, userId);

             if (stmt.executeUpdate() == 0) {
                 throw new SQLException("Removing user from class failed, no rows affected.");
             }
         }
    }

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