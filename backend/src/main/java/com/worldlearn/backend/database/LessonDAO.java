package com.worldlearn.backend.database;

import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;

import java.sql.*;
import java.util.*;

public class LessonDAO {
    private final Database database;

    public LessonDAO(Database database) { this.database = database; }

    public Lesson createLesson(Lesson lesson) throws SQLException {
        String sql = """
    INSERT INTO lessons (lesson_name, visibility)
    VALUES (?, ?::visibility_type)
""";
        System.out.println("DAO:"+lesson.getLessonName());
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, lesson.getLessonName());
                stmt.setString(2, lesson.getVisibility().getDbValue());

                int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lesson.setLessonId(generatedKeys.getInt(1));
                        return lesson;
                    }
                }
            }
        }
        return null;
    }

    public List<Lesson> getAllLessons() throws SQLException {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT lesson_name, visibility FROM lessons";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Lesson l = new Lesson(
                        rs.getInt("lesson_id"),
                        rs.getString("lesson_name"),
                        Question.Visibility.fromDbValue(rs.getString("visibility"))
                );
                lessons.add(l);
            }
        }
        return lessons;
    }

    public Lesson getLessonById(int id) throws SQLException {
        String sql = "SELECT lesson_id, lesson_name, visibility FROM lessons WHERE lesson_id = ?";

        try (Connection conn = database.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return new Lesson(
                            rs.getInt("lesson_id"),
                            rs.getString("lesson_name"),
                            Question.Visibility.fromDbValue(rs.getString("visibility"))
                    );

                }
            }

        }
        return null;
    }

    public void saveTeacherToLesson(int lessonId, int teacherId){
        String sql = "INSERT INTO teacher_lesson (teacher_role, lesson_id, user_id) VALUES (?::teacher_role_type, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "creator");
            stmt.setInt(2, lessonId);
            stmt.setInt(3, teacherId);
            System.out.println("Saving teacherId=" + teacherId + " lessonId=" + lessonId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into teacher_lesson", e);
        }
    }

    public void saveQuizToLesson(int lessonId, int quizId){
        String sql = "INSERT INTO lesson_quiz (lesson_id, quiz_id) VALUES (?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lessonId);
            stmt.setInt(2, quizId);
            stmt.executeUpdate();
            System.out.println("saving quiz to lesson");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into lesson_quiz", e);
        }
    }

    public List<Quiz> getLessonQuizzes(int id) throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = """
        SELECT q.quiz_id, q.quiz_name, q.visibility FROM quizzes q
        INNER JOIN lesson_quiz lq ON q.quiz_id = lq.quiz_id
        WHERE lq.lesson_id = ?
    """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Quiz q = new Quiz(
                            rs.getInt("quiz_id"),
                            rs.getString("quiz_name"),
                            Question.Visibility.fromDbValue(rs.getString("visibility"))
                    );
                    quizzes.add(q);
                }
            }
        }

        return quizzes;
    }

    public List<Lesson> getAllTeacherLessons(int teacherId) throws SQLException {
        List<Lesson> lessons = new ArrayList<>();

        String sql = """
        SELECT l.lesson_id, l.lesson_name, l.visibility
        FROM Lessons l
        INNER JOIN teacher_lesson tl ON l.lesson_id = tl.lesson_id
        WHERE tl.user_id = ?
    """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);

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
                    System.out.println("Loaded lesson: " + l.getLessonName());
                    lessons.add(l);
                }
            }
        }

        return lessons;
    }

    public List<Lesson> getPublicLessons() throws SQLException {
        List<Lesson> lessons = new ArrayList<>();

        String sql = """
        SELECT * FROM Lessons WHERE visibility = 'public';
    """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
                    System.out.println("Loaded lesson: " + l.getLessonName());
                    lessons.add(l);
                }
            }
        }

        return lessons;
    }

    // Update lesson basic info
    public void updateLesson(Lesson lesson) throws SQLException {
        String sql = "UPDATE lessons SET lesson_name = ?, visibility = ?::visibility_type WHERE lesson_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lesson.getLessonName());
            stmt.setString(2, lesson.getVisibility().toString().toLowerCase());
            stmt.setInt(3, lesson.getLessonId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Lesson not found with ID: " + lesson.getLessonId());
            }
        }
    }

    // Verify teacher owns the lesson
    public boolean verifyLessonOwnership(int lessonId, int teacherId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM teacher_lesson WHERE lesson_id = ? AND user_id = ? AND teacher_role = 'creator'";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lessonId);
            stmt.setInt(2, teacherId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    // Get current quiz IDs for a lesson
    public Set<Integer> getQuizIdsForLesson(int lessonId) throws SQLException {
        Set<Integer> quizIds = new HashSet<>();
        String sql = "SELECT quiz_id FROM lesson_quiz WHERE lesson_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lessonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                quizIds.add(rs.getInt("quiz_id"));
            }
        }

        return quizIds;
    }

    // Remove a quiz from a lesson
    public void removeQuizFromLesson(int lessonId, int quizId) throws SQLException {
        String sql = "DELETE FROM lesson_quiz WHERE lesson_id = ? AND quiz_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lessonId);
            stmt.setInt(2, quizId);
            stmt.executeUpdate();
        }
    }

}