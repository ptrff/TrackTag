package ru.ptrff.tracktag.api;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import ru.ptrff.tracktag.api.dto.LoginRequest;
import ru.ptrff.tracktag.api.dto.LoginResponse;
import ru.ptrff.tracktag.api.dto.RegisterRequest;
import ru.ptrff.tracktag.api.dto.RegisterResponse;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.models.Tag;

public class MapsRepository {

    private final MapsApi mapsApi;

    public MapsRepository() {
        if (UserData.getInstance().getAccessToken() != null) {
            mapsApi = MapsClient.getClient(
                    UserData.getInstance().getAccessToken()
            ).create(MapsApi.class);
        } else {
            mapsApi = MapsClient.getClient().create(MapsApi.class);
        }
    }

    public Single<List<Tag>> getAllTags() {
        return mapsApi.getAllTags();
    }

    public Single<RegisterResponse> register(RegisterRequest registerRequest) {
        return mapsApi.register(registerRequest);
    }

    public Single<LoginResponse> login(LoginRequest loginRequest) {
        return mapsApi.login(loginRequest.getUsername(), loginRequest.getPassword());
    }

    public Single<Tag> addTag(double latitude, double longitude, String description, MultipartBody.Part image) {
        return mapsApi.addTag(latitude, longitude, description, image);
    }

    public Single<Tag> addTag(double latitude, double longitude, String description) {
        return mapsApi.addTag(latitude, longitude, description);
    }
}
