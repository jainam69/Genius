package com.example.genius.Model;

import java.util.List;

public class NotificationModel {

     long NotificationID;
     List<NotificationTypeModel> NotificationType;
     BranchModel Branch;
     RowStatusModel RowStatus;
     TransactionModel Transaction;
     String NotificationMessage;
     String Notification_Date;
     String stdID;
     BranchCourseModel.BranchCourceData BranchCourse;
     List<NotificationStandardEntity> list;

    public NotificationModel() {
    }

    public NotificationModel(List<NotificationTypeModel> notificationType, BranchModel branch, RowStatusModel rowStatus, TransactionModel transaction, String notificationMessage, String noti_date, String stdID, BranchCourseModel.BranchCourceData BranchCourse) {
        NotificationType = notificationType;
        Branch = branch;
        RowStatus = rowStatus;
        Transaction = transaction;
        NotificationMessage = notificationMessage;
        Notification_Date = noti_date;
        this.stdID = stdID;
        this.BranchCourse = BranchCourse;
    }

    public NotificationModel(long notificationID, List<NotificationTypeModel> notificationType, BranchModel branch, RowStatusModel rowStatus, TransactionModel transaction, String notificationMessage,String noti_date,String stdID,BranchCourseModel.BranchCourceData BranchCourse) {
        NotificationID = notificationID;
        NotificationType = notificationType;
        Branch = branch;
        RowStatus = rowStatus;
        Transaction = transaction;
        NotificationMessage = notificationMessage;
        Notification_Date = noti_date;
        this.stdID = stdID;
        this.BranchCourse = BranchCourse;
    }

    public BranchCourseModel.BranchCourceData getBranchCourse() {
        return BranchCourse;
    }

    public void setBranchCourse(BranchCourseModel.BranchCourceData branchCourse) {
        BranchCourse = branchCourse;
    }

    public List<NotificationStandardEntity> getList() {
        return list;
    }

    public void setList(List<NotificationStandardEntity> list) {
        this.list = list;
    }

    public String getNotification_Date() {
        return Notification_Date;
    }

    public void setNotification_Date(String notification_Date) {
        Notification_Date = notification_Date;
    }

    public long getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(long notificationID) {
        NotificationID = notificationID;
    }

    public List<NotificationTypeModel> getNotificationType() {
        return NotificationType;
    }

    public void setNotificationType(List<NotificationTypeModel> notificationType) {
        NotificationType = notificationType;
    }

    public String getStdID() {
        return stdID;
    }

    public void setStdID(String stdID) {
        this.stdID = stdID;
    }

    public BranchModel getBranch() {
        return Branch;
    }

    public void setBranch(BranchModel branch) {
        Branch = branch;
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

    public String getNotificationMessage() {
        return NotificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        NotificationMessage = notificationMessage;
    }

    public static class NotificationTypeModel{
         long ID;
         String TypeText;
         int TypeID;

        public NotificationTypeModel(long ID, String typeText, int typeID) {
            this.ID = ID;
            TypeText = typeText;
            TypeID = typeID;
        }

        public NotificationTypeModel(String typeText, int typeID) {
            TypeText = typeText;
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

    public static class NotificationData1{

        boolean Completed;
        NotificationModel Data;
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

        public NotificationModel getData() {
            return Data;
        }

        public void setData(NotificationModel data) {
            Data = data;
        }
    }

    public static class NotificationStandardEntity{

        public long notification_std_id;
        public long std_id;
        public long sub_id;
        public String standard;
        public String subject;
        public long notif_id;
        public BranchCourseModel.BranchCourceData BranchCourse;
    }
}
