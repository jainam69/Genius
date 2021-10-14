package com.example.genius.Model;

import java.util.List;

public class NotificationData {
    boolean Completed;
    List<NotificationModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<NotificationModel> getData() {
        return Data;
    }

    public void setData(List<NotificationModel> data) {
        Data = data;
    }
}
