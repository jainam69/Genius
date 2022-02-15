package com.example.genius.Model;

public class TestScheduleModel {

    long TestID;
    String TestName;
    BranchModel Branch;
    StandardModel Standard;
    SubjectModel Subject;
    int BatchTimeID;
    String BatchTimeText;
    double Marks;
    String TestDate;
    String TestStartTime;
    String TestEndTime;
    String Remarks;
    RowStatusModel RowStatus;
    TransactionModel Transaction;
    boolean marksentered;
    BranchCourseModel.BranchCourceData BranchCourse;
    BranchClassSingleModel.BranchClassData BranchClass;
    BranchSubjectModel.BranchSubjectData BranchSubject;

    public TestScheduleModel(long testID, String testName, BranchModel branch, int batchTimeID, String batchTimeText, double marks, String testDate, String testStartTime, String testEndTime, String remarks, RowStatusModel rowStatus, TransactionModel transaction
            ,BranchCourseModel.BranchCourceData course,BranchClassSingleModel.BranchClassData bclass,BranchSubjectModel.BranchSubjectData subject) {
        TestID = testID;
        TestName = testName;
        Branch = branch;
        BatchTimeID = batchTimeID;
        BatchTimeText = batchTimeText;
        Marks = marks;
        TestDate = testDate;
        TestStartTime = testStartTime;
        TestEndTime = testEndTime;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
        BranchCourse = course;
        BranchClass = bclass;
        BranchSubject = subject;
    }

    public TestScheduleModel(String testName, BranchModel branch, int batchTimeID, String batchTimeText, double marks, String testDate, String testStartTime, String testEndTime, String remarks, RowStatusModel rowStatus, TransactionModel transaction
    ,BranchCourseModel.BranchCourceData course,BranchClassSingleModel.BranchClassData bclass,BranchSubjectModel.BranchSubjectData subject) {
        TestName = testName;
        Branch = branch;
        BatchTimeID = batchTimeID;
        BatchTimeText = batchTimeText;
        Marks = marks;
        TestDate = testDate;
        TestStartTime = testStartTime;
        TestEndTime = testEndTime;
        Remarks = remarks;
        RowStatus = rowStatus;
        Transaction = transaction;
        BranchCourse = course;
        BranchClass = bclass;
        BranchSubject = subject;
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

    public BranchSubjectModel.BranchSubjectData getBranchSubject() {
        return BranchSubject;
    }

    public void setBranchSubject(BranchSubjectModel.BranchSubjectData branchSubject) {
        BranchSubject = branchSubject;
    }

    public boolean isMarksentered() {
        return marksentered;
    }

    public void setMarksentered(boolean marksentered) {
        this.marksentered = marksentered;
    }

    public long getTestID() {
        return TestID;
    }

    public void setTestID(long testID) {
        TestID = testID;
    }

    public String getTestName() {
        return TestName;
    }

    public void setTestName(String testName) {
        TestName = testName;
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

    public SubjectModel getSubject() {
        return Subject;
    }

    public void setSubject(SubjectModel subject) {
        Subject = subject;
    }

    public int getBatchTimeID() {
        return BatchTimeID;
    }

    public void setBatchTimeID(int batchTimeID) {
        BatchTimeID = batchTimeID;
    }

    public String getBatchTimeText() {
        return BatchTimeText;
    }

    public void setBatchTimeText(String batchTimeText) {
        BatchTimeText = batchTimeText;
    }

    public double getMarks() {
        return Marks;
    }

    public void setMarks(double marks) {
        Marks = marks;
    }

    public String getTestDate() {
        return TestDate;
    }

    public void setTestDate(String testDate) {
        TestDate = testDate;
    }

    public String getTestStartTime() {
        return TestStartTime;
    }

    public void setTestStartTime(String testStartTime) {
        TestStartTime = testStartTime;
    }

    public String getTestEndTime() {
        return TestEndTime;
    }

    public void setTestEndTime(String testEndTime) {
        TestEndTime = testEndTime;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public RowStatusModel getRowStatus() {
        return RowStatus;
    }

    public void setRowStatus(RowStatusModel rowStatus) {
        RowStatus = rowStatus;
    }

    public TransactionModel getTransaction() {
        return Transaction;
    }

    public void setTransaction(TransactionModel transaction) {
        Transaction = transaction;
    }

    public static class TestScheduleData1 {

        boolean Completed;
        TestScheduleModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public TestScheduleModel getData() {
            return Data;
        }

        public void setData(TestScheduleModel data) {
            Data = data;
        }
    }

}
