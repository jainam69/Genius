package com.example.genius.Model;

import java.util.List;

public class PaperData {

    long UniqueID;
    long PaperID;
    String PaperPath;
    String PaperContentText;
    String FilePath;
    boolean Completed;
    List<PaperModel> Data;

    public PaperData(long uniqueID, String paperPath, String filePath) {
        UniqueID = uniqueID;
        PaperPath = paperPath;
        FilePath = filePath;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public long getUniqueID() {
        return UniqueID;
    }

    public void setUniqueID(long uniqueID) {
        UniqueID = uniqueID;
    }

    public long getPaperID() {
        return PaperID;
    }

    public void setPaperID(long paperID) {
        PaperID = paperID;
    }

    public String getPaperPath() {
        return PaperPath;
    }

    public void setPaperPath(String paperPath) {
        PaperPath = paperPath;
    }

    public String getPaperContentText() {
        return PaperContentText;
    }

    public void setPaperContentText(String paperContentText) {
        PaperContentText = paperContentText;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<PaperModel> getData() {
        return Data;
    }

    public void setData(List<PaperModel> data) {
        Data = data;
    }
}
