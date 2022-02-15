package com.example.genius.Model;

public class AnnouncementSingleModel {

    boolean Completed;
    String Message;
    AnnouncementModel.AnnouncementData Data;

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

    public AnnouncementModel.AnnouncementData getData() {
        return Data;
    }

    public void setData(AnnouncementModel.AnnouncementData data) {
        Data = data;
    }

    public static class AnnouncementData {

        public long AnnouncementID;
        public BranchModel BranchData;
        public TransactionModel TransactionData;
        public RowStatusModel RowStatusData;
        public String AnnouncementText;

        public long getAnnouncementID() {
            return AnnouncementID;
        }

        public void setAnnouncementID(long announcementID) {
            AnnouncementID = announcementID;
        }

        public BranchModel getBranchData() {
            return BranchData;
        }

        public void setBranchData(BranchModel branchData) {
            BranchData = branchData;
        }

        public TransactionModel getTransactionData() {
            return TransactionData;
        }

        public void setTransactionData(TransactionModel transactionData) {
            TransactionData = transactionData;
        }

        public RowStatusModel getRowStatusData() {
            return RowStatusData;
        }

        public void setRowStatusData(RowStatusModel rowStatusData) {
            RowStatusData = rowStatusData;
        }

        public String getAnnouncementText() {
            return AnnouncementText;
        }

        public void setAnnouncementText(String announcementText) {
            AnnouncementText = announcementText;
        }
    }
}
