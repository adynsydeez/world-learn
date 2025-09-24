package com.worldlearn.frontend.test;

import com.worldlearn.backend.database.*;
import com.worldlearn.backend.models.*;

import org.junit.jupiter.api.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class QuizTest {

    private Quiz quiz;
    private Teacher teacher;   // Teacher extends User
    private ArrayList<Question> questions;

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
            assertThrows(IllegalArgumentException.class, () -> new Quiz(questions, null, null));
        }

        @Test
        @DisplayName("constructor rejects null questions")
        void constructorRejectsNullQuestions() {
            assertThrows(IllegalArgumentException.class, () -> new Quiz(null, null, teacher));
        }
    }

    @Nested
    @DisplayName("Author Validation")
    class AuthorValidationTest {

        @Test
        @DisplayName("rejects null author setter")
        void rejectsNullAuthorSetter() {
            assertThrows(IllegalArgumentException.class, () -> quiz.setAuthor(null));
        }
    }

    @Nested
    @DisplayName("Questions Validation")
    class QuestionsTest {

        @Test
        @DisplayName("rejects null list")
        void rejectsNullList() {
            assertThrows(IllegalArgumentException.class, () -> quiz.setQuestions(null));
        }

        @Test
        @DisplayName("rejects empty list")
        void rejectsEmptyList() {
            ArrayList<Question> emptyList = new ArrayList<>();
            assertThrows(IllegalArgumentException.class, () -> quiz.setQuestions(emptyList));
        }

        @Test
        @DisplayName("rejects list when any element is null (even if others are non-null)")
        void rejectsWhenAnyNullPresent() {
            ArrayList<Question> qs = new ArrayList<>();
            qs.add(new Question());
            qs.add(null);  // <- should trigger rejection
            qs.add(new Question());

            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> quiz.setQuestions(qs));

            assertTrue(ex.getMessage().contains("questions[1]"));
        }

        @Test
        @DisplayName("accepts valid non-empty list of questions")
        void acceptsValidList() {
            ArrayList<Question> validList = new ArrayList<>();
            validList.add(new Question());
            validList.add(new Question());

            assertDoesNotThrow(() -> quiz.setQuestions(validList));
        }
    }
}
