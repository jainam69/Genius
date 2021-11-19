package com.example.genius.Model;

import java.util.List;

public class BranchCourseSingleModel {

    private BranchClassData Data;
    private String Message;
    private Boolean Completed;

    public BranchClassData getData() {
        return Data;
    }

    public void setData(BranchClassData data) {
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

    public static class BranchClassData {
        private long course_dtl_id;
        private BranchModel branch;
        private BranchCourseModel.BranchCourceData course;
        private TransactionModel Transaction;
        private RowStatusModel RowStatus;
        private List<BranchCourseModel.BranchCourceData> BranchCourseData;
        private List<CourceModel> CourseData;
    }

}
