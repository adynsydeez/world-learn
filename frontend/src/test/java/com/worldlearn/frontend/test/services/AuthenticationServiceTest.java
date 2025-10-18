package com.worldlearn.frontend.test.services;

import com.worldlearn.backend.database.UserDAO;
import com.worldlearn.backend.models.*;
import com.worldlearn.backend.services.AuthenticationService;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

/// //Mocks DAO
static class FakeUserDAO extends UserDAO {
    private final List<User> store = new CopyOnWriteArrayList<>();
    private int seq = 1; // simple auto-increment id

    FakeUserDAO() {
        super(null);
    }

    FakeUserDAO(Collection<User> seed) {
        this();
        if (seed != null) {
            for (User u : seed) {
                if (u.getId() == 0) u.setId(seq++);
                store.add(u);
            }
        }
    }
}

    /** DAO that simulates a DB error on create(). */
    static class ThrowingUserDAO extends FakeUserDAO {
        ThrowingUserDAO() { super(); }
        @Override public User createUser(User u) { throw new RuntimeException("DB error"); }
    }

    @Test
    void signUp_unknownRole_rejected() {
        var svc = new AuthenticationService(new FakeUserDAO());
        var ex = assertThrows(IllegalArgumentException.class, () ->
                svc.signUp("new@edu.com","pw","admin","A","B"));
        assertTrue(ex.getMessage().contains("Unknown role"));
    }

    @Test
    void signUp_daoFailure_wrapped() {
        var svc = new AuthenticationService(new ThrowingUserDAO());
        var ex = assertThrows(RuntimeException.class, () ->
                svc.signUp("x@edu.com","pw","student","X","Y"));
        assertTrue(ex.getMessage().startsWith("Failed to sign up user"));
        assertTrue(ex.getMessage().contains("DB error"));
    }

}

