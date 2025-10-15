package com.worldlearn.backend.models;

import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonValue;

public class Question {
    private int questionId;
    private String questionName;  // kept from damon-latest
    private String answer;
    private String[] options;
    private String prompt;
    private QuestionType type;
    private int pointsWorth;
    private Visibility visibility;

    /// ////CONSTRUCTOR
    public Question() {}

    public Question(int questionId,
                    String questionName,
                    String answer,
                    String[] options,
                    String prompt,
                    QuestionType type,
                    int pointsWorth,
                    Visibility visibility) {
        setQuestionId(questionId);
        setType(type);
        setQuestionName(questionName);
        setAnswer(answer);
        setOptions(options);
        setPrompt(prompt);
        setPointsWorth(pointsWorth);
        setVisibility(visibility);
    }

    /// ////GETTERS AND SETTERS

    // QUESTION ID
    public void setQuestionId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("questionId must be >= 0");
        }
        this.questionId = id;
    }
    public int getQuestionId() { return questionId; }

    // QUESTION NAME
    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }
    public String getQuestionName() { return questionName; }

    // ANSWER
    /*
    public void setAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("answer must not be null/blank");
        }
        this.answer = answer.trim();
    }
    */
    public void setAnswer(String answer) {
        if (getType() == QuestionType.mcq) {
            if (answer == null || answer.trim().isEmpty()) {
                throw new IllegalArgumentException("answer must not be null/blank");
            } else {
                this.answer = answer.trim();
            }
        } else {
            this.answer = "N/A";
        }
    }
    public String getAnswer() { return answer; }

    // OPTIONS
    public void setOptions(String[] options) {
        if (getType() == QuestionType.mcq) {
            if (options == null) {
                throw new IllegalArgumentException("options must not be null");
            }
            for (int i = 0; i < options.length; i++) {
                if (options[i] == null) {
                    throw new IllegalArgumentException("options[" + i + "] must not be null");
                }
            }
            this.options = Arrays.copyOf(options, options.length);
        } else {
            this.options = null;
        }

    }
    public String[] getOptions() { return options; }

    // PROMPT
    public void setPrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("prompt must not be null/blank");
        }
        this.prompt = prompt.trim();
    }
    public String getPrompt() { return prompt; }

    // TYPE
    public void setType(QuestionType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        this.type = type;
    }
    public QuestionType getType() { return type; }

    // POINTSWORTH
    public void setPointsWorth(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("pointsWorth must be >= 0");
        }
        this.pointsWorth = points;
    }
    public int getPointsWorth() { return pointsWorth; }

    // VISIBILITY
    public void setVisibility(Visibility vis) {
        if (vis == null) {
            throw new IllegalArgumentException("visibility must not be null");
        }
        this.visibility = vis;
    }
    public Visibility getVisibility() { return visibility; }

    // ENUMS
    public enum QuestionType {
        mcq, written, map;

        public static QuestionType fromDbValue(String dbValue) {
            return QuestionType.valueOf(dbValue.toLowerCase());
        }
        public String getDbValue() { return this.name().toLowerCase(); }
        @JsonValue
        public String toJson() { return this.name().toLowerCase(); }
    }

    public enum Visibility {
        PUBLIC("public"),
        PRIVATE("private");

        private final String dbValue;
        Visibility(String dbValue) { this.dbValue = dbValue; }
        public String getDbValue() { return dbValue; }

        @JsonValue
        public String toJSON() { return dbValue.toLowerCase(); }

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
