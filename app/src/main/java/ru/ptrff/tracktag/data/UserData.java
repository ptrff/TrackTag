package ru.ptrff.tracktag.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.annotations.SerializedName;

public class UserData {

    private SharedPreferences.Editor editor;

    private static UserData instance;
    private boolean isLoggedIn = false;
    private String userId;
    private String userName;
    private String accessToken;

    private UserData() {
    }

    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public void restoreData(SharedPreferences preferences) {
        editor = preferences.edit();

        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        userId = preferences.getString("userId", "");
        userName = preferences.getString("userName", "");
        accessToken = preferences.getString("accessToken", "");
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        editor.putBoolean("isLoggedIn", isLoggedIn).commit();
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        editor.putString("userId", userId).commit();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        editor.putString("userName", userName).commit();
    }

    public String getUserName() {
        return userName;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        editor.putString("accessToken", accessToken).commit();
    }

    public String getAccessToken() {
        return accessToken;
    }
}

