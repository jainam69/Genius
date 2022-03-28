package com.example.genius.Model;

public class PaperModel {

    long PaperID;
    BranchModel Branch;
    StandardModel Standard;
    SubjectModel Subject;
    int BatchTypeID;
    String BatchTypeText;
    String Remarks;
    RowStatusModel RowStatus;
    TransactionModel Transaction;
    PaperData PaperData;
    BranchCourseModel.BranchCourceData BranchCourse;
    BranchClassSingleModel.BranchClassData BranchClass;
    BranchSubjectModel.BranchSubjectData BranchSubject;

    public PaperModel(long paperID, BranchModel branch, int batchTypeID, String remarks, RowStatusModel rowStatus, TransactionModel transaction, com.example.genius.Model.PaperData paperData, BranchCourseModel.BranchCourceData branchCourse, BranchClassSingleModel.BranchClassData branchClass, BranchSubjectModel.BranchSubjectData branchSubject) {
        PaperID = paperID;
        Branch = branch;
        BatchTypeID = batchTypeID;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
        PaperData = paperData;
        BranchCourse = branchCourse;
        BranchClass = branchClass;
        BranchSubject = branchSubject;
    }

    public BranchCourseModel.BranchCourceData getBranchCourse() {
        return BranchCourse;
    }

    public void setBranchCourse(BranchCourseModel.BranchCourceData branchCourse) {
        BranchCourse = branchCourse;
    }

    public BranchClassSingleModel.BranchClassData getBranchClass() {
        return BranchClass;
    }

    public void setBranchClass(BranchClassSingleModel.BranchClassData branchClass) {
        BranchClass = branchClass;
    }

    public BranchSubjectModel.BranchSubjectData getBranchSubject() {
        return BranchSubject;
    }

    public void setBranchSubject(BranchSubjectModel.BranchSubjectData branchSubject) {
        BranchSubject = branchSubject;
    }

    public long getPaperID() {
        return PaperID;
    }

    public void setPaperID(long paperID) {
        PaperID = paperID;
    }

    public BranchModel getBranch() {
        return Branch;
    }

    public void setBranch(BranchModel branch) {
        Branch = branch;
    }

    public StandardModel getStandard() {
        return Standard;
    }

    public void setStandard(StandardModel standard) {
        Standard = standard;
    }

    public SubjectModel getSubject() {
        return Subject;
    }

    public void setSubject(SubjectModel subject) {
        Subject = subject;
    }

    public int getBatchTypeID() {
        return BatchTypeID;
    }

    public void setBatchTypeID(int batchTypeID) {
        BatchTypeID = batchTypeID;
    }

    public String getBatchTypeText() {
        return BatchTypeText;
    }

    public void setBatchTypeText(String batchTypeText) {
        BatchTypeText = batchTypeText;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public RowStatusModel getRowStatus() {
        return RowStatus;
    }

    public void setRowStatus(RowStatusModel rowStatus) {
        RowStatus = rowStatus;
    }

    public TransactionModel getTransaction() {
        return Transaction;
    }

    public void setTransaction(TransactionModel transaction) {
        Transaction = transaction;
    }

    public com.example.genius.Model.PaperData getPaperData() {
        return PaperData;
    }

    public void setPaperData(com.example.genius.Model.PaperData paperData) {
        PaperData = paperData;
    }

    public static class PaperData1 {

        boolean Completed;
        PaperModel Data;
        String Message;

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public PaperModel getData() {
            return Data;
        }

        public void setData(PaperModel data) {
            Data = data;
        }
    }
}
