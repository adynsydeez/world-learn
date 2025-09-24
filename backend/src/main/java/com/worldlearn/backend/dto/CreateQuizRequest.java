package com.worldlearn.backend.dto;

import com.worldlearn.backend.models.Question.Visibility;
import java.util.List;

public class CreateQuizRequest {
    private String quizName;
    private Visibility visibility;
    private List<Integer> questionIds;

    public CreateQuizRequest(){}

    public CreateQuizRequest(String quizName, Visibility visibility, List<Integer> questionIds) {
        this.quizName = quizName;
        this.visibility = visibility;
        this.questionIds = questionIds;
    }

    public void setQuizName(String quizName) {
        // add validation
        this.quizName = quizName;
    }

    public String getQuizName() {return quizName;}

    public void setVisibility(Visibility visibility) {
        //add validation
        this.visibility = visibility;
    }

    public Visibility getVisibility() { return visibility;}

    public void setQuestionIds(List<Integer> ids){
        //add validation
        this.questionIds = ids;
    }

    public List<Integer> getQuestionIds() { return questionIds;}
}
