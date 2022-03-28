package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.net.PortUnreachableException;
import java.util.List;

public class BatchModel implements Serializable {
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
    @SerializedName("BatchType")
     String BatchType;
    BranchCourseModel.BranchCourceData BranchCourse;
    BranchClassSingleModel.BranchClassData BranchClass;

    public BatchModel(long batchID, int batchTime, String monFriBatchTime, String satBatchTime, String sunBatchTime, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo, BranchCourseModel.BranchCourceData branchCourse, BranchClassSingleModel.BranchClassData branchClass) {
        BatchID = batchID;
        BatchTime = batchTime;
        MonFriBatchTime = monFriBatchTime;
        SatBatchTime = satBatchTime;
        SunBatchTime = sunBatchTime;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
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

    public String getBatchType() {
        return BatchType;
    }

    public void setBatchType(String batchType) {
        BatchType = batchType;
    }

    public static class BatchData{

        boolean Completed;
        List<BatchModel> Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public List<BatchModel> getData() {
            return Data;
        }

        public void setData(List<BatchModel> data) {
            Data = data;
        }
    }

    public static class BatchResponseModel{

        boolean Completed;
        BatchModel Data;
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

        public BatchModel getData() {
            return Data;
        }

        public void setData(BatchModel data) {
            Data = data;
        }
    }
}
