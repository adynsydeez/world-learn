package com.worldlearn.frontend.test;

import com.worldlearn.backend.database.*;
import com.worldlearn.backend.models.*;



import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/*import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class QuizTest {

    public Quiz quiz;
    public User user;

    @BeforeEach void seUp() {
        User user = new Teacher("Student@edu.com", "student", "Anna", "Banana", "teacher");
        Quiz quiz = new Quiz();
        ArrayList<Question> questions =
    }

    @Nested
    @DisplayName("firstName validation")
    class emptyTest {

            @ParameterizedTest
            @ValueSource(strings = {""," "})
            void emptyInput(String empty) {
                assertThrows(IllegalArgumentException.class, () -> quiz.setQuestions(empty));
            }

            @Test
            void rejectsNull() {assertThrows(IllegalArgumentException.class, () -> quiz.setQuestions(null));}

    @Nested
    @DisplayName("AuthorValidation")
    class authorTest {

            @ParameterizedTest
            @ValueSource(strings = "string")
            void stringInput(String string) {
                    assertThrows(IllegalArgumentException.class, () -> quiz.setAuthor(string));
            }

            @Test
            void rejectsNull() {assertThrows(IllegalArgumentException.class, (() -> quiz.setQuestions(null)));}

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

