package com.example.genius.Model;

import java.util.HashMap;

public class UserRolesModel {
    boolean Completed;
    HashMap<String,Integer> Data;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public HashMap<String, Integer> getData() {
        return Data;
    }

    public void setData(HashMap<String, Integer> data) {
        Data = data;
    }
}
