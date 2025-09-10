package com.worldlearn.backend.models;

import java.util.ArrayList;

public class Question {
    private String id;
    private String answer;
    private String[] options = new String[3];
    private String prompt;
    private enum type{};
    private int pointsWorth;
    private enum visibility{};

    public Question() {}
}
