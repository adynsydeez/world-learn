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

        /// //CONSTRUCTOR
        @Nested
        @DisplayName("Constructor")
        class ConstructorTest {
            static class TestUser extends User {
                TestUser() { super(); }
                TestUser(String email, String password) { super(email, password); }
                TestUser(String email, String password, String first, String last, String role) {
                    super(email, password, first, last, role);
                }
                @Override public String getUserRole() { return getRole(); }
            }

            @Nested
            @DisplayName("User() no-arg constructor")
            class NoArgConstructor {
                @Test
                void defaultFieldsAreNullOrZero() {
                    User u = new TestUser();
                    assertAll(
                            () -> assertEquals(0, u.getId()),
                            () -> assertNull(u.getEmail()),
                            () -> assertNull(u.getPassword()),
                            () -> assertNull(u.getFirstName()),
                            () -> assertNull(u.getLastName()),
                            () -> assertNull(u.getRole())
                    );
                }
            }

            @Nested
            @DisplayName("User(String email, String password)")
            class TwoArgConstructor {
                @Test
                void setsEmailAndPasswordOnly() {
                    User u = new TestUser("user@edu.com", "pw");
                    assertAll(
                            () -> assertEquals("user@edu.com", u.getEmail()),
                            () -> assertEquals("pw", u.getPassword()),
                            () -> assertNull(u.getFirstName()),
                            () -> assertNull(u.getLastName()),
                            () -> assertNull(u.getRole()),
                            () -> assertEquals(0, u.getId())
                    );
                }
            }

            @Nested
            @DisplayName("User(String email, String password, String first, String last, String role)")
            class FiveArgConstructor {
                @Test
                void setsAllProvidedFields() {
                    User u = new TestUser("a@edu.com", "secret", "Alice", "Smith-Jones", "teacher");
                    assertAll(
                            () -> assertEquals("a@edu.com", u.getEmail()),
                            () -> assertEquals("secret", u.getPassword()),
                            () -> assertEquals("Alice", u.getFirstName()),
                            () -> assertEquals("Smith-Jones", u.getLastName()),
                            () -> assertEquals("teacher", u.getRole()),
                            () -> assertEquals(0, u.getId())
                    );
                }
            }
        }


        /// //FIRSTNAME
        @Nested
        @DisplayName("firstName validation")
        class FirstNameTest {
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
        class LastNameTest {
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
        class EmailTest {
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

        /// //PASSWORD
        @Nested
        @DisplayName("Password Test")
        class PasswordTest {
            @Test
            void setPasswordGetPasswordReturnsSameValue() {
                User any = new Teacher("t@edu.com", "start", "Ann", "Lee", "teacher");
                any.setPassword("secret123");
                assertEquals("secret123", any.getPassword());
            }
        }

        /// //ROLE
        @Nested
        @DisplayName("role validation")
        class RoleTests {
            @Test
            void acceptsRoleStrings() {
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
            void UserRoleMethodMatchesSubclass() {
                assertEquals(expectedUserRole(), user.getUserRole());
            }
        }


        protected abstract String expectedUserRole();

    }

    /// //TEACHER
    @Nested
    @DisplayName("Teacher")
    class TeacherTests extends CommonUserTests {
        @BeforeEach
        void setUp() {
            this.user = new Teacher("Teacher@edu.com", "teacher", "Anna", "Banana", "teacher");
        }

        @Override
        protected String expectedUserRole() {
            return "teacher";
        }

        @Test
        void getUserRoleReturnRole() {
            assertEquals(user.getUserRole(), "teacher");
        }

        @Test
        void teacherConstructor5SetsFields() {
            Teacher s = new Teacher("teacher@edu.com", "teacher", "Sam", "Jones", "teacher");

            assertAll(
                    () -> assertEquals("teacher@edu.com", s.getEmail()),
                    () -> assertEquals("teacher", s.getPassword()),
                    () -> assertEquals("Sam", s.getFirstName()),
                    () -> assertEquals("Jones", s.getLastName()),
                    () -> assertEquals("teacher", s.getRole()),
                    () -> assertEquals("teacher", s.getUserRole())
            );
        }

        @Test
        void teacherConstructor2SetsFields() {
            this.user = new Teacher("Teacher@edu.com", "teacher");

            assertAll(
            () -> assertEquals("Teacher@edu.com", this.user.getEmail()),
                    () -> assertEquals("teacher", this.user.getPassword()),
                    () -> assertEquals("teacher", this.user.getRole()),
                    () -> assertEquals("teacher", this.user.getUserRole()),
                    () -> assertNull(this.user.getFirstName()),
                    () -> assertNull(this.user.getLastName()),
                    () -> assertEquals(0, this.user.getId()));        }
    }

    /// //STUDENT
    @Nested
    @DisplayName("Student")
    class StudentTests extends CommonUserTests {
        @BeforeEach
        void setUp() {
            this.user = new Student("student@edu.com", "student", "Sam", "Jones", "student");
        }

        @Override
        protected String expectedUserRole() {
            return "student";
        }

        @Test
        void getUserRoleReturnRole() {
            assertEquals(user.getUserRole(), "student");
        }

        @Test
        void studentConstructor5SetsFields() {
            Student s = new Student("student@edu.com", "student", "Sam", "Jones", "student");

            assertAll(
                    () -> assertEquals("student@edu.com", s.getEmail()),
                    () -> assertEquals("student", s.getPassword()),
                    () -> assertEquals("Sam", s.getFirstName()),
                    () -> assertEquals("Jones", s.getLastName()),
                    () -> assertEquals("student", s.getRole()),
                    () -> assertEquals("student", s.getUserRole())
            );
        }

        @Test
        void studentConstructor2SetsFields() {
            Student s = new Student("student@edu.com", "pw");
            assertAll(
                    () -> assertEquals("student@edu.com", s.getEmail()),
                    () -> assertEquals("pw", s.getPassword()),
                    () -> assertEquals("student", s.getRole()),
                    () -> assertEquals("student", s.getUserRole()),
                    () -> assertNull(s.getFirstName()),
                    () -> assertNull(s.getLastName()),
                    () -> assertEquals(0, s.getId())
            );
        }

    }

    /// //TO STRING
    @Nested
    @DisplayName("toString")
    class toStringTest {
        @Test
        void toStringContainsFields() {
            User any = new Student("x@edu.com", "pw", "Ana", "Lee", "student");
            any.setId(7);
            String s = any.toString();
            assertAll(
                    () -> assertTrue(s.contains("id=7")),
                    () -> assertTrue(s.contains("Ana")),
                    () -> assertTrue(s.contains("Lee")),
                    () -> assertTrue(s.contains("x@edu.com")),
                    () -> assertTrue(s.contains("student"))
            );
        }
    }
}