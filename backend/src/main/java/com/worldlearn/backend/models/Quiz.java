package com.worldlearn.backend.models;

import java.util.ArrayList;

public class Quiz {

    private ArrayList<Question> questions;
    private Teacher author;
    private int quizID;

    /// ////CONSTRUCTOR
    public Quiz() {

    }
    public Quiz (ArrayList<Question> questions, Teacher author) {
        setAuthor(author);
        setQuestions(questions);
    }

    /// /////GETTERS AND SETTERS
    /// AUTHOR
    public void setAuthor(Teacher author) {
        if (author == null) {
            throw new IllegalArgumentException("Author (Teacher) cannot be null");
        }
        this.author = author;
    }

    public Teacher getAuthor() {
        return author;
    }

    /// QUESTIONS
    public void setQuestions(java.util.ArrayList<Question> questions) {
        if (questions == null) {
            throw new IllegalArgumentException("questions list cannot be null");
        }
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i) == null) {
                throw new IllegalArgumentException("questions[" + i + "] cannot be null");
            }
        }
        this.questions = questions;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
