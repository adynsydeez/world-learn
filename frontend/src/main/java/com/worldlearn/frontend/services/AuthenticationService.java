package com.worldlearn.backend.services;

import com.worldlearn.backend.models.*;


import java.util.ArrayList;

public class AuthenticationService implements IAuthenticationService {
    private ArrayList<User> users;

    //Constructor
    public AuthenticationService() {
        users = new ArrayList<>();
        // Add a default user (for testing) - you'll need to update this based on your User model constructor
        User user1 = new Student("ass@ass.com", "asspw", "Test", "User", "student");
        users.add(user1);
    }

    //signsUp user. adds user to authentication service and returns user
    @Override
    public User signUp(String email, String password, String role, String firstName, String lastName) {
        //Checks that email is in correct format, throws exception if not
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: please use name@edu.com");
        }
        //checks that a password is entered, throws exception if not
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password required");
        }
        //checks that first name is entered, throws exception if not
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name required");
        }
        //checks that last name is entered, throws exception if not
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name required");
        }

        // Check if email is already taken
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                throw new IllegalArgumentException("Email Already Exists"); // username exists
            }
        }
        // If not taken, create new user and add to list
        //switch statement creates user based on role
        System.out.println(role);
        User newUser;
        switch (role) {
            case "student":
                newUser = new Student(email.trim(), password, firstName.trim(), lastName.trim(), role);
                break;
            case "teacher":
                newUser = new Teacher(email.trim(), password, firstName.trim(), lastName.trim(), role);
                break;
            default:
                // shouldn't happen due to validation above
                throw new IllegalStateException("Unexpected role: " + role);
        }
        users.add(newUser);

        ////////////////////////////////////////////////////Print to terminal for debugging
        System.out.println("New user signed up: "
                + newUser.getEmail() + " (" + newUser.getRole() + ") - "
                + firstName + " " + lastName);

        System.out.println("All users in system:");
        for (User u : users) {
            System.out.println(" - " + u.getEmail() + " (" + u.getRole() + ")");
        }

        return newUser;
    }

    // Keep the original method for backward compatibility (overloaded method)
    @Override
    public User signUp(String email, String password, String role) {
        // Call the new method with empty first/last names or throw an exception
        throw new IllegalArgumentException("First name and last name are required for signup");
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