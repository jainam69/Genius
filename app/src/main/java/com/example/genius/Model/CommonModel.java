package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;

public class CommonModel {

    @SerializedName("Completed")
    boolean Completed;
    @SerializedName("Data")
    boolean Data;
    @SerializedName("Message")
    String Message;
    @SerializedName("Status")
    boolean Status;

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

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

    public boolean isData() {
        return Data;
    }

    public void setData(boolean data) {
        Data = data;
    }

    public static class ResponseModel
    {
        CommonModel Data;
        boolean Completed;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public CommonModel getData() {
            return Data;
        }

        public void setData(CommonModel data) {
            Data = data;
        }
    }
}
