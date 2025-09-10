package com.worldlearn.frontend;

//class makes it easier to set a user to student or teacher, instead of relying on strings

public enum Role {
    STUDENT,
    TEACHER;

    //returns role in string form
    @Override
    public String toString() {
        // Pretty-print for UI (instead of "STUDENT")
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}


