package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 13/3/15.
 */
public class ConceptValue implements Parcelable {

    private String value;

    private String format;

    @SerializedName("safe_value")
    private String safeValue;

    public String getValue() {
        return value == null ? "" : value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFormat() {
        return format == null ? "" : format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSafeValue() {
        return safeValue == null ? "" : safeValue;
    }

    public void setSafeValue(String safeValue) {
        this.safeValue = safeValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.format);
        dest.writeString(this.safeValue);
    }

    public ConceptValue() {
    }

    private ConceptValue(Parcel in) {
        this.value = in.readString();
        this.format = in.readString();
        this.safeValue = in.readString();
    }

    public static final Parcelable.Creator<ConceptValue> CREATOR = new Parcelable.Creator<ConceptValue>() {
        public ConceptValue createFromParcel(Parcel source) {
            return new ConceptValue(source);
        }

        public ConceptValue[] newArray(int size) {
            return new ConceptValue[size];
        }
    };
}
