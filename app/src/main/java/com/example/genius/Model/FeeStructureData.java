package com.example.genius.Model;

import java.util.List;

public class FeeStructureData {

    boolean Completed;
    List<FeeStructureModel> Data;
    String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<FeeStructureModel> getData() {
        return Data;
    }

    public void setData(List<FeeStructureModel> data) {
        Data = data;
    }
}
