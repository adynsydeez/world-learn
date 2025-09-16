package com.worldlearn.backend.controllers;

import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.services.QuestionService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    public void getUserById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Optional<Question> question = questionService.getQuestionById(id);

            if (question.isPresent()) {
                ctx.json(question);
            } else {
                ctx.status(404).result("Question not found");
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    public void createQuestion(Context ctx) {
        System.out.println("hey");
        try {
            int teacherId = Integer.parseInt(ctx.queryParam("teacherId"));
            System.out.println("teacherId param: " + ctx.queryParam("teacherId"));
            Question question = ctx.bodyAsClass(Question.class);
            Question createdQuestion = questionService.createQuestion(question, teacherId);
            ctx.status(201).json(createdQuestion);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void getAllQuestions(Context ctx) {
        try {
            List<Question> questions = questionService.getAllQuestions();
            ctx.json(questions);
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void getAllTeacherQuestions(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.pathParam("id"));
            List<Question> questions = questionService.getAllTeacherQuestions(teacherId);
            ctx.json(questions);
        } catch (Exception e) {
            ctx.status(500).result("Failed to get questions: " + e.getMessage());
        }
    }

    public void updateQuestion(Context ctx) {
        try {
            Question question = ctx.bodyAsClass(Question.class);
            Question updatedQuestion = questionService.updateQuestion(question);

            if (updatedQuestion != null) {
                ctx.json(updatedQuestion);
            } else {
                ctx.status(404).result("Question not found");
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void deleteQuestion(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean deleted = questionService.deleteQuestion(id);

            if (deleted) {
                ctx.status(204);
            } else {
                ctx.status(404).result("Question not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid question ID");
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }
}