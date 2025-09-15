package com.worldlearn.backend.models;

import com.fasterxml.jackson.annotation.JsonValue;

public class Question {
    private int questionId;
    private String questionName;
    private String answer;
    private String[] options;
    private String prompt;
    private QuestionType type;
    private int pointsWorth;
    private Visibility visibility;

    public enum QuestionType {
        mcq, written, map;

        public static QuestionType fromDbValue(String dbValue) {
            return QuestionType.valueOf(dbValue.toLowerCase());
        }

        public String getDbValue() {
            return this.name().toLowerCase();
        }

        @JsonValue
        public String toJson() {
            return this.name().toLowerCase();
        }

    }


    public enum Visibility {
        PUBLIC("public"),
        PRIVATE("private");

        private final String dbValue;
        Visibility(String dbValue) { this.dbValue = dbValue; }
        public String getDbValue() { return dbValue; }

        @JsonValue
        public String toJSON() {return dbValue.toLowerCase();}

        public static Visibility fromDbValue(String value) {
            for (Visibility v : values()) {
                if (v.dbValue.equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unknown visibility: " + value);
        }
    }

    public Question(int questionId, String questionName, String answer, String[] options,
                    String prompt, QuestionType type, int pointsWorth, Visibility visibility) {
        this.questionId = questionId;
        this.questionName = questionName;
        this.answer = answer;
        this.options = options;
        this.prompt = prompt;
        this.type = type;
        this.pointsWorth = pointsWorth;
        this.visibility = visibility;
    }

    public int getQuestionId() { return questionId; }
    public String getQuestionName() {return questionName; }
    public String getAnswer() { return answer; }
    public String[] getOptions() { return options; }
    public String getPrompt() { return prompt; }
    public QuestionType getType() { return type; }
    public int getPointsWorth() { return pointsWorth; }
    public Visibility getVisibility() { return visibility; }

    public void setQuestionId(int id) { this.questionId = id; }
    public void setQuestionName(String questionName) {this.questionName = questionName; }
    public void setAnswer(String answer) {this.answer = answer;}
    public void setOptions(String[] options) {this.options = options;}
    public void setPrompt (String prompt) {this.prompt = prompt;}
    public void setType(QuestionType type) {this.type = type;}
    public void setPointsWorth(int points) {this.pointsWorth = points;}
    public void setVisibility(Visibility vis) {this.visibility = vis;}

    public Question() {}
}
