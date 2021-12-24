package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {
    @SerializedName("Completed")
    boolean Completed;
    @SerializedName("Message")
    String Message;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
