package com.worldlearn.backend.dto;

public class AssignStudentRequest {
    private int userId;
    private int joinCode;

    public int getUserId() { return this.userId; }
    public int getJoinCode() { return this.joinCode; }
}