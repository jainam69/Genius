package com.example.genius.Model;

import java.io.Serializable;

public class RowStatusModel implements Serializable {

    int RowStatusId;
    String RowStatusText;
    int RowStatus;

    public RowStatusModel() {
    }

    public RowStatusModel(int rowStatusId) {
        RowStatusId = rowStatusId;
    }

    public RowStatusModel(int rowStatusId, String RowStatusText) {
        RowStatusId = rowStatusId;
        this.RowStatusText = RowStatusText;
    }

    public int getRowStatusId() {
        return RowStatusId;
    }

    public void setRowStatusId(int rowStatusId) {
        RowStatusId = rowStatusId;
    }

    public String getRowStatusText() {
        return RowStatusText;
    }

    public void setRowStatusText(String rowStatusText) {
        RowStatusText = rowStatusText;
    }

    public int getRowStatus() {
        return RowStatus;
    }

    public void setRowStatus(int rowStatus) {
        RowStatus = rowStatus;
    }
}
