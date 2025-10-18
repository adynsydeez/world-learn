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

/**
 * Class controller
 */
public class ClassController {
    private final ClassService classService;

    /**
     * Constructs controller
     * @param classService
     */
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    /**
     * Creates class
     * @param ctx
     */
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

    /**
     * Updates class
     * @param ctx
     */
    public void updateClass(Context ctx) {
        try {
            int teacherId = Integer.parseInt(ctx.queryParam("teacherId"));
            CreateClassRequest req = ctx.bodyAsClass(CreateClassRequest.class);

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

    /**
     * Gets all classes for user
     * @param ctx
     */
    public void getAllClassesForUser(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("id"));

            UserService userService = new UserService(new UserDAO(new Database()));
            User user = userService.getUserById(String.valueOf(userId));
            if (user == null) {
                ctx.status(404).json(Map.of("error", "User not found"));
                return;
            }

            List<WlClass> classes = classService.getAllClassesForUser(user);
            ctx.status(200).json(classes);

        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid user id"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Database error: " + e.getMessage()));
        }
    }

    /**
     * Gets lessons for class
     * @param ctx
     */
    public void getClassLessons(Context ctx) {
        try {
            int classId = Integer.parseInt(ctx.pathParam("id"));

            ClassService classService = new ClassService(new ClassDAO(new Database()));
            WlClass wlClass = classService.getClassById(classId);
            if (wlClass == null) {
                ctx.status(404).json(Map.of("error", "Class not found"));
                return;
            }

            List<Lesson> lessons = classService.getClassLessons(classId);
            ctx.status(200).json(lessons);

        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid class id"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Database error: " + e.getMessage()));
        }
    }

    /**
     * Assigns student to class
     * @param ctx
     */
    public void assignStudentToClass(Context ctx) {
        try {
            AssignStudentRequest req = ctx.bodyAsClass(AssignStudentRequest.class);

            int classId = classService.getClassIdByJoinCode(req.getJoinCode());

            classService.assignStudentToClass(classId, req.getUserId());
            ctx.status(200).result("User added to class successfully");

        } catch (Exception e) {
            ctx.status(500).result("Internal server error: " + e.getMessage());
        }
    }
}
