package com.worldlearn.backend.models;

import com.fasterxml.jackson.annotation.JsonValue;
import com.worldlearn.backend.models.Question.Visibility;

public class Lesson {
    private int lessonId;
    private String lessonName;
    private Visibility visibility;

    public Lesson() {}

    /// //CONSTRUCTOR
    public Lesson(int lessonId, String lessonName, Visibility visibility) {
        setLessonId(lessonId);
        setLessonName(lessonName);
        setVisibility(visibility);
    }

    /// //LESSON ID
    public void setLessonId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("lessonId must be >= 0");
        }
        this.lessonId = id;
    }
    public int getLessonId() { return lessonId; }

    /**
     *
     * @param lessonName
     */
    public void setLessonName(String lessonName) {
        if (lessonName == null || lessonName.isBlank()) {
            throw new IllegalArgumentException("lessonName must not be null/blank");
        }
        this.lessonName = lessonName.trim();
    }
    public String getLessonName() { return lessonName; }

    /// //VISIBILITY
    public void setVisibility(Visibility visibility) {
        if (visibility == null) {
            throw new IllegalArgumentException("visibility must not be null");
        }
        this.visibility = visibility;
    }
    public Visibility getVisibility() { return visibility; }
}
