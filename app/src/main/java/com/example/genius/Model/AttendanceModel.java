package com.example.genius.Model;

import java.util.List;

public class AttendanceModel {

    long AttendanceID;
    BranchModel Branch;
    StandardModel Standard;
    int BatchTypeID;
    String BatchTypeText;
    String AttendanceDate;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    List<AttendanceDetailEntity> AttendanceDetail;
    BranchCourseModel.BranchCourceData BranchCourse;
    BranchClassSingleModel.BranchClassData BranchClass;

    public AttendanceModel(long attendanceID, BranchModel branch, int batchTypeID, String batchTypeText, String attendanceDate, TransactionModel transaction, RowStatusModel rowStatus, List<AttendanceDetailEntity> attendanceDetail,BranchCourseModel.BranchCourceData Course, BranchClassSingleModel.BranchClassData bclass) {
        AttendanceID = attendanceID;
        Branch = branch;
        BatchTypeID = batchTypeID;
        BatchTypeText = batchTypeText;
        AttendanceDate = attendanceDate;
        Transaction = transaction;
        RowStatus = rowStatus;
        AttendanceDetail = attendanceDetail;
        BranchCourse = Course;
        BranchClass = bclass;
    }

    public AttendanceModel(BranchModel branch, int batchTypeID, String batchTypeText, String attendanceDate, TransactionModel transaction, RowStatusModel rowStatus, List<AttendanceDetailEntity> attendanceDetail, BranchCourseModel.BranchCourceData Course, BranchClassSingleModel.BranchClassData bclass) {
        Branch = branch;
        BatchTypeID = batchTypeID;
        BatchTypeText = batchTypeText;
        AttendanceDate = attendanceDate;
        Transaction = transaction;
        RowStatus = rowStatus;
        AttendanceDetail = attendanceDetail;
        BranchCourse = Course;
        BranchClass = bclass;
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

    public long getAttendanceID() {
        return AttendanceID;
    }

    public void setAttendanceID(long attendanceID) {
        AttendanceID = attendanceID;
    }

    public BranchModel getBranch() {
        return Branch;
    }

    public void setBranch(BranchModel branch) {
        Branch = branch;
    }

    public StandardModel getStandard() {
        return Standard;
    }

    public void setStandard(StandardModel standard) {
        Standard = standard;
    }

    public int getBatchTypeID() {
        return BatchTypeID;
    }

    public void setBatchTypeID(int batchTypeID) {
        BatchTypeID = batchTypeID;
    }

    public String getBatchTypeText() {
        return BatchTypeText;
    }

    public void setBatchTypeText(String batchTypeText) {
        BatchTypeText = batchTypeText;
    }

    public String getAttendanceDate() {
        return AttendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        AttendanceDate = attendanceDate;
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

    public List<AttendanceDetailEntity> getAttendanceDetail() {
        return AttendanceDetail;
    }

    public void setAttendanceDetail(List<AttendanceDetailEntity> attendanceDetail) {
        AttendanceDetail = attendanceDetail;
    }

    public static class AttendanceDetailEntity{

        long DetailID;
        long HeaderID;
        StudentModel Student;
        boolean IsAbsent;
        boolean IsPresent;
        String Remarks;

        public AttendanceDetailEntity()
        {

        }

        public AttendanceDetailEntity(long headerID, StudentModel student, boolean isAbsent, boolean isPresent, String remarks) {
            HeaderID = headerID;
            Student = student;
            IsAbsent = isAbsent;
            IsPresent = isPresent;
            Remarks = remarks;
        }

        public AttendanceDetailEntity(long detailID, long headerID, StudentModel student, boolean isAbsent, boolean isPresent, String remarks) {
            DetailID = detailID;
            HeaderID = headerID;
            Student = student;
            IsAbsent = isAbsent;
            IsPresent = isPresent;
            Remarks = remarks;
        }

        public AttendanceDetailEntity(StudentModel student, boolean isAbsent, boolean isPresent, String remarks) {
            Student = student;
            IsAbsent = isAbsent;
            IsPresent = isPresent;
            Remarks = remarks;
        }

        public long getDetailID() {
            return DetailID;
        }

        public void setDetailID(long detailID) {
            DetailID = detailID;
        }

        public long getHeaderID() {
            return HeaderID;
        }

        public void setHeaderID(long headerID) {
            HeaderID = headerID;
        }

        public StudentModel getStudent() {
            return Student;
        }

        public void setStudent(StudentModel student) {
            Student = student;
        }

        public boolean isAbsent() {
            return IsAbsent;
        }

        public void setAbsent(boolean absent) {
            IsAbsent = absent;
        }

        public boolean isPresent() {
            return IsPresent;
        }

        public void setPresent(boolean present) {
            IsPresent = present;
        }

        public String getRemarks() {
            return Remarks;
        }

        public void setRemarks(String remarks) {
            Remarks = remarks;
        }
    }

    public static class AttendanceData1 {

        boolean Completed;
        AttendanceModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public AttendanceModel getData() {
            return Data;
        }

        public void setData(AttendanceModel data) {
            Data = data;
        }
    }
}
