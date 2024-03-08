package ru.ptrff.tracktag.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Tag implements Parcelable {
    @SerializedName("id")
    private String id;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("description")
    private String description;

    @SerializedName("image")
    private String image;

    @SerializedName("likes")
    private int likes;

    @SerializedName("is_liked")
    private boolean isLiked;

    @SerializedName("user")
    private User user;

    public Tag(String id, double latitude, double longitude, String description, String image, int likes, boolean isLiked, User user) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.image = image;
        this.likes = likes;
        this.isLiked = isLiked;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    //Parcelable
    protected Tag(Parcel in) {
        id = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        image = in.readString();
        likes = in.readInt();
        isLiked = in.readByte() != 0;
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeInt(this.likes);
        dest.writeByte(this.isLiked ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.user, flags);
    }
}
