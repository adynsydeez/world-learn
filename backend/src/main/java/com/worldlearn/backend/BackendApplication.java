package com.worldlearn.backend;

import com.worldlearn.backend.controllers.UserController;
import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.services.UserService;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class BackendApplication {
    public static void main(String[] args) {
        // Initialize dependencies (Dependency Injection)
        Database database = new Database();
        UserDAO userDAO = new UserDAO(database);
        UserService userService = new UserService(userDAO);
        UserController userController = new UserController(userService);

        // Test database connection
        if (!database.testConnection()) {
            System.err.println("Failed to connect to database. Exiting...");
            System.exit(1);
        }
        System.out.println("Database connection successful!");

        // Create Javalin app
        Javalin app = Javalin.create(config -> {
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

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            app.stop();
        }));
    }
}