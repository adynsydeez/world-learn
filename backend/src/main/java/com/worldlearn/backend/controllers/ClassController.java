package com.worldlearn.backend.controllers;

import com.worldlearn.backend.models.WlClass;
import com.worldlearn.backend.services.ClassService;
import io.javalin.http.Context;

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

    // Future method for adding users to existing classes
    // public void addUserToClass(Context ctx) {
    //     try {
    //         int classId = Integer.parseInt(ctx.pathParam("classId"));
    //         int userId = Integer.parseInt(ctx.pathParam("userId"));
    //         String role = ctx.queryParam("role"); // "viewer" or "editor"
    //
    //         classService.addUserToClass(classId, userId, role);
    //         ctx.status(200).result("User added to class successfully");
    //     } catch (Exception e) {
    //         ctx.status(500).result("Internal server error: " + e.getMessage());
    //     }
    // }
}