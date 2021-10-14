package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;

public class BatchModel {
    @SerializedName("BatchID")
     long BatchID;
    @SerializedName("BatchTime")
     int BatchTime;
    @SerializedName("MonFriBatchTime")
     String MonFriBatchTime;
    @SerializedName("SatBatchTime")
     String SatBatchTime;
    @SerializedName("SunBatchTime")
     String SunBatchTime;
    @SerializedName("Transaction")
     TransactionModel Transaction;
    @SerializedName("RowStatus")
     RowStatusModel RowStatus;
    @SerializedName("BranchInfo")
     BranchModel BranchInfo;
    @SerializedName("StandardInfo")
     StandardModel StandardInfo;
    @SerializedName("BatchType")
     String BatchType;

    public BatchModel() {
    }

    public BatchModel(int batchTime) {
        BatchTime = batchTime;
    }

    public BatchModel(String batchType) {
        BatchType = batchType;
    }

    public long getBatchID() {
        return BatchID;
    }

    public void setBatchID(long batchID) {
        BatchID = batchID;
    }

    public int getBatchTime() {
        return BatchTime;
    }

    public void setBatchTime(int batchTime) {
        BatchTime = batchTime;
    }

    public String getMonFriBatchTime() {
        return MonFriBatchTime;
    }

    public void setMonFriBatchTime(String monFriBatchTime) {
        MonFriBatchTime = monFriBatchTime;
    }

    public String getSatBatchTime() {
        return SatBatchTime;
    }

    public void setSatBatchTime(String satBatchTime) {
        SatBatchTime = satBatchTime;
    }

    public String getSunBatchTime() {
        return SunBatchTime;
    }

    public void setSunBatchTime(String sunBatchTime) {
        SunBatchTime = sunBatchTime;
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

    public StandardModel getStandardInfo() {
        return StandardInfo;
    }

    public void setStandardInfo(StandardModel standardInfo) {
        StandardInfo = standardInfo;
    }

    public String getBatchType() {
        return BatchType;
    }

    public void setBatchType(String batchType) {
        BatchType = batchType;
    }
}
