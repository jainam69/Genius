package com.example.genius.Model;

import java.util.List;

public class AttendanceData {

    boolean Completed;
    List<AttendanceModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<AttendanceModel> getData() {
        return Data;
    }

    public void setData(List<AttendanceModel> data) {
        Data = data;
    }
}
