package com.example.genius.Model;

import java.io.Serializable;
import java.net.PortUnreachableException;
import java.util.List;

public class FacultyModel implements Serializable {

    long FacultyID;
    UserModel user;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    BranchModel BranchInfo;
    StaffModel staff;
    SubjectModel subject;
    StandardModel standard;
    String Descripation;
    String FacultyContentFileName;
    String FilePath;
    BranchSubjectModel.BranchSubjectData branchSubject;
    BranchCourseModel.BranchCourceData BranchCourse;
    BranchClassSingleModel.BranchClassData BranchClass;

    public long getFacultyID() {
        return FacultyID;
    }

    public void setFacultyID(long facultyID) {
        FacultyID = facultyID;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public TransactionModel getTransaction() {
        return Transaction;
    }

    public void setTransaction(TransactionModel transaction) {
        Transaction = transaction;
    }

    public RowStatusModel getRowStatus() {
        return RowStatus;
    }

    public void setRowStatus(RowStatusModel rowStatus) {
        RowStatus = rowStatus;
    }

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
    }

    public StaffModel getStaff() {
        return staff;
    }

    public void setStaff(StaffModel staff) {
        this.staff = staff;
    }

    public SubjectModel getSubject() {
        return subject;
    }

    public void setSubject(SubjectModel subject) {
        this.subject = subject;
    }

    public StandardModel getStandard() {
        return standard;
    }

    public void setStandard(StandardModel standard) {
        this.standard = standard;
    }

    public String getDescripation() {
        return Descripation;
    }

    public void setDescripation(String descripation) {
        Descripation = descripation;
    }

    public String getFacultyContentFileName() {
        return FacultyContentFileName;
    }

    public void setFacultyContentFileName(String facultyContentFileName) {
        FacultyContentFileName = facultyContentFileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public BranchSubjectModel.BranchSubjectData getBranchSubject() {
        return branchSubject;
    }

    public void setBranchSubject(BranchSubjectModel.BranchSubjectData branchSubject) {
        this.branchSubject = branchSubject;
    }

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

    public static class FacultyData
    {
        List<FacultyModel> Data;
        boolean Completed;

        public List<FacultyModel> getData() {
            return Data;
        }

        public void setData(List<FacultyModel> data) {
            Data = data;
        }

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }
    }

    public static class FacultyModelData
    {
        FacultyModel Data;
        boolean Completed;
        String Message;

        public FacultyModel getData() {
            return Data;
        }

        public void setData(FacultyModel data) {
            Data = data;
        }

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }
    }
}
