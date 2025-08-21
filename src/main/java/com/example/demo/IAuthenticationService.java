package com.example.demo;

public interface IAuthenticationService {
    User signUp(String email, String username, String password);
    User logIn(String username, String password);
}
