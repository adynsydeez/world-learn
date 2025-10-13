package com.worldlearn.frontend.test.model;

import com.worldlearn.backend.models.Lesson;
import com.worldlearn.backend.models.Question;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LessonTest {

    @Test
    void constructorStoresAllFields() {
        Lesson l = new Lesson(7, "  Intro to Circuits  ", Question.Visibility.PUBLIC);
        assertEquals(7, l.getLessonId());
        assertEquals("Intro to Circuits", l.getLessonName()); // trimmed
        assertEquals(Question.Visibility.PUBLIC, l.getVisibility());
    }

    @Test
    void setLessonIdRejectsNegative() {
        Lesson l = new Lesson();
        assertThrows(IllegalArgumentException.class, () -> l.setLessonId(-1));
    }

    @Test
    void setLessonIdAcceptsZeroAndPositive() {
        Lesson l = new Lesson();
        l.setLessonId(0);
        assertEquals(0, l.getLessonId());
        l.setLessonId(42);
        assertEquals(42, l.getLessonId());
    }

    @Test
    void setLessonNameRejectsNullAndBlank() {
        Lesson l = new Lesson();
        assertThrows(IllegalArgumentException.class, () -> l.setLessonName(null));
        assertThrows(IllegalArgumentException.class, () -> l.setLessonName("   "));
    }

    @Test
    void setLessonNameAcceptsAndTrims() {
        Lesson l = new Lesson();
        l.setLessonName("  Week 1: Basics  ");
        assertEquals("Week 1: Basics", l.getLessonName());
    }

    @Test
    void setVisibilityRejectsNull() {
        Lesson l = new Lesson();
        assertThrows(IllegalArgumentException.class, () -> l.setVisibility(null));
    }

    @Test
    void setVisibilityAcceptsValidEnum() {
        Lesson l = new Lesson();
        l.setVisibility(Question.Visibility.PRIVATE);
        assertEquals(Question.Visibility.PRIVATE, l.getVisibility());
    }
}
