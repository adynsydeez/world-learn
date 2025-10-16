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
    void signUp_student_setsIdAndReturnsStudent() {
        var dao = new FakeUserDAO(); // no seed
        var svc = new AuthenticationService(dao);

        User u = svc.signUp("sam@edu.com","pw","student","Sam","Jones");

        assertTrue(u instanceof Student);
        assertEquals("sam@edu.com", u.getEmail());
        assertTrue(u.getId() > 0);
    }

    @Test
    void signUp_duplicateEmail_caseInsensitive_rejected() {
        var existing = new Teacher("exist@edu.com","pw","Ann","Lee","teacher");
        existing.setId(42);
        var dao = new FakeUserDAO(List.of(existing));
        var svc = new AuthenticationService(dao);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                svc.signUp("EXIST@edu.com","x","student","A","B"));
        assertTrue(ex.getMessage().toLowerCase().contains("email already exists"));
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

    @Test
    void login_success_and_failures() {
        var existing = new Student("login@edu.com","pw","Sam","Jones","student");
        existing.setId(11);
        var svc = new AuthenticationService(new FakeUserDAO(List.of(existing)));

        User ok = svc.logIn("login@edu.com","pw");
        assertEquals(11, ok.getId());

        var badPw = assertThrows(IllegalArgumentException.class, () ->
                svc.logIn("login@edu.com","nope"));
        assertEquals("Incorrect email or password", badPw.getMessage());

        var badEmail = assertThrows(IllegalArgumentException.class, () ->
                svc.logIn("nouser@edu.com","pw"));
        assertEquals("Incorrect email or password", badEmail.getMessage());
    }
}

