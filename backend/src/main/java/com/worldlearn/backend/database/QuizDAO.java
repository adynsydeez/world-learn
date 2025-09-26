package com.worldlearn.backend.database;

import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.models.Question.QuestionType;
import com.worldlearn.backend.models.Question.Visibility;
import com.worldlearn.backend.models.Teacher;
import org.eclipse.jetty.websocket.api.Session;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizDAO {
    private final Database database;

    public QuizDAO(Database database) {
        this.database = database;
    }

    public Quiz createQuiz(Quiz quiz) throws SQLException {
        String sql = """
    INSERT INTO quizzes (quiz_name, visibility)
    VALUES (?, ?::visibility_type)
""";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, quiz.getQuizName());     // question_name
            stmt.setString(2, quiz.getVisibility().getDbValue());            // answer

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        quiz.setQuizID(generatedKeys.getInt(1));
                        return quiz;
                    }
                }
            }
        }
        return null;
    }

    public List<Quiz> getAllQuizzes() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT quiz_id, quiz_name, visibility FROM quizzes";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String visibilityStr = rs.getString("visibility");
                Visibility visibility = (visibilityStr != null)
                        ? Visibility.fromDbValue(visibilityStr)
                        : null;

                quizzes.add(new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("quiz_name"),
                        visibility
                ));
            }
        }
        return quizzes;
    }
/*
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
*/
    /*public List<Question> getPublicQuestions() throws SQLException {
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

*/

    public Optional<Quiz> getQuizByID(int id) throws SQLException {
        String sql = "SELECT quiz_id, quiz_name, visibility FROM quizzes WHERE quiz_id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String visStr = rs.getString("visibility");
                    Visibility vis = (visStr != null) ? Visibility.fromDbValue(visStr) : null;

                    Quiz q = new Quiz(
                            rs.getInt("quiz_id"),
                            rs.getString("quiz_name"),
                            vis
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

    public void saveTeacherToQuiz(int quizId, int teacherId){
        String sql = "INSERT INTO teacher_quiz (teacher_role, quiz_id, user_id) VALUES (?::teacher_role_type, ?, ?)";
        System.out.println("saveTeachertoQuiz");
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "creator");
            stmt.setInt(2, quizId);
            stmt.setInt(3, teacherId);
            System.out.println("Saving teacherId=" + teacherId + " quizId=" + quizId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into teacher_quiz", e);
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

    public void saveQuestionToQuiz(int quizId, int questionId){
        String sql = "INSERT INTO quiz_question (quiz_id, question_id) VALUES (?, ?)";
        System.out.println("saving question to quiz");
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            stmt.setInt(2, questionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into teacher_quiz", e);
        }
    }

    public List<Question> getQuizQuestions(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = """
        SELECT q.question_id, q.question_name, q.answer, 
        q.options, q.prompt, q.type, q.points_worth, q.visibility
        FROM questions q INNER JOIN quiz_question qq
        ON q.question_id = qq.question_id
        WHERE qq.quiz_id = ?
        """;

        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quizId);

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
}
