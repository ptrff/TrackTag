package ru.ptrff.tracktag.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap<String, String> lastTags = new HashMap<>();

    private boolean isNotificationsAllowed;
    private int notificationsInterval; // 1h by default

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
        isNotificationsAllowed = preferences.getBoolean("isNotificationsAllowed", false);

        notificationsInterval = preferences.getInt("notificationsInterval", 3600);

        Gson gson = new Gson();
        String hashMapString = preferences.getString("lastTags", "{}");
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        lastTags = gson.fromJson(hashMapString, type);
    }

    public void logout() {
        editor.clear().commit();
        isLoggedIn = false;
        userId = "";
        userName = "";
        accessToken = "";
        subs.clear();
        lastTags.clear();
        isNotificationsAllowed = false;
        notificationsInterval = 3600;
    }

    public int getNotificationsInterval() {
        return notificationsInterval;
    }

    public void setNotificationsInterval(int notificationsInterval) {
        this.notificationsInterval = notificationsInterval;
        editor.putInt("notificationsInterval", notificationsInterval).commit();
    }

    public void setLastTagId(User user, String tagId) {
        Gson gson = new Gson();
        lastTags.put(user.getUsername(), tagId);
        editor.putString("lastTags", gson.toJson(lastTags)).commit();
    }

    public String getLastTagId(User user) {
        return lastTags.get(user.getUsername());
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

    public boolean isNotificationsAllowed() {
        return isNotificationsAllowed;
    }

    public void setNotificationsAllowed(boolean notificationsAllowed) {
        isNotificationsAllowed = notificationsAllowed;
        editor.putBoolean("isNotificationsAllowed", notificationsAllowed).commit();
    }
}

