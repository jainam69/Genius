package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryData {
    @SerializedName("Completed")
    boolean Completed;
    @SerializedName("Data")
    List<CategoryModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<CategoryModel> getData() {
        return Data;
    }

    public void setData(List<CategoryModel> data) {
        Data = data;
    }
}
