package com.example.demo;
import java.util.*;

public class WLSystem {
    private List<User> systemUsers;

    public WLSystem() {
        this.systemUsers = new ArrayList<User>();
    }

    //Adds a user to system, if user is null or already exists in system, exception is thrown
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (systemUsers.contains(user)) {
            throw new IllegalArgumentException("User:" + user.getUsername() + "has already been added");
        }
        systemUsers.add(user);
    }
}
