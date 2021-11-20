package com.example.genius.Model;

import java.util.List;

public class SuperAdminSubjectModel {
    public long SubjectID;
    public String SubjectName;
    public TransactionModel Transaction;
    public RowStatusModel RowStatus;

    public long getSubjectID() {
        return SubjectID;
    }

    public void setSubjectID(long subjectID) {
        SubjectID = subjectID;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
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

    public class SuperAdminSubjectData{
        boolean Completed;
        List<SuperAdminSubjectModel> Data;
        String Message;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public List<SuperAdminSubjectModel> getData() {
            return Data;
        }

        public void setData(List<SuperAdminSubjectModel> data) {
            Data = data;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }
    }
}
