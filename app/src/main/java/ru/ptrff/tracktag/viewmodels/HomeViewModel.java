package ru.ptrff.tracktag.viewmodels;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.util.Log;

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

public class HomeViewModel extends ViewModel {
    private MapsRepository repo;
    private TagLocalRepository localRepo;
    private final MutableLiveData<List<Option>> options = new MutableLiveData<>();
    private final MutableLiveData<List<Tag>> tags = new MutableLiveData<>();

    public HomeViewModel() {
        repo = new MapsRepository();

        // Инициализация данных для списков
        options.setValue(UserData.getInstance().isLoggedIn() ? Options.user : Options.guest);
    }

    public void setLocalRepo(TagLocalRepository localRepo) {
        this.localRepo = localRepo;
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

    public void addNetworkConnectionListener(ConnectivityManager connectivityManager) {
        if (connectivityManager.isDefaultNetworkActive()) {
            getData();
        }
        connectivityManager.addDefaultNetworkActiveListener(() -> {
            if (connectivityManager.isDefaultNetworkActive()) {
                getData();
            }
        });
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
