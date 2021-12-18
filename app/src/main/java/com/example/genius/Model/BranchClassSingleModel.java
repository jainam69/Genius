package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BranchClassSingleModel {

    BranchClassData Data;
    private String Message;
    private Boolean Completed;

    public BranchClassData getData() {
        return Data;
    }

    public void setData(BranchClassData data) {
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

    public static class BranchClassData implements Serializable {
        public long Class_dtl_id;
        public BranchModel branch;
        @SerializedName("Class")
        public ClassModel.ClassData classModel;
        public BranchClassData branchClass;
        public BranchCourseModel.BranchCourceData BranchCourse;
        public TransactionModel Transaction;
        public RowStatusModel RowStatus;
        public List<BranchClassData> BranchClassData;
        public List<BranchCourseModel.BranchCourceData> BranchClassList;
        public List<ClassModel.ClassData> ClassData;
        public boolean isClass;

        public long getClass_dtl_id() {
            return Class_dtl_id;
        }

        public void setClass_dtl_id(long class_dtl_id) {
            Class_dtl_id = class_dtl_id;
        }

        public BranchModel getBranch() {
            return branch;
        }

        public void setBranch(BranchModel branch) {
            this.branch = branch;
        }

        public BranchClassSingleModel.BranchClassData getBranchClass() {
            return branchClass;
        }

        public void setBranchClass(BranchClassSingleModel.BranchClassData branchClass) {
            this.branchClass = branchClass;
        }

        public BranchCourseModel.BranchCourceData getBranchCourse() {
            return BranchCourse;
        }

        public void setBranchCourse(BranchCourseModel.BranchCourceData branchCourse) {
            BranchCourse = branchCourse;
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

        public List<BranchClassSingleModel.BranchClassData> getBranchClassData() {
            return BranchClassData;
        }

        public void setBranchClassData(List<BranchClassSingleModel.BranchClassData> branchClassData) {
            BranchClassData = branchClassData;
        }

        public List<BranchCourseModel.BranchCourceData> getBranchClassList() {
            return BranchClassList;
        }

        public void setBranchClassList(List<BranchCourseModel.BranchCourceData> branchClassList) {
            BranchClassList = branchClassList;
        }

        public List<ClassModel.ClassData> getClassData() {
            return ClassData;
        }

        public void setClassData(List<ClassModel.ClassData> classData) {
            ClassData = classData;
        }

        public boolean isClass() {
            return isClass;
        }

        public void setClass(boolean aClass) {
            isClass = aClass;
        }

        public ClassModel.ClassData getClassModel() {
            return classModel;
        }

        public void setClassModel(ClassModel.ClassData classModel) {
            this.classModel = classModel;
        }

        public BranchClassData(List<BranchClassSingleModel.BranchClassData> branchClassData) {
            BranchClassData = branchClassData;
        }

        public BranchClassData() {

        }

        public BranchClassData(long class_dtl_id) {
            Class_dtl_id = class_dtl_id;
        }

        public BranchClassData(long class_dtl_id, BranchModel branch, ClassModel.ClassData classModel, TransactionModel transaction, RowStatusModel rowStatus, BranchCourseModel.BranchCourceData branchCourse, boolean isClass) {
            Class_dtl_id = class_dtl_id;
            this.branch = branch;
            this.classModel = classModel;
            Transaction = transaction;
            RowStatus = rowStatus;
            BranchCourse = branchCourse;
            this.isClass = isClass;
        }
    }

}
