package com.example.genius.Model;

public class SchoolModel {
    long SchoolID;
    String SchoolName;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    BranchModel BranchInfo;

    public SchoolModel() {
    }

    public SchoolModel(long schoolID) {
        SchoolID = schoolID;
    }

    public SchoolModel(long schoolID, String schoolName, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        SchoolID = schoolID;
        SchoolName = schoolName;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public SchoolModel(String schoolName, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        SchoolName = schoolName;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public long getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(long schoolID) {
        SchoolID = schoolID;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
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

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
    }

    public static class SchoolData1 {
        boolean Completed;
        SchoolModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public SchoolModel getData() {
            return Data;
        }

        public void setData(SchoolModel data) {
            Data = data;
        }
    }
}
