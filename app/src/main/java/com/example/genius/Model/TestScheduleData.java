package com.example.genius.Model;

import java.util.List;

public class TestScheduleData {

    boolean Completed;
    List<TestScheduleModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<TestScheduleModel> getData() {
        return Data;
    }

    public void setData(List<TestScheduleModel> data) {
        Data = data;
    }
}
