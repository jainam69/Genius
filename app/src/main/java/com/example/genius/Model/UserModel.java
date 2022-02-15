package com.example.genius.Model;

import java.io.Serializable;
import java.util.List;

public class UserModel {

    long UserID;
    String Username;
    String Password;
    String ClientSecret;
    RowStatusModel RowStatus;
    TransactionModel Transaction;
    String UserType;
    String UserTypeText;
    long StudentID;
    long ParentID;
    long StaffID;
    BranchModel BranchInfo;
    List<RolesModel> Roles;
    List<UserPermission> Permission;

    public UserModel() {
    }

    public UserModel(String username) {
        Username = username;
    }

    public UserModel(long userID, TransactionModel transaction, List<RolesModel> roles) {
        UserID = userID;
        Transaction = transaction;
        Roles = roles;
    }

    public UserModel(long userID) {
        UserID = userID;
    }

    public List<UserPermission> getPermission() {
        return Permission;
    }

    public void setPermission(List<UserPermission> permission) {
        Permission = permission;
    }

    public List<RolesModel> getRoles() {
        return Roles;
    }

    public void setRoles(List<RolesModel> roles) {
        Roles = roles;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getClientSecret() {
        return ClientSecret;
    }

    public void setClientSecret(String clientSecret) {
        ClientSecret = clientSecret;
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

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getUserTypeText() {
        return UserTypeText;
    }

    public void setUserTypeText(String userTypeText) {
        UserTypeText = userTypeText;
    }

    public long getStudentID() {
        return StudentID;
    }

    public void setStudentID(long studentID) {
        StudentID = studentID;
    }

    public long getParentID() {
        return ParentID;
    }

    public void setParentID(long parentID) {
        ParentID = parentID;
    }

    public long getStaffID() {
        return StaffID;
    }

    public void setStaffID(long staffID) {
        StaffID = staffID;
    }

    public BranchModel getBranchInfo() {
        return BranchInfo;
    }

    public void setBranchInfo(BranchModel branchInfo) {
        BranchInfo = branchInfo;
    }

    public static class UserPermission
    {
        PackageRightsEntity PackageRightinfo;
        PageInfoEntity PageInfo;

        public PackageRightsEntity getPackageRightinfo() {
            return PackageRightinfo;
        }

        public void setPackageRightinfo(PackageRightsEntity packageRightinfo) {
            PackageRightinfo = packageRightinfo;
        }

        public PageInfoEntity getPageInfo() {
            return PageInfo;
        }

        public void setPageInfo(PageInfoEntity pageInfo) {
            PageInfo = pageInfo;
        }
    }

    public static class PackageRightsEntity
    {
        boolean Createstatus;
        boolean Deletestatus;
        boolean Viewstatus;

        public boolean isCreatestatus() {
            return Createstatus;
        }

        public void setCreatestatus(boolean createstatus) {
            Createstatus = createstatus;
        }

        public boolean isDeletestatus() {
            return Deletestatus;
        }

        public void setDeletestatus(boolean deletestatus) {
            Deletestatus = deletestatus;
        }

        public boolean isViewstatus() {
            return Viewstatus;
        }

        public void setViewstatus(boolean viewstatus) {
            Viewstatus = viewstatus;
        }
    }

    public static class PageInfoEntity
    {
        String Page;
        long PageID;

        public String getPage() {
            return Page;
        }

        public void setPage(String page) {
            Page = page;
        }

        public long getPageID() {
            return PageID;
        }

        public void setPageID(long pageID) {
            PageID = pageID;
        }
    }

    public static class UserData {

        boolean Completed;
        UserModel Data;
        String Message;

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }

        public boolean isCompleted() {
            return Completed;
        }

        public void setCompleted(boolean completed) {
            Completed = completed;
        }

        public UserModel getData() {
            return Data;
        }

        public void setData(UserModel data) {
            Data = data;
        }
    }
}
