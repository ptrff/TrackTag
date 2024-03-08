package ru.ptrff.tracktag.api;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ru.ptrff.tracktag.models.RegisterRequest;
import ru.ptrff.tracktag.models.Tag;

public class MapsRepository {

    private final ApiService apiService;

    public MapsRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public Single<List<Tag>> getAllTags() {
        return apiService.getAllTags();
    }

    public void register(RegisterRequest registerRequest){
        apiService.register(registerRequest);
    }
}
