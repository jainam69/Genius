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
    public static final String KEY_VIDEO_BASE = "KEY_VIDEO_BASE";
    public static final String KEY_FINANCIAL_YEAR = "KEY_FINANCIAL_YEAR";

    public static final String KEY_FIRE_NOTIFICATION = "KEY_FIRE_NOTIFICATION";

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
