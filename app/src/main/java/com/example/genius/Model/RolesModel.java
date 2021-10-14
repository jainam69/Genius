package com.example.genius.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class RolesModel {
    long RoleID;
    long UserID;
    String Permission;
    String RoleName;
    boolean HasAccess;

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }

    public long getRoleID() {
        return RoleID;
    }

    public void setRoleID(long roleID) {
        RoleID = roleID;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public String getPermission() {
        return Permission;
    }

    public void setPermission(String permission) {
        Permission = permission;
    }

    public boolean isHasAccess() {
        return HasAccess;
    }

    public void setHasAccess(boolean hasAccess) {
        HasAccess = hasAccess;
    }

}
