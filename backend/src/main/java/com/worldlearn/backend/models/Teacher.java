package com.worldlearn.backend.models;

import com.worldlearn.backend.database.User;

public class Teacher extends User {

    public Teacher(String email, String password) {
        super(email, password);
        setRole("teacher");
    }

    public Teacher(int user_id, String email, String password, String first, String last, String teacher) {
        super(user_id, email, password, first, last, teacher);
    }


    @Override
    public String getUserRole(){
        return "Teacher";
    }
}
