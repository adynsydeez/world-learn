package com.worldlearn.backend.services;

import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.database.QuizDAO;
import com.worldlearn.backend.models.Question;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Quiz service
 */
public class QuizService {
    private final QuizDAO quizDAO;

    /**
     * Constructs QuizService
     * @param quizDAO
     */
    public QuizService(QuizDAO quizDAO) {
        this.quizDAO = quizDAO;
    }

    /**
     * Creates quiz and links teacher/questions
     * @param quiz
     * @param teacherId
     * @param questionIds
     * @return quiz
     * @throws SQLException
     */
    public Quiz createQuiz(Quiz quiz, int teacherId, List<Integer> questionIds) throws SQLException {
        Quiz saved = quizDAO.createQuiz(quiz);
        quizDAO.saveTeacherToQuiz(saved.getQuizID(), teacherId);
        System.out.println("Linking teacherId=" + teacherId + " to quizId=" + saved.getQuizID());
        for (int q : questionIds) {
            quizDAO.saveQuestionToQuiz(saved.getQuizID(), q);
            System.out.println("Saving question " + q + " to quiz " + saved.getQuizID());
        }
        return saved;
    }

    /**
     * Updates quiz details and question mappings
     * @param quiz
     * @param teacherId
     * @param questions
     * @return quiz
     * @throws SQLException
     * @throws SecurityException
     */
    public Quiz updateQuiz(Quiz quiz, int teacherId, List<Integer> questions) throws SQLException {
        int quizId = quiz.getQuizID();

        if (!quizDAO.verifyQuizOwnership(quizId, teacherId)) {
            throw new SecurityException("You do not have permission to update this Quiz");
        }

        System.out.println("Updating Quiz: " + quiz.getQuizName());

        quizDAO.updateQuiz(quiz);
        System.out.println("Updated quiz info for quiz ID: " + quizId);

        Set<Integer> currentQuestionIds = quizDAO.getQuestionIdsForQuiz(quizId);
        Set<Integer> newQuestionIds = new HashSet<>(questions != null ? questions : new ArrayList<>());

        Set<Integer> questionsToAdd = new HashSet<>(newQuestionIds);
        questionsToAdd.removeAll(currentQuestionIds);

        Set<Integer> questionsToRemove = new HashSet<>(currentQuestionIds);
        questionsToRemove.removeAll(newQuestionIds);

        for (Integer questionId : questionsToRemove) {
            quizDAO.removeQuestionFromQuiz(quizId, questionId);
            System.out.println("Removed Question: " + questionId + " from Quiz: " + quizId);
        }

        for (Integer questionId : questionsToAdd) {
            quizDAO.saveQuestionToQuiz(quizId, questionId);
            System.out.println("Added Question: " + questionId + " to Quiz: " + quizId);
        }

        return quizDAO.getQuizByID(quizId);
    }

    /**
     * Gets quiz by id
     * @param id
     * @return quiz
     * @throws SQLException
     */
    public Quiz getQuizById(int id) throws SQLException {
        return quizDAO.getQuizByID(id);
    }

    /**
     * Gets questions for quiz
     * @param id
     * @return questions
     * @throws SQLException
     */
    public List<Question> getQuizQuestions(int id) throws SQLException {
        return quizDAO.getQuizQuestions(id);
    }

    /**
     * Gets all quizzes
     * @return quizzes
     * @throws SQLException
     */
    public List<Quiz> getAllQuizzes() throws SQLException {
        return quizDAO.getAllQuizzes();
    }

    /**
     * Gets all quizzes for teacher
     * @param userId
     * @return quizzes
     * @throws SQLException
     */
    public List<Quiz> getAllTeacherQuizzes(int userId) throws SQLException {
        return quizDAO.getAllTeacherQuizzes(userId);
    }

    /**
     * Gets all public quizzes
     * @return quizzes
     * @throws SQLException
     */
    public List<Quiz> getPublicQuizzes() throws SQLException {
        return quizDAO.getPublicQuizzes();
    }

}
