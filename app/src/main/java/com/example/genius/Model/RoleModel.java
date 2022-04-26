package com.example.genius.Model;

import java.util.List;

public class RoleModel {

    public long RoleID;
    public String RoleName;
    public TransactionModel Transaction;
    public RowStatusModel RowStatus;
    public BranchModel BranchInfo;

    public RoleModel(long roleID) {
        RoleID = roleID;
    }

    public RoleModel(long roleID, String roleName, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        RoleID = roleID;
        RoleName = roleName;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public static class RoleData{
        public boolean Completed;
        public List<RoleModel> Data;
        public String Message;
    }

    public static class RoleResponse{
        public boolean Completed;
        public RoleModel Data;
        public String Message;
    }
}
