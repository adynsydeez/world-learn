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

    public Question createQuestion(Question question) throws SQLException {
        String sql = """
            INSERT INTO questions (answer, options, prompt, type, points_worth, visibility)
            VALUES (?, ?, ?, ?::question_type, ?, ?::visibility_type)
        """;
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, question.getAnswer());
            Array optionsArray = conn.createArrayOf("text", question.getOptions());
            stmt.setArray(2, optionsArray);
            stmt.setString(3, question.getPrompt());
            stmt.setString(4, question.getType().getDbValue());
            stmt.setInt(5, question.getPointsWorth());
            stmt.setString(6, question.getVisibility().getDbValue());

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
        String sql = "SELECT question_id, answer, options, prompt, type, points_worth, visibility FROM questions";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] options = (String[]) rs.getArray("options").getArray();
                Question q = new Question(
                        rs.getInt("question_id"),
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
            SELECT question_id, answer, options, prompt, type, points_worth, visibility
            FROM questions WHERE question_id = ?
        """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] options = (String[]) rs.getArray("options").getArray();
                    Question q = new Question(
                            rs.getInt("question_id"),
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
            SET answer = ?, options = ?, prompt = ?, 
                type = ?::question_type, points_worth = ?, visibility = ?::visibility_type
            WHERE question_id = ?
        """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, question.getAnswer());
            Array optionsArray = conn.createArrayOf("text", question.getOptions());
            stmt.setArray(2, optionsArray);
            stmt.setString(3, question.getPrompt());
            stmt.setString(4, question.getType().getDbValue());
            stmt.setInt(5, question.getPointsWorth());
            stmt.setString(6, question.getVisibility().getDbValue());
            stmt.setInt(7, question.getQuestionId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                question.setQuestionId(question.getQuestionId());
                return question;
            }

            return null;
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
