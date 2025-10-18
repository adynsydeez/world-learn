package com.worldlearn.backend.models;

import com.fasterxml.jackson.annotation.JsonValue;
import com.worldlearn.backend.models.Question.Visibility;

/**
 * Lesson model
 */
public class Lesson {
    private int lessonId;
    private String lessonName;
    private Visibility visibility;

    /**
     * No-arg constructor
     */
    public Lesson() {}

    /**
     * Full constructor
     * @param lessonId
     * @param lessonName
     * @param visibility
     */
    public Lesson(int lessonId, String lessonName, Visibility visibility) {
        setLessonId(lessonId);
        setLessonName(lessonName);
        setVisibility(visibility);
    }

    /**
     * Sets lesson ID
     * @param id
     */
    public void setLessonId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("lessonId must be >= 0");
        }
        this.lessonId = id;
    }

    /**
     * Gets lesson ID
     */
    public int getLessonId() { return lessonId; }

    /**
     * Sets lesson name
     * @param lessonName
     */
    public void setLessonName(String lessonName) {
        if (lessonName == null || lessonName.isBlank()) {
            throw new IllegalArgumentException("lessonName must not be null/blank");
        }
        this.lessonName = lessonName.trim();
    }

    /**
     * Gets lesson name
     */
    public String getLessonName() { return lessonName; }

    /**
     * Sets visibility
     * @param visibility
     */
    public void setVisibility(Visibility visibility) {
        if (visibility == null) {
            throw new IllegalArgumentException("visibility must not be null");
        }
        this.visibility = visibility;
    }

    /**
     * Gets visibility
     */
    public Visibility getVisibility() { return visibility; }
}