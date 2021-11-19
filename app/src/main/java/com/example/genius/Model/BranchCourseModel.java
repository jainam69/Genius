package com.example.genius.Model;

import java.util.List;

public class BranchCourseModel {

    private List<BranchCourceData> Data;
    private String Message;
    private Boolean Completed;

    public List<BranchCourceData> getData() {
        return Data;
    }

    public void setData(List<BranchCourceData> data) {
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

    public static class BranchCourceData {
        private long course_dtl_id;
        private BranchModel branch;
        private BranchCourseModel.BranchCourceData course;
        private TransactionModel Transaction;
        private RowStatusModel RowStatus;
        private List<BranchCourceData> BranchCourseData;
        private List<CourceModel> CourseData;
    }

}
