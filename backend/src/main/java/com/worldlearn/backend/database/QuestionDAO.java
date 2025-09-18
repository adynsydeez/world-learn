package com.worldlearn.backend.database;

import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Question.QuestionType;
import com.worldlearn.backend.models.Question.Visibility;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestionDAO {
    private final Database database;

    public QuestionDAO(Database database) {
        this.database = database;
    }

    public Question createQuestion(Question question, int teacherId) throws SQLException {
        String sql = """
    INSERT INTO questions (question_name, answer, options, prompt, type, points_worth, visibility)
    VALUES (?, ?, ?, ?, ?::question_type, ?, ?::visibility_type)
""";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, question.getQuestionName());     // question_name
            stmt.setString(2, question.getAnswer());            // answer
            Array optionsArray = conn.createArrayOf("text", question.getOptions());
            stmt.setArray(3, optionsArray);                     // options
            stmt.setString(4, question.getPrompt());            // prompt
            stmt.setString(5, question.getType().getDbValue()); // type
            stmt.setInt(6, question.getPointsWorth());          // points_worth
            stmt.setString(7, question.getVisibility().getDbValue()); // visibility

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        question.setQuestionId(generatedKeys.getInt(1));
                        return question;
                    }
                }
            }
        }
        return null;
    }

    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT question_id, question_name, answer, options, prompt, type, points_worth, visibility FROM questions";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] options = (String[]) rs.getArray("options").getArray();
                Question q = new Question(
                        rs.getInt("question_id"),
                        rs.getString("question_name"),
                        rs.getString("answer"),
                        options,
                        rs.getString("prompt"),
                        QuestionType.fromDbValue(rs.getString("type")),
                        rs.getInt("points_worth"),
                        Visibility.fromDbValue(rs.getString("visibility"))
                );
                questions.add(q);
            }
        }
        return questions;
    }

    public List<Question> getAllTeacherQuestions(int userId) throws SQLException {
        List<Question> questions = new ArrayList<>();

        String sql = """
        SELECT q.question_id, q.question_name, q.answer, q.options, q.prompt,
               q.type, q.points_worth, q.visibility
        FROM questions q
        INNER JOIN teacher_question tq ON q.question_id = tq.question_id
        WHERE tq.user_id = ?
    """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Fetch options array safely
                    String[] options = null;
                    try {
                        Array optionsArray = rs.getArray("options");
                        if (optionsArray != null) {
                            options = (String[]) optionsArray.getArray();
                        }
                    } catch (SQLException e) {
                        System.err.println("Failed to fetch options array for question_id="
                                + rs.getInt("question_id") + ": " + e.getMessage());
                        options = null;
                    }

                    // Fetch other fields safely
                    String typeStr = rs.getString("type");
                    QuestionType type = null;
                    if (typeStr != null) {
                        type = QuestionType.fromDbValue(typeStr);
                    }

                    String visibilityStr = rs.getString("visibility");
                    Visibility visibility = null;
                    if (visibilityStr != null) {
                        visibility = Visibility.fromDbValue(visibilityStr);
                    }

                    Question q = new Question(
                            rs.getInt("question_id"),
                            rs.getString("question_name"),
                            rs.getString("answer"),
                            options,
                            rs.getString("prompt"),
                            type,
                            rs.getInt("points_worth"),
                            visibility
                    );

                    // Debug print for each row
                    System.out.println("Loaded question: " + q.getQuestionId() + ", prompt=" + q.getPrompt());

                    questions.add(q);
                }
            }
        }

        return questions;
    }

    public List<Question> getPublicQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT question_id, question_name, answer, options, prompt, type, points_worth, visibility FROM questions WHERE visibility = 'public'";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Array optionsArray = rs.getArray("options");
                String[] options = optionsArray != null ? (String[]) optionsArray.getArray() : new String[0];

                Question q = new Question(
                        rs.getInt("question_id"),
                        rs.getString("question_name"),
                        rs.getString("answer"),
                        options,
                        rs.getString("prompt"),
                        QuestionType.fromDbValue(rs.getString("type")),
                        rs.getInt("points_worth"),
                        Visibility.fromDbValue(rs.getString("visibility"))
                );
                questions.add(q);
            }
        }
        return questions;
    }



    public Optional<Question> getQuestionByID(int id) throws SQLException {
        String sql = """
        SELECT question_id, question_name, answer, options, prompt, type, points_worth, visibility
        FROM questions WHERE question_id = ?
    """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] options = (String[]) rs.getArray("options").getArray();
                    Question q = new Question(
                            rs.getInt("question_id"),
                            rs.getString("question_name"),
                            rs.getString("answer"),
                            options,
                            rs.getString("prompt"),
                            QuestionType.fromDbValue(rs.getString("type")),
                            rs.getInt("points_worth"),
                            Visibility.fromDbValue(rs.getString("visibility"))
                    );
                    return Optional.of(q);
                }
            }
        }
        return Optional.empty();
    }


    public Question updateQuestion(Question question) throws SQLException {
        String sql = """
            UPDATE questions
            SET question_name = ?, answer = ?, options = ?, prompt = ?,
                type = ?::question_type, points_worth = ?, visibility = ?::visibility_type
            WHERE question_id = ?
        """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, question.getQuestionName());
            stmt.setString(2, question.getAnswer());
            Array optionsArray = conn.createArrayOf("text", question.getOptions());
            stmt.setArray(3, optionsArray);
            stmt.setString(4, question.getPrompt());
            stmt.setString(5, question.getType().getDbValue());
            stmt.setInt(6, question.getPointsWorth());
            stmt.setString(7, question.getVisibility().getDbValue());
            stmt.setInt(8, question.getQuestionId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                question.setQuestionId(question.getQuestionId());
                return question;
            }

            return null;
        }
    }

    public void saveTeacherToQuestion(int questionId, int teacherId){
        String sql = "INSERT INTO teacher_question (teacher_role, question_id, user_id) VALUES (?::teacher_role_type, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "creator");
            stmt.setInt(2, questionId);
            stmt.setInt(3, teacherId);
            System.out.println("Saving teacherId=" + teacherId + " questionId=" + questionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into teacher_question", e);
        }
    }

    public boolean deleteQuestion(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE question_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
