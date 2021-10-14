package com.example.genius.Model;

import java.util.List;

public class UserData1 {
    boolean Completed;
    List<UserModel> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<UserModel> getData() {
        return Data;
    }

    public void setData(List<UserModel> data) {
        Data = data;
    }
}
