package com.example.genius.Model;

public class HomeworkModel {

    long HomeworkID;
    BranchModel BranchInfo;
    String HomeworkDate;
    StandardModel StandardInfo;
    SubjectModel SubjectInfo;
    int BatchTimeID;
    String BatchTimeText;
    String Remarks;
    String HomeworkContentText;
    String HomeworkContentFileName;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    String FilePath;

    public HomeworkModel(long homeworkID, BranchModel branchInfo, String homeworkDate, StandardModel standardInfo, SubjectModel subjectInfo, int batchTimeID, String batchTimeText, String remarks, String homeworkContentText, String homeworkContentFileName, TransactionModel transaction, RowStatusModel rowStatus) {
        HomeworkID = homeworkID;
        BranchInfo = branchInfo;
        HomeworkDate = homeworkDate;
        StandardInfo = standardInfo;
        SubjectInfo = subjectInfo;
        BatchTimeID = batchTimeID;
        BatchTimeText = batchTimeText;
        Remarks = remarks;
        HomeworkContentText = homeworkContentText;
        HomeworkContentFileName = homeworkContentFileName;
        Transaction = transaction;
        RowStatus = rowStatus;
    }

    public HomeworkModel(BranchModel branchInfo, String homeworkDate, StandardModel standardInfo, SubjectModel subjectInfo, int batchTimeID, String batchTimeText, String remarks, String homeworkContentText, String homeworkContentFileName, TransactionModel transaction, RowStatusModel rowStatus) {
        BranchInfo = branchInfo;
        HomeworkDate = homeworkDate;
        StandardInfo = standardInfo;
        SubjectInfo = subjectInfo;
        BatchTimeID = batchTimeID;
        BatchTimeText = batchTimeText;
        Remarks = remarks;
        HomeworkContentText = homeworkContentText;
        HomeworkContentFileName = homeworkContentFileName;
        Transaction = transaction;
        RowStatus = rowStatus;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public long getHomeworkID() {
        return HomeworkID;
    }

    public void setHomeworkID(long homeworkID) {
        HomeworkID = homeworkID;
    }

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
    }

    public String getHomeworkDate() {
        return HomeworkDate;
    }

    public void setHomeworkDate(String homeworkDate) {
        HomeworkDate = homeworkDate;
    }

    public StandardModel getStandardInfo() {
        return StandardInfo;
    }

    public void setStandardInfo(StandardModel standardInfo) {
        StandardInfo = standardInfo;
    }

    public SubjectModel getSubjectInfo() {
        return SubjectInfo;
    }

    public void setSubjectInfo(SubjectModel subjectInfo) {
        SubjectInfo = subjectInfo;
    }

    public int getBatchTimeID() {
        return BatchTimeID;
    }

    public void setBatchTimeID(int batchTimeID) {
        BatchTimeID = batchTimeID;
    }

    public String getBatchTimeText() {
        return BatchTimeText;
    }

    public void setBatchTimeText(String batchTimeText) {
        BatchTimeText = batchTimeText;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getHomeworkContentText() {
        return HomeworkContentText;
    }

    public void setHomeworkContentText(String homeworkContentText) {
        HomeworkContentText = homeworkContentText;
    }

    public String getHomeworkContentFileName() {
        return HomeworkContentFileName;
    }

    public void setHomeworkContentFileName(String homeworkContentFileName) {
        HomeworkContentFileName = homeworkContentFileName;
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

    public static class HomeworkData1{
        boolean Completed;
        HomeworkModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public HomeworkModel getData() {
            return Data;
        }

        public void setData(HomeworkModel data) {
            Data = data;
        }
    }
}
