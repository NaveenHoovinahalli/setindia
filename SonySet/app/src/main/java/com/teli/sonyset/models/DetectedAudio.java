package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madhuri on 15/3/15.
 */
public class DetectedAudio implements Parcelable {

    private String name;

    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
    }

    public DetectedAudio() {
    }

    private DetectedAudio(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<DetectedAudio> CREATOR = new Parcelable.Creator<DetectedAudio>() {
        public DetectedAudio createFromParcel(Parcel source) {
            return new DetectedAudio(source);
        }

        public DetectedAudio[] newArray(int size) {
            return new DetectedAudio[size];
        }
    };
}
