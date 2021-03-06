package com.teli.sonyset.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.Utils.SonyObjectRequest;
import com.teli.sonyset.models.CountryInfo;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by madhuri on 11/3/15.
 */
public class SplashActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String APP_ID = "";
    @InjectView(R.id.videoView)
    VideoView mVideoView;

    HashMap<String,String> menu = new HashMap<>();
    private boolean alertDisplayed;
    private CountryInfo countryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);


       // com.facebook.AppEventsLogger.activateApp(this, APP_ID);

        String countryCode = getCountryCode();
        Log.d("Country Code test-splash", " code::" + countryCode);


        String versionCode = getVersionNumber();

        if(countryCode.isEmpty()){
            Toast.makeText(this,"No Sim detected",Toast.LENGTH_SHORT).show();
            return;
        }

        if (countryCode!=null && !countryCode.isEmpty()){
            fetchFirstApi(countryCode,versionCode);
          //  Toast.makeText(this,"Unable to fetch Country Code!",Toast.LENGTH_SHORT).show();
        }
        setUpVideoView();
    }

    private String getVersionNumber() {
        return AndroidUtils.getAppVersionNumber(SplashActivity.this);
    }

    private String getCountryCode() {

        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode;
        if(!tm.getSimCountryIso().isEmpty()){
            countryCode = tm.getSimCountryIso();
            Log.d("Country Code test", " code::" + tm.getSimCountryIso());
        }else {
            if(!tm.getNetworkCountryIso().isEmpty()){
                countryCode = tm.getNetworkCountryIso();
                Log.d("Country Code test"," code::"+tm.getNetworkCountryIso());

            }else {
                countryCode = "in";
            }
        }
        Log.d("Country Code test"," code::"+countryCode);

        if (countryCode!=null && !countryCode.isEmpty())
            SonyDataManager.init(this).saveCountryCode(countryCode);
        return countryCode;
    }

    private void setUpVideoView() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        mVideoView.setLayoutParams(params);

        String uriPath = "android.resource://" + getPackageName() + "/" + R.raw.splash_screen_video;
        Uri uri = Uri.parse(uriPath);
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(!AndroidUtils.isNetworkOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry! No Internet Connection", Toast.LENGTH_SHORT).show();
            // noInternet();
            finish();
            return;
        }

        //checkForUpgrade();
        launch();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        //checkForUpgrade();
        launch();
        return false;
    }

    private void checkForUpgrade() {

        //String playStoreVersion = countryInfo.getData().getVersion();

                        /*if(!currentVersion.equals(playStoreVersion)){*/
        String forceUpgrade = countryInfo.getForceUpgrade();
        String message = countryInfo.getMessages().get(0);


        if(forceUpgrade.equals("1") && !message.isEmpty()){
            showDialogForForceUpdate(message);
            alertDisplayed = true;
        }else if(forceUpgrade.equals("0") && !message.isEmpty()){
            alertDisplayed = true;
            showDialogForUpdate(message);
        }else if(forceUpgrade.equals("0") && message.isEmpty()){
            launch();
        }
        //}

    }


    private void launch() {

        Intent intent = new Intent(SplashActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchFirstApi(String countryCode, String versionCode) {

        String url = String.format(Constants.COUNTRY_API,versionCode,countryCode);
        Log.d("SplashActivityFirstApi", "url::" + url);
        SonyObjectRequest request = new SonyObjectRequest(this,url) {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("SplashActivityFirstApi","response::"+jsonObject);
                String currentVersion = AndroidUtils.getAppVersionNumber(SplashActivity.this);

                if(jsonObject!=null){
                    countryInfo = new Gson().fromJson(jsonObject.toString(),CountryInfo.class);
                    if(countryInfo!=null){

                        SonyDataManager.init(SplashActivity.this).saveCountryId(countryInfo.getCcid());
                        SonyDataManager.init(SplashActivity.this).saveNoCountryId(countryInfo.getCid());

                        for (int i = 0; i<countryInfo.getMenuItems().size();i++){
                            SonyDataManager.init(SplashActivity.this).saveMenuItemUrl(countryInfo.getMenuItems().get(i).getTitle(),
                                    countryInfo.getMenuItems().get(i).getUrl());
                        }

                    }
                }
            }

            @Override
            public void onError(String e) {
                Log.d("SplashActivityFirstApi","error::"+e);
            }
        };
        request.execute();
    }


    private void showDialogForForceUpdate(String message) {
        alertDisplayed = true;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Sony SAB Update");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        navigateToPlayStore();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void navigateToPlayStore() {
        if (AndroidUtils.isNetworkOnline(SplashActivity.this)) {
            String sabPlayStoreLink = "https://play.google.com/store/apps/details?id=com.mobicule.sabtv";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sabPlayStoreLink));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(SplashActivity.this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void showDialogForUpdate(String message) {

        alertDisplayed = true;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Sony SAB Update");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        navigateToPlayStore();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       launch();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

}
