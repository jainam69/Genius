package com.example.genius.Model;

public class TransactionModel {

    long TransactionId;
    String CreatedDate;
    String CreatedBy;
    long CreatedId;
    String LastUpdateDate;
    String LastUpdateBy;
    long LastUpdateId;

    public TransactionModel(long transactionId, String createdBy, long createdId, String lastUpdateBy, long lastUpdateId) {
        TransactionId = transactionId;
        CreatedBy = createdBy;
        CreatedId = createdId;
        LastUpdateBy = lastUpdateBy;
        LastUpdateId = lastUpdateId;
    }

    public TransactionModel() {
    }

    public TransactionModel(long transactionId, String lastUpdateBy, long lastUpdateId) {
        TransactionId = transactionId;
        LastUpdateBy = lastUpdateBy;
        LastUpdateId = lastUpdateId;
    }

    public TransactionModel(String createdBy, long createdId, String lastUpdateDate) {
        CreatedBy = createdBy;
        CreatedId = createdId;
        LastUpdateDate = lastUpdateDate;
    }


    public long getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(long transactionId) {
        TransactionId = transactionId;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public long getCreatedId() {
        return CreatedId;
    }

    public void setCreatedId(long createdId) {
        CreatedId = createdId;
    }

    public String getLastUpdateDate() {
        return LastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        LastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdateBy() {
        return LastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        LastUpdateBy = lastUpdateBy;
    }

    public long getLastUpdateId() {
        return LastUpdateId;
    }

    public void setLastUpdateId(long lastUpdateId) {
        LastUpdateId = lastUpdateId;
    }
}
