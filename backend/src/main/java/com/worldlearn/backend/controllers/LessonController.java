package com.worldlearn.backend.controllers;

import com.worldlearn.backend.dto.CreateLessonRequest;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.services.LessonService;
import io.javalin.http.Context;

import javax.sound.midi.SysexMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) { this.lessonService = lessonService; }

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

    public void getAllLessons(Context ctx) {
        try {
            List<Lesson> lessons = lessonService.getAllLessons();
            ctx.json(lessons);
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void getLessonQuizzes(Context ctx) {
        try {
            int lessonId = Integer.parseInt(ctx.pathParam("id"));
            System.out.println("Getting Quizzes for Lesson: "+lessonId);

            List<Quiz> quizzes = lessonService.getLessonQuizzes(lessonId);
            ctx.json(quizzes);
        } catch (Exception e) {
            ctx.status(500).result("Failed to get quizzes: " + e.getMessage());
        }
    }
}
