package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naveen on 12/3/15.
 */
public class ScheduleDetails implements Parcelable {

    @SerializedName("episode_schedule_id")
     String episodeScheduleId;

    @SerializedName("program_title")
    String programTitle;

    @SerializedName("schedule_item_date_time")
    String scheduleItemDateTime;

    @SerializedName("title")
    String title;

    @SerializedName("uri")
    String uri;

    @SerializedName("colourcode")
    String colourcode;

    @SerializedName("nid")
    String nid;

    @SerializedName("nowshowing")
    String nowShowing;

    public String getNowShowing() {
        return nowShowing;
    }

    public void setNowShowing(String nowShowing) {
        this.nowShowing = nowShowing;
    }

    public String getColourcode() {
        return colourcode;
    }

    public void setColourcode(String colourcode) {
        this.colourcode = colourcode;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public static Creator<ScheduleDetails> getCreator() {
        return CREATOR;
    }

    public String getEpisodeScheduleId() {
        return episodeScheduleId;
    }

    public void setEpisodeScheduleId(String episodeScheduleId) {
        this.episodeScheduleId = episodeScheduleId;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }

    public String getScheduleItemDateTime() {
        return scheduleItemDateTime;
    }

    public void setScheduleItemDateTime(String scheduleItemDateTime) {
        this.scheduleItemDateTime = scheduleItemDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ScheduleDetails() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.episodeScheduleId);
        dest.writeString(this.programTitle);
        dest.writeString(this.scheduleItemDateTime);
        dest.writeString(this.title);
        dest.writeString(this.uri);
        dest.writeString(this.colourcode);
        dest.writeString(this.nid);
        dest.writeString(this.nowShowing);
    }

    private ScheduleDetails(Parcel in) {
        this.episodeScheduleId = in.readString();
        this.programTitle = in.readString();
        this.scheduleItemDateTime = in.readString();
        this.title = in.readString();
        this.uri = in.readString();
        this.colourcode = in.readString();
        this.nid = in.readString();
        this.nowShowing = in.readString();
    }

    public static final Creator<ScheduleDetails> CREATOR = new Creator<ScheduleDetails>() {
        public ScheduleDetails createFromParcel(Parcel source) {
            return new ScheduleDetails(source);
        }

        public ScheduleDetails[] newArray(int size) {
            return new ScheduleDetails[size];
        }
    };
}
