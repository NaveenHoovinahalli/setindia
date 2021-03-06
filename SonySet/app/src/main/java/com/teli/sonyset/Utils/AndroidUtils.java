package com.teli.sonyset.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.teli.sonyset.R;

import java.util.Locale;

/**
 * Created by madhuri on 5/3/15.
 */
public class AndroidUtils {

    public static int getScreenHeight(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            return haveConnectedWifi || haveConnectedMobile;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static String getAppVersionNumber(Context context) {
        return getAppVersionNumber(context, null);
    }

    public static String getAppVersionNumber(Context context, String packageName) {
        String versionName;

        if (packageName == null) {
            packageName = context.getPackageName();
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            versionName = "";
        }
        return versionName;
    }

    public static void slide_down(Context ctx, View v) {
        Animation slideDownAnim = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if (slideDownAnim != null) {
            slideDownAnim.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(slideDownAnim);
            }
        }
    }

    public static void slide_up(Context ctx, View v) {
        Animation slideUpAnim = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (slideUpAnim != null) {
            slideUpAnim.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(slideUpAnim);
            }
        }
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getOsVersionName() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }


    public static String getAppVersionCode(Context context) {
        return getAppVersionCode(context, null);
    }

    public static String getAppVersionCode(Context context, String packageName) {
        String versionCode;

        if (packageName == null) {
            packageName = context.getPackageName();
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionCode = Integer.toString(packageInfo.versionCode);
        } catch (Exception e) {
            versionCode = "";
        }

        return versionCode;
    }


    public static String getUniqueId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }


    public static boolean isValidEmail(CharSequence email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            if(phoneNumber.matches("\\d{10}"))
                return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    public static boolean isValidName(String name){
        if(!name.isEmpty()){
            if(name.matches("^[a-zA-Z\\s]+$")){
                return true;
            }
        }
        return false;
    }


}
