package com.worldlearn.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldlearn.backend.controllers.QuestionController;
import com.worldlearn.backend.controllers.QuizController;
import com.worldlearn.backend.controllers.UserController;
import com.worldlearn.backend.controllers.ClassController;
import com.worldlearn.backend.controllers.LessonController;
import com.worldlearn.backend.database.*;
import com.worldlearn.backend.services.*;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.json.JavalinJackson;

public class BackendApplication {
    public static void main(String[] args) {
        // Initialize dependencies (Dependency Injection)
        Database database = new Database();
        UserDAO userDAO = new UserDAO(database);
        ClassDAO classDAO = new ClassDAO(database);
        QuestionDAO questionDAO = new QuestionDAO(database);
        QuizDAO quizDAO = new QuizDAO(database);
        LessonDAO lessonDAO = new LessonDAO(database);


        UserService userService = new UserService(userDAO);
        ClassService classService = new ClassService(classDAO);
        QuestionService questionService = new QuestionService((questionDAO));
        QuizService quizService = new QuizService((quizDAO));
        LessonService lessonService = new LessonService(lessonDAO);


        UserController userController = new UserController(userService);
        ClassController classController = new ClassController(classService);
        QuestionController questionController = new QuestionController(questionService);
        QuizController quizController = new QuizController(quizService);
        LessonController lessonController = new LessonController(lessonService);

        // Test database connection
        if (!database.testConnection()) {
            System.err.println("Failed to connect to database. Exiting...");
            System.exit(1);
        }
        System.out.println("Database connection successful!");

        // Create Javalin app with Jackson configuration
        Javalin app = Javalin.create(config -> {
            // Configure JSON mapper to use Jackson
            config.jsonMapper(new JavalinJackson(new ObjectMapper()));

            // Configure CORS
            config.plugins.enableCors(cors -> {
                cors.add(CorsPluginConfig::anyHost);
            });
        }).start(7000);

        System.out.println("Backend server started on http://localhost:7000");

        // Health check
        app.get("/", ctx -> ctx.result("WorldLearn Backend is running"));
        app.get("/health", ctx -> ctx.result("OK"));

        // User API endpoints
        app.get("/api/users", userController::getAllUsers);
        app.get("/api/users/{id}", userController::getUserById);
        app.post("/api/users", userController::createUser);
        app.put("/api/users/{id}", userController::updateUser);
        app.delete("/api/users/{id}", userController::deleteUser);
        app.put("/api/users/password/{id}", userController::updatePassword);
        app.get("/api/users/{id}/questions", questionController::getAllTeacherQuestions);
        app.get("/api/users/{id}/quizzes", quizController::getAllTeacherQuizzes);
        app.get("api/users/{id}/lessons", lessonController::getAllTeacherLessons);

        // LOGIN endpoint
        app.post("/api/users/login", userController::logIn);

        // Class API endpoints
        app.post("/api/classes", classController::createClass);
        app.get("/api/classes/user/{id}", classController::getAllClassesForUser);
        app.post("/api/classes/student", classController::assignStudentToClass);
        app.get("api/classes/{id}/lessons", classController::getClassLessons);

        // Question API endpoints
        app.post("/api/questions", questionController::createQuestion);
        app.get("/api/questions", questionController::getAllQuestions);
        app.get("/api/questions/public", questionController::getPublicQuestions);
        app.get("/api/questions/{id}", questionController::getQuestionById);
        app.post("/api/questions/submit", questionController::submitAnswer);
        app.get("/api/questions/{id}/student-answer", questionController::getStudentAnswer);

        // Quiz API endpoints

        app.post("/api/quizzes", quizController::createQuiz);
        app.get("/api/quizzes", quizController::getAllQuizzes);
        app.get("/api/quizzes/public", quizController::getPublicQuizzes);
        app.get("/api/quizzes/{id}/questions", quizController::getQuizQuestions);


        // Lesson API endpoints
        app.post("/api/lessons", lessonController::createLesson);
        app.get("/api/lessons", lessonController::getAllLessons);
        app.get("/api/lessons/public", lessonController::getPublicLessons);
        app.get("/api/lessons/{id}", lessonController::getLessonQuizzes);

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            app.stop();
        }));
    }
}