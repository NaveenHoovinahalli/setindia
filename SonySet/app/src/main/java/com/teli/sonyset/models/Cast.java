package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by madhuri on 12/3/15.
 */
public class Cast implements Parcelable, Serializable {

    private String name;

    private String description;

    private String thumbnail;

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail == null ? "" : thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.thumbnail);
    }

    public Cast() {
    }

    private Cast(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<Cast> CREATOR = new Parcelable.Creator<Cast>() {
        public Cast createFromParcel(Parcel source) {
            return new Cast(source);
        }

        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
}
