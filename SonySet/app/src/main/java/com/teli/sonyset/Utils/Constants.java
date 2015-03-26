package com.teli.sonyset.Utils;

/**
 * Created by madhuri on 5/3/15.
 */
public class Constants {

    public static final String BRIGHT_COVE_TOKEN = "KOXTTwEYaBxRZp9AhnsUME6jrgN8qslgoeNj-zn3m5xzLsBoWSMSQA..";
    public static final String BRIGHT_COVE_TOKEN_EPISODE = "pJgVx7pOTpXw5m8_D8Rk2yHwym1XqPnjRRmfjnWMxZRo80GMSna1QQ..";
    public static final String BRIGHT_COVE_THUMBNAIL = "http://api.brightcove.com/services/library?" +
            "command=find_video_by_id&video_id=%s&video_fields=videoStillURL&media_delivery=http&token="+BRIGHT_COVE_TOKEN;

    public static final String BRIGHT_COVE_EPISODE_THUMBNAIL = "http://api.brightcove.com/services/library?" +
            "command=find_video_by_id&video_id=%s&video_fields=videoStillURL&media_delivery=http&token="+BRIGHT_COVE_TOKEN_EPISODE;

    public static final String EPG_BASE_URL = "http://appcms.setindia.com";

    public static final String BASE_URL = "http://beta.setindia.com/setindia_api/";
    public static final String COUNTRY_API = BASE_URL + "upgrade/android.json?versionNo=%s&cc=%s";
    public static final String ALL_SHOWS = BASE_URL + "shows/retrieve.json?showid=0&cid=%s&sw=%s&sh=%s";
    public static final String ALL_PROMOS = BASE_URL + "video/promo.json?cid=%s";
    public static final String ALL_EPISODES = BASE_URL + "video/episode.json?cid=%s";
    public static final String ALL_VIDEOS = BASE_URL + "video/postcap.json?cid=%s";
    public static final String SHOW_DETAILS = BASE_URL + "shows/retrieve.json?showid=%s&cid=%s&sw=%s&sh=%s";
    public static final String URL_NOW_PLAYING = EPG_BASE_URL + "/sony_set/now_playing/";
    public static final String URL_ON_OFF_STATE = EPG_BASE_URL + "/sony_set/get_second_screen_on_off_status/%s/";
    public static final String URL_CHANNEL_ID = EPG_BASE_URL + "/sony_set/get_channel_id/";
    public static final String SUBMIT_FEEDBACK = BASE_URL + "feedbacksubmit/create.json?nid=%s&showid=%s&cid=%s&fullname=%s&email=%s&contactno=%s&usermsg=%s";


    //Constants for SonyDataManager
    public static final String PREFS = "SonyPreference";
    public static final String COUNTRY_ID = "country_id";
    public static final String SHOW_TITLE = "show_title";
    public static final String SHOW_NID = "show_nid";
    public static final String SHOW_ID = "show_id";
    public static final String NO_COUNTRY_ID = "no_country_id";
    //end


    public static final String SCHEDULE_SD=BASE_URL + "episode/%s.json?&hd=0&ln=en";
    public static final String SCHEDULE_HD=BASE_URL + "episode/%s.json?&hd=1&ln=en";

    public static final String VIDEO_ID = "bright_cove_video_id";
    public static final String EPISODE_RESPONSE = "episode_response_object";
    public static final String EPISODE_THUMBNAILS = "episode_thumbnails";
    public static final String IS_EPISODE = "is_episode";
    public static final String IS_PROMO = "is_promo";
    public static final String PROMO_RESPONSE = "promo_response_object";
    public static final String PROMO_THUMBNAILS = "promo_thumbnails";
    public static final String OPEN_IS_SD = "is_sd";
    public static final String OPEN_IS_HD = "is_hd";
    public static final String OPEN_PROMOS = "open_promo";
    public static final String OPEN_PRECAPS = "open_precaps";
    public static final String COUNTRY_CODE = "close_code";
    public static final String OPEN_EPISODES = "open_episodes";
    public static final String VIDEO_URL_1 = "http://api.brightcove.com/services/library?command=find_video_by_id&video_id=";
    public static final String VIDEO_URL_2 = "&video_fields=FLVURL&media_delivery=http&token=";
    public static final String POSITION = "position";


    //Constants for GA
    public static final String LANDING_SCREEN = "Landing Screen";
    public static final String CLICK = "ui_click";
    public static final String SHOW_DETAILS_SCREEN = "Show Details Screen";
    public static final String VIDEO_DETAILS_SCREEN = "Video Details Screen";
    //end
}
