package com.example.demo;

import java.util.ArrayList;

public class AuthenticationService implements IAuthenticationService {
    private ArrayList<User> users;

    //Constructor
    public AuthenticationService() {
        users = new ArrayList<>();
        // Add a default user (for testing)
        users.add(new Student("admin@email.com",  "password123"));
    }

    @Override
    public User signUp(String email, String password, Role role) {
        //Checks that email is in correct format, throws exception if not
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: please use name@edu.com");
        }
        //checks that a password is entered, throws exception if not
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password required");
        }

        // Check if email is already taken
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                throw new IllegalArgumentException("Email Already Exists"); // username exists
            }
        }
        // If not taken, create new user and add to list
        User newUser = switch (role) {
            case STUDENT -> new Student(email.trim(), password);
            case TEACHER -> new Teacher(email.trim(), password);
        };
        users.add(newUser);

        ////////////////////////////////////////////////////Print to terminal for debugging
        System.out.println("New user signed up: "
                + newUser.getEmail() + " (" + newUser.getRole() + ")");

        System.out.println("All users in system:");
        for (User u : users) {
            System.out.println(" - " + u.getEmail() + " (" + u.getRole() + ")");
        }

        return newUser;
    }

    @Override
    public User logIn(String email, String password) {
        email = email.toLowerCase();
        for (User user : users) {
            if (user.getEmail().toLowerCase().equals(email) &&
                    user.getPassword().equals(password)) {
                return user; // Successful login
            }
        }
        throw new IllegalArgumentException("Log in credentials incorrect"); // Not found / wrong credentials
    }
}

    /* Login/signup for students and teachers to be seperated and implemented
*/
