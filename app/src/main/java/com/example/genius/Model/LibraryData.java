package com.example.genius.Model;

import java.util.List;

public class LibraryData {

    boolean Completed;
    List<LibraryModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<LibraryModel> getData() {
        return Data;
    }

    public void setData(List<LibraryModel> data) {
        Data = data;
    }

}

