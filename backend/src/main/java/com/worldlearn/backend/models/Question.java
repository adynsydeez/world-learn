package com.worldlearn.backend.models;

import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Question model
 */
public class Question {
    private int questionId;
    private String questionName;  // kept from damon-latest
    private String answer;
    private String[] options;
    private String prompt;
    private QuestionType type;
    private int pointsWorth;
    private Visibility visibility;

    /**
     * No-arg constructor
     */
    public Question() {}

    /**
     * Full constructor
     * @param questionId
     * @param questionName
     * @param answer
     * @param options
     * @param prompt
     * @param type
     * @param pointsWorth
     * @param visibility
     */
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

    /**
     * Sets question ID
     * @param id
     * @throws IllegalArgumentException
     */
    public void setQuestionId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("questionId must be >= 0");
        }
        this.questionId = id;
    }

    /**
     * Gets question ID
     * @return questionId
     */
    public int getQuestionId() { return questionId; }

    /**
     * Sets question name
     * @param questionName
     */
    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    /**
     * Gets question name
     * @return questionName
     */
    public String getQuestionName() { return questionName; }

    /**
     * Sets answer (trimmed); accepts null
     * @param answer
     */
    public void setAnswer(String answer) {
        if (answer != null) {
            this.answer = answer.trim();
        } else {
            this.answer = null;
        }
    }

    /**
     * Gets answer
     * @return answer
     */
    public String getAnswer() { return answer; }

    /**
     * Sets options (defensive copy); accepts null
     * @param options
     */
    public void setOptions(String[] options) {
        if (options != null) {
            this.options = Arrays.copyOf(options, options.length);
        } else {
            this.options = null;
        }
    }

    /**
     * Gets options
     * @return options
     */
    public String[] getOptions() { return options; }

    /**
     * Sets prompt (trimmed, non-blank)
     * @param prompt
     * @throws IllegalArgumentException
     */
    public void setPrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("prompt must not be null/blank");
        }
        this.prompt = prompt.trim();
    }

    /**
     * Gets prompt
     * @return prompt
     */
    public String getPrompt() { return prompt; }

    /**
     * Sets question type (non-null)
     * @param type
     * @throws IllegalArgumentException
     */
    public void setType(QuestionType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        this.type = type;
    }

    /**
     * Gets question type
     * @return type
     */
    public QuestionType getType() { return type; }

    /**
     * Sets points worth
     * @param points
     * @throws IllegalArgumentException
     */
    public void setPointsWorth(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("pointsWorth must be >= 0");
        }
        this.pointsWorth = points;
    }

    /**
     * Gets points worth
     * @return pointsWorth
     */
    public int getPointsWorth() { return pointsWorth; }

    /**
     * Sets visibility (non-null)
     * @param vis
     * @throws IllegalArgumentException
     */
    public void setVisibility(Visibility vis) {
        if (vis == null) {
            throw new IllegalArgumentException("visibility must not be null");
        }
        this.visibility = vis;
    }

    /**
     * Gets visibility
     * @return visibility
     */
    public Visibility getVisibility() { return visibility; }

    /**
     * Question types
     */
    public enum QuestionType {
        mcq, written, map;

        /**
         * Maps DB value to enum (case-insensitive)
         * @param dbValue
         * @return QuestionType
         * @throws IllegalArgumentException
         */
        public static QuestionType fromDbValue(String dbValue) {
            return QuestionType.valueOf(dbValue.toLowerCase());
        }

        /**
         * Gets DB value (lowercase)
         * @return db value
         */
        public String getDbValue() { return this.name().toLowerCase(); }

        /**
         * Serializes to JSON (lowercase)
         * @return json value
         */
        @JsonValue
        public String toJson() { return this.name().toLowerCase(); }
    }

    /**
     * Visibility levels
     */
    public enum Visibility {
        PUBLIC("public"),
        PRIVATE("private");

        private final String dbValue;

        /**
         * Visibility constructor
         * @param dbValue
         */
        Visibility(String dbValue) { this.dbValue = dbValue; }

        /**
         * Gets DB value
         * @return dbValue
         */
        public String getDbValue() { return dbValue; }

        /**
         * Serializes to JSON (lowercase)
         * @return json value
         */
        @JsonValue
        public String toJSON() { return dbValue.toLowerCase(); }

        /**
         * Maps DB value to enum (case-insensitive)
         * @param value
         * @return Visibility
         * @throws IllegalArgumentException
         */
        public static Visibility fromDbValue(String value) {
            for (Visibility v : values()) {
                if (v.dbValue.equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unknown visibility: " + value);
        }
    }

    /**
     * Validates internal consistency
     * @throws IllegalArgumentException
     */
    public void validate() {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }

        if (type == QuestionType.mcq) {
            if (answer == null || answer.trim().isEmpty()) {
                throw new IllegalArgumentException("MCQ answer must not be null/blank");
            }
            if (options == null || options.length == 0) {
                throw new IllegalArgumentException("MCQ must have options");
            }
        }
        // ... other validations
    }
}
