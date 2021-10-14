package com.example.genius.Model;

public class SubjectModel {

    long SubjectID;
    String Subject;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    BranchModel BranchInfo;

    public SubjectModel() {
    }

    public SubjectModel(long subjectID) {
        SubjectID = subjectID;
    }

    public SubjectModel(String subject, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        Subject = subject;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public SubjectModel(long subjectID, String subject, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        SubjectID = subjectID;
        Subject = subject;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public long getSubjectID() {
        return SubjectID;
    }

    public void setSubjectID(long subjectID) {
        SubjectID = subjectID;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
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

    public static class SubjectData1 {
        boolean Completed;
        SubjectModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public SubjectModel getData() {
            return Data;
        }

        public void setData(SubjectModel data) {
            Data = data;
        }
    }
}
