package com.example.genius.Model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StandardData {
    @SerializedName("Completed")
    boolean Completed;
    @SerializedName("Data")
    List<StandardModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<StandardModel> getData() {
        return Data;
    }

    public void setData(List<StandardModel> data) {
        Data = data;
    }
}
