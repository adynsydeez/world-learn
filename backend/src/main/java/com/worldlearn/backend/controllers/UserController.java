package com.worldlearn.backend.controllers;

import com.worldlearn.backend.dto.LoginRequest;
import com.worldlearn.backend.dto.UpdatePasswordRequest;
import com.worldlearn.backend.dto.UpdateUserProfileRequest;
import com.worldlearn.backend.dto.UserRequest;
import com.worldlearn.backend.models.Student;
import com.worldlearn.backend.models.Teacher;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.services.UserService;
import io.javalin.http.Context;
import java.util.List;

/**
 * User controller
 */
public class UserController {
    private final UserService userService;

    /**
     * Constructs controller
     * @param userService
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets user by id
     * @param ctx
     */
    public void getUserById(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            User user = userService.getUserById(id);

            if (user != null) {
                ctx.json(user);
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    }

    /**
     * Logs in a user
     * @param ctx
     */
    public void logIn(Context ctx) {
        try {
            LoginRequest loginRequest = ctx.bodyAsClass(LoginRequest.class);
            User user = userService.logIn(loginRequest.getEmail(), loginRequest.getPassword());
            if (user != null) {
                ctx.status(200).json(user);
            } else {
                ctx.status(401).result("Invalid email or password");
            }
        } catch (Exception e) {
            ctx.status(500).result("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a user
     * @param ctx
     */
    public void createUser(Context ctx) {
        try {
            UserRequest userRequest = ctx.bodyAsClass(UserRequest.class);

            User newUser;
            String role = userRequest.getRole().toLowerCase().trim();

            switch (role) {
                case "student":
                    newUser = new Student(
                            userRequest.getEmail(),
                            userRequest.getPassword(),
                            userRequest.getFirstName(),
                            userRequest.getLastName(),
                            role
                    );
                    break;
                case "teacher":
                    newUser = new Teacher(
                            userRequest.getEmail(),
                            userRequest.getPassword(),
                            userRequest.getFirstName(),
                            userRequest.getLastName(),
                            role
                    );
                    break;
                default:
                    ctx.status(400).json("Invalid role: " + userRequest.getRole());
                    return;
            }

            User createdUser = userService.createUser(newUser);
            ctx.status(201).json(createdUser);

        } catch (Exception e) {
            ctx.status(500).json("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets all users
     * @param ctx
     */
    public void getAllUsers(Context ctx) {
        try {
            List<User> users = userService.getAllUsers();
            ctx.json(users);
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Updates a user
     * @param ctx
     */
    public void updateUser(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            UpdateUserProfileRequest user = ctx.bodyAsClass(UpdateUserProfileRequest.class);
            User updatedUser = userService.updateUser(id, user);

            if (updatedUser != null) {
                ctx.json(updatedUser);
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid user ID");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Deletes a user
     * @param ctx
     */
    public void deleteUser(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean deleted = userService.deleteUser(id);

            if (deleted) {
                ctx.status(204);
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid user ID");
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Updates a user's password
     * @param ctx
     */
    public void updatePassword(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            UpdatePasswordRequest user = ctx.bodyAsClass(UpdatePasswordRequest.class);
            User updatedUser = userService.updatePassword(id, user);

            if (updatedUser != null) {
                ctx.json(updatedUser);
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid user ID");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }
}
