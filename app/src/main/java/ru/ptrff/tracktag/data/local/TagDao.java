package ru.ptrff.tracktag.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import ru.ptrff.tracktag.models.Tag;

@Dao
public interface TagDao {
    @Query("SELECT * FROM tags")
    Flowable<List<Tag>> getAllLocalTags();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceTags(List<Tag> tagsToInsertReplace);

    @Delete
    Completable deleteTags(List<Tag> tagsToDelete);
}
