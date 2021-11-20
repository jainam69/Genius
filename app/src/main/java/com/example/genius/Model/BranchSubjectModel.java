package com.example.genius.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BranchSubjectModel {
    public long Class_dtl_id;
    public long Subject_dtl_id;
    public String JsonData;
    public BranchModel branch;
    @SerializedName("Class")
    public ClassModel ClassModel;
    public BranchSubjectModel branchSubject;
    public BranchCourseModel BranchCourse;
    public BranchClassModel BranchClass;
    public TransactionModel Transaction;
    public RowStatusModel RowStatus;
    public List<BranchSubjectModel> BranchSubjectData;
    public List<SuperAdminSubjectModel> SubjectData;
    public List<BranchCourseModel> BranchClassList;
    public List<ClassModel> ClassData;
    public Boolean isClass;
    public Boolean isSubject;

    public long getClass_dtl_id() {
        return Class_dtl_id;
    }

    public void setClass_dtl_id(long class_dtl_id) {
        Class_dtl_id = class_dtl_id;
    }

    public long getSubject_dtl_id() {
        return Subject_dtl_id;
    }

    public void setSubject_dtl_id(long subject_dtl_id) {
        Subject_dtl_id = subject_dtl_id;
    }

    public String getJsonData() {
        return JsonData;
    }

    public void setJsonData(String jsonData) {
        JsonData = jsonData;
    }

    public BranchModel getBranch() {
        return branch;
    }

    public void setBranch(BranchModel branch) {
        this.branch = branch;
    }

    public com.example.genius.Model.ClassModel getClassModel() {
        return ClassModel;
    }

    public void setClassModel(com.example.genius.Model.ClassModel classModel) {
        ClassModel = classModel;
    }

    public BranchSubjectModel getBranchSubject() {
        return branchSubject;
    }

    public void setBranchSubject(BranchSubjectModel branchSubject) {
        this.branchSubject = branchSubject;
    }

    public BranchCourseModel getBranchCourse() {
        return BranchCourse;
    }

    public void setBranchCourse(BranchCourseModel branchCourse) {
        BranchCourse = branchCourse;
    }

    public BranchClassModel getBranchClass() {
        return BranchClass;
    }

    public void setBranchClass(BranchClassModel branchClass) {
        BranchClass = branchClass;
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

    public List<BranchSubjectModel> getBranchSubjectData() {
        return BranchSubjectData;
    }

    public void setBranchSubjectData(List<BranchSubjectModel> branchSubjectData) {
        BranchSubjectData = branchSubjectData;
    }

    public List<SuperAdminSubjectModel> getSubjectData() {
        return SubjectData;
    }

    public void setSubjectData(List<SuperAdminSubjectModel> subjectData) {
        SubjectData = subjectData;
    }

    public List<BranchCourseModel> getBranchClassList() {
        return BranchClassList;
    }

    public void setBranchClassList(List<BranchCourseModel> branchClassList) {
        BranchClassList = branchClassList;
    }

    public List<com.example.genius.Model.ClassModel> getClassData() {
        return ClassData;
    }

    public void setClassData(List<com.example.genius.Model.ClassModel> classData) {
        ClassData = classData;
    }


    public Boolean getIsClass() {
        return isClass;
    }

    public void setIsClass(Boolean aClass) {
        isClass = aClass;
    }

    public Boolean getSubject() {
        return isSubject;
    }

    public void setSubject(Boolean subject) {
        isSubject = subject;
    }

    public class BranchSubjectData {
        boolean Completed;
        List<BranchSubjectModel> Data;
        String Message;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public List<BranchSubjectModel> getData() {
            return Data;
        }

        public void setData(List<BranchSubjectModel> data) {
            Data = data;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }
    }
}
