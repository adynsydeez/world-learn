package com.example.demo;

import java.util.ArrayList;

public class AuthenticationService implements IAuthenticationService {
    private ArrayList<User> users;

    //Constructor
    public AuthenticationService() {
        users = new ArrayList<>();
        // Add a default user (for testing)
        users.add(new Student("admin@email.com", "admin", "password123"));
    }

    @Override
    public User signUp(String email, String username, String password) {
        // Check if username is already taken
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                throw new IllegalArgumentException("Username Already Exists"); // username exists
            }
        }
        // If not taken, create new user and add to list
        User newUser = new Student(email, username, password);
        users.add(newUser);
        return newUser;
    }

    @Override
    public User logIn(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password)) {
                return user; // Successful login
            }
        }
        throw new IllegalArgumentException("User not found"); // Not found / wrong credentials
    }
}

    /* Login/signup for students and teachers to be seperated and implemented
*/
