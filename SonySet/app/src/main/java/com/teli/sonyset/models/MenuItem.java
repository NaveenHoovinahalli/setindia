package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sahana on 10/3/15.
 */
public class MenuItem implements Parcelable {

    private String title;

    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    public MenuItem() {
    }

    private MenuItem(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<MenuItem> CREATOR = new Parcelable.Creator<MenuItem>() {
        public MenuItem createFromParcel(Parcel source) {
            return new MenuItem(source);
        }

        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };
}
