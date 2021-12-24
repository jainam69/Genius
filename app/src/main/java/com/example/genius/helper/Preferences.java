package com.example.genius.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static final String KEY_LOGIN = "KEY_LOGIN";
    public static final String KEY_USER_ID ="KEY_USER_ID";
    public static final String KEY_STAFF_ID ="KEY_STAFF_ID";

    public static final String KEY_USER_NAME = "KEY_USER_NAME";
    public static final String KEY_USER_TYPE = "KEY_USER_TYPE";
    public static final String KEY_BRANCH_ID = "KEY_BRNACH_ID";
    public static final String KEY_BRANCH_NAME = "KEY_BRNACH_NAME";
    public static final String KEY_PERMISSION_LIST = "KEY_PERMISSION_LIST";

    public static final String KEY_STAFF_MASTER = "KEY_STAFF_MASTER";
    public static final String KEY_STANDARD_MASTER = "KEY_STANDARD_MASTER";
    public static final String KEY_SCHOOL_MASTER = "KEY_SCHOOL_MASTER";
    public static final String KEY_SUBJECT_MASTER = "KEY_SUBJECT_MASTER";
    public static final String KEY_ANNOUNCEMENT_MASTER = "KEY_ANNOUNCEMENT_MASTER";
    public static final String KEY_BATCH = "KEY_BATCH";
    public static final String KEY_ONLINE_PAYMENT = "KEY_ONLINE_PAYMENT";
    public static final String KEY_ADD_UPI_DETAIL = "KEY_ADD_UPI_DETAIL";
    public static final String KEY_FEE_STRUCTURE = "KEY_FEE_STRUCTURE";
    public static final String KEY_STUDENTS = "KEY_STUDENTS";
    public static final String KEY_ATTENDANCE = "KEY_ATTENDANCE";
    public static final String KEY_TEST_SCHEDULE = "KEY_TEST_SCHEDULE";
    public static final String KEY_TEST_MARKS = "KEY_TEST_MARKS";
    public static final String KEY_PRACTICE_PAPER = "KEY_PRACTICE_PAPER";
    public static final String KEY_HOMEWORK = "KEY_HOMEWORK";
    public static final String KEY_GALLERY = "KEY_GALLERY";
    public static final String KEY_VIDEO = "KEY_VIDEO";
    public static final String KEY_LIVE_VIDEO = "KEY_LIVE_VIDEO";
    public static final String KEY_UPLOAD_PAPER = "KEY_UPLOAD_PAPER";
    public static final String KEY_VIDEO_BASE = "KEY_VIDEO_BASE";

    public static final String KEY_STAFF_MASTER_PERMISSION = "KEY_STAFF_MASTER_PERMISSION";
    public static final String KEY_BATCH_PERMISSION = "KEY_BATCH_PERMISSION";
    public static final String KEY_ONLINE_PAYMENT_PERMISSION = "KEY_ONLINE_PAYMENT_PERMISSION";
    public static final String KEY_ADD_UPI_DETAIL_PERMISSION = "KEY_ADD_UPI_DETAIL_PERMISSION";
    public static final String KEY_FEE_STRUCTURE_PERMISSION = "KEY_FEE_STRUCTURE_PERMISSION";
    public static final String KEY_STANDARD_MASTER_PERMISSION = "KEY_STANDARD_MASTER_PERMISSION";
    public static final String KEY_SCHOOL_MASTER_PERMISSION = "KEY_SCHOOL_MASTER_PERMISSION";
    public static final String KEY_SUBJECT_MASTER_PERMISSION = "KEY_SUBJECT_MASTER_PERMISSION";
    public static final String KEY_ANNOUNCEMENT_MASTER_PERMISSION = "KEY_ANNOUNCEMENT_MASTER_PERMISSION";
    public static final String KEY_STUDENTS_PERMISSION = "KEY_STUDENTS_PERMISSION";
    public static final String KEY_ATTENDANCE_PERMISSION = "KEY_ATTENDANCE_PERMISSION";
    public static final String KEY_TEST_SCHEDULE_PERMISSION = "KEY_TEST_SCHEDULE_PERMISSION";
    public static final String KEY_TEST_MARKS_PERMISSION = "KEY_TEST_MARKS_PERMISSION";
    public static final String KEY_PRACTICE_PAPER_PERMISSION = "KEY_PRACTICE_PAPER_PERMISSION";
    public static final String KEY_HOMEWORK_PERMISSION = "KEY_HOMEWORK_PERMISSION";
    public static final String KEY_GALLERY_PERMISSION = "KEY_GALLERY_PERMISSION";
    public static final String KEY_VIDEO_PERMISSION = "KEY_VIDEO_PERMISSION";
    public static final String KEY_LIVE_VIDEO_PERMISSION = "KEY_LIVE_VIDEO_PERMISSION";
    public static final String KEY_UPLOAD_PAPER_PERMISSION = "KEY_UPLOAD_PAPER_PERMISSION";

    private static final String PREF_NAME = "com.ashirvadstudycircle.app";
    private static Preferences instance;
    Context context;
    private final SharedPreferences sharedPref;

    public Preferences(Context context)
    {
        this.context = context;
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Preferences getInstance(Context c)
    {
        if (instance==null)
        {
            instance = new Preferences(c);
        }
        return instance;
    }

    public SharedPreferences getSharedPref()
    {
        return sharedPref;
    }

    public String getString(String key)
    {
        return sharedPref.getString(key,"");
    }

    public void setString(String key,String value)
    {
        SharedPreferences.Editor sharedPrefEditor = getSharedPref().edit();
        String preferencevalue = value;
        sharedPrefEditor.putString(key,preferencevalue);
        sharedPrefEditor.commit();
    }

    public int getInt(String key)
    {
        return sharedPref.getInt(key,0);
    }

    public void setInt(String key,int value)
    {
        SharedPreferences.Editor sharedPrefEditor = getSharedPref().edit();
        sharedPrefEditor.putInt(key,value);
        sharedPrefEditor.apply();
    }

    public boolean getBoolean(String key)
    {
        return sharedPref.getBoolean(key,false);
    }

    public void setBoolean(String key,boolean value)
    {
        SharedPreferences.Editor sharedPrefEditor = getSharedPref().edit();
        sharedPrefEditor.putBoolean(key,value);
        sharedPrefEditor.apply();
    }

    public long getLong(String key)
    {
        return sharedPref.getLong(key,0);
    }

    public void setLong(String key,long value)
    {
        SharedPreferences.Editor sharedprefEditor = getSharedPref().edit();
        sharedprefEditor.putLong(key,value);
        sharedprefEditor.apply();
    }
}
