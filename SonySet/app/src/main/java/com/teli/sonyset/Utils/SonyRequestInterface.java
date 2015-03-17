package com.teli.sonyset.Utils;

import org.json.JSONArray;

/**
 * Created by madhuri on 10/3/15.
 */
public interface SonyRequestInterface {
    public void onResponse(JSONArray jsonArray);
    public void onError(String e);
}
