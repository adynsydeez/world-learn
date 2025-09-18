package com.worldlearn.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worldlearn.backend.controllers.QuestionController;
import com.worldlearn.backend.controllers.UserController;
import com.worldlearn.backend.controllers.ClassController;
import com.worldlearn.backend.database.ClassDAO;
import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.database.QuestionDAO;
import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.services.ClassService;
import com.worldlearn.backend.services.QuestionService;
import com.worldlearn.backend.services.UserService;
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

        UserService userService = new UserService(userDAO);
        ClassService classService = new ClassService(classDAO);
        QuestionService questionService = new QuestionService((questionDAO));

        UserController userController = new UserController(userService);
        ClassController classController = new ClassController(classService);
        QuestionController questionController = new QuestionController(questionService);

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
        app.get("/api/users/{id}/questions", questionController::getAllTeacherQuestions);

        // LOGIN endpoint
        app.post("/api/users/login", userController::logIn);

        // Class API endpoints
        app.post("/api/classes", classController::createClass);
        app.get("/api/classes/user/{id}", classController::getAllClassesForUser);
        app.post("/api/classes/student", classController::assignStudentToClass);

        // Question API endpoints
        app.post("/api/questions", questionController::createQuestion);
        app.get("/api/questions/public", questionController::getPublicQuestions);


        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            app.stop();
        }));
    }
}