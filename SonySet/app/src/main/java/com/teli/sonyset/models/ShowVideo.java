package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 13/3/15.
 */
public class ShowVideo implements Parcelable{

    @SerializedName("bc_id")
    private String brightCoveId;

    @SerializedName("episodenumber")
    private String episodeNumber;

    @SerializedName("on_air_date")
    private String onAirDate;

    private String duration;

    public String getBrightCoveId() {
        return brightCoveId;
    }

    public void setBrightCoveId(String brightCoveId) {
        this.brightCoveId = brightCoveId;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getOnAirDate() {
        return onAirDate;
    }

    public void setOnAirDate(String onAirDate) {
        this.onAirDate = onAirDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.brightCoveId);
        dest.writeString(this.episodeNumber);
        dest.writeString(this.onAirDate);
        dest.writeString(this.duration);
    }

    public ShowVideo() {
    }

    private ShowVideo(Parcel in) {
        this.brightCoveId = in.readString();
        this.episodeNumber = in.readString();
        this.onAirDate = in.readString();
        this.duration = in.readString();
    }

    public static final Creator<ShowVideo> CREATOR = new Creator<ShowVideo>() {
        public ShowVideo createFromParcel(Parcel source) {
            return new ShowVideo(source);
        }

        public ShowVideo[] newArray(int size) {
            return new ShowVideo[size];
        }
    };
}
