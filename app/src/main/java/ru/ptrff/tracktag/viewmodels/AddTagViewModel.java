package ru.ptrff.tracktag.viewmodels;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.ptrff.tracktag.api.MapsRepository;

public class AddTagViewModel extends ViewModel {

    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private Uri imageUri;
    private String description;
    private double latitude, longitude;

    public AddTagViewModel() {
    }

    public void createImageUri(ContentResolver resolver) {
        String imageFileName = "photo";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        setImageUri(imageUri);
    }

    @SuppressLint("CheckResult")
    public void createTag(ContentResolver resolver, double latitude, double longitude, String description) {
        if (imageUri == null) {
            createTag(latitude, longitude, description);
        } else {
            Observable
                    .fromCallable(() -> convertToByteArray(resolver, imageUri))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(byteArray -> {
                        createTag(latitude, longitude, description, byteArray);
                    }, throwable -> {
                        success.postValue(false);
                        Log.e(getClass().getCanonicalName(), "Error converting image: " + throwable.getMessage());
                    });
        }
    }

    @SuppressLint("CheckResult")
    private void createTag(double latitude, double longitude, String description, byte[] byteArray) {
        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), byteArray);

        MapsRepository repo = new MapsRepository();
        repo.addTag(
                        latitude,
                        longitude,
                        description,
                        MultipartBody.Part.createFormData("image", "image.png", imageBody)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imgBBResponse -> {
                    success.postValue(true);
                }, throwable -> {
                    success.postValue(false);
                    Log.e(getClass().getCanonicalName(), "Error creating tag: " + throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    private void createTag(double latitude, double longitude, String description) {
        MapsRepository repo = new MapsRepository();
        repo.addTag(
                        latitude,
                        longitude,
                        description
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imgBBResponse -> {
                    success.postValue(true);
                }, throwable -> {
                    success.postValue(false);
                    Log.e(getClass().getCanonicalName(), "Error creating tag: " + throwable.getMessage());
                });
    }

    private byte[] convertToByteArray(ContentResolver resolver, Uri imageUri) {
        try {
            InputStream inputStream = resolver.openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            byte[] byteArray = byteBuffer.toByteArray();
            inputStream.close();
            return byteArray;
        } catch (IOException e) {
            Log.e(getClass().getCanonicalName(), "Error getting byte array from uri: " + e.getMessage());
            return null;
        }
    }

    public MutableLiveData<Boolean> getSuccess() {
        return success;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}