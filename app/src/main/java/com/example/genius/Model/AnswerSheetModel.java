package com.example.genius.Model;

public class AnswerSheetModel {

    long AnsSheetID;
    TestScheduleModel TestInfo;
    BranchModel BranchInfo;
    StudentModel StudentInfo;
    String AnswerSheetContentText;
    String AnswerSheetName;
    int Status;
    String StatusText;
    String Remarks;
    RowStatusModel RowStatus;
    TransactionModel Transaction;
    String SubmitDate;

    public long getAnsSheetID() {
        return AnsSheetID;
    }

    public void setAnsSheetID(long ansSheetID) {
        AnsSheetID = ansSheetID;
    }

    public TestScheduleModel getTestInfo() {
        return TestInfo;
    }

    public void setTestInfo(TestScheduleModel testInfo) {
        TestInfo = testInfo;
    }

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
    }

    public StudentModel getStudentInfo() {
        return StudentInfo;
    }

    public void setStudentInfo(StudentModel studentInfo) {
        StudentInfo = studentInfo;
    }

    public String getAnswerSheetContentText() {
        return AnswerSheetContentText;
    }

    public void setAnswerSheetContentText(String answerSheetContentText) {
        AnswerSheetContentText = answerSheetContentText;
    }

    public String getAnswerSheetName() {
        return AnswerSheetName;
    }

    public void setAnswerSheetName(String answerSheetName) {
        AnswerSheetName = answerSheetName;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getStatusText() {
        return StatusText;
    }

    public void setStatusText(String statusText) {
        StatusText = statusText;
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

    public String getSubmitDate() {
        return SubmitDate;
    }

    public void setSubmitDate(String submitDate) {
        SubmitDate = submitDate;
    }

    public static class AnswerSheetData1{

        boolean Completed;
        AnswerSheetModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public AnswerSheetModel getData() {
            return Data;
        }

        public void setData(AnswerSheetModel data) {
            Data = data;
        }
    }
}
