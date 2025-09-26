package com.worldlearn.backend.dto;

import com.worldlearn.backend.models.Question.Visibility;
import java.util.List;

public class CreateLessonRequest {
    private String lessonName;
    private Visibility visibility;
    private List<Integer> quizIds;

    public CreateLessonRequest(){}

    public CreateLessonRequest(String lessonName, Visibility visibility, List<Integer> quizIds) {
        this.lessonName = lessonName;
        this.visibility = visibility;
        this.quizIds = quizIds;
    }

    public void setLessonName(String lessonName) {
        // add validation
        this.lessonName = lessonName;
    }

    public String getLessonName() {return lessonName;}

    public void setVisibility(Visibility visibility) {
        //add validation
        this.visibility = visibility;
    }

    public Visibility getVisibility() { return visibility;}

    public void setQuizIds(List<Integer> ids){
        //add validation
        this.quizIds = ids;
    }

    public List<Integer> getQuizIds() { return quizIds;}
}
