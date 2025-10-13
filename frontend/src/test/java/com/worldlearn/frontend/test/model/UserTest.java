package com.worldlearn.frontend.test.model;

import com.worldlearn.backend.models.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    /// //COMMON TEST LOGIC
    static abstract class CommonUserTests {

        protected User user;

        /// //FIRSTNAME
        @Nested
        @DisplayName("firstName validation")
        class FirstName {
            @ParameterizedTest
            @ValueSource(strings = {"Alice", "Bob", "John"})
            void acceptsLettersOnly(String name) {
                user.setFirstName(name);
                assertEquals(name, user.getFirstName());
            }

            @ParameterizedTest
            @ValueSource(strings = {"", " ", "A1", "J@ne", "John-Doe"})
            void rejectsEmptyOrNonLetters(String bad) {
                assertThrows(IllegalArgumentException.class, () -> user.setFirstName(bad));
            }

            @Test
            void rejectsNull() {
                assertThrows(IllegalArgumentException.class, () -> user.setFirstName(null));
            }
        }

        /// //LAST NAME
        @Nested
        @DisplayName("lastName validation")
        class LastName {
            @ParameterizedTest
            @ValueSource(strings = {"Smith", "Smith-Jones", "Brown-Lee"})
            void allowsLettersAndHyphen(String name) {
                user.setLastName(name);
                assertEquals(name, user.getLastName());
            }

            @ParameterizedTest
            @ValueSource(strings = {"", "Sm!th", "Lee_", "-"})
            void rejectsInvalidForms(String bad) {
                assertThrows(IllegalArgumentException.class, () -> user.setLastName(bad));
            }

            @Test
            void rejectsNull() {
                assertThrows(IllegalArgumentException.class, () -> user.setLastName(null));
            }
        }

        /// //EMAIL
        @Nested
        @DisplayName("email validation")
        class Email {
            @ParameterizedTest
            @ValueSource(strings = {
                    "alice@example.com",
                    "user.name+tag@domain.co",
                    "UPPER.CASE@EXAMPLE.COM"
            })
            void acceptsValid(String email) {
                user.setEmail(email);
                assertEquals(email, user.getEmail());
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "not-an-email", "user@", "@domain.com",
                    "user@domain", "user@domain.c", "user@domain.123", ""
            })
            void rejectsInvalid(String bad) {
                var ex = assertThrows(IllegalArgumentException.class, () -> user.setEmail(bad));
                assertTrue(ex.getMessage().contains("Invalid email format"));
            }

            @Test
            void rejectsNull() {
                assertThrows(IllegalArgumentException.class, () -> user.setEmail(null));
            }
        }

        /// //ROLE
        @Nested
        @DisplayName("role validation")
        class RoleTests {
            @Test
            void acceptsNonNullRoleStrings_currentImplementation() {
                // Current User.setRole allows any non-null string.
                user.setRole("student");
                assertEquals("student", user.getRole());
                user.setRole("teacher");
                assertEquals("teacher", user.getRole());
            }

            @Test
            void rejectsNull() {
                assertThrows(IllegalArgumentException.class, () -> user.setRole(null));
            }

            @Test
            void userRoleMethod_matchesSubclass() {
                assertEquals(expectedUserRole(), user.getUserRole());
            }
        }


        protected abstract String expectedUserRole();
    }

    /// //TEACHER
    @Nested
    @DisplayName("Teacher")
    class TeacherSuite extends CommonUserTests {
        @BeforeEach
        void setUp() {
            this.user = new Teacher("Teacher@edu.com", "teacher", "Anna", "Banana", "teacher");
        }

        @Override
        protected String expectedUserRole() {
            return "teacher";
        }
    }

    /// //STUDENT
    @Nested
    @DisplayName("Student")
    class StudentSuite extends CommonUserTests {
        @BeforeEach
        void setUp() {
            this.user = new Student("student@edu.com", "student", "Sam", "Jones", "student");
        }

        @Override
        protected String expectedUserRole() {
            return "student";
        }
    }
}