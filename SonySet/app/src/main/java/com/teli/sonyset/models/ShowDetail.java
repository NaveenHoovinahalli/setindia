package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 5/3/15.
 */
public class ShowDetail implements Parcelable {

    @SerializedName("showid")
    private String showId;

    @SerializedName("show_title")
    private String showTitle;

    @SerializedName("genre")
    private String genre;

    @SerializedName("colourcode")
    private String colorCode;

    @SerializedName("show_logo")
    private String showLogo;

    @SerializedName("show_upcoming_new")
    private String showUpcomingNew;

    @SerializedName("show_ddt")
    private String showDate;

    @SerializedName("banner")
    private String banner;

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

    public String getGenre() {
        return genre == null ? "": genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getColorCode() {
        return colorCode == null ? "" : colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getShowLogo() {
        return showLogo == null ? "" : showLogo;
    }

    public void setShowLogo(String showLogo) {
        this.showLogo = showLogo;
    }

    public String getShowUpcomingNew() {
        return showUpcomingNew == null ? "":showUpcomingNew;
    }

    public void setShowUpcomingNew(String showUpcomingNew) {
        this.showUpcomingNew = showUpcomingNew;
    }

    public String getShowDate() {
        return showDate == null ? "" : showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getBanner() {
        return banner == null ? "" : banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.showId);
        dest.writeString(this.showTitle);
        dest.writeString(this.genre);
        dest.writeString(this.colorCode);
        dest.writeString(this.showLogo);
        dest.writeString(this.showUpcomingNew);
        dest.writeString(this.showDate);
        dest.writeString(this.banner);
    }

    public ShowDetail() {
    }

    private ShowDetail(Parcel in) {
        this.showId = in.readString();
        this.showTitle = in.readString();
        this.genre = in.readString();
        this.colorCode = in.readString();
        this.showLogo = in.readString();
        this.showUpcomingNew = in.readString();
        this.showDate = in.readString();
        this.banner = in.readString();
    }

    public static final Parcelable.Creator<ShowDetail> CREATOR = new Parcelable.Creator<ShowDetail>() {
        public ShowDetail createFromParcel(Parcel source) {
            return new ShowDetail(source);
        }

        public ShowDetail[] newArray(int size) {
            return new ShowDetail[size];
        }
    };
}
