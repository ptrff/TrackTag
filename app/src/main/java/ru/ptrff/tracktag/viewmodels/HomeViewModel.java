package ru.ptrff.tracktag.viewmodels;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.api.MapsRepository;
import ru.ptrff.tracktag.data.Options;
import ru.ptrff.tracktag.models.Option;
import ru.ptrff.tracktag.models.Tag;

public class HomeViewModel extends ViewModel {

    private MapsRepository repo;
    private final MutableLiveData<List<Option>> options = new MutableLiveData<>();
    private final MutableLiveData<List<Tag>> tags = new MutableLiveData<>();
    private final List<Tag> allTags = new ArrayList<>();
    int position = 0;

    public HomeViewModel() {
        repo = new MapsRepository();

        // Инициализация данных для списков
        options.setValue(Options.guest);
    }


    @SuppressLint("CheckResult")
    public void getData() {
        repo
                .getAllTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        receivedTags -> {
                            allTags.addAll(receivedTags);
                            Collections.reverse(allTags);
                            loadMore();
                        },
                        error -> {
                            Log.e(getClass().getCanonicalName(), error.toString());
                        }
                );
    }

    public void loadMore(){
        if(position + 10 <= allTags.size()){
            position += 10;
        }else{
            position += allTags.size() - position;
        }
        tags.postValue(allTags.subList(0, position));
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
