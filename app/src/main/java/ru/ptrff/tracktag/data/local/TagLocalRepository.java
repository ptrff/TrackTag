package ru.ptrff.tracktag.data.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.ptrff.tracktag.models.Tag;

public class TagLocalRepository {

    private final TagDao tagDao;

    public TagLocalRepository(Context context) {
        TagDatabase database = TagDatabase.getInstance(context);
        tagDao = database.tagDao();
    }

    public Flowable<List<Tag>> getAllLocalTags() {
        return tagDao.getAllLocalTags();
    }

    public Completable insertOrReplaceTags(List<Tag> tags) {
        return tagDao.insertOrReplaceTags(tags);
    }

    @SuppressLint("CheckResult")
    public Completable insertOrReplaceOrDelete(List<Tag> newTags) {
        getAllLocalTags()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(currentTags -> {
                    List<Tag> tagsToInsertReplace = new ArrayList<>(newTags);
                    List<Tag> tagsToDelete = new ArrayList<>(currentTags);
                    tagsToDelete.removeAll(newTags);
                    tagDao.insertOrReplaceTags(tagsToInsertReplace).subscribe();
                    tagDao.deleteTags(tagsToDelete).subscribe();
                }, throwable -> {
                    Log.e(getClass().getCanonicalName(), throwable.toString());
                    Completable.error(throwable);
                });
        return Completable.complete();
    }
}
