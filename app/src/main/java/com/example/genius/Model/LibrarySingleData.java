package com.example.genius.Model;

public class LibrarySingleData {

    boolean Completed;
    LibraryModel Data;
    String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public LibraryModel getData() {
        return Data;
    }

    public void setData(LibraryModel data) {
        Data = data;
    }

}
