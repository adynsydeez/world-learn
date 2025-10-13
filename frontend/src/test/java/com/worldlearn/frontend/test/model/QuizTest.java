package com.worldlearn.frontend.test.model;

import com.worldlearn.backend.models.Question;
import com.worldlearn.backend.models.Quiz;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    @Test
    void constructorStoresAllFields() {
        Quiz quiz = new Quiz(1, "Midterm", Question.Visibility.PUBLIC);
        assertEquals(1, quiz.getQuizID());
        assertEquals("Midterm", quiz.getQuizName());
        assertEquals(Question.Visibility.PUBLIC, quiz.getVisibility());
    }

    @Test
    void setQuizIDRejectsNegative() {
        Quiz quiz = new Quiz();
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> quiz.setQuizID(-1));
        assertEquals("quizId must be >= 0", ex.getMessage());
    }

    @Test
    void setQuizIDAcceptsZeroAndPositive() {
        Quiz quiz = new Quiz();
        quiz.setQuizID(0);
        assertEquals(0, quiz.getQuizID());
        quiz.setQuizID(42);
        assertEquals(42, quiz.getQuizID());
    }

    @Test
    void quizNameSetterGetter_allowsNullAndAnyString_currentImpl() {
        Quiz quiz = new Quiz();
        quiz.setQuizName(null);                 // current class allows null
        assertNull(quiz.getQuizName());

        quiz.setQuizName("  Midterm  ");        // no trimming/validation in current class
        assertEquals("  Midterm  ", quiz.getQuizName());
    }

    @Test
    void visibilitySetterGetter_allowsNull_currentImpl() {
        Quiz quiz = new Quiz();
        quiz.setVisibility(null);               // current class allows null
        assertNull(quiz.getVisibility());

        quiz.setVisibility(Question.Visibility.PRIVATE);
        assertEquals(Question.Visibility.PRIVATE, quiz.getVisibility());
    }
}
