package com.example.genius.Model;

import java.util.List;

public class StudentData {

    long branchID;
    long stdID;
    int batchID;
    boolean Completed;
    List<StudentModel> Data;

    public StudentData(long branchID, long stdID, int batchID) {
        this.branchID = branchID;
        this.stdID = stdID;
        this.batchID = batchID;
    }

    public long getBranchID() {
        return branchID;
    }

    public void setBranchID(long branchID) {
        this.branchID = branchID;
    }

    public long getStdID() {
        return stdID;
    }

    public void setStdID(long stdID) {
        this.stdID = stdID;
    }

    public int getBatchID() {
        return batchID;
    }

    public void setBatchID(int batchID) {
        this.batchID = batchID;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<StudentModel> getData() {
        return Data;
    }

    public void setData(List<StudentModel> data) {
        Data = data;
    }
}
