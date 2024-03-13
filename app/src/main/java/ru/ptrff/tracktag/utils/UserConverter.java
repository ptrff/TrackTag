package ru.ptrff.tracktag.utils;

import androidx.room.TypeConverter;

import ru.ptrff.tracktag.models.User;

public class UserConverter {
    @TypeConverter
    public String fromUser(User user) {
        if(user == null) {
            return "\\\\\\";
        }
        return user.getId() +
                "///" +
                user.getUsername();
    }

    @TypeConverter
    public User toUser(String value) {
        if(value.equals("\\\\\\")) return null;
        String[] parts = value.split("///");
        return new User(parts[0], parts[1]);
    }
}
