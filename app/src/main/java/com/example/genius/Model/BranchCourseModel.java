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

        public long getCourse_dtl_id() {
            return course_dtl_id;
        }

        public void setCourse_dtl_id(long course_dtl_id) {
            this.course_dtl_id = course_dtl_id;
        }

        public BranchModel getBranch() {
            return branch;
        }

        public void setBranch(BranchModel branch) {
            this.branch = branch;
        }

        public BranchCourceData getCourse() {
            return course;
        }

        public void setCourse(BranchCourceData course) {
            this.course = course;
        }

        public TransactionModel getTransaction() {
            return Transaction;
        }

        public void setTransaction(TransactionModel transaction) {
            Transaction = transaction;
        }

        public RowStatusModel getRowStatus() {
            return RowStatus;
        }

        public void setRowStatus(RowStatusModel rowStatus) {
            RowStatus = rowStatus;
        }

        public List<BranchCourceData> getBranchCourseData() {
            return BranchCourseData;
        }

        public void setBranchCourseData(List<BranchCourceData> branchCourseData) {
            BranchCourseData = branchCourseData;
        }

        public List<CourceModel> getCourseData() {
            return CourseData;
        }

        public void setCourseData(List<CourceModel> courseData) {
            CourseData = courseData;
        }
    }

}
