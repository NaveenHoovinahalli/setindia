package com.teli.sonyset.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.teli.audio.common.RecordController;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SetRequestQueue;
import com.teli.sonyset.models.ChannelId;
import com.teli.sonyset.models.DetectedAudio;
import com.teli.sonyset.views.BusProvider;

import org.json.JSONObject;

public class BackgroundService extends Service {


    public static final String RESPONSE_KEY = "audio_response";
    public static final String MY_ACTION = "action_broadcast";
    private RecordController mController;
    private RequestQueue requestQueue;
    private ChannelId channelIds;
    private String hdChannelId;
    private String sdChannelId;
    private String previousChannelId = "-1";
    private boolean sonySet;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        BusProvider.getInstance().register(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mController = new RecordController(this);
        mController.setResponseCallback(new MyControllerEvents());
        requestQueue = Volley.newRequestQueue(this);
        fetchChannelId();

        Log.d("overlay_log", "BG_Service::onStartCommand");
        if (isRecording()) {
            stopRecording();
        } else {
            Log.d("overlay_log", "BG_Service::startrecording");
            startRecording();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private class MyControllerEvents extends RecordController.ControllerEventCallback {

        @Override
        protected void doSendRequest() {
            super.doSendRequest();
            Log.d("overlay_log", "BG_Service::doSendRequest");

        }

        @Override
        protected boolean audioDetectedResponse(String response, int statuscode) {

            super.audioDetectedResponse(response, statuscode);

            if (channelIds!=null && !channelIds.toString().isEmpty()){
                hdChannelId = channelIds.getHdId();
                sdChannelId =   channelIds.getSdId();
            }
            Log.d("overlay_log", "BG_Service::Audio Detected::" + response);

            DetectedAudio detectedAudio = new Gson().fromJson(response, DetectedAudio.class);

            String channelId = detectedAudio.getId();
            //channelId = "256";
            if (!previousChannelId.equals(channelId)){
                if (channelId.equals(hdChannelId) || channelId.equals(sdChannelId)){
                    Log.d("Sercvice","Call intent");
                    previousChannelId = channelId;
                    Intent intent = new Intent();
                    intent.putExtra(RESPONSE_KEY, response);
                    intent.setAction(MY_ACTION);
                    sendBroadcast(intent);

                    sonySet = true;
                }
            }
            return sonySet;
        }

        @Override
        protected void audioRecorderInited(int sampleRate) {
            super.audioRecorderInited(sampleRate);
            Log.d("overlay_log", "BG_Service::Audio Init");
        }

        @Override
        protected void audioRecorderInitError() {
            super.audioRecorderInitError();
            Log.d("overlay_log", "BG_Service::Audio ERROR");
        }
    }

    private void startRecording() {
        if (mController != null) {
            mController.startRecognition();
            Log.d("overlay_log", "BG_Service::Recording Started");
        }
    }

    private void stopRecording() {
        if (mController != null) {
            mController.stopRecognition();
            Log.d("overlay_log", "BG_Service::Recording Stopped");
        }
    }

    private boolean isRecording() {
        if (mController != null) {
            return mController.isRecording();
        } else
            return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mController != null) {
            mController.destroy();
        }
    }

    public void fetchChannelId(){
        JsonObjectRequest request = new JsonObjectRequest(Constants.URL_CHANNEL_ID,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response!=null && !response.toString().isEmpty()){
                            Log.d("BackgroundService","Response::" + response);

                            channelIds = new Gson().fromJson(response.toString(), ChannelId.class);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        SetRequestQueue.getInstance(this).getRequestQueue().add(request);
    }
}
