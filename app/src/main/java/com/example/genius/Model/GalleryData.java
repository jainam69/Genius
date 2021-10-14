package com.example.genius.Model;

import java.util.List;

public class GalleryData {

    boolean Completed;
    List<GalleryModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<GalleryModel> getData() {
        return Data;
    }

    public void setData(List<GalleryModel> data) {
        Data = data;
    }
}
