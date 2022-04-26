package com.example.genius.Model;

import java.util.List;

public class UserRightsModel {

    public Long UserWiseRightsID;
    public Long UserID;
    public RoleRightsModel RoleRightinfo;
    public RoleModel Roleinfo;
    public UserModel userinfo;
    public BranchModel branchinfo;
    public Boolean Createstatus;
    public Boolean Editstatus;
    public Boolean Deletestatus;
    public Boolean Viewstatus;
    public int Status;
    public String StatusText;
    public UserModel.PageInfoEntity PageInfo;
    public List<UserRightsModel> list;
    public List<UserModel.PageInfoEntity> PageList;
    public TransactionModel Transaction;
    public RowStatusModel RowStatus;

    public UserRightsModel(Long userWiseRightsID, Long userID, RoleModel roleinfo, TransactionModel transaction, RowStatusModel rowStatus) {
        UserWiseRightsID = userWiseRightsID;
        UserID = userID;
        Roleinfo = roleinfo;
        Transaction = transaction;
        RowStatus = rowStatus;
    }

    public static class UserRightsData{
        public boolean Completed;
        public List<UserRightsModel> Data;
        public String Message;
    }

    public class UserRightsResponse{
        public boolean Completed;
        public UserRightsModel Data;
        public String Message;
    }
}
