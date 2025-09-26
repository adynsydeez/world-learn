package com.worldlearn.backend.services;

import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.database.QuizDAO;
import com.worldlearn.backend.models.Question;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class QuizService {
    private final QuizDAO quizDAO;

    public QuizService(QuizDAO quizDAO) {
        this.quizDAO = quizDAO;
    }

    public Quiz createQuiz(Quiz quiz, int teacherId, List<Integer> questionIds) throws SQLException {
        Quiz saved = quizDAO.createQuiz(quiz);
        quizDAO.saveTeacherToQuiz(saved.getQuizID(), teacherId);
        System.out.println("Linking teacherId=" + teacherId + " to quizId=" + saved.getQuizID());
        for (int q : questionIds) {
            quizDAO.saveQuestionToQuiz(saved.getQuizID(), q);
            System.out.println("Saving question " + q + " to quiz "+saved.getQuizID());
        }


        return saved;
    }

    public Optional<Quiz> getQuizById(int id) throws SQLException {
        return quizDAO.getQuizByID(id);
    }

    public List<Question> getQuizQuestions(int id) throws SQLException {
        return quizDAO.getQuizQuestions(id);
    }

    public List<Quiz> getAllQuizzes() throws SQLException {
        return quizDAO.getAllQuizzes();
    }

}
