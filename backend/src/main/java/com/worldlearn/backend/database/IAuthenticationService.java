package com.worldlearn.backend.database;

public interface IAuthenticationService {
    User signUp(String email, String password, String role);
    User logIn(String email, String password);
}
