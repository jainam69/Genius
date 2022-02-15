package com.example.genius.Model;

import java.util.List;

public class MarksModel {

    long MarksID;
    StudentModel student;
    BranchModel branch;
    SubjectModel SubjectInfo;
    String MarksDate;
    String AchieveMarks;
    String MarksContentFileName;
    String MarksFilepath;
    TestScheduleModel testEntityInfo;
    long TestID;
    String TestDate;
    BranchCourseModel.BranchCourceData BranchCourse;
    BranchClassSingleModel.BranchClassData BranchClass;
    BranchSubjectModel.BranchSubjectData BranchSubject;

    public BranchCourseModel.BranchCourceData getBranchCourse() {
        return BranchCourse;
    }

    public void setBranchCourse(BranchCourseModel.BranchCourceData branchCourse) {
        BranchCourse = branchCourse;
    }

    public BranchClassSingleModel.BranchClassData getBranchClass() {
        return BranchClass;
    }

    public void setBranchClass(BranchClassSingleModel.BranchClassData branchClass) {
        BranchClass = branchClass;
    }

    public BranchSubjectModel.BranchSubjectData getBranchSubject() {
        return BranchSubject;
    }

    public void setBranchSubject(BranchSubjectModel.BranchSubjectData branchSubject) {
        BranchSubject = branchSubject;
    }

    public SubjectModel getSubjectInfo() {
        return SubjectInfo;
    }

    public void setSubjectInfo(SubjectModel subjectInfo) {
        SubjectInfo = subjectInfo;
    }

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

    public TestScheduleModel getTestEntityInfo() {
        return testEntityInfo;
    }

    public void setTestEntityInfo(TestScheduleModel testEntityInfo) {
        this.testEntityInfo = testEntityInfo;
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

    public static class MarksData1
    {
        MarksModel Data;
        String Message;
        boolean Completed;

        public MarksModel getData() {
            return Data;
        }

        public void setData(MarksModel data) {
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
