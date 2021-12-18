package com.example.genius.Model;

import java.io.Serializable;
import java.util.List;

public class ClassModel {

    private String Message;
    private Boolean Completed;

    private List<ClassData> Data;

    public List<ClassData> getData() {
        return Data;
    }

    public void setData(List<ClassData> data) {
        Data = data;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Boolean getCompleted() {
        return Completed;
    }

    public void setCompleted(Boolean completed) {
        Completed = completed;
    }

    public static class ClassData implements Serializable {

        long ClassID;
        String ClassName;
        Boolean iscourse = false;

        public ClassData() {
        }

        public ClassData(long courseID) {
            ClassID = courseID;
        }

        public ClassData(Boolean iscourse) {
            this.iscourse = iscourse;
        }

        public ClassData(long courseID, String courseName, Boolean iscourse) {
            this.iscourse = iscourse;
            this.ClassID = courseID;
            this.ClassName = courseName;
        }

        public ClassData(long classID, String className) {
            ClassID = classID;
            ClassName = className;
        }

        public Boolean getIscourse() {
            return iscourse;
        }

        public void setIscourse(Boolean iscourse) {
            this.iscourse = iscourse;
        }

        public long getClassID() {
            return ClassID;
        }

        public void setClassID(long classID) {
            ClassID = classID;
        }

        public String getClassName() {
            return ClassName;
        }

        public void setClassName(String className) {
            ClassName = className;
        }

    }
}
