package com.worldlearn.backend.controllers;

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
            Optional<Quiz> quiz = quizService.getQuizById(id);

            if (quiz.isPresent()) {
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

    public void getAllTeacherQuizzes(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.pathParam("id"));
            List<Quiz> quizzes = quizService.getAllTeacherQuizzes(teacherId);
            ctx.json(quizzes);
        } catch (Exception e) {
            ctx.status(500).result("Failed to get quizzes: " + e.getMessage());
        }
    }

    public void getPublicQuizzes(Context ctx) {
        try {
            List<Quiz> quizzes = quizService.getPublicQuizzes();
            ctx.json(quizzes);
        } catch (Exception e) {
            ctx.status(500).result("Failed to get quizzes:" + e.getMessage());
        }
    }
}