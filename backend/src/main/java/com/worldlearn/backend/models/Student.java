package com.worldlearn.backend.models;

import com.worldlearn.backend.database.User;

public class Student extends User {

    public Student(String email, String password) {
        super(email, password);
        // rely on User's validation (IllegalArgumentException on bad/null)
        setRole("student");
    }

    public Student(int userId, String email, String password, String first, String last, String student) {
        super(userId,email, password, first, last, student);
    }


    @Override
    public String getUserRole(){
        return "Student";
    }
}
