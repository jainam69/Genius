package com.example.genius.Model;

public class GalleryModel {

    long UniqueID;
    BranchModel Branch;
    String FileEncoded;
    String Remarks;
    RowStatusModel RowStatus;
    TransactionModel Transaction;
    int GalleryType;
    String FilePath;
    String FileName;

    public GalleryModel(long uniqueID, BranchModel branch, String remarks, RowStatusModel rowStatus, TransactionModel transaction, int galleryType, String filePath, String fileName) {
        UniqueID = uniqueID;
        Branch = branch;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
        GalleryType = galleryType;
        FilePath = filePath;
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public long getUniqueID() {
        return UniqueID;
    }

    public void setUniqueID(long uniqueID) {
        UniqueID = uniqueID;
    }

    public BranchModel getBranch() {
        return Branch;
    }

    public void setBranch(BranchModel branch) {
        Branch = branch;
    }

    public String getFileEncoded() {
        return FileEncoded;
    }

    public void setFileEncoded(String fileEncoded) {
        FileEncoded = fileEncoded;
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

    public int getGalleryType() {
        return GalleryType;
    }

    public void setGalleryType(int galleryType) {
        GalleryType = galleryType;
    }

    public static class GallaryData1{

        boolean Completed;
        GalleryModel Data;
        String Message;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public GalleryModel getData() {
            return Data;
        }

        public void setData(GalleryModel data) {
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
