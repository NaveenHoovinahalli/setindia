package com.teli.sonyset;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.teli.sonyset.Utils.AndroidUtils;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by sahana on 23/3/15.
 */
public class SonySet extends Application {


    public static String deviceId, deviceMake, deviceModel, appVersion, deviceImei, language;

    private static final String PROPERTY_ID = "sss";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker > mTrackers = new HashMap<TrackerName, Tracker>();

    public SonySet() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        deviceId = AndroidUtils.getUniqueId(this);
        deviceMake = AndroidUtils.getDeviceManufacturer();
        deviceModel = AndroidUtils.getDeviceModel();
        appVersion = AndroidUtils.getAppVersionCode(this);
        deviceImei = AndroidUtils.getDeviceImei(this);
        language = AndroidUtils.getDeviceLanguage();

    }



}
