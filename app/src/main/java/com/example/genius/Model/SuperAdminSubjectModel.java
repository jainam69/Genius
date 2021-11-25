package com.example.genius.Model;

import java.util.List;

public class SuperAdminSubjectModel {

    boolean Completed;
    List<SuperAdminSubjectData> Data;
    String Message;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<SuperAdminSubjectData> getData() {
        return Data;
    }

    public void setData(List<SuperAdminSubjectData> data) {
        Data = data;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public static class SuperAdminSubjectData{

        public long SubjectID;
        public String SubjectName;
        public TransactionModel Transaction;
        public RowStatusModel RowStatus;
        public boolean isSubject;

        public SuperAdminSubjectData(long subjectID, String subjectName, boolean isSubject) {
            SubjectID = subjectID;
            SubjectName = subjectName;
            this.isSubject = isSubject;
        }

        public boolean isSubject() {
            return isSubject;
        }

        public void setSubject(boolean subject) {
            isSubject = subject;
        }

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

    }
}
