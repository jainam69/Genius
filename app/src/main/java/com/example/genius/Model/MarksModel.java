package com.example.genius.Model;

import java.util.List;

public class MarksModel {

    long MarksID;
    StudentModel student;
    BranchModel branch;
    String MarksDate;
    String AchieveMarks;
    String MarksContentFileName;
    String MarksFilepath;
    TestScheduleModel testmodel;
    long TestID;
    String TestDate;

    public long getTestID() {
        return TestID;
    }

    public void setTestID(long testID) {
        TestID = testID;
    }

    public String getTestDate() {
        return TestDate;
    }

    public void setTestDate(String testDate) {
        TestDate = testDate;
    }

    public long getMarksID() {
        return MarksID;
    }

    public void setMarksID(long marksID) {
        MarksID = marksID;
    }

    public StudentModel getStudent() {
        return student;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
    }

    public BranchModel getBranch() {
        return branch;
    }

    public void setBranch(BranchModel branch) {
        this.branch = branch;
    }

    public String getMarksDate() {
        return MarksDate;
    }

    public void setMarksDate(String marksDate) {
        MarksDate = marksDate;
    }

    public String getAchieveMarks() {
        return AchieveMarks;
    }

    public void setAchieveMarks(String achieveMarks) {
        AchieveMarks = achieveMarks;
    }

    public String getMarksContentFileName() {
        return MarksContentFileName;
    }

    public void setMarksContentFileName(String marksContentFileName) {
        MarksContentFileName = marksContentFileName;
    }

    public String getMarksFilepath() {
        return MarksFilepath;
    }

    public void setMarksFilepath(String marksFilepath) {
        MarksFilepath = marksFilepath;
    }

    public TestScheduleModel getTestmodel() {
        return testmodel;
    }

    public void setTestmodel(TestScheduleModel testmodel) {
        this.testmodel = testmodel;
    }

    public static class MarksData
    {
        List<MarksModel> Data;
        String Message;
        boolean Completed;

        public List<MarksModel> getData() {
            return Data;
        }

        public void setData(List<MarksModel> data) {
            Data = data;
        }

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
    }
}
