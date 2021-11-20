package com.example.genius.Model;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BranchClassModel {

    long Class_dtl_id;
    BranchModel branch;
    @SerializedName("Class")
    ClassModel classModel;
    BranchCourseModel.BranchCourceData BranchCourse;
    TransactionModel Transaction;
    RowStatusModel RowStatus;

    public ClassModel getClassModel() {
        return classModel;
    }

    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
    }

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

    public static class BranchClassData
    {
        BranchModel branch;
        BranchCourseModel.BranchCourceData BranchCourse;

    }

}
