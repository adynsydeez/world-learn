package com.worldlearn.backend.database;

import com.worldlearn.backend.dto.AnswerResponse;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Question.QuestionType;
import com.worldlearn.backend.models.Question.Visibility;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Question data access object
 */
public class QuestionDAO {
    private final Database database;

    /**
     * Constructs QuestionDAO
     * @param database
     */
    public QuestionDAO(Database database) {
        this.database = database;
    }

    /**
     * Creates a question
     * @param question
     * @return created question or null
     * @throws SQLException
     */
    public Question createQuestion(Question question) throws SQLException {
        String sql = """
    INSERT INTO questions (question_name, answer, options, prompt, type, points_worth, visibility)
    VALUES (?, ?, ?, ?, ?::question_type, ?, ?::visibility_type)
""";
        System.out.println("DAO:"+question.getAnswer());
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, question.getQuestionName());
            stmt.setString(2, question.getAnswer());
            Array optionsArray = conn.createArrayOf("text", question.getOptions());
            stmt.setArray(3, optionsArray);
            stmt.setString(4, question.getPrompt());
            stmt.setString(5, question.getType().getDbValue());
            stmt.setInt(6, question.getPointsWorth());
            stmt.setString(7, question.getVisibility().getDbValue());

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

    /**
     * Gets all questions
     * @return list of questions
     * @throws SQLException
     */
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

    /**
     * Gets all questions created by a teacher
     * @param userId
     * @return list of questions
     * @throws SQLException
     */
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
                    Array optionsArray = rs.getArray("options");
                    String[] options = optionsArray != null ? (String[]) optionsArray.getArray() : new String[0];

                    String typeStr = rs.getString("type");
                    QuestionType type = null;
                    if (typeStr != null) {
                        type = QuestionType.fromDbValue(typeStr);
                    }

                    String answer = rs.getString("answer") != null ? rs.getString("answer") : "null";

                    String visibilityStr = rs.getString("visibility");
                    Visibility visibility = null;
                    if (visibilityStr != null) {
                        visibility = Visibility.fromDbValue(visibilityStr);
                    }

                    Question q = new Question(
                            rs.getInt("question_id"),
                            rs.getString("question_name"),
                            answer,
                            options,
                            rs.getString("prompt"),
                            type,
                            rs.getInt("points_worth"),
                            visibility
                    );

                    System.out.println("Loaded question: " + q.getQuestionId() + ", prompt=" + q.getPrompt());

                    questions.add(q);
                }
            }
        }

        return questions;
    }

    /**
     * Gets all public questions
     * @return list of questions
     * @throws SQLException
     */
    public List<Question> getPublicQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT question_id, question_name, answer, options, prompt, type, points_worth, visibility FROM questions WHERE visibility = 'public'";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Array optionsArray = rs.getArray("options");
                String[] options = optionsArray != null ? (String[]) optionsArray.getArray() : new String[0];

                String answer = rs.getString("answer") != null ? rs.getString("answer") : "null";

                Question q = new Question(
                        rs.getInt("question_id"),
                        rs.getString("question_name"),
                        answer,
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

    /**
     * Gets a question by id
     * @param id
     * @return optional question
     * @throws SQLException
     */
    public Optional<Question> getQuestionByID(int id) throws SQLException {
        String sql = """
        SELECT question_id, question_name, answer, options, prompt, type, points_worth, visibility
        FROM questions WHERE question_id = ?
    """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

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

    /**
     * Updates a question
     * @param question
     * @return updated question or null
     * @throws SQLException
     */
    public Question updateQuestion(Question question) throws SQLException {
        String sql = """
            UPDATE questions
            SET question_name = ?, 
                answer = ?, 
                options = ?, 
                prompt = ?,
                type = ?::question_type, 
                points_worth = ?, 
                visibility = ?::visibility_type
            WHERE question_id = ?;
        """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, question.getQuestionName());
            stmt.setString(2, question.getAnswer());
            Array optionsArray = conn.createArrayOf("text", question.getOptions());
            stmt.setArray(3, optionsArray);
            stmt.setString(4, question.getPrompt());
            stmt.setString(5, question.getType().toString());
            stmt.setInt(6, question.getPointsWorth());
            stmt.setString(7, question.getVisibility().toString().toLowerCase());
            stmt.setInt(8, question.getQuestionId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                question.setQuestionId(question.getQuestionId());
                return question;
            }

            return null;
        }
    }

    /**
     * Saves teacher-to-question mapping
     * @param questionId
     * @param teacherId
     */
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

    /**
     * Deletes a question
     * @param id
     * @return true if deleted
     * @throws SQLException
     */
    public boolean deleteQuestion(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE question_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Submits a student's answer
     * @param questionId
     * @param userId
     * @param givenAnswer
     * @return answer response
     * @throws SQLException
     */
    public AnswerResponse submitAnswer(int questionId, int userId, String givenAnswer) throws SQLException {
        String getQuestionSql = "SELECT answer, points_worth FROM questions WHERE question_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement getStmt = conn.prepareStatement(getQuestionSql)) {

            getStmt.setInt(1, questionId);

            try (ResultSet rs = getStmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Question not found with id: " + questionId);
                }

                String correctAnswer = rs.getString("answer");
                int pointsWorth = rs.getInt("points_worth");

                boolean isCorrect = correctAnswer.trim().equalsIgnoreCase(givenAnswer.trim());
                int pointsEarned = isCorrect ? pointsWorth : 0;

                String insertSql = """
                INSERT INTO student_answer (question_id, user_id, given_answer, points_earned, answered_at)
                VALUES (?, ?, ?, ?, NOW())
                RETURNING answered_at
            """;

                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, questionId);
                    insertStmt.setInt(2, userId);
                    insertStmt.setString(3, givenAnswer);
                    insertStmt.setInt(4, pointsEarned);

                    try (ResultSet insertRs = insertStmt.executeQuery()) {
                        if (insertRs.next()) {
                            Timestamp timestamp = insertRs.getTimestamp("answered_at");
                            LocalDateTime answeredAt = timestamp.toLocalDateTime();

                            System.out.println("Answer submitted: userId=" + userId +
                                    ", questionId=" + questionId +
                                    ", correct=" + isCorrect +
                                    ", points=" + pointsEarned);

                            return new AnswerResponse(
                                    questionId,
                                    userId,
                                    givenAnswer,
                                    pointsEarned,
                                    answeredAt.toString(),
                                    isCorrect
                            );
                        }
                    }
                }
            }
        }

        throw new SQLException("Failed to submit answer");
    }

    /**
     * Gets a student's submitted answer
     * @param questionId
     * @param userId
     * @return optional answer response
     * @throws SQLException
     */
    public Optional<AnswerResponse> getStudentAnswer(int questionId, int userId) throws SQLException {
        String sql = """
        SELECT sa.question_id, sa.user_id, sa.given_answer, sa.points_earned, sa.answered_at,
               q.answer as correct_answer
        FROM student_answer sa
        JOIN questions q ON sa.question_id = q.question_id
        WHERE sa.question_id = ? AND sa.user_id = ?
    """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String givenAnswer = rs.getString("given_answer");
                    String correctAnswer = rs.getString("correct_answer");
                    boolean isCorrect = givenAnswer.trim().equalsIgnoreCase(correctAnswer.trim());

                    Timestamp timestamp = rs.getTimestamp("answered_at");
                    String answeredAt = timestamp.toLocalDateTime().toString();

                    AnswerResponse response = new AnswerResponse(
                            rs.getInt("question_id"),
                            rs.getInt("user_id"),
                            givenAnswer,
                            rs.getInt("points_earned"),
                            answeredAt,
                            isCorrect
                    );

                    return Optional.of(response);
                }
            }
        }
        return Optional.empty();
    }
}
