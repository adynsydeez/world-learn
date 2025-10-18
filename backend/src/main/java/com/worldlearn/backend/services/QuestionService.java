package com.worldlearn.backend.services;

import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.dto.AnswerRequest;
import com.worldlearn.backend.dto.AnswerResponse;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.database.QuestionDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Question service
 */
public class QuestionService {
    private final QuestionDAO questionDAO;

    /**
     * Constructs QuestionService
     * @param questionDAO
     */
    public QuestionService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    /**
     * Creates question and links teacher
     * @param question
     * @param teacherId
     * @return Question
     * @throws SQLException
     */
    public Question createQuestion(Question question, int teacherId) throws SQLException {
        Question saved = questionDAO.createQuestion(question);
        System.out.println("Linking teacherId=" + teacherId + " to questionId=" + saved.getQuestionId());
        questionDAO.saveTeacherToQuestion(saved.getQuestionId(), teacherId);
        System.out.println("service:" + question.getAnswer());

        return saved;
    }

    /**
     * Gets question by id
     * @param id
     * @return Optional<Question>
     * @throws SQLException
     */
    public Optional<Question> getQuestionById(int id) throws SQLException {
        return questionDAO.getQuestionByID(id);
    }

    /**
     * Deletes question by id
     * @param id
     * @return true if deleted
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public boolean deleteQuestion(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return questionDAO.deleteQuestion(id);
    }

    /**
     * Gets all questions
     * @return List<Question>
     * @throws SQLException
     */
    public List<Question> getAllQuestions() throws SQLException {
        return questionDAO.getAllQuestions();
    }

    /**
     * Gets all questions for teacher
     * @param userId
     * @return List<Question>
     * @throws SQLException
     */
    public List<Question> getAllTeacherQuestions(int userId) throws SQLException {
        return questionDAO.getAllTeacherQuestions(userId);
    }

    /**
     * Gets all public questions
     * @return List<Question>
     * @throws SQLException
     */
    public List<Question> getPublicQuestions() throws SQLException {
        return questionDAO.getPublicQuestions();
    }

    /**
     * Updates question
     * @param question
     * @return Question
     * @throws SQLException
     */
    public Question updateQuestion(Question question) throws SQLException {
        return questionDAO.updateQuestion(question);
    }

    /**
     * Submits answer
     * @param questionId
     * @param userId
     * @param givenAnswer
     * @return AnswerResponse
     * @throws SQLException
     */
    public AnswerResponse submitAnswer(int questionId, int userId, String givenAnswer) throws SQLException {
        return questionDAO.submitAnswer(questionId, userId, givenAnswer);
    }

    /**
     * Gets student's answer
     * @param questionId
     * @param userId
     * @return Optional<AnswerResponse>
     * @throws SQLException
     */
    public Optional<AnswerResponse> getStudentAnswer(int questionId, int userId) throws SQLException {
        return questionDAO.getStudentAnswer(questionId, userId);
    }
}
