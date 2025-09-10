package com.worldlearn.frontend;

public class Student extends User {

    public Student(String email, String password) {
        super(email, password, Role.STUDENT);
    }

    @Override
    public String getUserRole(){
        return "Student";
    }
}
