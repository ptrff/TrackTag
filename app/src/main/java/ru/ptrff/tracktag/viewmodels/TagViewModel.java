package ru.ptrff.tracktag.viewmodels;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.ptrff.tracktag.api.MapsRepository;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.models.Tag;
import ru.ptrff.tracktag.models.User;

public class TagViewModel extends ViewModel {
    private MapsRepository repo;
    private MutableLiveData<Boolean> deleteDone = new MutableLiveData<>();

    public TagViewModel() {
        repo = new MapsRepository();
    }

    @SuppressLint("CheckResult")
    public void likeTag(Tag tag, boolean like) {
        if (like) {
            repo
                    .likeTag(tag.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((tag1, throwable) -> {});
        } else {
            repo
                    .deleteLikeFromTag(tag.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((unused, throwable) -> {});
        }
    }

    @SuppressLint("CheckResult")
    public void deleteTag(Tag tag) {
        repo
                .deleteTag(tag.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((unused, throwable) -> deleteDone.postValue(true));
    }

    public void subscribe(Tag tag) {
        UserData data = UserData.getInstance();
        User user = tag.getUser();
        if (!data.isSubscribed(user)) {
            data.addSub(user);
        } else {
            data.removeSub(user);
        }
    }

    public MutableLiveData<Boolean> getDeleteDone() {
        return deleteDone;
    }
}
