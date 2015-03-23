package com.teli.sonyset.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 23/3/15.
 */
public class BrightCoveThumbnail {

    @SerializedName("videoStillURL")
    private String videoStillUrl;

    public String getVideoStillUrl() {
        return videoStillUrl == null ? "" : videoStillUrl;
    }

    public void setVideoStillUrl(String videoStillUrl) {
        this.videoStillUrl = videoStillUrl;
    }
}
