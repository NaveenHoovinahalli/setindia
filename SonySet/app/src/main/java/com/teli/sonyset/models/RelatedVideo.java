package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nith on 3/15/15.
 */
public class RelatedVideo implements Parcelable {

    String videoId;
    String ThumbnailUrl;
    String videoTitle;
    String showName;
    String category;
    String publishedDate;
    String colorCode;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public RelatedVideo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.videoId);
        dest.writeString(this.ThumbnailUrl);
        dest.writeString(this.videoTitle);
        dest.writeString(this.showName);
        dest.writeString(this.category);
        dest.writeString(this.publishedDate);
        dest.writeString(this.colorCode);
    }

    private RelatedVideo(Parcel in) {
        this.videoId = in.readString();
        this.ThumbnailUrl = in.readString();
        this.videoTitle = in.readString();
        this.showName = in.readString();
        this.category = in.readString();
        this.publishedDate = in.readString();
        this.colorCode = in.readString();
    }

    public static final Creator<RelatedVideo> CREATOR = new Creator<RelatedVideo>() {
        public RelatedVideo createFromParcel(Parcel source) {
            return new RelatedVideo(source);
        }

        public RelatedVideo[] newArray(int size) {
            return new RelatedVideo[size];
        }
    };
}
