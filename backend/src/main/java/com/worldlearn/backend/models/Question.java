package com.worldlearn.backend.models;

import java.util.Arrays;

public class Question {
    private int questionId;
    private String answer;
    private String[] options;
    private String prompt;
    private QuestionType type;
    private int pointsWorth;
    private Visibility visibility;


    /// ////CONSTRUCTOR
    public Question() {};

    public Question(int questionId,
                    String answer,
                    String[] options,
                    String prompt,
                    QuestionType type,
                    int pointsWorth,
                    Visibility visibility) {
        setQuestionId(questionId);
        setAnswer(answer);
        setOptions(options);
        setPrompt(prompt);
        setType(type);
        setPointsWorth(pointsWorth);
        setVisibility(visibility);
    }

    /// ////GETTERS AND SETTERS
    //QUESTION ID
    public void setQuestionId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("questionId must be >= 0");
        }
        this.questionId = id;
    }
    public int getQuestionId() { return questionId; }

    //ANSWER
    public void setAnswer(String answer) {
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("answer must not be null/blank");
        }
        this.answer = answer.trim();
    }
    public String getAnswer() { return answer; }

    //OPTIONS
    public void setOptions(String[] options) {
        if (options == null) {
            throw new IllegalArgumentException("options must not be null");
        }
        // Disallow null/blank elements
        for (int i = 0; i < options.length; i++) {
            String opt = options[i];
            if (opt == null || opt.isBlank()) {
                throw new IllegalArgumentException("options contains null/blank at index " + i);
            }
        }
        // Defensive copy
        this.options = Arrays.copyOf(options, options.length);
    }
    public String[] getOptions() { return options; }

    //PROMPT
    public void setPrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("prompt must not be null/blank");
        }
        this.prompt = prompt.trim();
    }
    public String getPrompt() { return prompt; }

    //TYPE
    public void setType(QuestionType type) {this.type = type;}
    public QuestionType getType() { return type; }

    //POINTSWORTH
    public void setPointsWorth(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("pointsWorth must be >= 0");
        }
        this.pointsWorth = points;
    }
    public int getPointsWorth() { return pointsWorth; }

    //VISIBILITY
    public void setVisibility(Visibility vis) {this.visibility = vis;}
    public Visibility getVisibility() { return visibility; }

    public enum QuestionType {
        mcq, written, map;

        public static QuestionType fromDbValue(String dbValue) {
            return QuestionType.valueOf(dbValue.toLowerCase());
        }

        public String getDbValue() {
            return this.name().toLowerCase();
        }
    }


    public enum Visibility {
        PUBLIC("public"),
        PRIVATE("private");

        private final String dbValue;
        Visibility(String dbValue) { this.dbValue = dbValue; }
        public String getDbValue() { return dbValue; }

        public static Visibility fromDbValue(String value) {
            for (Visibility v : values()) {
                if (v.dbValue.equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unknown visibility: " + value);
        }
    }
}
