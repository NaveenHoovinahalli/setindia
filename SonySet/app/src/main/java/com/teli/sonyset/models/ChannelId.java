package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 16/3/15.
 */
public class ChannelId implements Parcelable {

    @SerializedName("SD")
    private String sdId;

    @SerializedName("HD")
    private String hdId;

    public String getSdId() {
        return sdId;
    }

    public void setSdId(String sdId) {
        this.sdId = sdId;
    }

    public String getHdId() {
        return hdId;
    }

    public void setHdId(String hdId) {
        this.hdId = hdId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sdId);
        dest.writeString(this.hdId);
    }

    public ChannelId() {
    }

    private ChannelId(Parcel in) {
        this.sdId = in.readString();
        this.hdId = in.readString();
    }

    public static final Parcelable.Creator<ChannelId> CREATOR = new Parcelable.Creator<ChannelId>() {
        public ChannelId createFromParcel(Parcel source) {
            return new ChannelId(source);
        }

        public ChannelId[] newArray(int size) {
            return new ChannelId[size];
        }
    };
}
