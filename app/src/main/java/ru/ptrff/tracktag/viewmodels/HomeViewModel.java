package ru.ptrff.tracktag.viewmodels;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.ptrff.tracktag.api.MapsRepository;
import ru.ptrff.tracktag.data.Options;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.data.local.TagLocalRepository;
import ru.ptrff.tracktag.models.Option;
import ru.ptrff.tracktag.models.Tag;
import ru.ptrff.tracktag.models.User;

public class HomeViewModel extends ViewModel {
    private MapsRepository repo;
    private TagLocalRepository localRepo;
    private final MutableLiveData<List<Option>> options = new MutableLiveData<>();
    private final MutableLiveData<List<Tag>> tags = new MutableLiveData<>();
    private boolean isInitiated;

    public HomeViewModel() {
        repo = new MapsRepository();
    }

    public void setLocalRepo(TagLocalRepository localRepo) {
        this.localRepo = localRepo;
    }

    public void initOptions() {
        options.postValue(UserData.getInstance().isLoggedIn() ? Options.user : Options.guest);
    }

    @SuppressLint("CheckResult")
    public void getData() {
        repo
                .getAllTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        receivedTags -> {
                            Collections.reverse(receivedTags);
                            tags.postValue(receivedTags);
                            saveLocalData(receivedTags);
                            updateLastTagsIDs(receivedTags);
                        },
                        throwable -> {
                            getLocalData();
                            Log.e(getClass().getCanonicalName(), throwable.toString());
                        }
                );
    }

    @SuppressLint("CheckResult")
    private void getLocalData() {
        localRepo
                .getAllLocalTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        localTags -> {
                            tags.postValue(localTags);
                            Log.d(getClass().getCanonicalName(), localTags.size() + " local tags loaded");
                        },
                        throwable -> {
                            Log.e(getClass().getCanonicalName(), throwable.toString());
                        }
                );
    }

    @SuppressLint("CheckResult")
    private void saveLocalData(List<Tag> tags) {
        localRepo
                .insertOrReplaceOrDelete(tags)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(getClass().getCanonicalName(), "local tags updated");
                }, throwable -> {
                    Log.e(getClass().getCanonicalName(), throwable.toString());
                });
    }

    public void updateLastTagsIDs(List<Tag> tags) {
        UserData data = UserData.getInstance();
        if (tags != null && !tags.isEmpty()) {
            for (User sub : data.getSubs()) {
                Log.d(getClass().getCanonicalName(), "updating last tag id for " + sub.getUsername());
                for (Tag tag : tags) {
                    if (tag.getUser() != null && tag.getUser().getUsername().equals(sub.getUsername())) {
                        Log.d(getClass().getCanonicalName(), "last tag set to " + tag.getId());
                        data.setLastTagId(sub, tag.getId());
                        break;
                    }
                }
            }
        }
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
                .subscribe((unused, throwable) -> getData());
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

    public void updateLastTagsIDs() {
        updateLastTagsIDs(tags.getValue());
    }

    public void addNetworkConnectionListener(ConnectivityManager connectivityManager) {
        if (connectivityManager.isDefaultNetworkActive()) {
            getData();
        }
        /*connectivityManager.addDefaultNetworkActiveListener(() -> {
            if (connectivityManager.isDefaultNetworkActive()) {
                getData();
            }
        });*/
    }

    public boolean isInitiated() {
        return isInitiated;
    }

    public void setInitiated(boolean initiated) {
        isInitiated = initiated;
    }

    public MutableLiveData<List<Tag>> getTags() {
        return tags;
    }

    public MutableLiveData<List<Option>> getOptions() {
        return options;
    }

    public List<Tag> getTagsAsList() {
        return tags.getValue();
    }

    public List<Option> getOptionsAsList() {
        return options.getValue();
    }
}
