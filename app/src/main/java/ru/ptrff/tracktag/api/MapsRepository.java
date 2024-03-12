package ru.ptrff.tracktag.api;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import ru.ptrff.tracktag.api.dto.LoginRequest;
import ru.ptrff.tracktag.api.dto.LoginResponse;
import ru.ptrff.tracktag.api.dto.RegisterRequest;
import ru.ptrff.tracktag.api.dto.RegisterResponse;
import ru.ptrff.tracktag.models.Tag;

public class MapsRepository {

    private final ApiService apiService;

    public MapsRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public Single<List<Tag>> getAllTags() {
        return apiService.getAllTags();
    }

    public Single<RegisterResponse> register(RegisterRequest registerRequest){
        return apiService.register(registerRequest);
    }

    public Single<LoginResponse> login(LoginRequest loginRequest){
        return apiService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }
}
