package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by naveen on 12/3/15.
 */
public class ScheduleDayDetail implements Parcelable {

    @SerializedName("date")
    String date;

    @SerializedName("details")
    ArrayList<ScheduleDetails> scheduleDetailsArrayList=new ArrayList<>();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<ScheduleDetails> getScheduleDetailsArrayList() {
        return scheduleDetailsArrayList;
    }

    public void setScheduleDetailsArrayList(ArrayList<ScheduleDetails> scheduleDetailsArrayList) {
        this.scheduleDetailsArrayList = scheduleDetailsArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeSerializable(this.scheduleDetailsArrayList);
    }

    public ScheduleDayDetail() {
    }

    private ScheduleDayDetail(Parcel in) {
        this.date = in.readString();
        this.scheduleDetailsArrayList = (ArrayList<ScheduleDetails>) in.readSerializable();
    }

    public static final Parcelable.Creator<ScheduleDayDetail> CREATOR = new Parcelable.Creator<ScheduleDayDetail>() {
        public ScheduleDayDetail createFromParcel(Parcel source) {
            return new ScheduleDayDetail(source);
        }

        public ScheduleDayDetail[] newArray(int size) {
            return new ScheduleDayDetail[size];
        }
    };
}
