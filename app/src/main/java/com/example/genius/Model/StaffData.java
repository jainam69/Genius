package com.example.genius.Model;

import java.util.List;

public class StaffData {
    boolean Completed;
    List<StaffModel> Data;
StaffModel data1;

    public StaffModel getData1() {
        return data1;
    }

    public void setData1(StaffModel data1) {
        this.data1 = data1;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<StaffModel> getData() {
        return Data;
    }

    public void setData(List<StaffModel> data) {
        Data = data;
    }
}
