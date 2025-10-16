package com.worldlearn.backend.database;

import com.worldlearn.backend.dto.CreateClassRequest;
import com.worldlearn.backend.models.*;
import com.worldlearn.backend.services.ClassService;

import java.sql.*;
import java.util.*;

public class ClassDAO {
    private final Database database;

    public ClassDAO(Database database) {
        this.database = database;
    }

    public WlClass getClassById(int id) throws SQLException {
        String sql = "SELECT class_id, class_name, join_code FROM classes WHERE class_id = ?";

        try (Connection conn = database.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return new WlClass(
                            rs.getInt("class_id"),
                            rs.getString("class_name"),
                            rs.getInt("join_code")
                    );
                }
            }
        }
        return null;
    }

    // Simple class creation - just inserts into Classes table for testing
    public WlClass createClass(WlClass wlClass) throws SQLException {
        String sql = """
    INSERT INTO classes (class_name, join_code)
    VALUES (?, ?)
""";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, wlClass.getClassName());     // question_name
            stmt.setInt(2, wlClass.getJoinCode());            // answer

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        wlClass.setId(generatedKeys.getInt(1));
                        return wlClass;
                    }
                }
            }
        }
        return null;
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

    public List<Lesson> getClassLessons(int classId) throws SQLException {
        List<Lesson> lessons = new ArrayList<>();
        String sql = """
        SELECT l.lesson_id, l.lesson_name, l.visibility
        FROM lessons l INNER JOIN class_lesson cl
        ON l.lesson_id = cl.lesson_id
        WHERE cl.class_id = ?
        """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    String visibilityStr = rs.getString("visibility");
                    Question.Visibility visibility = null;
                    if (visibilityStr != null) {
                        visibility = Question.Visibility.fromDbValue(visibilityStr);
                    }

                    Lesson l = new Lesson(
                            rs.getInt("lesson_id"),
                            rs.getString("lesson_name"),
                            visibility
                    );

                    // Debug print for each row
                    System.out.println("Loaded question: " + l.getLessonId() + ", name=" + l.getLessonName());

                    lessons.add(l);
                }
            }
        }

        return lessons;
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

    public boolean joinCodeExists(int joinCode) {
        String sql = "SELECT COUNT(*) FROM classes WHERE join_code = ?";
        try (Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, joinCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update Class basic info
    public void updateClass(WlClass wlClass) throws SQLException {
        String sql = "UPDATE classes SET class_name = ? WHERE class_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, wlClass.getClassName());
            stmt.setInt(2, wlClass.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Class not found with ID: " + wlClass.getId());
            }
        }
    }

    // Get current lesson IDs for a cass
    public Set<Integer> getLessonIdsForClass(int wLClassId) throws SQLException {
        Set<Integer> lessonIds = new HashSet<>();
        String sql = "SELECT lesson_id FROM class_lesson WHERE class_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wLClassId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lessonIds.add(rs.getInt("lesson_id"));
            }
        }

        return lessonIds;
    }

    // Remove a lesson from a class
    public void removeLessonFromClass(int wLClassId, int lessonId) throws SQLException {
        String sql = "DELETE FROM class_lesson WHERE class_id = ? AND lesson_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, wLClassId);
            stmt.setInt(2, lessonId);
            stmt.executeUpdate();
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