package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 12/3/15.
 */
public class Synopsis implements Parcelable {

    private String title;

    private int date;

    private String text;

    @SerializedName("video_title")
    private String videoTitle;

    @SerializedName("bc_id")
    private String brightCoveId;

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getBrightCoveId() {
        return brightCoveId;
    }

    public void setBrightCoveId(String brightCoveId) {
        this.brightCoveId = brightCoveId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeInt(this.date);
        dest.writeString(this.text);
        dest.writeString(this.videoTitle);
        dest.writeString(this.brightCoveId);
    }

    public Synopsis() {
    }

    private Synopsis(Parcel in) {
        this.title = in.readString();
        this.date = in.readInt();
        this.text = in.readString();
        this.videoTitle = in.readString();
        this.brightCoveId = in.readString();
    }

    public static final Creator<Synopsis> CREATOR = new Creator<Synopsis>() {
        public Synopsis createFromParcel(Parcel source) {
            return new Synopsis(source);
        }

        public Synopsis[] newArray(int size) {
            return new Synopsis[size];
        }
    };
}
