package com.example.genius.Model;

import java.util.List;

public class RoleRightsModel {

    public long RoleRightsId;
    public RoleModel Roleinfo;
    public Boolean Createstatus;
    public Boolean Editstatus;
    public Boolean Deletestatus;
    public Boolean Viewstatus;
    public int Status;
    public String StatusText;
    public UserModel.PageInfoEntity PageInfo;
    public List<RoleRightsModel> list;
    public List<UserModel.PageInfoEntity> PageList;
    public TransactionModel Transaction;
    public RowStatusModel RowStatus;
    public BranchModel BranchInfo;

    public RoleRightsModel() {
    }

    public RoleRightsModel(long roleRightsId, RoleModel roleinfo, Boolean createstatus, Boolean editstatus, Boolean deletestatus, Boolean viewstatus, UserModel.PageInfoEntity pageInfo, TransactionModel transaction, RowStatusModel rowStatus, BranchModel branchInfo) {
        RoleRightsId = roleRightsId;
        Roleinfo = roleinfo;
        Createstatus = createstatus;
        Editstatus = editstatus;
        Deletestatus = deletestatus;
        Viewstatus = viewstatus;
        PageInfo = pageInfo;
        Transaction = transaction;
        RowStatus = rowStatus;
        BranchInfo = branchInfo;
    }

    public static class RoleRightsData{
        public boolean Completed;
        public List<RoleRightsModel> Data;
        public String Message;
    }

    public static class RoleRightsResponse{
        public boolean Completed;
        public RoleRightsModel Data;
        public String Message;
    }
}
