package com.worldlearn.backend.controllers;

import com.worldlearn.backend.dto.CreateLessonRequest;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.services.LessonService;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import javax.sound.midi.SysexMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Lesson controller
 */
public class LessonController {
    private final LessonService lessonService;

    /**
     * Constructs controller
     * @param lessonService
     */
    public LessonController(LessonService lessonService) { this.lessonService = lessonService; }

    /**
     * Creates lesson
     * @param ctx
     */
    public void createLesson(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.queryParam("teacherId"));
            System.out.println("teacherId param: " + ctx.queryParam("teacherId"));

            CreateLessonRequest req = ctx.bodyAsClass(CreateLessonRequest.class);

            Lesson lesson = new Lesson(0, req.getLessonName(), req.getVisibility());
            Lesson createdLesson = lessonService.createLesson(lesson, teacherId, req.getQuizIds());

            ctx.status(201).json(createdLesson);

        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Updates lesson
     * @param ctx
     */
    public void updateLesson(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.queryParam("teacherId"));
            CreateLessonRequest req = ctx.bodyAsClass(CreateLessonRequest.class);

            // Validate that lessonId is provided
            if (req.getLessonId() <= 0) {
                ctx.status(400).result("Lesson ID is required for update");
                return;
            }

            Lesson lesson = new Lesson(req.getLessonId(), req.getLessonName(), req.getVisibility());
            Lesson updatedLesson = lessonService.updateLesson(lesson, teacherId, req.getQuizIds());

            ctx.status(200).json(updatedLesson);

        } catch (SecurityException e) {
            ctx.status(403).result("Forbidden: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Gets all lessons
     * @param ctx
     */
    public void getAllLessons(Context ctx) {
        try {
            List<Lesson> lessons = lessonService.getAllLessons();
            ctx.json(lessons);
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Gets all teacher lessons
     * @param ctx
     */
    public void getAllTeacherLessons(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.pathParam("id"));
            List<Lesson> lessons = lessonService.getAllTeacherLessons(teacherId);
            ctx.json(lessons);
        } catch (Exception e) {
            ctx.status(500).result("Failed to get lessons: " + e.getMessage());
        }
    }

    /**
     * Gets public lessons
     * @param ctx
     */
    public void getPublicLessons(Context ctx) {
        try {
            List<Lesson> lessons = lessonService.getPublicLessons();
            ctx.json(lessons);
        } catch (Exception e) {
            ctx.status(500).result("Failed to get lessons:" + e.getMessage());
        }
    }

    /**
     * Gets quizzes for a lesson
     * @param ctx
     */
    public void getLessonQuizzes(Context ctx) {
        try {
            int lessonId = Integer.parseInt(ctx.pathParam("id"));
            System.out.println("Getting Quizzes for Lesson: " + lessonId);

            List<Quiz> quizzes = lessonService.getLessonQuizzes(lessonId);
            ctx.json(quizzes);
        } catch (Exception e) {
            ctx.status(500).result("Failed to get quizzes: " + e.getMessage());
        }
    }
}
