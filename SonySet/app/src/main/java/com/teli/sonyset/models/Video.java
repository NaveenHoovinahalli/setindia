package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 6/3/15.
 */
public class Video implements Parcelable {

    @SerializedName("showid")
    private String showId;

    @SerializedName("title")
    private String showTitle;

    @SerializedName("show_name")
    private String showName;

    @SerializedName("bc_id")
    private String brightCoveId;

    @SerializedName("player")
    private String player;

    @SerializedName("episodenumber")
    private String episodeNumber;

    @SerializedName("on_air_date")
    private String onAirDate;

    @SerializedName("duration")
    private String duration;

    @SerializedName("colourcode")
    private String colorCode;

    public String getShowId() {
        return showId == null ? "" : showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getShowTitle() {
        return showTitle == null ? "" : showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getShowName() {
        return showName == null ? "" : showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getBrightCoveId() {
        return brightCoveId == null ? "" : brightCoveId;
    }

    public void setBrightCoveId(String brightCoveId) {
        this.brightCoveId = brightCoveId;
    }

    public String getPlayer() {
        return player == null ? "" : player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getEpisodeNumber() {
        return episodeNumber == null ? "" : episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getOnAirDate() {
        return onAirDate == null ? "" : onAirDate;
    }

    public void setOnAirDate(String onAirDate) {
        this.onAirDate = onAirDate;
    }

    public String getDuration() {
        return duration == null ? "" : duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getColorCode() {
        return colorCode == null ? "" : colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.showId);
        dest.writeString(this.showTitle);
        dest.writeString(this.showName);
        dest.writeString(this.brightCoveId);
        dest.writeString(this.player);
        dest.writeString(this.episodeNumber);
        dest.writeString(this.onAirDate);
        dest.writeString(this.duration);
        dest.writeString(this.colorCode);
    }

    public Video() {
    }

    private Video(Parcel in) {
        this.showId = in.readString();
        this.showTitle = in.readString();
        this.showName = in.readString();
        this.brightCoveId = in.readString();
        this.player = in.readString();
        this.episodeNumber = in.readString();
        this.onAirDate = in.readString();
        this.duration = in.readString();
        this.colorCode = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
