package com.teli.sonyset.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sahana on 10/3/15.
 */
public class SonyDataManager {

    private static final String HD_MENU = "hd_is_from_menu";
    private static final String PRECAPS_MENU = "precap_is_menu";
    private final SharedPreferences msharedpreference;
    Context context;
    public static final String SHOWS="shows";

    public SonyDataManager(Context context){
       this.context = context;
       msharedpreference = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
    }

    public static SonyDataManager init(Context context){
        return new SonyDataManager(context);
    }

    public String getCountryId(){
        return msharedpreference.getString(Constants.COUNTRY_ID,"");
    }

    public void saveCountryId(String s){
        msharedpreference.edit().putString(Constants.COUNTRY_ID,s).apply();
    }

    public void saveScheduledId(String id, int value){
        msharedpreference.edit().putInt(id,value).apply();
    }

    public int getScheduledId(String id){
        return msharedpreference.getInt(id,0);
    }

    public void removeSharedPrefrence(String id){
        msharedpreference.edit().remove(id).apply();
    }

    public void setShows(String response){
        msharedpreference.edit().putString(SHOWS,response).apply();
    }

    public String getShows(){
        return msharedpreference.getString(SHOWS,"");
    }

    public void saveCountryCode(String countryCode) {
        msharedpreference.edit().putString(Constants.COUNTRY_CODE,countryCode).apply();
    }

    public String getConutryCode() {
        return msharedpreference.getString(Constants.COUNTRY_CODE,"");
    }

    public void saveHdIsFromMenu(boolean b) {
        msharedpreference.edit().putBoolean(HD_MENU, b).apply();
    }

    public boolean getSavedHdIsFromMenu() {
        return msharedpreference.getBoolean(HD_MENU, false);
    }

    public void savePrecapsIsFromMenu(boolean b) {
        msharedpreference.edit().putBoolean(PRECAPS_MENU,b).apply();
    }

    public boolean getPrecapsIsFromMenu(boolean b) {
       return msharedpreference.getBoolean(PRECAPS_MENU,false);
    }

    public void saveMenuItemUrl(String title, String url) {
        msharedpreference.edit().putString(title,url).apply();
    }

    public String getMenuItemUrl(String s) {
        return msharedpreference.getString(s,"");
    }
}
