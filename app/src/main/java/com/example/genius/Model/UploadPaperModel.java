package com.example.genius.Model;

import android.inputmethodservice.Keyboard;

public class UploadPaperModel {

    long TestPaperID;
    long TestID;
    String TestName;
    String TestDate;
    int PaperTypeID;
    String PaperType;
    String DocContentText;
    String FileName;
    String FilePath;
    String DocLink;
    String Remarks;
    RowStatusModel RowStatus;
    TransactionModel Transaction;

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public UploadPaperModel(long testPaperID, long testID, String testName, String testDate, int paperTypeID, String paperType, String docContentText, String fileName, String docLink, String remarks, RowStatusModel rowStatus, TransactionModel transaction) {
        TestPaperID = testPaperID;
        TestID = testID;
        TestName = testName;
        TestDate = testDate;
        PaperTypeID = paperTypeID;
        PaperType = paperType;
        DocContentText = docContentText;
        FileName = fileName;
        DocLink = docLink;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
    }

    public UploadPaperModel(long testID, String testName, String testDate, int paperTypeID, String paperType, String docContentText, String fileName, String docLink, String remarks, RowStatusModel rowStatus, TransactionModel transaction) {
        TestID = testID;
        TestName = testName;
        TestDate = testDate;
        PaperTypeID = paperTypeID;
        PaperType = paperType;
        DocContentText = docContentText;
        FileName = fileName;
        DocLink = docLink;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
    }

    public long getTestPaperID() {
        return TestPaperID;
    }

    public void setTestPaperID(long testPaperID) {
        TestPaperID = testPaperID;
    }

    public long getTestID() {
        return TestID;
    }

    public void setTestID(long testID) {
        TestID = testID;
    }

    public String getTestName() {
        return TestName;
    }

    public void setTestName(String testName) {
        TestName = testName;
    }

    public String getTestDate() {
        return TestDate;
    }

    public void setTestDate(String testDate) {
        TestDate = testDate;
    }

    public int getPaperTypeID() {
        return PaperTypeID;
    }

    public void setPaperTypeID(int paperTypeID) {
        PaperTypeID = paperTypeID;
    }

    public String getPaperType() {
        return PaperType;
    }

    public void setPaperType(String paperType) {
        PaperType = paperType;
    }

    public String getDocContentText() {
        return DocContentText;
    }

    public void setDocContentText(String docContentText) {
        DocContentText = docContentText;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getDocLink() {
        return DocLink;
    }

    public void setDocLink(String docLink) {
        DocLink = docLink;
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

    public static class UploadPaperData1 {

        boolean Completed;
        UploadPaperModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public UploadPaperModel getData() {
            return Data;
        }

        public void setData(UploadPaperModel data) {
            Data = data;
        }
    }
}
