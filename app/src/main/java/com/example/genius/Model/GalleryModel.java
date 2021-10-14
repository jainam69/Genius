package com.example.genius.Model;

public class GalleryModel {

    long UniqueID;
    BranchModel Branch;
    String FileEncoded;
    String Remarks;
    RowStatusModel RowStatus;
    TransactionModel Transaction;
    int GalleryType;

    public GalleryModel(long uniqueID, BranchModel branch, String fileEncoded, String remarks, RowStatusModel rowStatus, TransactionModel transaction) {
        UniqueID = uniqueID;
        Branch = branch;
        FileEncoded = fileEncoded;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
    }

    public GalleryModel(BranchModel branch, String fileEncoded, String remarks, RowStatusModel rowStatus, TransactionModel transaction) {
        Branch = branch;
        FileEncoded = fileEncoded;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
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
    }
}
