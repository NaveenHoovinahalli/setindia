package com.teli.sonyset.Utils;

/**
 * Created by madhuri on 15/3/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by madhuri on 10/3/15.
 */
public abstract class SonyObjectRequest extends AsyncTask<String, Void, JSONObject> implements SonyObjectRequestInterface {

    String url;
    Context mContext;

    public SonyObjectRequest(Context mContext, String url) {
        this.url = url;
        this.mContext = mContext;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpclient.execute(httpGet);
            Log.d("ShowFragment", "show response" + response.getStatusLine().getStatusCode());

            if (response!=null) {
                Log.d("MainActivity", "response" + response.getStatusLine().toString());

                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity1 = response.getEntity();

                    if (entity1!=null) {
                        InputStream instream1 = null;
                        try {
                            instream1 = entity1.getContent();
                            JSONObject jsonObject = convertInputStreamToJSONObject(instream1);
                            instream1.close();

                            return jsonObject;
                        }catch (IOException e){
                            e.printStackTrace();}
                    }
                }
            }
        } catch (Exception e) {
            onError(e.getMessage());
            e.printStackTrace();
            Log.d("ShowFragment", "EXCEPTION" + e);
        }
        return null;
    }

    private static JSONObject convertInputStreamToJSONObject(InputStream inputStream)
            throws JSONException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(result);
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        if (s!=null)
            onResponse(s);
        super.onPostExecute(s);
    }
}

