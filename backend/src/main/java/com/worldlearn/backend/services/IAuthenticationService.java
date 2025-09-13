package com.worldlearn.backend.services;

import com.worldlearn.backend.models.User;

public interface IAuthenticationService {
    //signsUp user. adds user to authentication service and returns user
    User signUp(String email, String password, String role, String firstName, String lastName);

    User signUp(String email, String password, String role);
    User logIn(String email, String password);
}
