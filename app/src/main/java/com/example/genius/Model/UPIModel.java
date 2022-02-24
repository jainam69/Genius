package com.example.genius.Model;

import java.util.List;

public class UPIModel {

    public long UPIId;
    public String UPICode;
    public BranchModel BranchData;
    public RowStatusModel RowStatusData;
    public TransactionModel TransactionData;

    public UPIModel(long UPIId, String UPICode, BranchModel branchData, RowStatusModel rowStatusData, TransactionModel transactionData) {
        this.UPIId = UPIId;
        this.UPICode = UPICode;
        BranchData = branchData;
        RowStatusData = rowStatusData;
        TransactionData = transactionData;
    }

    public static class UPIData
    {
        boolean Completed;
        List<UPIModel> Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public List<UPIModel> getData() {
            return Data;
        }

        public void setData(List<UPIModel> data) {
            Data = data;
        }
    }

    public static class UPIResponse
    {
        boolean Completed;
        long Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public long getData() {
            return Data;
        }

        public void setData(long data) {
            Data = data;
        }
    }
}
