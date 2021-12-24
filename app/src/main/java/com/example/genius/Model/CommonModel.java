package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;

public class CommonModel {
    @SerializedName("Completed")
    boolean Completed;
    @SerializedName("Data")
    boolean Data;
    @SerializedName("Message")
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

    public boolean isData() {
        return Data;
    }

    public void setData(boolean data) {
        Data = data;
    }
}
