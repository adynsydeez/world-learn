package com.worldlearn.frontend;

public class Teacher extends User {

    public Teacher(String email, String password) {
        super(email, password, Role.TEACHER);
    }

    public Teacher(String email, String password, Role role) {
        super(email, password, role);
    }

    @Override
    public String getUserRole(){
        return "Teacher";
    }
}
