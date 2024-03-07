package ru.ptrff.tracktag.api;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import ru.ptrff.tracktag.models.Tag;

public interface ApiService {
    @GET("api/tags/")
    Single<List<Tag>> getAllTags();
}
