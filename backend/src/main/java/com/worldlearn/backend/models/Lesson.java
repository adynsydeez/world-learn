package com.worldlearn.backend.models;

import com.fasterxml.jackson.annotation.JsonValue;
import com.worldlearn.backend.models.Question.Visibility;

public class Lesson {
    private int lessonId;
    private String lessonName;
    private Visibility visibility;

    public Lesson(){}

    public Lesson(int lessonId, String lessonName, Visibility visibility){
        setLessonId(lessonId);
        setLessonName(lessonName);
        setVisibility(visibility);
    }

    public void setLessonId(int id){
        this.lessonId = id;
    }

    public int getLessonId(){ return lessonId; }

    public void setLessonName(String lessonName){
        this.lessonName = lessonName;
    }

    public String getLessonName() { return lessonName; }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() { return visibility; }
}
