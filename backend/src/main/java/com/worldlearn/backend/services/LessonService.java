package com.worldlearn.backend.services;

import com.worldlearn.backend.database.LessonDAO;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LessonService {
    private final LessonDAO lessonDAO;

    public LessonService(LessonDAO lessonDAO) { this.lessonDAO = lessonDAO; }

    public Lesson createLesson(Lesson lesson, int teacherId, List<Integer> quizzes) throws SQLException {
        Lesson saved = lessonDAO.createLesson(lesson);
        int lessonId = saved.getLessonId();
        System.out.println("Created lesson:" +lesson.getLessonName());
        lessonDAO.saveTeacherToLesson(lessonId, teacherId);
        System.out.println("Saving teacher: "+teacherId+" to Lesson: "+lessonId);
        for (Integer q : quizzes){
            lessonDAO.saveQuizToLesson(lessonId, q);
            System.out.println("Saving Quiz: "+q+" to Lesson: " +lessonId);
        }
        return saved;
    }

    public Lesson updateLesson(Lesson lesson, int teacherId, List<Integer> quizzes) throws SQLException {
        int lessonId = lesson.getLessonId();

        // 1. Verify ownership
        if (!lessonDAO.verifyLessonOwnership(lessonId, teacherId)) {
            throw new SecurityException("You do not have permission to update this lesson");
        }

        System.out.println("Updating lesson: " + lesson.getLessonName());

        // 2. Update basic lesson info
        lessonDAO.updateLesson(lesson);
        System.out.println("Updated lesson info for lesson ID: " + lessonId);

        // 3. Get current quiz associations
        Set<Integer> currentQuizIds = lessonDAO.getQuizIdsForLesson(lessonId);
        Set<Integer> newQuizIds = new HashSet<>(quizzes != null ? quizzes : new ArrayList<>());

        // 4. Determine which quizzes to add and remove
        Set<Integer> quizzesToAdd = new HashSet<>(newQuizIds);
        quizzesToAdd.removeAll(currentQuizIds); // Only new ones

        Set<Integer> quizzesToRemove = new HashSet<>(currentQuizIds);
        quizzesToRemove.removeAll(newQuizIds); // Only removed ones

        // 5. Remove quizzes that are no longer associated
        for (Integer quizId : quizzesToRemove) {
            lessonDAO.removeQuizFromLesson(lessonId, quizId);
            System.out.println("Removed Quiz: " + quizId + " from Lesson: " + lessonId);
        }

        // 6. Add new quiz associations
        for (Integer quizId : quizzesToAdd) {
            lessonDAO.saveQuizToLesson(lessonId, quizId);
            System.out.println("Added Quiz: " + quizId + " to Lesson: " + lessonId);
        }

        // 7. Return the updated lesson
        return lessonDAO.getLessonById(lessonId);
    }

    public List<Lesson> getAllLessons() throws SQLException{
        return lessonDAO.getAllLessons();
    }

    public List<Quiz> getLessonQuizzes(int id) throws SQLException {
        System.out.println("Sending to DAO from Service.");
        return lessonDAO.getLessonQuizzes(id);
    }

    public List<Lesson> getAllTeacherLessons(int teacherId) throws SQLException {
        return lessonDAO.getAllTeacherLessons(teacherId);

    public List<Lesson> getPublicLessons() throws SQLException {
        return lessonDAO.getPublicLessons();
    }
}
