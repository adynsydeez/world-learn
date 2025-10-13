package com.worldlearn.backend.models;

import java.util.Random;

public class WlClass {
    private int id;
    private String className;
    private int joinCode;
    private final Random random = new Random();

    /// //CONSTRUCTOR
    public WlClass() {
    }

    public WlClass(int id, String className, int joinCode) {
        this.id = id;
        this.className = className;
        this.joinCode = joinCode;
    }

    /// //ID
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    /// //CLASS NAME
    public String getClassName() { return this.className; }
    public void setClassName(String className) { this.className = className; }

    /// //JOIN CODE
    public int getJoinCode() { return this.joinCode; }
    public void setJoinCode(int joinCode) { this.joinCode = joinCode; }

    /// ///METHODS
    @Override
    public String toString() {
        return String.format("Class{id=%s, className='%s', joinCode='%s'}",
                id, className, joinCode);
    }
}
