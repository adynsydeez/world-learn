package com.worldlearn.backend.controllers;

import com.worldlearn.backend.dto.CreateLessonRequest;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;
import com.worldlearn.backend.services.QuizService;
import io.javalin.http.Context;
import com.worldlearn.backend.dto.CreateQuizRequest;

import java.util.List;
import java.util.Optional;

public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    public void getUserById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Quiz quiz = quizService.getQuizById(id);

            if (quiz != null) {
                ctx.json(quiz);
            } else {
                ctx.status(404).result("Quiz not found");
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    public void createQuiz(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.queryParam("teacherId"));
            System.out.println("teacherId param: " + ctx.queryParam("teacherId"));

            CreateQuizRequest req = ctx.bodyAsClass(CreateQuizRequest.class);

            Quiz quiz = new Quiz(0, req.getQuizName(), req.getVisibility());
            Quiz createdQuiz = quizService.createQuiz(quiz, teacherId, req.getQuestionIds());

            ctx.status(201).json(createdQuiz);

        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void updateQuiz(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.queryParam("teacherId"));
            CreateQuizRequest req = ctx.bodyAsClass(CreateQuizRequest.class);

            // Validate that lessonId is provided
            if (req.getQuizId() <= 0) {
                ctx.status(400).result("Quiz ID is required for update");
                return;
            }

            Quiz quiz = new Quiz(req.getQuizId(), req.getQuizName(), req.getVisibility());
            Quiz updatedQuiz = quizService.updateQuiz(quiz, teacherId, req.getQuestionIds());

            ctx.status(200).json(updatedQuiz);

        } catch (SecurityException e) {
            ctx.status(403).result("Forbidden: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void getAllQuizzes(Context ctx) {
        try {
            List<Quiz> questions = quizService.getAllQuizzes();
            ctx.json(questions);
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void getQuizQuestions(Context ctx) {
        try {
            int quizId = Integer.parseInt(ctx.pathParam("id"));
            List<Question> questions = quizService.getQuizQuestions(quizId);
            ctx.json(questions);
        } catch (Exception e) {
            ctx.status(500).result("Failed to get questions: " + e.getMessage());
        }
    }
}