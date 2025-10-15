package com.worldlearn.backend.services;

import com.worldlearn.backend.database.LessonDAO;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Quiz;

import java.sql.SQLException;
import java.util.List;

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

    public List<Lesson> getAllLessons() throws SQLException{
        return lessonDAO.getAllLessons();
    }

    public List<Quiz> getLessonQuizzes(int id) throws SQLException {
        System.out.println("Sending to DAO from Service.");
        return lessonDAO.getLessonQuizzes(id);
    }

    public List<Lesson> getAllTeacherLessons(int teacherId) throws SQLException {
        return lessonDAO.getAllTeacherLessons(teacherId);
    }
}
