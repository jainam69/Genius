package com.example.genius.Model;

import java.util.List;

public class TodoModel {

     long ToDoID;
     String ToDoDate;
     BranchModel BranchInfo;
     UserModel UserInfo;
     String ToDoDescription;
     String ToDoContentText;
     String ToDoFileName;
     RowStatusModel RowStatus;
     TransactionModel Transaction;
     String FilePath;

    public TodoModel(long toDoID, String toDoDate, BranchModel branchInfo, UserModel userInfo, String toDoDescription, String toDoFileName, RowStatusModel rowStatus, TransactionModel transaction, String filePath) {
        ToDoID = toDoID;
        ToDoDate = toDoDate;
        BranchInfo = branchInfo;
        UserInfo = userInfo;
        ToDoDescription = toDoDescription;
        ToDoFileName = toDoFileName;
        RowStatus = rowStatus;
        Transaction = transaction;
        FilePath = filePath;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public long getToDoID() {
        return ToDoID;
    }

    public void setToDoID(long toDoID) {
        ToDoID = toDoID;
    }

    public String getToDoDate() {
        return ToDoDate;
    }

    public void setToDoDate(String toDoDate) {
        ToDoDate = toDoDate;
    }

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
    }

    public UserModel getUserInfo() {
        return UserInfo;
    }

    public void setUserInfo(UserModel userInfo) {
        UserInfo = userInfo;
    }

    public String getToDoDescription() {
        return ToDoDescription;
    }

    public void setToDoDescription(String toDoDescription) {
        ToDoDescription = toDoDescription;
    }

    public String getToDoContentText() {
        return ToDoContentText;
    }

    public void setToDoContentText(String toDoContentText) {
        ToDoContentText = toDoContentText;
    }

    public String getToDoFileName() {
        return ToDoFileName;
    }

    public void setToDoFileName(String toDoFileName) {
        ToDoFileName = toDoFileName;
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

    public class TodoData1{
       boolean Completed;
       TodoModel Data;
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

       public TodoModel getData() {
           return Data;
       }

       public void setData(TodoModel data) {
           Data = data;
       }
   }
}
