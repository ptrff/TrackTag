package ru.ptrff.tracktag.api;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.ptrff.tracktag.api.dto.LoginRequest;
import ru.ptrff.tracktag.api.dto.LoginResponse;
import ru.ptrff.tracktag.api.dto.RegisterRequest;
import ru.ptrff.tracktag.api.dto.RegisterResponse;
import ru.ptrff.tracktag.models.Tag;

public interface ApiService {
    @GET("api/tags/")
    Single<List<Tag>> getAllTags();

    @POST("api/auth/register")
    @Headers("Content-Type: application/json")
    Single<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @FormUrlEncoded
    @POST("api/auth/jwt/login")
    Single<LoginResponse> login(@Field(value = "username") String username, @Field(value = "password") String password);
}
