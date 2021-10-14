package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;

public class StandardModel {
    @SerializedName("StandardID")
    long StandardID;
    @SerializedName("Standard")
    String Standard;
    @SerializedName("Transaction")
    TransactionModel Transaction;
    @SerializedName("RowStatus")
    RowStatusModel RowStatus;
    @SerializedName("BranchInfo")
    BranchModel BranchInfo;

    public StandardModel() {
    }

    public StandardModel(long standardID, String standard, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        StandardID = standardID;
        Standard = standard;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public StandardModel(long standardID) {
        StandardID = standardID;
    }

    public StandardModel(String standard, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        Standard = standard;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public long getStandardID() {
        return StandardID;
    }

    public void setStandardID(long standardID) {
        StandardID = standardID;
    }

    public String getStandard() {
        return Standard;
    }

    public void setStandard(String standard) {
        Standard = standard;
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

    public static class StandardData1{
        @SerializedName("Completed")
        boolean Completed;
        @SerializedName("Data")
        StandardModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public StandardModel getData() {
            return Data;
        }

        public void setData(StandardModel data) {
            Data = data;
        }
    }
}
