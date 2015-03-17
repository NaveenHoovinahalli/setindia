package com.teli.sonyset.Utils;

/**
 * Created by madhuri on 5/3/15.
 */

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nith on 1/25/15.
 */
public class SetRequestQueue {
    private static SetRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private SetRequestQueue(Context context){
        this.mContext = context;
        this.mRequestQueue = getRequestQueue();
    }

    public static synchronized SetRequestQueue getInstance(Context context){
        if(mInstance == null){
            mInstance = new SetRequestQueue(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.mRequestQueue = requestQueue;
    }

}

