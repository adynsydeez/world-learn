package com.worldlearn.frontend.services;

import com.worldlearn.backend.models.Student;
import com.worldlearn.backend.models.Teacher;
import com.worldlearn.backend.models.User;

import java.util.concurrent.CompletableFuture;

public class AuthClientService {

    private final ApiService apiService;
    private User currentUser;

    public AuthClientService() {
        this.apiService = new ApiService();
    }

    public User signUp(String email, String password, String role, String firstName, String lastName) {
        // Only create UserRequest via ApiService, don't instantiate Student/Teacher locally
        User createdUser = apiService.createUserAsync(new Student(email, password, firstName, lastName, role))
                .join(); // or just pass a simple DTO if you refactor ApiService
        currentUser = createdUser;
        return createdUser;
    }

    public CompletableFuture<User> logIn(String email, String password) {
        return apiService.logInAsync(email, password)
                .thenApply(user -> {
                    currentUser = switch (user.getRole().toLowerCase()) {
                        case "student" -> new Student(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getRole());
                        case "teacher" -> new Teacher(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getRole());
                        default -> user;
                    };
                    return currentUser;
                });
    }



    public User getCurrentUser() {
        return currentUser;
    }

    public void logOut() {
        currentUser = null;
    }
}
