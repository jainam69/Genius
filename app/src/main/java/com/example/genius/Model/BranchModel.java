package com.example.genius.Model;

import java.io.Serializable;
import java.util.List;

public class BranchModel implements Serializable {

    long BranchID;
    String BranchName;
    String AboutUs;
    String ContactNo;
    String MobileNo;
    String EmailID;
    boolean Completed;
    List<BranchData> Data;

    public BranchModel() {
    }

    public BranchModel(long branchID) {
        BranchID = branchID;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public List<BranchData> getData() {
        return Data;
    }

    public void setData(List<BranchData> data) {
        Data = data;
    }

    public long getBranchID() {
        return BranchID;
    }

    public void setBranchID(long branchID) {
        BranchID = branchID;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public String getAboutUs() {
        return AboutUs;
    }

    public void setAboutUs(String aboutUs) {
        AboutUs = aboutUs;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public static class BranchData{

        long BranchID;
        String BranchName;

        public long getBranchID() {
            return BranchID;
        }

        public void setBranchID(long branchID) {
            BranchID = branchID;
        }

        public String getBranchName() {
            return BranchName;
        }

        public void setBranchName(String branchName) {
            BranchName = branchName;
        }
    }
}
