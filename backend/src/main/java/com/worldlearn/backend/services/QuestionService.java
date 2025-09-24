package com.worldlearn.backend.services;

import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.database.QuestionDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class QuestionService {
    private final QuestionDAO questionDAO;

    public QuestionService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    public Question createQuestion(Question question, int teacherId) throws SQLException {
        Question saved = questionDAO.createQuestion(question, teacherId);
        System.out.println("Linking teacherId=" + teacherId + " to questionId=" + saved.getQuestionId());
        questionDAO.saveTeacherToQuestion(saved.getQuestionId(), teacherId);
        System.out.println("service:" + question.getAnswer());

        return saved;
    }

    public Optional<Question> getQuestionById(int id) throws SQLException {
        return questionDAO.getQuestionByID(id);
    }

    public boolean deleteQuestion(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return questionDAO.deleteQuestion(id);
    }

    public List<Question> getAllQuestions() throws SQLException {
        return questionDAO.getAllQuestions();
    }

    public List<Question> getAllTeacherQuestions(int userId) throws SQLException {
        return questionDAO.getAllTeacherQuestions(userId);
    }

    public List<Question> getPublicQuestions() throws SQLException {
        return questionDAO.getPublicQuestions();
    }

    public Question updateQuestion(Question question) throws SQLException {
        return questionDAO.updateQuestion(question);
    }


}
