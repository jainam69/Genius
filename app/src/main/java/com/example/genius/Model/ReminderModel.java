package com.example.genius.Model;

import java.util.List;

public class ReminderModel {
     long ReminderID;
     BranchModel BranchInfo;
     long UserID;
     String Username;
     String ReminderDate;
     String ReminderTime;
     String ReminderDesc;
     TransactionModel Transaction;
     RowStatusModel RowStatus;

    public ReminderModel(BranchModel branchInfo, long userID, String username, String reminderDate, String reminderTime, String reminderDesc, TransactionModel transaction, RowStatusModel rowStatus) {
        BranchInfo = branchInfo;
        UserID = userID;
        Username = username;
        ReminderDate = reminderDate;
        ReminderTime = reminderTime;
        ReminderDesc = reminderDesc;
        Transaction = transaction;
        RowStatus = rowStatus;
    }

    public ReminderModel(long reminderID, BranchModel branchInfo, long userID, String username, String reminderDate, String reminderTime, String reminderDesc, TransactionModel transaction, RowStatusModel rowStatus) {
        ReminderID = reminderID;
        BranchInfo = branchInfo;
        UserID = userID;
        Username = username;
        ReminderDate = reminderDate;
        ReminderTime = reminderTime;
        ReminderDesc = reminderDesc;
        Transaction = transaction;
        RowStatus = rowStatus;
    }

    public long getReminderID() {
        return ReminderID;
    }

    public void setReminderID(long reminderID) {
        ReminderID = reminderID;
    }

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getReminderDate() {
        return ReminderDate;
    }

    public void setReminderDate(String reminderDate) {
        ReminderDate = reminderDate;
    }

    public String getReminderTime() {
        return ReminderTime;
    }

    public void setReminderTime(String reminderTime) {
        ReminderTime = reminderTime;
    }

    public String getReminderDesc() {
        return ReminderDesc;
    }

    public void setReminderDesc(String reminderDesc) {
        ReminderDesc = reminderDesc;
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
    public class ReminderData1{
        boolean Completed;
        ReminderModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public ReminderModel getData() {
            return Data;
        }

        public void setData(ReminderModel data) {
            Data = data;
        }
    }
}
