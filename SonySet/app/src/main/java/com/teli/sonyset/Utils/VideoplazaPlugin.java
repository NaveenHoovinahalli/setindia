package com.teli.sonyset.Utils;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.teli.sonyset.views.SonyVideoView;

import java.util.ArrayList;
import java.util.List;

import se.videoplaza.kit.adrequestor.AdRequestor;
import se.videoplaza.kit.adrequestor.ContentMetadata;
import se.videoplaza.kit.adrequestor.RequestSettings;
import se.videoplaza.kit.model.Ad;
import se.videoplaza.kit.model.LinearCreative;
import se.videoplaza.kit.tracker.Tracker;

/*
 * VideoplazaPlugin encapsulates all code that implemet's videoplaza SDK.
 * 
 * SDK reference: http://www.videoplaza.com/docs/android-sdk/latest/index.html
 * 
 */

public class VideoplazaPlugin {

    private SonyVideoView videoplayer;
    private AdRequestor adRequestor;
    private ContentMetadata contentMetadata;
    private RequestSettings requestSettings;
    private Tracker tracker;
    private Uri videoContentURI;
    private String mVideoId;
    private List<Ad> adsList = new ArrayList<Ad>();
    private LinearCreative linearCreative;

    public void init(SonyVideoView player, String videoId) {
        Log.d("VideoplazaPlugin", "init VideoplazaPlugin");
        this.mVideoId = videoId;
        videoplayer = player;
        /*
		 * Store a reference to the original video content, this should be done
		 * as we will replace the player video source with the ad, then after the
		 * ad has finish playing we ad the video source back.
		 */
        videoContentURI = player.getVideoURI();
        configPlayer();
        configVideoplaza();
    }

    /*
     * Init Videoplaza SDK
     */
    private void configVideoplaza() {
        Log.d("VideoplazaPlugin", "config");

        //init SDK modules
        contentMetadata = new ContentMetadata();
        tracker = new Tracker();
		
		/* 
		 * manually set a persistent unique id for the user. This id can be used in videoplaza backend
		 * as a unique identifier. This is done dynamic by the SDK with the id stored in a third-part cookie,
		 * but if the app don't have permissions for third-party cookies you can use setPersistentId to do it
		 * manually
		 * 
		 */
//		AdRequestorSettings settings = new AdRequestorSettings();
//		settings.setPersistentId("123456");
//		adRequestor = new AdRequestor("e732f657-a956-4263-b01c-3fc1d20e20d1", settings);
		
		/*
		 * configure the client account settings:
		 * 
		 * @vpHost: xx-clientName.videoplaza.tv
		 * @category: the category the player belong to, e.g. "Sports" or "News"
		 * @tags: additional metadata to identify the content, e.g. "football", "champions league"
		 * 
		 */
        adRequestor = new AdRequestor("vp-validation.videoplaza.tv");
        contentMetadata.setCategory("validation");
        contentMetadata.setContentForm("VPContentFormShortForm");
//        contentMetadata.setDuration();
        contentMetadata.setContentId(mVideoId);
        contentMetadata.addTag("standard");
        //contentMetadata.addFlag("nocom"); //uncomment here for testing with an empty ad response
		
	    /*
	     * AdRequestor.OnInfoListener callback information about: invalid arguments, request fail, invalid response, etc. 
	     */
        adRequestor.setOnInfoListener(new AdRequestor.OnInfoListener() {
            @Override
            public void onInfo(String type, String detailedMessage) {
                Log.i("DemoPlayer", "Ad Request Warning: " + type + ": " + detailedMessage);
            }
        });
	    
	    /*
	     * Tracker.OnInfoListener callback information about: request fail, invalid tracking events, etc.
	     */
        tracker.setOnInfoListener(new Tracker.OnInfoListener() {
            @Override
            public void onInfo(String type, String detailedMessage) {
                Log.i("DemoPlayer", "Tracker Warning: " + type + ": " + detailedMessage);
            }
        });
    }

    /*
     * Add event listeners to the player, so instead of playing the video content we
     * initiate a new ad section. A preroll ad section may look like below:
     *
     * 1. Application start
     * 2. The user click the play button
     * 3. Instead of playing the content, we interrupt the play event and pause the player
     * 4. Make a new ad request
     * 5. Get an ad response containing a list of ads (0~N ad elements)
     * 6. Filter the ad list
     * 		6.1. if list is empty, start the playback
     * 		6.2. if list has ads type inventory (empty ads), only track
     * 		6.3. if the list has ads, play first ad
     * 7. Replace the video source with the ad source URI, and play ad
     * 8. When the first ad has completed playing, remove ad from the list and check for more ads
     * 		8.1. if list is empty, start the playback
     * 		8.2. if there is more ads in the list, play next ad
     * 9. When all ads in the break have played, start video content
     *
     */
    private void configPlayer() {

        videoplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.i("DemoPlayer", "Player READY");
                videoplayer.start();
            }
        });

        videoplayer.setMediaStateListener(new SonyVideoView.PlayPauseListener() {

            /*
             *  When the user click play we need to interrupt the player and make an ad request
             *  passing the correct metadata (contentMetadata and requestSettings) to videoplaza
             *  back-end.
             */
            @Override
            public void onPlay() {
                Log.i("DemoPlayer", "Player PLAY");
        		
        		/*
        		 * RequestSettings for ad type:
        		 * 
        		 * Preroll 	==> INSERTION_POINT_TYPE_ON_BEFORE_CONTENT
        		 * Postroll ==> INSERTION_POINT_TYPE_ON_CONTENT_END
        		 * Midroll 	==> INSERTION_POINT_TYPE_PLAYBACK_POSITION
        		 */
                requestSettings = new RequestSettings();
                requestSettings.addInsertionPointType(RequestSettings.INSERTION_POINT_TYPE_ON_BEFORE_CONTENT);
				
				/*
				 * The settings below are optional, they help Videoplaza backend select the best bitrate and video size
				 * for your device.
				 * 
				 * @setHeight the height of your player
				 * @setWidth the width of your player
				 * @setMaxBitRate the max bitrate video file you want use. If set to 700 as example, 
				 * your default will be 700 or below.
				 */
                requestSettings.setHeight(270);
                requestSettings.setWidth(480);
                requestSettings.setMaxBitRate(700);

                newAdRequest(requestSettings);

                videoplayer.setMediaStateListener(null);
            }

            @Override
            public void onPause() {
                Log.i("DemoPlayer", "Player PAUSE");
            }
        });

        videoplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            /*
             * When the first ad is complete you should check if there is no more ads to be shown,
             * when all ads have been displayed the player replace the ad URI with the original
             * video content "videoContentURI" and start the playback.
             */
            @Override
            public void onCompletion(MediaPlayer arg0) {
                Log.i("DemoPlayer", "Player COMPLETED");

                tracker.track(linearCreative, Tracker.CREATIVE_TRACKING_EVENT_COMPLETE);

                // After this ad is displayed, remove from the list of "available" ads
                adsList.remove(0);

                // check if there is more preroll ads in the list
                checkNumOfAvailableAds();
                videoplayer.start();
            }
        });
    }

    /*
     * Make a new HTTP request to videoplaza backend.
     * @requestSettings defines the HTTP request settings, e.g. Ad type (pre, mid or post-roll)
     */
    private void newAdRequest(RequestSettings requestSettings) {
        Log.i("DemoPlayer", "NEW AD REQUEST");

        // first thing, we stop the player playing the content
        videoplayer.stopPlayback();
		
		/*
		 *  make a new HTTP request. Videoplaza backend will return a Ad object response 
		 *  with a list of campaigns that match the contentMetadata and requestSettings
		 */
        adRequestor.requestAds(contentMetadata, requestSettings, new AdRequestor.AdRequestListener() {
            @Override
            public void onComplete(List<Ad> ads) {

                adsList = ads;
                checkNumOfAvailableAds();
            }
        });
    }

    /*
     * Check if all ads in the ad break have been played. E.g. an ad break may have 2 prerolls, so
     * both ads should be played and tracked at the begin of the playback.
     */
    private void checkNumOfAvailableAds() {
        Log.i("DemoPlayer", "Number of Ads to play: " + adsList.size());

        // if there is no ads in the list, start the content
        if (adsList.isEmpty()) {
            playContent();
        }
        // if there is ads to display, we check if the ad is a supported ad type
        else {
            filterAds();
        }
    }

    /*
     * In the "ad list" response we get back from the HTTP request from videoplaza backend we
     * may get different types of ads. The next step is to filter out "inventory" ads (inventory
     * are "empty" ads, they represent an available opportunity to display and ad, but the
     * client didn't have available campaigns when the request was made, so videoplaza backend
     * returns a empty ad object type "inventory" that represents a missed opportunity. Those
     * missed opportunities should still be tracked)
     *
     */
    private void filterAds() {

        Ad ad = adsList.get(0);

        // if it is a supported ad type, we play it
        if (ad.getType().equals(Ad.AD_TYPE_SPOT_STANDARD)) {
            playAd(ad);
            return;
        }
        // if it is a inventory type, track as "inventory" and check for more ads in the list
        else if (ad.getType() == Ad.AD_TYPE_INVENTORY) {
            tracker.track(ad, Tracker.AD_TRACKING_EVENT_IMPRESSION);
        }
        // if it is a unknown ad type, track as "error" and check for more ads in the list
        else {
            Log.i("DemoPlayer", "Ad Type not supported");
            tracker.track(ad, Tracker.AD_ERROR_AD_TYPE_NOT_SUPPORTED);
        }

        // remove the current ad from the list, and check next ad
        adsList.remove(0);
        checkNumOfAvailableAds();
    }

    /*
     * Play the first ad in the list
     */
    private void playAd(final Ad ad) {
        Log.i("DemoPlayer", "CAMPAIGN ID: " + adsList.get(0).getCampaignId());

        // 1. get the first creative element
        linearCreative = (LinearCreative) ad.getCreatives().get(0);

        // 2. get the URI to the creative 
        String adUriString = linearCreative.getMediaFiles().get(0).getUri();

        // 3. If URI is valid, replace the player source with the ad URI and start the video
        if (isValidURI(adUriString)) {

            videoplayer.setVideoURI(Uri.parse(adUriString));
            videoplayer.start();
        }
        // 3.1. If URI is not valid, start the content
        else {
            Log.i("DemoPlayer", "AD URI ERROR: Fail to parse Ad URI string");
            tracker.track(ad, Tracker.CREATIVE_ERROR_INVALID_ASSET_URI);

            //start the player
            playContent();

            return;
        }

        // 4. track
        videoplayer.setMediaStateListener(new SonyVideoView.PlayPauseListener() {
        	
        	/*
        	 * only track "impression" and "start" if the video ad actually start
        	 */

            @Override
            public void onPlay() {
                Log.i("DemoPlayer", "Player PLAY AD");

                tracker.track(ad, Tracker.AD_TRACKING_EVENT_IMPRESSION);
                tracker.track(linearCreative, Tracker.CREATIVE_TRACKING_EVENT_START);

                videoplayer.setMediaStateListener(null);
            }

            @Override
            public void onPause() {
                Log.i("DemoPlayer", "Player PAUSE AD");
            }
        });

        // 5. add the click-through to the ad
        final String clickThrough = linearCreative.getClickThroughUri();

        videoplayer.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN && clickThrough != null) {
                    Log.i("DemoPlayer", "User click the ad");

                    // open new browser window
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(clickThrough));
                    view.getContext().startActivity(intent);

                    //track
                    tracker.track(linearCreative, Tracker.CREATIVE_TRACKING_EVENT_CLICK_THROUGH);
                }
                return false;
            }
        });

    }

    /*
     * Play the original video content
     */
    private void playContent() {
        Log.i("DemoPlayer", "Start Video Content: " + videoContentURI);

        videoplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                videoplayer.start();
            }
        });

        videoplayer.setVideoURI(videoContentURI);
        videoplayer.setOnCompletionListener(null);
        videoplayer.setOnTouchListener(null);
    }

    /*
     * Check if the Ad URI is valid or not
     */
    public boolean isValidURI(String uriStr) {
        try {
            Uri.parse(uriStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}