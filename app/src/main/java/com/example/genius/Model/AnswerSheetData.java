package com.example.genius.Model;

import java.util.List;

public class AnswerSheetData {

    boolean Completed;
    List<AnswerSheetModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<AnswerSheetModel> getData() {
        return Data;
    }

    public void setData(List<AnswerSheetModel> data) {
        Data = data;
    }
}
