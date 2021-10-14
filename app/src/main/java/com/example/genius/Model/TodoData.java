package com.example.genius.Model;

import java.util.List;

public class TodoData {
    boolean Completed;
    List<TodoModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<TodoModel> getData() {
        return Data;
    }

    public void setData(List<TodoModel> data) {
        Data = data;
    }
}
