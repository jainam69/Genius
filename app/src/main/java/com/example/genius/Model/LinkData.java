package com.example.genius.Model;

import java.util.List;

public class LinkData {
    boolean Completed;
    List<LinkModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<LinkModel> getData() {
        return Data;
    }

    public void setData(List<LinkModel> data) {
        Data = data;
    }
}
