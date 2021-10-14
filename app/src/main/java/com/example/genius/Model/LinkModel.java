package com.example.genius.Model;

public class LinkModel {
    long UniqueID;
    BranchModel Branch;
    long StandardID;
    String LinkDesc;
    String LinkURL;
    RowStatusModel RowStatus;
    TransactionModel Transaction;
    String Title;
    String StandardName;

    public LinkModel(long uniqueID, BranchModel branch, long standardID, String linkURL, RowStatusModel rowStatus, TransactionModel transaction, String title) {
        UniqueID = uniqueID;
        Branch = branch;
        StandardID = standardID;
        LinkURL = linkURL;
        RowStatus = rowStatus;
        Transaction = transaction;
        Title = title;
    }

    public LinkModel(BranchModel branch, long standardID, String linkURL, RowStatusModel rowStatus, TransactionModel transaction, String title) {
        Branch = branch;
        StandardID = standardID;
        LinkURL = linkURL;
        RowStatus = rowStatus;
        Transaction = transaction;
        Title = title;
    }

    public LinkModel(long uniqueID, BranchModel branch, long standardID, String linkDesc, String linkURL, RowStatusModel rowStatus, TransactionModel transaction,String title) {
        UniqueID = uniqueID;
        Branch = branch;
        StandardID = standardID;
        LinkDesc = linkDesc;
        LinkURL = linkURL;
        RowStatus = rowStatus;
        Transaction = transaction;
        Title = title;
    }

    public LinkModel(BranchModel branch, long standardID, String linkDesc, String linkURL, RowStatusModel rowStatus, TransactionModel transaction, String title) {
        Branch = branch;
        StandardID = standardID;
        LinkDesc = linkDesc;
        LinkURL = linkURL;
        RowStatus = rowStatus;
        Transaction = transaction;
        Title = title;
    }

    public String getStandardName() {
        return StandardName;
    }

    public void setStandardName(String standardName) {
        StandardName = standardName;
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

    public long getStandardID() {
        return StandardID;
    }

    public void setStandardID(long standardID) {
        StandardID = standardID;
    }

    public String getLinkDesc() {
        return LinkDesc;
    }

    public void setLinkDesc(String linkDesc) {
        LinkDesc = linkDesc;
    }

    public String getLinkURL() {
        return LinkURL;
    }

    public void setLinkURL(String linkURL) {
        LinkURL = linkURL;
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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public static class LinkData1 {
        boolean Completed;
        LinkModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public LinkModel getData() {
            return Data;
        }

        public void setData(LinkModel data) {
            Data = data;
        }
    }
}
