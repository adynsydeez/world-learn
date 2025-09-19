package com.worldlearn.backend.models;

public class Student extends User {

    public Student(String email, String password) {
        super(email, password);
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
