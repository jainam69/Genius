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

    public PaperModel(long paperID, BranchModel branch, StandardModel standard, SubjectModel subject, int batchTypeID, String batchTypeText, String remarks, RowStatusModel rowStatus, TransactionModel transaction, com.example.genius.Model.PaperData paperData) {
        PaperID = paperID;
        Branch = branch;
        Standard = standard;
        Subject = subject;
        BatchTypeID = batchTypeID;
        BatchTypeText = batchTypeText;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
        PaperData = paperData;
    }

    public PaperModel(BranchModel branch, StandardModel standard, SubjectModel subject, int batchTypeID, String batchTypeText, String remarks, RowStatusModel rowStatus, TransactionModel transaction, PaperData paperData) {
        Branch = branch;
        Standard = standard;
        Subject = subject;
        BatchTypeID = batchTypeID;
        BatchTypeText = batchTypeText;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
        PaperData = paperData;
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
