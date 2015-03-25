package com.teli.sonyset.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 24/3/15.
 */
public class OnOffState {

    @SerializedName("on_state")
    private String onState;

    private String message;

    public String getOnState() {
        return onState;
    }

    public void setOnState(String onState) {
        this.onState = onState;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
