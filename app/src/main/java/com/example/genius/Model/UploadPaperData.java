package com.example.genius.Model;

import java.util.List;

public class UploadPaperData {

    boolean Completed;
    List<UploadPaperModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<UploadPaperModel> getData() {
        return Data;
    }

    public void setData(List<UploadPaperModel> data) {
        Data = data;
    }
}
