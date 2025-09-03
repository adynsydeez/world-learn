package com.worldlearn.backend;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class BackendApplication {
    public static void main(String[] args) {
        // Create Javalin app
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost()); // allow frontend to talk to backend
            });
        }).start(7000); // backend runs on port 7000

        // Health check
        app.get("/", ctx -> ctx.result("Backend is running 🚀"));

        // Example API endpoints
        app.get("/api/users/{id}", BackendApplication::getUser);
        app.post("/api/users", BackendApplication::createUser);
    }

    // Example: get user (dummy)
    private static void getUser(Context ctx) {
        String id = ctx.pathParam("id");
        ctx.json(new User(id, "Alice", "Smith", "alice@example.com", "teacher"));
    }

    // Example: create user (dummy, no DB yet)
    private static void createUser(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        ctx.json(user); // just echo back for now
    }

    // Simple User model
    public record User(String id, String firstName, String lastName, String email, String role) {}
}
