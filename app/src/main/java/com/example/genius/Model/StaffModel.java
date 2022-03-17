package com.example.genius.Model;

public class StaffModel {

    long StaffID;
    String Name;
    String Education;
    String DOB;
    String Gender;
    String Address;
    String ApptDT;
    String JoinDT;
    String LeavingDT;
    String EmailID;
    String MobileNo;
    String Userrole;
    long UserID;
    String User_Password;
    TransactionModel Transaction;
    RowStatusModel RowStatus;
    BranchModel BranchInfo;

    public StaffModel() {
    }

    public StaffModel(long staffID) {
        StaffID = staffID;
    }

    public StaffModel(long staffID, String name, String education, String DOB, String gender, String address, String apptDT, String joinDT, String leavingDT, String emailID, String mobileNo, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo, long userid, String password) {
        StaffID = staffID;
        Name = name;
        Education = education;
        this.DOB = DOB;
        Gender = gender;
        Address = address;
        ApptDT = apptDT;
        JoinDT = joinDT;
        LeavingDT = leavingDT;
        EmailID = emailID;
        MobileNo = mobileNo;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
        UserID = userid;
        User_Password = password;
    }

    public StaffModel(String name, String education, String DOB, String gender, String address, String apptDT, String joinDT, String leavingDT, String emailID, String mobileNo, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo, String Userrole,String password) {
        Name = name;
        Education = education;
        this.DOB = DOB;
        Gender = gender;
        Address = address;
        ApptDT = apptDT;
        JoinDT = joinDT;
        LeavingDT = leavingDT;
        EmailID = emailID;
        MobileNo = mobileNo;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
        Userrole = Userrole;
        User_Password = password;
    }

    public StaffModel(long staffID, long userID, String name, String emailID, String mobileNo, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        UserID = userID;
        StaffID = staffID;
        Name = name;
        EmailID = emailID;
        MobileNo = mobileNo;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public String getUser_Password() {
        return User_Password;
    }

    public void setUser_Password(String user_Password) {
        User_Password = user_Password;
    }

    public String getUserrole() {
        return Userrole;
    }

    public void setUserrole(String userrole) {
        Userrole = userrole;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public long getStaffID() {
        return StaffID;
    }

    public void setStaffID(long staffID) {
        StaffID = staffID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEducation() {
        return Education;
    }

    public void setEducation(String education) {
        Education = education;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getApptDT() {
        return ApptDT;
    }

    public void setApptDT(String apptDT) {
        ApptDT = apptDT;
    }

    public String getJoinDT() {
        return JoinDT;
    }

    public void setJoinDT(String joinDT) {
        JoinDT = joinDT;
    }

    public String getLeavingDT() {
        return LeavingDT;
    }

    public void setLeavingDT(String leavingDT) {
        LeavingDT = leavingDT;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
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

    public static class StaffData1 {
        boolean Completed;
        StaffModel Data;

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public StaffModel getData() {
            return Data;
        }

        public void setData(StaffModel data) {
            Data = data;
        }
    }
}
