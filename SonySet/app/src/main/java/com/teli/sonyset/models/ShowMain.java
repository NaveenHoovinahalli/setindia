package com.teli.sonyset.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by madhuri on 12/3/15.
 */
public class ShowMain implements Parcelable{

    String title;

    @SerializedName("field_text1")
    Concept timeConcept = new Concept();

    @SerializedName("field_long_text2")
    Concept concept = new Concept();

    String banner;

    @SerializedName("show_logo")
    String showLogo;

    @SerializedName("episodess")
    ArrayList<Video> episodess = new ArrayList<>();

    @SerializedName("promos")
    ArrayList<Video> promos = new ArrayList<>();

    @SerializedName("cast")
    ArrayList<Cast> casts = new ArrayList<>();

    @SerializedName("synopsis")
    ArrayList<Synopsis> synopsises = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Concept getTimeConcept() {
        return timeConcept;
    }

    public void setTimeConcept(Concept timeConcept) {
        this.timeConcept = timeConcept;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public ArrayList<Video> getEpisodess() {
        return episodess;
    }

    public void setEpisodess(ArrayList<Video> episodess) {
        this.episodess = episodess;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public ArrayList<Video> getPromos() {
        return promos;
    }

    public void setPromos(ArrayList<Video> promos) {
        this.promos = promos;
    }

    public ArrayList<Cast> getCasts() {
        return casts;
    }

    public void setCasts(ArrayList<Cast> casts) {
        this.casts = casts;
    }

    public ArrayList<Synopsis> getSynopsises() {
        return synopsises;
    }

    public void setSynopsises(ArrayList<Synopsis> synopsises) {
        this.synopsises = synopsises;
    }

    public String getShowLogo() {
        return showLogo == null ? "" : showLogo;
    }

    public void setShowLogo(String showLogo) {
        this.showLogo = showLogo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeParcelable(this.timeConcept, 0);
        dest.writeParcelable(this.concept, 0);
        dest.writeString(this.banner);
        dest.writeString(this.showLogo);
        dest.writeSerializable(this.episodess);
        dest.writeSerializable(this.promos);
        dest.writeSerializable(this.casts);
        dest.writeSerializable(this.synopsises);
    }

    public ShowMain() {
    }

    private ShowMain(Parcel in) {
        this.title = in.readString();
        this.timeConcept = in.readParcelable(Concept.class.getClassLoader());
        this.concept = in.readParcelable(Concept.class.getClassLoader());
        this.banner = in.readString();
        this.showLogo = in.readString();
        this.episodess = (ArrayList<Video>) in.readSerializable();
        this.promos = (ArrayList<Video>) in.readSerializable();
        this.casts = (ArrayList<Cast>) in.readSerializable();
        this.synopsises = (ArrayList<Synopsis>) in.readSerializable();
    }

    public static final Creator<ShowMain> CREATOR = new Creator<ShowMain>() {
        public ShowMain createFromParcel(Parcel source) {
            return new ShowMain(source);
        }

        public ShowMain[] newArray(int size) {
            return new ShowMain[size];
        }
    };
}
