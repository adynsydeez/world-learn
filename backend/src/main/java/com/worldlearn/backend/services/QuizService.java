package com.worldlearn.backend.services;

import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.database.QuizDAO;
import com.worldlearn.backend.models.Question;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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

    public Quiz updateQuiz(Quiz quiz, int teacherId, List<Integer> questions) throws SQLException {
        int quizId = quiz.getQuizID();

        // 1. Verify ownership
        if (!quizDAO.verifyQuizOwnership(quizId, teacherId)) {
            throw new SecurityException("You do not have permission to update this Quiz");
        }

        System.out.println("Updating Quiz: " + quiz.getQuizName());

        // 2. Update basic quiz info
        quizDAO.updateQuiz(quiz);
        System.out.println("Updated quiz info for quiz ID: " + quizId);

        // 3. Get current question associations
        Set<Integer> currentQuestionIds = quizDAO.getQuestionIdsForQuiz(quizId);
        Set<Integer> newQuestionIds = new HashSet<>(questions != null ? questions : new ArrayList<>());

        // 4. Determine which questions to add and remove
        Set<Integer> questionsToAdd = new HashSet<>(newQuestionIds);
        questionsToAdd.removeAll(currentQuestionIds); // Only new ones

        Set<Integer> questionsToRemove = new HashSet<>(currentQuestionIds);
        questionsToRemove.removeAll(newQuestionIds); // Only removed ones

        // 5. Remove questions that are no longer associated
        for (Integer questionId : questionsToRemove) {
            quizDAO.removeQuestionFromQuiz(quizId, questionId);
            System.out.println("Removed Question: " + questionId + " from Quiz: " + quizId);
        }

        // 6. Add new question associations
        for (Integer questionId : questionsToAdd) {
            quizDAO.saveQuestionToQuiz(quizId, questionId);
            System.out.println("Added Question: " + questionId + " to Quiz: " + quizId);
        }

        // 7. Return the updated lesson
        return quizDAO.getQuizByID(quizId);
    }

    public Quiz getQuizById(int id) throws SQLException {
        return quizDAO.getQuizByID(id);
    }

    public List<Question> getQuizQuestions(int id) throws SQLException {
        return quizDAO.getQuizQuestions(id);
    }

    public List<Quiz> getAllQuizzes() throws SQLException {
        return quizDAO.getAllQuizzes();
    }

}
