package com.example.genius.Model;

import java.util.List;

public class BannerModel {

    long BannerID;
    String BannerImageText;
    List<BannerTypeEntity> BannerType;
    BranchModel BranchInfo;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    String FilePath;
    String FileName;

    public BannerModel(long bannerID, List<BannerTypeEntity> bannerType, BranchModel branch, TransactionModel transaction, RowStatusModel rowStatus, String filePath, String fileName) {
        BannerID = bannerID;
        BannerType = bannerType;
        BranchInfo = branch;
        Transaction = transaction;
        RowStatus = rowStatus;
        FilePath = filePath;
        FileName = fileName;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public BranchModel getBranch() {
        return BranchInfo;
    }

    public void setBranch(BranchModel branch) {
        BranchInfo = branch;
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

    public long getBannerID() {
        return BannerID;
    }

    public void setBannerID(long bannerID) {
        BannerID = bannerID;
    }

    public String getBannerImageText() {
        return BannerImageText;
    }

    public void setBannerImageText(String bannerImageText) {
        BannerImageText = bannerImageText;
    }

    public List<BannerTypeEntity> getBannerType() {
        return BannerType;
    }

    public void setBannerType(List<BannerTypeEntity> bannerType) {
        BannerType = bannerType;
    }

    public static class BannerTypeEntity{

        long ID;
        String TypeText;
        int TypeID;

        public BannerTypeEntity() {
        }

        public BannerTypeEntity(long ID, int typeID) {
            this.ID = ID;
            TypeID = typeID;
        }

        public BannerTypeEntity(int typeID) {
            TypeID = typeID;
        }

        public long getID() {
            return ID;
        }

        public void setID(long ID) {
            this.ID = ID;
        }

        public String getTypeText() {
            return TypeText;
        }

        public void setTypeText(String typeText) {
            TypeText = typeText;
        }

        public int getTypeID() {
            return TypeID;
        }

        public void setTypeID(int typeID) {
            TypeID = typeID;
        }
    }

    public static class BannerlData1 {

        boolean Completed;
        BannerModel Data;
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

        public BannerModel getData() {
            return Data;
        }

        public void setData(BannerModel data) {
            Data = data;
        }
    }
}
