package com.worldlearn.backend.models;

import java.util.Random;

/**
 * WlClass model
 */
public class WlClass {
    private int id;
    private String className;
    private int joinCode;
    private final Random random = new Random();

    /**
     * No-arg constructor
     */
    public WlClass() { }

    /**
     * Full constructor
     * @param id
     * @param className
     * @param joinCode
     */
    public WlClass(int id, String className, int joinCode) {
        this.id = id;
        this.className = className;
        this.joinCode = joinCode;
    }

    /**
     * Gets id
     * @return id
     */
    public int getId() { return this.id; }

    /**
     * Sets id
     * @param id
     */
    public void setId(int id) { this.id = id; }

    /**
     * Gets class name
     * @return className
     */
    public String getClassName() { return this.className; }

    /**
     * Sets class name
     * @param className
     */
    public void setClassName(String className) { this.className = className; }

    /**
     * Gets join code
     * @return joinCode
     */
    public int getJoinCode() { return this.joinCode; }

    /**
     * Sets join code
     * @param joinCode
     */
    public void setJoinCode(int joinCode) { this.joinCode = joinCode; }

    /**
     * Returns string representation
     * @return string
     */
    @Override
    public String toString() {
        return String.format("Class{id=%s, className='%s', joinCode='%s'}",
                id, className, joinCode);
    }
}
