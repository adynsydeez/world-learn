package com.worldlearn.backend.services;

import com.worldlearn.backend.database.ClassDAO;
import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.User;
import com.worldlearn.backend.models.WlClass;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ClassService {
    private final ClassDAO classDAO;

    public ClassService(ClassDAO classDAO) {
        this.classDAO = classDAO;
    }

    public Optional<WlClass> getClassById(int id) throws SQLException{
        return classDAO.getClassById(id);
    }

    // Simple class creation - just inserts into Classes table
    public WlClass createClass(WlClass wlClass, int teacherId, List<Integer> lessons) throws SQLException {
        int joinCode = generateCode();
        WlClass toSave = new WlClass(wlClass.getId(), wlClass.getClassName(), joinCode);
        WlClass saved = classDAO.createClass(toSave);
        int classId = saved.getId();
        classDAO.saveTeacherToClass(classId, teacherId);
        for(Integer l : lessons){
            classDAO.saveLessonToClass(classId, l);
        }
        return saved;
    }

    public int generateCode() {
        Random random = new Random();
        int joinCode;

        int attempts = 0;
        do {
            joinCode = 100000 + random.nextInt(900000);
            attempts++;
            if (attempts > 1000) throw new RuntimeException("Unable to generate unique join code");
        } while (classDAO.joinCodeExists(joinCode)); // check DB

        return joinCode;
    }


    public List<WlClass> getAllClassesForUser(User user) throws SQLException {
        return classDAO.getAllClassesForUser(user);
    }

    public List<Lesson> getClassLessons(int classId) throws SQLException {
        return classDAO.getClassLessons(classId);
    }

     public void assignStudentToClass(int classId, int userId) throws SQLException {
         classDAO.assignStudentToClass(classId, userId);
     }

     public int getClassIdByJoinCode(int joinCode) throws SQLException {
        return classDAO.getClassIdByJoinCode(joinCode);
     }

    // Remove user from class
    public void removeTeacherFromClass(int classId, int teacherId) throws SQLException {
         classDAO.removeTeacherFromClass(classId, teacherId);
    }

    // Update user role in class
    // public void updateUserRole(int classId, int userId, String newRole) throws SQLException {
    //     classDAO.updateUserRole(classId, userId, newRole);
    // }
}