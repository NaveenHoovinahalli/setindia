package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 15/3/15.
 */
public class NowPlayingResponse implements Parcelable {

    private String id;

    @SerializedName("program_id")
    private String programId;

    @SerializedName("program_name")
    private String programName;

    @SerializedName("program_link")
    private String programLink;

    @SerializedName("description")
    private String description;

    private String duration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getProgramLink() {
        return programLink;
    }

    public void setProgramLink(String programLink) {
        this.programLink = programLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.programId);
        dest.writeString(this.programName);
        dest.writeString(this.programLink);
        dest.writeString(this.description);
        dest.writeString(this.duration);
    }

    public NowPlayingResponse() {
    }

    private NowPlayingResponse(Parcel in) {
        this.id = in.readString();
        this.programId = in.readString();
        this.programName = in.readString();
        this.programLink = in.readString();
        this.description = in.readString();
        this.duration = in.readString();
    }

    public static final Parcelable.Creator<NowPlayingResponse> CREATOR = new Parcelable.Creator<NowPlayingResponse>() {
        public NowPlayingResponse createFromParcel(Parcel source) {
            return new NowPlayingResponse(source);
        }

        public NowPlayingResponse[] newArray(int size) {
            return new NowPlayingResponse[size];
        }
    };
}
