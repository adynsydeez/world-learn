/*package com.worldlearn.frontend.test;

import com.worldlearn.backend.database.*;
import com.worldlearn.backend.models.*;



import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public Quiz quiz;
public User user;

public class QuizTest {
    @BeforeEach void seUp() {
        User teacher = new Teacher("teacher", "teacher");
        Quiz quiz = new Quiz();
    }

    @Nested
    @DisplayName("firstName validation")
    class emptyTest {

            @ParameterizedTest
            @ValueSource(strings = {""," "})
            void emptyInput(String empty) {
                assertThrows(IllegalArgumentException.class, () -> quiz.setQuestion(empty));
            }

            @Test
            void rejectsNull() {assertThrows(IllegalArgumentException.class, () -> quiz.setQuestion(null));}

    @Nested
    @DisplayName("AuthorValidation")
    class authorTest {

            @ParameterizedTest
            @ValueSource(strings = "string")
            void stringInput(String string) {
                    assertThrows(IllegalArgumentException.class, () -> quiz.setAuthor(string))
            }

            @Test
            void rejectsNull() {assertThrows(IllegalArgumentException.class, (() -> quiz.setQuestion(null)));}

        /// /////
        @ParameterizedTest
        @ValueSource(strings = {"Smith", "Smith-Jones", "Brown-Lee"})
            void acceptsTeacherOnly() {
                quiz.setAuthor(this.user);

            }
        }


    }
}

 */
