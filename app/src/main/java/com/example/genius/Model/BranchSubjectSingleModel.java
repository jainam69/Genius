package com.example.genius.Model;

public class BranchSubjectSingleModel {

    private BranchSubjectModel.BranchSubjectData Data;
    private String Message;
    private Boolean Completed;

    public BranchSubjectModel.BranchSubjectData getData() {
        return Data;
    }

    public void setData(BranchSubjectModel.BranchSubjectData data) {
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
