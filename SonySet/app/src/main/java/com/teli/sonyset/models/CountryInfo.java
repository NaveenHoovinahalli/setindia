package com.teli.sonyset.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sahana on 10/3/15.
 */
public class CountryInfo implements Parcelable{

    String title;

    @SerializedName("field_android_force_upgrade")
    String forceUpgrade;

    @SerializedName("field_android_message")
    ArrayList<String> messages;

    String ccid;

    String cid;

    @SerializedName("menu")
    ArrayList<MenuItem> menuItems;

    @SerializedName("popup")
    ArrayList<MenuItem> popUpItems;

    public String getTitle() {
        return title == null? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForceUpgrade() {
        return forceUpgrade == null ? "" : forceUpgrade;
    }

    public void setForceUpgrade(String forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public String getCcid() {
        return ccid == null ? "" : ccid;
    }

    public void setCcid(String ccid) {
        this.ccid = ccid;
    }

    public ArrayList<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public ArrayList<MenuItem> getPopUpItems() {
        return popUpItems;
    }

    public void setPopUpItems(ArrayList<MenuItem> popUpItems) {
        this.popUpItems = popUpItems;
    }

    public String getCid() {
        return cid == null ? "" : cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }


}
