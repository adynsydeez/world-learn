package com.worldlearn.backend.dto;

import java.time.LocalDateTime;

public class AnswerResponse {
    private int questionId;
    private int userId;
    private String givenAnswer;
    private int pointsEarned;
    private String answeredAt;
    private boolean isCorrect;

    public AnswerResponse() {}

    public AnswerResponse(int questionId, int userId, String givenAnswer,
                          int pointsEarned, String answeredAt, boolean isCorrect) {
        this.questionId = questionId;
        this.userId = userId;
        this.givenAnswer = givenAnswer;
        this.pointsEarned = pointsEarned;
        this.answeredAt = answeredAt;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGivenAnswer() {
        return givenAnswer;
    }

    public void setGivenAnswer(String givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(String answeredAt) {
        this.answeredAt = answeredAt;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}