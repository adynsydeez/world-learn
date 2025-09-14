package com.worldlearn.backend.services;

import com.worldlearn.backend.database.ClassDAO;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.models.WlClass;

import java.sql.SQLException;
import java.util.List;

public class ClassService {
    private final ClassDAO classDAO;

    public ClassService(ClassDAO classDAO) {
        this.classDAO = classDAO;
    }

    // Simple class creation - just inserts into Classes table
    public WlClass createClass(WlClass wlClass) throws SQLException {
        return classDAO.createClass(wlClass);
    }

    public List<WlClass> getAllClassesForUser(User user) throws SQLException {
        return classDAO.getAllClassesForUser(user);
    }

    // Future methods

    // Create class and automatically assign creator as owner
    // public WlClass createClassWithOwner(WlClass wlClass, User creator) throws SQLException {
    //     return classDAO.createClassWithOwner(wlClass, creator);
    // }

    // Add additional users to existing classes
    // public void addUserToClass(int classId, int userId, String role) throws SQLException {
    //     classDAO.addUserToClass(classId, userId, role);
    // }

    // Remove user from class
    // public void removeUserFromClass(int classId, int userId) throws SQLException {
    //     classDAO.removeUserFromClass(classId, userId);
    // }

    // Update user role in class
    // public void updateUserRole(int classId, int userId, String newRole) throws SQLException {
    //     classDAO.updateUserRole(classId, userId, newRole);
    // }
}