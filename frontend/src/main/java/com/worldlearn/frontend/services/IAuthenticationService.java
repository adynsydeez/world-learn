package com.worldlearn.frontend.services;

import com.worldlearn.frontend.Role;
import com.worldlearn.frontend.User;

public interface IAuthenticationService {
    User signUp(String email, String password, Role role);
    User logIn(String email, String password);
}
