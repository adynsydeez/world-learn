package com.worldlearn.backend.controllers;

import com.worldlearn.backend.database.ClassDAO;
import com.worldlearn.backend.database.Database;
import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.dto.AssignStudentRequest;
import com.worldlearn.backend.dto.CreateClassRequest;
import com.worldlearn.backend.dto.CreateLessonRequest;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.services.UserService;
import com.worldlearn.backend.models.WlClass;
import com.worldlearn.backend.services.ClassService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    public void createClass(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.queryParam("teacherId"));
            System.out.println("teacherId param: " + ctx.queryParam("teacherId"));

            CreateClassRequest req = ctx.bodyAsClass(CreateClassRequest.class);

            WlClass classroom = new WlClass(0, req.getClassName(), 0);
            WlClass createdClass = classService.createClass(classroom, teacherId, req.getLessonIds());

            ctx.status(201).json(createdClass);

        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }

    public void updateClass(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.queryParam("teacherId"));
            CreateClassRequest req = ctx.bodyAsClass(CreateClassRequest.class);

            // Validate that lessonId is provided
            if (req.getId() <= 0) {
                ctx.status(400).result("Class ID is required for update");
                return;
            }

            WlClass classroom = new WlClass(req.getId(), req.getClassName(), 0);
            WlClass updatedClass = classService.updateClass(classroom, teacherId, req.getLessonIds());

            ctx.status(200).json(updatedClass);

        } catch (SecurityException e) {
            ctx.status(403).result("Forbidden: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Validation error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
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

    public void getClassLessons(Context ctx) {
        try {
            // Extract class ID from path
            int classId = Integer.parseInt(ctx.pathParam("id"));

            ClassService classService = new ClassService(new ClassDAO(new Database()));
            WlClass wlClass = classService.getClassById(classId); // make sure this returns User with role
            if (wlClass == null) {
                ctx.status(404).json(Map.of("error", "Class not found"));
                return;
            }

            // Fetch classes
            List<Lesson> lessons = classService.getClassLessons(classId);

            // Always return JSON array (empty if no classes)
            ctx.status(200).json(lessons);

        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid class id"));
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