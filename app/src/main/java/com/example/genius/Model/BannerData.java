package com.example.genius.Model;

import java.util.List;

public class BannerData {

    boolean Completed;
    List<BannerModel> Data;



    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<BannerModel> getData() {
        return Data;
    }

    public void setData(List<BannerModel> data) {
        Data = data;
    }
}
