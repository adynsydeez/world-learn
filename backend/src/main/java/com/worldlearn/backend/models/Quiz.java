package com.worldlearn.backend.models;

import java.util.ArrayList;
import com.worldlearn.backend.models.Question.Visibility;

public class Quiz {

    private String quizName;
    private Visibility visibility;
    private int quizID;

    /// ////CONSTRUCTOR
    public Quiz() {

    }
    public Quiz (int quizID, String quizName, Visibility vis) {
        setQuizID(quizID);
        setQuizName(quizName);
        setVisibility(vis);
    }

    /// /////GETTERS AND SETTERS
    public void setQuizID(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("quizId must be >= 0");
        }
        this.quizID = id;
    }

    /// AUTHOR
    /*
    public void setAuthor(Teacher author) {
        if (author == null) {
            throw new IllegalArgumentException("Author (Teacher) cannot be null");
        }
        this.author = author;
    }

    public Teacher getAuthor() {
        return author;
    }
*/
    /// QUESTIONS
    //public void setQuestions(java.util.ArrayList<Question> questions) {
    //    if (questions == null) {
    //        throw new IllegalArgumentException("questions list cannot be null");
    //    }
    //    for (int i = 0; i < questions.size(); i++) {
    //        if (questions.get(i) == null) {
    //            throw new IllegalArgumentException("questions[" + i + "] cannot be null");
    //        }
    //    }
    //    this.questions = questions;
    //}

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public void setVisibility(Visibility vis) {
        this.visibility = vis;
    }

    public int getQuizID() { return quizID; }

    //public ArrayList<Question> getQuestions() {
    //    return questions;
    //}

    public String getQuizName() {return quizName;}

    public Visibility getVisibility() {return visibility;}
}
