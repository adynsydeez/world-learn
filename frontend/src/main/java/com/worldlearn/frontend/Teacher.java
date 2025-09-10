package com.worldlearn.frontend;

public class Teacher extends User {

    public Teacher(String email, String password) {
        super(email, password, Role.TEACHER);
    }

    @Override
    public String getUserRole(){
        return "Teacher";
    }
}
