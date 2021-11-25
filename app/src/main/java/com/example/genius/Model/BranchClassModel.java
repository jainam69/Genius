package com.example.genius.Model;

import java.util.List;

public class BranchClassModel {

    List<BranchClassSingleModel.BranchClassData> Data;
    private String Message;
    private Boolean Completed;

    public List<BranchClassSingleModel.BranchClassData> getData() {
        return Data;
    }

    public void setData(List<BranchClassSingleModel.BranchClassData> data) {
        Data = data;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Boolean getCompleted() {
        return Completed;
    }

    public void setCompleted(Boolean completed) {
        Completed = completed;
    }

}
