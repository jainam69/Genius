package com.example.genius.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BranchSubjectModel {

    boolean Completed;
    List<BranchSubjectData> Data;
    String Message;

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<BranchSubjectData> getData() {
        return Data;
    }

    public void setData(List<BranchSubjectData> data) {
        Data = data;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public static class BranchSubjectData {
        public long Class_dtl_id;
        public long Subject_dtl_id;
        public String JsonData;
        public BranchModel branch;
        @SerializedName("Class")
        public ClassModel.ClassData ClassModel;
        public BranchSubjectData branchSubject;
        public BranchCourseModel.BranchCourceData BranchCourse;
        public BranchClassSingleModel.BranchClassData BranchClass;
        public TransactionModel Transaction;
        public RowStatusModel RowStatus;
        public List<BranchSubjectData> BranchSubjectData;
        public List<SuperAdminSubjectModel> SubjectData;
        public SuperAdminSubjectModel.SuperAdminSubjectData Subject;
        public List<BranchCourseModel.BranchCourceData> BranchClassList;
        public List<ClassModel.ClassData> ClassData;
        public Boolean isClass;
        public Boolean isSubject;

        public BranchSubjectData(long subject_dtl_id) {
            Subject_dtl_id = subject_dtl_id;
        }

        public BranchSubjectData(List<BranchSubjectModel.BranchSubjectData> branchSubjectData) {
            this.BranchSubjectData = branchSubjectData;
        }

        public BranchSubjectData(long subject_dtl_id, BranchModel branch, BranchCourseModel.BranchCourceData branchCourse, BranchClassSingleModel.BranchClassData branchClass, TransactionModel transaction, RowStatusModel rowStatus, SuperAdminSubjectModel.SuperAdminSubjectData subject, Boolean isSubject) {
            Subject_dtl_id = subject_dtl_id;
            this.branch = branch;
            BranchCourse = branchCourse;
            BranchClass = branchClass;
            Transaction = transaction;
            RowStatus = rowStatus;
            Subject = subject;
            this.isSubject = isSubject;
        }

        public void setSubject(SuperAdminSubjectModel.SuperAdminSubjectData subject) {
            Subject = subject;
        }

        public SuperAdminSubjectModel.SuperAdminSubjectData getSubject() {
            return Subject;
        }

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

        public ClassModel.ClassData getClassModel() {
            return ClassModel;
        }

        public void setClassModel(ClassModel.ClassData classModel) {
            ClassModel = classModel;
        }

        public BranchSubjectData getBranchSubject() {
            return branchSubject;
        }

        public void setBranchSubject(BranchSubjectData branchSubject) {
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

        public List<BranchSubjectData> getBranchSubjectData() {
            return BranchSubjectData;
        }

        public void setBranchSubjectData(List<BranchSubjectData> branchSubjectData) {
            BranchSubjectData = branchSubjectData;
        }

        public List<SuperAdminSubjectModel> getSubjectData() {
            return SubjectData;
        }

        public void setSubjectData(List<SuperAdminSubjectModel> subjectData) {
            SubjectData = subjectData;
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

        public Boolean getIsClass() {
            return isClass;
        }

        public void setIsClass(Boolean aClass) {
            isClass = aClass;
        }

        public Boolean isSubject() {
            return isSubject;
        }

        public void setSubject(Boolean subject) {
            isSubject = subject;
        }

    }
}
