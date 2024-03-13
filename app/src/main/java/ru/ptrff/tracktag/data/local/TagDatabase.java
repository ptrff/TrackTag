package ru.ptrff.tracktag.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.ptrff.tracktag.models.Tag;

@Database(entities = {Tag.class}, version = 1)
public abstract class TagDatabase extends RoomDatabase {

    private static TagDatabase instance;

    public abstract TagDao tagDao();

    public static synchronized TagDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TagDatabase.class, "tags")
                    .build();
        }
        return instance;
    }
}
