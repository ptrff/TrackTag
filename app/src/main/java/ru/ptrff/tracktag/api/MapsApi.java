package ru.ptrff.tracktag.api;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import ru.ptrff.tracktag.api.dto.LoginResponse;
import ru.ptrff.tracktag.api.dto.RegisterRequest;
import ru.ptrff.tracktag.api.dto.RegisterResponse;
import ru.ptrff.tracktag.models.Tag;

public interface MapsApi {
    @GET("api/tags/")
    Single<List<Tag>> getAllTags();

    @POST("api/auth/register")
    @Headers("Content-Type: application/json")
    Single<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @FormUrlEncoded
    @POST("api/auth/jwt/login")
    Single<LoginResponse> login(@Field(value = "username") String username, @Field(value = "password") String password);

    @Multipart
    @POST("api/tags/")
    Single<Tag> addTag(
            @Part("latitude")  double latitude,
            @Part("longitude")  double longitude,
            @Part("description") String description,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("api/tags/")
    Single<Tag> addTag(
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("description") String description
    );

    @DELETE("api/tags/{tag_id}")
    Single<Void> deleteTag(@Path("tag_id") String tagId);

    @POST("api/tags/{tag_id}/likes")
    Single<Tag> likeTag(@Path("tag_id") String tagId);

    @DELETE("api/tags/{tag_id}/likes")
    Single<Void> deleteLikeFromTag(@Path("tag_id") String tagId);
}
