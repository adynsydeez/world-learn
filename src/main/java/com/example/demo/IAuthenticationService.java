package com.example.demo;

public interface IAuthenticationService {
    User signUp(String email, String password, Role role);
    User logIn(String email, String password);
}
