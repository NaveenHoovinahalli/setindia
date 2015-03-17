package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by madhuri on 13/3/15.
 */
public class Concept implements Parcelable {

    @SerializedName("und")
    ArrayList<ConceptValue> values = new ArrayList<>();

    public ArrayList<ConceptValue> getValues() {
        return values;
    }

    public void setValues(ArrayList<ConceptValue> values) {
        this.values = values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.values);
    }

    public Concept() {
    }

    private Concept(Parcel in) {
        this.values = (ArrayList<ConceptValue>) in.readSerializable();
    }

    public static final Parcelable.Creator<Concept> CREATOR = new Parcelable.Creator<Concept>() {
        public Concept createFromParcel(Parcel source) {
            return new Concept(source);
        }

        public Concept[] newArray(int size) {
            return new Concept[size];
        }
    };
}
