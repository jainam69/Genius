package com.example.genius.Model;

import java.util.List;

public class HomeworkData {

    boolean Completed;
    List<HomeworkModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<HomeworkModel> getData() {
        return Data;
    }

    public void setData(List<HomeworkModel> data) {
        Data = data;
    }

}
