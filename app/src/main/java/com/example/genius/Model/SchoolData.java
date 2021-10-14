package com.example.genius.Model;

import java.util.List;

public class SchoolData{
    boolean Completed;
    List<SchoolModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<SchoolModel> getData() {
        return Data;
    }

    public void setData(List<SchoolModel> data) {
        Data = data;
    }
}
