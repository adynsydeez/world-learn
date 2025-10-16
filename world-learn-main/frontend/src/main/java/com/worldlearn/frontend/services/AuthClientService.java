package com.worldlearn.frontend.services;

import com.worldlearn.backend.dto.UpdatePasswordRequest;
import com.worldlearn.backend.dto.UpdateUserProfileRequest;
import com.worldlearn.backend.models.Student;
import com.worldlearn.backend.models.Teacher;
import com.worldlearn.backend.models.User;
import com.worldlearn.frontend.Session;

import java.util.concurrent.CompletableFuture;

public class AuthClientService {

    private final ApiService apiService;
    private User currentUser;

    public AuthClientService() {
        this.apiService = new ApiService();
    }

    public User signUp(String email, String password, String role, String firstName, String lastName) {
        User createdUser = apiService.createUserAsync(new Student(email, password, firstName, lastName, role))
                .join();
        currentUser = createdUser;
        return createdUser;
    }

    public CompletableFuture<User> logIn(String email, String password) {
        return apiService.logInAsync(email, password)
                .thenApply(user -> {
                    currentUser = switch (user.getRole().toLowerCase()) {
                        case "student" -> {
                            Student s = new Student(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getRole());
                            s.setId(user.getId());
                            yield s;
                        }
                        case "teacher" -> {
                            Teacher t = new Teacher(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getRole());
                            t.setId(user.getId());
                            yield t;
                        }
                        default -> user;
                    };
                    return currentUser;
                });
    }

    public User updateProfile(int id, String email, String password, String role, String firstName, String lastName) {
        User updatedUser = apiService.updateUserAsync(id, new UpdateUserProfileRequest(firstName, lastName, email))
                .join();
        this.currentUser = updatedUser;
        return updatedUser;
    }

    public User updatePassword(int id, String password) {
        User updatedUser = apiService.updatePasswordAsync(id, new UpdatePasswordRequest(password))
                .join();
        currentUser = updatedUser;
        return updatedUser;
    }

    public CompletableFuture<User> refreshCurrentUser() {
        if (currentUser == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("No user is currently logged in."));
        }

        return apiService.getUserByIdAsync(currentUser.getId())
                .thenApply(refreshedUser -> {
                    currentUser = switch (refreshedUser.getRole().toLowerCase()) {
                        case "student" -> {
                            Student s = new Student(
                                    refreshedUser.getEmail(),
                                    refreshedUser.getPassword(),
                                    refreshedUser.getFirstName(),
                                    refreshedUser.getLastName(),
                                    refreshedUser.getRole()
                            );
                            s.setId(refreshedUser.getId());
                            yield s;
                        }
                        case "teacher" -> {
                            Teacher t = new Teacher(
                                    refreshedUser.getEmail(),
                                    refreshedUser.getPassword(),
                                    refreshedUser.getFirstName(),
                                    refreshedUser.getLastName(),
                                    refreshedUser.getRole()
                            );
                            t.setId(refreshedUser.getId());
                            yield t;
                        }
                        default -> refreshedUser;
                    };

                    Session.setCurrentUser(currentUser);
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
