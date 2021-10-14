package com.example.genius.Model;

import java.util.List;

public class ReminderData {
    boolean Completed;
    List<ReminderModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<ReminderModel> getData() {
        return Data;
    }

    public void setData(List<ReminderModel> data) {
        Data = data;
    }
}
