package com.example.genius.Model;

public class FeeStructureModel {

    long FeesID;
    long FeesDetailID;
    String FileName;
    StandardModel standardInfo;
    BranchModel BranchInfo;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    String FilePath;
    String Remark;
    BranchCourseModel.BranchCourceData BranchCourse;
    BranchClassSingleModel.BranchClassData BranchClass;

    public FeeStructureModel(long feesID, long feesDetailID, String fileName, BranchModel branchInfo, TransactionModel transaction, RowStatusModel rowStatus, String filePath, String remark, BranchCourseModel.BranchCourceData branchCourse, BranchClassSingleModel.BranchClassData branchClass) {
        FeesID = feesID;
        FeesDetailID = feesDetailID;
        FileName = fileName;
        BranchInfo = branchInfo;
        Transaction = transaction;
        RowStatus = rowStatus;
        FilePath = filePath;
        Remark = remark;
        BranchCourse = branchCourse;
        BranchClass = branchClass;
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

    public long getFeesID() {
        return FeesID;
    }

    public void setFeesID(long feesID) {
        FeesID = feesID;
    }

    public long getFeesDetailID() {
        return FeesDetailID;
    }

    public void setFeesDetailID(long feesDetailID) {
        FeesDetailID = feesDetailID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public StandardModel getStandardInfo() {
        return standardInfo;
    }

    public void setStandardInfo(StandardModel standardInfo) {
        this.standardInfo = standardInfo;
    }

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
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

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

}
