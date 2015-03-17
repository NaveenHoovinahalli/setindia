package com.teli.sonyset.Utils;

import org.json.JSONObject;

/**
 * Created by madhuri on 15/3/15.
 */
public interface SonyObjectRequestInterface {
    public void onResponse(JSONObject jsonObject);
    public void onError(String e);
}
