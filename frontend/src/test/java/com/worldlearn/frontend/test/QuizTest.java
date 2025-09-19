package com.worldlearn.frontend.test;

import com.worldlearn.backend.database.*;
import com.worldlearn.backend.models.*;



import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class QuizTest {

    public Quiz quiz;
    public Teacher teacher;   // assuming Teacher extends User in your codebase
    public ArrayList<Question> questions;

    @BeforeEach
    void setUp() {
        // Build a valid Teacher and a fresh Quiz before each test
        teacher = new Teacher("teacher@edu.com", "password", "Anna", "Banana", "teacher");
        quiz = new Quiz();

        // Start with a valid list of non-null questions
        questions = new ArrayList<>();
        questions.add(new Question());
        questions.add(new Question());
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTest {

        @Test
        @DisplayName("constructor rejects null author")
        void constructorRejectsNullAuthor() {
            assertThrows(IllegalArgumentException.class, () -> new Quiz(questions, null));
        }

        @Test
        @DisplayName("constructor rejects null questions")
        void constructorRejectsNullQuestions() {
            assertThrows(IllegalArgumentException.class, () -> new Quiz(null, teacher));
        }


    }

    @Nested
    @DisplayName("AuthorValidation")
    class authorTest {
        @Test
        @DisplayName("rejects null author")
        void rejectsNullAuthor() {
            assertThrows(IllegalArgumentException.class, () -> quiz.setAuthor(null));
        }

        @Test
        @DisplayName("constructor rejects null author")
        void constructorRejectsNullAuthor() {
            assertThrows(IllegalArgumentException.class, () -> new Quiz(questions, null));
        }
    }

    @Nested
    @DisplayName("QuestionsValidation")
    class questionsTest {

        @DisplayName("rejects null list")
        void rejectsNullList() {
            assertThrows(IllegalArgumentException.class, () -> quiz.setQuestions(null));
        }

        @Test
        @DisplayName("rejects list when any element is null (even if others are non-null)")
        void rejectsWhenAnyNullPresent() {
            ArrayList<Question> qs = new ArrayList<>();
            qs.add(new Question());
            qs.add(null);                 // <- triggers rejection
            qs.add(new Question());

            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> quiz.setQuestions(qs));

            // matches your setter message: "questions[<i>] cannot be null"
            assertTrue(ex.getMessage().contains("questions[1]"));
        }
    }
}



