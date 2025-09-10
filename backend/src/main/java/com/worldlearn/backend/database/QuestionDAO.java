package com.worldlearn.backend.database;

import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Question.QuestionType;
import com.worldlearn.backend.models.Question.Visibility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestionDAO {
    private final Connection conn;

    public QuestionDAO(Connection conn) {
        this.conn = conn;
    }

    // INSERT
    public void insert(Question question) throws SQLException {
        String sql = """
            INSERT INTO questions (answer, options, prompt, type, points_worth, visibility)
            VALUES (?, ?, ?, ?::question_type, ?, ?::visibility_type)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, question.getAnswer());
            Array optionsArray = conn.createArrayOf("text", question.getOptions());
            stmt.setArray(2, optionsArray);
            stmt.setString(3, question.getPrompt());
            stmt.setString(4, question.getType().getDbValue());
            stmt.setInt(5, question.getPointsWorth());
            stmt.setString(6, question.getVisibility().getDbValue());

            stmt.executeUpdate();
        }
    }

    // SELECT all
    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT question_id, answer, options, prompt, type, points_worth, visibility FROM questions";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
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


    // SELECT by ID
    public Optional<Question> findById(int id) throws SQLException {
        String sql = """
            SELECT question_id, answer, options, prompt, type, points_worth, visibility
            FROM questions WHERE question_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

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

    // UPDATE
    public void update(Question question) throws SQLException {
        String sql = """
            UPDATE questions
            SET answer = ?, options = ?, prompt = ?, 
                type = ?::question_type, points_worth = ?, visibility = ?::visibility_type
            WHERE question_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, question.getAnswer());
            Array optionsArray = conn.createArrayOf("text", question.getOptions());
            stmt.setArray(2, optionsArray);
            stmt.setString(3, question.getPrompt());
            stmt.setString(4, question.getType().getDbValue());
            stmt.setInt(5, question.getPointsWorth());
            stmt.setString(6, question.getVisibility().getDbValue());
            stmt.setInt(7, question.getQuestionId());

            stmt.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE question_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
