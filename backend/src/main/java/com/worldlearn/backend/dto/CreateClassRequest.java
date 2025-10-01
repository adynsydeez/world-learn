package com.worldlearn.backend.dto;


import java.util.List;

public class CreateClassRequest {
    private int id;
    private String className;
    private int joinCode;
    private List<Integer> lessonIds;

    public CreateClassRequest() {
    }

    public CreateClassRequest(int id, String className, int joinCode, List<Integer> lessonIds) {
        this.id = id;
        this.className = className;
        this.joinCode = joinCode;
        this.lessonIds = lessonIds;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getClassName() { return this.className; }
    public void setClassName(String className) { this.className = className; }

    public int getJoinCode() { return this.joinCode; }
    public void setJoinCode(int joinCode) { this.joinCode = joinCode; }

    public void setLessonIds(List<Integer> lessonIds) {
        // validation
        this.lessonIds = lessonIds;
    }
    public List<Integer> getLessonIds() { return lessonIds; }

    @Override
    public String toString() {
        return String.format("Class{id=%s, className='%s', joinCode='%s'}",
                id, className, joinCode);
    }
}
