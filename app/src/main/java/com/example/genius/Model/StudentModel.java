package com.example.genius.Model;

public class StudentModel {
    long StudentID;
    String GrNo;
    String FirstName;
    String MiddleName;
    String LastName;
    String DOB;
    String AdmissionDate;
    String Address;
    int SchoolTime;
    int LastYearResult;
    String Grade;
    String LastYearClassName;
    String ContactNo;
    String StudImage;
    StandardModel StandardInfo;
    SchoolModel SchoolInfo;
    BranchModel BranchInfo;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    BatchModel BatchInfo;
    StudentMaintModel StudentMaint;

    public StudentModel(long studentID) {
        StudentID = studentID;
    }

    public StudentModel(long studentID, String grNo, String firstName, String middleName, String lastName, String DOB, String admissionDate, String address, int schoolTime, int lastYearResult, String grade, String lastYearClassName, String contactNo, String studImage, StandardModel standardInfo, SchoolModel schoolInfo, BranchModel branchInfo, TransactionModel transaction, RowStatusModel rowStatus, BatchModel batchInfo, StudentMaintModel studentMaint) {
        StudentID = studentID;
        GrNo = grNo;
        FirstName = firstName;
        MiddleName = middleName;
        LastName = lastName;
        this.DOB = DOB;
        AdmissionDate = admissionDate;
        Address = address;
        SchoolTime = schoolTime;
        LastYearResult = lastYearResult;
        Grade = grade;
        LastYearClassName = lastYearClassName;
        ContactNo = contactNo;
        StudImage = studImage;
        StandardInfo = standardInfo;
        SchoolInfo = schoolInfo;
        BranchInfo = branchInfo;
        Transaction = transaction;
        RowStatus = rowStatus;
        BatchInfo = batchInfo;
        StudentMaint = studentMaint;
    }

    public StudentModel(String grNo, String firstName, String middleName, String lastName, String DOB, String admissionDate, String address, int schoolTime, int lastYearResult, String grade, String lastYearClassName, String contactNo, String studImage, StandardModel standardInfo, SchoolModel schoolInfo, BranchModel branchInfo, TransactionModel transaction, RowStatusModel rowStatus, BatchModel batchInfo, StudentMaintModel studentMaint) {
        GrNo = grNo;
        FirstName = firstName;
        MiddleName = middleName;
        LastName = lastName;
        this.DOB = DOB;
        AdmissionDate = admissionDate;
        Address = address;
        SchoolTime = schoolTime;
        LastYearResult = lastYearResult;
        Grade = grade;
        LastYearClassName = lastYearClassName;
        ContactNo = contactNo;
        StudImage = studImage;
        StandardInfo = standardInfo;
        SchoolInfo = schoolInfo;
        BranchInfo = branchInfo;
        Transaction = transaction;
        RowStatus = rowStatus;
        BatchInfo = batchInfo;
        StudentMaint = studentMaint;
    }

    public StudentModel() {
    }


    public String getAdmissionDate() {
        return AdmissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        AdmissionDate = admissionDate;
    }

    public String getStudImage() {
        return StudImage;
    }

    public void setStudImage(String studImage) {
        StudImage = studImage;
    }

    public long getStudentID() {
        return StudentID;
    }

    public void setStudentID(long studentID) {
        StudentID = studentID;
    }

    public String getGrNo() {
        return GrNo;
    }

    public void setGrNo(String grNo) {
        GrNo = grNo;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getSchoolTime() {
        return SchoolTime;
    }

    public void setSchoolTime(int schoolTime) {
        SchoolTime = schoolTime;
    }

    public int getLastYearResult() {
        return LastYearResult;
    }

    public void setLastYearResult(int lastYearResult) {
        LastYearResult = lastYearResult;
    }

    public String getGrade() {
        return Grade;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }

    public String getLastYearClassName() {
        return LastYearClassName;
    }

    public void setLastYearClassName(String lastYearClassName) {
        LastYearClassName = lastYearClassName;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public StandardModel getStandardInfo() {
        return StandardInfo;
    }

    public void setStandardInfo(StandardModel standardInfo) {
        StandardInfo = standardInfo;
    }

    public SchoolModel getSchoolInfo() {
        return SchoolInfo;
    }

    public void setSchoolInfo(SchoolModel schoolInfo) {
        SchoolInfo = schoolInfo;
    }

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
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

    public BatchModel getBatchInfo() {
        return BatchInfo;
    }

    public void setBatchInfo(BatchModel batchInfo) {
        BatchInfo = batchInfo;
    }

    public StudentMaintModel getStudentMaint() {
        return StudentMaint;
    }

    public void setStudentMaint(StudentMaintModel studentMaint) {
        StudentMaint = studentMaint;
    }

    public static class StudentMaintModel {
        public long StudentID;
        public String ParentName;
        public String FatherOccupation;
        public String MotherOccupation;
        public String ContactNo;
        long ParentID;

        public StudentMaintModel() {
        }

        public StudentMaintModel(String parentName, String fatherOccupation, String motherOccupation, String contactNo) {
            ParentName = parentName;
            FatherOccupation = fatherOccupation;
            MotherOccupation = motherOccupation;
            ContactNo = contactNo;
        }

        public StudentMaintModel(long studentID, String parentName, String fatherOccupation, String motherOccupation, String contactNo, long parentID) {
            StudentID = studentID;
            ParentName = parentName;
            FatherOccupation = fatherOccupation;
            MotherOccupation = motherOccupation;
            ContactNo = contactNo;
            ParentID = parentID;
        }

        public long getParentID() {
            return ParentID;
        }

        public void setParentID(long parentID) {
            ParentID = parentID;
        }

        public long getStudentID() {
            return StudentID;
        }

        public void setStudentID(long studentID) {
            StudentID = studentID;
        }

        public String getParentName() {
            return ParentName;
        }

        public void setParentName(String parentName) {
            ParentName = parentName;
        }

        public String getFatherOccupation() {
            return FatherOccupation;
        }

        public void setFatherOccupation(String fatherOccupation) {
            FatherOccupation = fatherOccupation;
        }

        public String getMotherOccupation() {
            return MotherOccupation;
        }

        public void setMotherOccupation(String motherOccupation) {
            MotherOccupation = motherOccupation;
        }

        public String getContactNo() {
            return ContactNo;
        }

        public void setContactNo(String contactNo) {
            ContactNo = contactNo;
        }
    }

    public static class StudentData1 {
        boolean Completed;
        StudentModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public StudentModel getData() {
            return Data;
        }

        public void setData(StudentModel data) {
            Data = data;
        }
    }
}
