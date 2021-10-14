package com.example.genius.Model;

import java.util.List;

public class SubjectData {
    boolean Completed;
    List<SubjectModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<SubjectModel> getData() {
        return Data;
    }

    public void setData(List<SubjectModel> data) {
        Data = data;
    }
}
