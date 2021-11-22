package com.example.genius.Model;

import java.util.List;

public class CourceModel {

    private List<CourceData> Data;
    private String Message;
    private Boolean Completed;

    public List<CourceData> getData() {
        return Data;
    }

    public void setData(List<CourceData> data) {
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

    public static class CourceData {

        public long CourseID;
        public String CourseName;
        Boolean iscourse;

        public CourceData(Boolean iscourse) {
            this.iscourse = iscourse;
        }

        public Boolean getIscourse() {
            return iscourse;
        }

        public void setIscourse(Boolean iscourse) {
            this.iscourse = iscourse;
        }

        public CourceData(long courseID) {
            CourseID = courseID;
        }

        public long getCourseID() {
            return CourseID;
        }

        public void setCourseID(long courseID) {
            CourseID = courseID;
        }

        public String getCourseName() {
            return CourseName;
        }

        public void setCourseName(String courseName) {
            CourseName = courseName;
        }

    }
}
