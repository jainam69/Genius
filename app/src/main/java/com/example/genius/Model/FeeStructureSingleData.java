package com.example.genius.Model;

public class FeeStructureSingleData {
    boolean Completed;
    FeeStructureModel Data;
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

    public FeeStructureModel getData() {
        return Data;
    }

    public void setData(FeeStructureModel data) {
        Data = data;
    }
}
