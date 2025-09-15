package com.worldlearn.backend.controllers;

import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.dto.AssignStudentRequest;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.services.UserService;
import com.worldlearn.backend.models.WlClass;
import com.worldlearn.backend.services.ClassService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    public void createClass(Context ctx) {
        try {
            WlClass wlClass = ctx.bodyAsClass(WlClass.class);
            WlClass createdClass = classService.createClass(wlClass);
            ctx.status(201).json(createdClass);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void getAllClassesForUser(Context ctx) {
        try {
            // Extract user ID from path
            int userId = Integer.parseInt(ctx.pathParam("id"));

            // Fetch full user from DB so we know the role
            UserService userService = new UserService(new UserDAO(new Database()));
            User user = userService.getUserById(String.valueOf(userId)); // make sure this returns User with role
            if (user == null) {
                ctx.status(404).json(Map.of("error", "User not found"));
                return;
            }

            // Fetch classes
            List<WlClass> classes = classService.getAllClassesForUser(user);

            // Always return JSON array (empty if no classes)
            ctx.status(200).json(classes);

        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid user id"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Database error: " + e.getMessage()));
        }
    }

    public void assignStudentToClass(Context ctx) {
        try {
            AssignStudentRequest req = ctx.bodyAsClass(AssignStudentRequest.class);

            // classService should look up classId by joinCode
            int classId = classService.getClassIdByJoinCode(req.getJoinCode());

            classService.assignStudentToClass(classId, req.getUserId());
            ctx.status(200).result("User added to class successfully");

        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }
}