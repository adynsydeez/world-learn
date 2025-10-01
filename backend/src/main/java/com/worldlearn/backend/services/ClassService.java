package com.worldlearn.backend.services;

import com.worldlearn.backend.database.ClassDAO;
import com.worldlearn.backend.models.Lesson;
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
    public WlClass createClass(WlClass wlClass, int teacherId, List<Integer> lessons) throws SQLException {
        WlClass saved = classDAO.createClass(wlClass);
        int classId = saved.getId();
        classDAO.saveTeacherToClass(classId, teacherId);
        for(Integer l : lessons){
            classDAO.saveLessonToClass(classId, l);
        }
        return saved;
    }

    public List<WlClass> getAllClassesForUser(User user) throws SQLException {
        return classDAO.getAllClassesForUser(user);
    }

    public static int generateJoinCode(WlClass wlClass) {
        System.out.println("adding " + wlClass.getId() + "to 100000");
        int code = 100000 + wlClass.getId();
        System.out.println(code);
        return code;
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