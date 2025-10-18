package com.worldlearn.backend.models;

import java.util.ArrayList;
import com.worldlearn.backend.models.Question.Visibility;

/**
 * Quiz model
 */
public class Quiz {

    private String quizName;
    private Visibility visibility;
    private int quizID;

    /**
     * No-arg constructor
     */
    public Quiz() { }

    /**
     * Full constructor
     * @param quizID
     * @param quizName
     * @param vis
     */
    public Quiz (int quizID, String quizName, Visibility vis) {
        setQuizID(quizID);
        setQuizName(quizName);
        setVisibility(vis);
    }

    /**
     * Sets quiz ID
     * @param id
     */
    public void setQuizID(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("quizId must be >= 0");
        }
        this.quizID = id;
    }

    /**
     * Sets quiz name
     * @param quizName
     */
    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    /**
     * Sets visibility
     * @param vis
     */
    public void setVisibility(Visibility vis) {
        this.visibility = vis;
    }

    /**
     * Gets quiz ID
     * @return quizID
     */
    public int getQuizID() { return quizID; }

    /**
     * Gets quiz name
     * @return quizName
     */
    public String getQuizName() { return quizName; }

    /**
     * Gets visibility
     * @return visibility
     */
    public Visibility getVisibility() { return visibility; }
}
