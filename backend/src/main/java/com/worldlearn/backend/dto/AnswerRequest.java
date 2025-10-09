package com.worldlearn.backend.dto;

public class AnswerRequest {
    private String givenAnswer;
    private int userId;

    public AnswerRequest() {}

    public String getGivenAnswer() {
        return givenAnswer;
    }

    public void setGivenAnswer(String givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}