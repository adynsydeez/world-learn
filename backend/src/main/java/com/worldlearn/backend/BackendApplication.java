package com.worldlearn.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.CorsPluginConfig;

import com.worldlearn.backend.User;

import java.sql.SQLException;

public class BackendApplication {
    public static void main(String[] args) {
        // Create Javalin app
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(CorsPluginConfig::anyHost); // allow frontend to talk to backend
            });
        }).start(7000); // backend runs on port 7000

        // Health check
        app.get("/", ctx -> ctx.result("Backend is running"));

        // Example API endpoints
        app.get("/api/users/{id}", BackendApplication::getUser);
        app.post("/api/users", BackendApplication::createUser);
    }

    private static void getUser(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            User user = Database.getUserById(id);
            if (user != null) {
                ctx.json(user);
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (Exception e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    private static void createUser(Context ctx) {
        try {
            // Parse the request body into a User object
            User user = ctx.bodyAsClass(User.class);

            // Validate required fields
            if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
                ctx.status(400).result("First name is required");
                return;
            }
            if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                ctx.status(400).result("Last name is required");
                return;
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                ctx.status(400).result("Email is required");
                return;
            }
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                ctx.status(400).result("User role is required");
                return;
            }

            // Call your database method to create the user
            int userId = Database.createUser(user.getFirstName(), user.getLastName(),
                    user.getEmail(), user.getRole());

            // Set the generated ID on the user object
            user.setId(userId);

            // Return the created user with 201 Created status
            ctx.status(201).json(user);

        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

}