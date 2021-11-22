package com.example.genius.Model;

import java.util.List;

public class BranchCourseSingleModel {

    private BranchCourseModel.BranchCourceData Data;
    private String Message;
    private Boolean Completed;

    public BranchCourseModel.BranchCourceData getData() {
        return Data;
    }

    public void setData(BranchCourseModel.BranchCourceData data) {
        Data = data;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Boolean getCompleted() {
        return Completed;
    }

    public void setCompleted(Boolean completed) {
        Completed = completed;
    }

}
