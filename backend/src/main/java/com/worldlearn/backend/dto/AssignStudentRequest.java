package com.worldlearn.backend.dto;

public class AssignStudentRequest {
    private int userId;
    private int classId;

    public int getUserId() { return this.userId; }
    public int getClassId() { return this.classId; }
}