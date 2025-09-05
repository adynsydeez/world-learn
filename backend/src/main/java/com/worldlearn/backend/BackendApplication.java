package com.worldlearn.backend;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.CorsPluginConfig;

import com.worldlearn.backend.User;

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

    // Example: get user from database
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

    // Example: create user (dummy, no DB yet)
    private static void createUser(Context ctx) {
        try {
            User user = ctx.bodyAsClass(User.class);
            ctx.json(user); // just echo back for now
        } catch (Exception e) {
            ctx.status(400).result("Invalid user data: " + e.getMessage());
        }
    }

    // Simple User model
    public record User(String id, String firstName, String lastName, String email, String role) {}
}