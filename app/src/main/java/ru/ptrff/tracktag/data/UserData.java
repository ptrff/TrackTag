package ru.ptrff.tracktag.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ru.ptrff.tracktag.models.User;
import ru.ptrff.tracktag.utils.UserConverter;

public class UserData {

    private SharedPreferences.Editor editor;

    private static UserData instance;
    private boolean isLoggedIn = false;
    private String userId;
    private String userName;
    private String accessToken;
    private final HashSet<String> subs = new HashSet<>();

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
        subs.addAll(preferences.getStringSet("subs", new HashSet<>()));
    }

    public void logout() {
        editor.clear().commit();
        isLoggedIn = false;
        userId = "";
        userName = "";
        accessToken = "";
        subs.clear();
    }

    public void addSub(User user) {
        UserConverter converter = new UserConverter();
        if (subs.add(converter.fromUser(user))) {
            editor.putStringSet("subs", new HashSet<>(subs)).commit();
        }
        System.out.println("Subs: " + subs);
    }

    public boolean isSubscribed(User user) {
        UserConverter converter = new UserConverter();
        return subs.contains(converter.fromUser(user));
    }

    public void removeSub(User user) {
        UserConverter converter = new UserConverter();
        if (subs.remove(converter.fromUser(user))) {
            editor.putStringSet("subs", new HashSet<>(subs)).commit();
        }
        System.out.println("Subs: " + subs);
    }

    public List<User> getSubs() {
        UserConverter converter = new UserConverter();
        List<User> users = new ArrayList<>();
        for (String s : subs) {
            users.add(converter.toUser(s));
        }
        return users;
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

