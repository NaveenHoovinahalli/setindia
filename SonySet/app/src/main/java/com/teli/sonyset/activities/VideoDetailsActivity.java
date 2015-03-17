package com.teli.sonyset.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SetRequestQueue;
import com.teli.sonyset.Utils.VideoplazaPlugin;
import com.teli.sonyset.adapters.RelatedVideoAdapter;
import com.teli.sonyset.models.RelatedVideo;
import com.teli.sonyset.views.SonyVideoView;
import com.teli.sonyset.views.SonyTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class VideoDetailsActivity extends Activity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.bc_video_view)
    SonyVideoView mBCVideoView;

    @InjectView(R.id.video_top_color_bar)
    View mVideoTopColorBar;

    @InjectView(R.id.back_arrow)
    ImageView mBackArrow;

    @InjectView(R.id.related_video_list)
    ListView mRelatedVideoList;

    @InjectView(R.id.video_title_layout)
    RelativeLayout mVideoTitleLayout;

    @InjectView(R.id.video_title)
    SonyTextView mVideoTitle;

    @InjectView(R.id.show_name)
    SonyTextView mVideoShowName;

    @InjectView(R.id.category)
    SonyTextView mVideoCategory;

    @InjectView(R.id.published_on)
    SonyTextView mPublishedOn;

    EventEmitter eventEmitter;
    RelatedVideoAdapter mRelatedVideoAdapter;
    String mToken = "KOXTTwEYaBzBSRHvS2lLfsDKcn3S98e-pOq3o0S4x6dBj2k1xBa1Hg..";
    String mVideoId = "4039707197001";
    private ArrayList<RelatedVideo> mAllVideos = new ArrayList<>();
    private ArrayList<RelatedVideo> mRelatedVideos = new ArrayList<>();
    private ArrayList<String> mThumbnails = new ArrayList<>();
    private ArrayList<com.teli.sonyset.models.Video> mEpisodes = new ArrayList<>();
    boolean isEpisode;
    boolean isPromo;
    public int mPosition;
    private Intent receivedIntent = null;
    private int oldPosition;
    private RelatedVideo nRelatedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.inject(this);
        eventEmitter = mBCVideoView.getEventEmitter();
        fetchData();
        playVideo();
    }


    private void fetchData() {
        if (receivedIntent == null) {
            receivedIntent = getIntent();
            mPosition = Integer.parseInt(receivedIntent.getStringExtra(Constants.POSITION));
        }
        if (receivedIntent != null && receivedIntent.hasExtra(Constants.EPISODE_RESPONSE)) {

            if (receivedIntent.getBooleanExtra(Constants.IS_EPISODE, false)) {
                mToken = "pJgVx7pOTpXw5m8_D8Rk2yHwym1XqPnjRRmfjnWMxZRo80GMSna1QQ..";
                isEpisode = true;
            } else if (receivedIntent.getBooleanExtra(Constants.IS_PROMO, false)) {
                isPromo = true;
            }

            mThumbnails = receivedIntent.getStringArrayListExtra(Constants.EPISODE_THUMBNAILS);
            mEpisodes = receivedIntent.getParcelableArrayListExtra(Constants.EPISODE_RESPONSE);
            for (int i = 0; i < mEpisodes.size(); i++) {

                RelatedVideo relatedVideo = new RelatedVideo();
                relatedVideo.setVideoId(mEpisodes.get(i).getBrightCoveId());
                relatedVideo.setThumbnailUrl(mThumbnails.get(i));
                relatedVideo.setVideoTitle(mEpisodes.get(i).getShowTitle());
                relatedVideo.setShowName(mEpisodes.get(i).getShowName());
                relatedVideo.setPublishedDate(mEpisodes.get(i).getOnAirDate());
                relatedVideo.setColorCode(mEpisodes.get(i).getColorCode());
                Log.d("VideoDetailsActivity", "Color::" + mEpisodes.get(i).getColorCode());
                if (isEpisode) {
                    relatedVideo.setCategory("Episode");
                } else if (isPromo) {
                    relatedVideo.setCategory("Promo");
                } else {
                    relatedVideo.setCategory("Video");
                }
                mAllVideos.add(relatedVideo);
                mRelatedVideos.add(relatedVideo);
            }
            display(mAllVideos.get(mPosition));
            loadAdapter(mRelatedVideos);
        }
    }

    private void playVideo() {

        Catalog catalog = new Catalog(mToken);

        if (!isEpisode) {
            catalog.findVideoByID(mVideoId, new VideoListener() {

                @Override
                public void onVideo(Video video) {
                    mBCVideoView.add(video);
                    mBCVideoView.requestFocus();
                    mBCVideoView.start();
                    getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).edit().putInt("VIDEO_HEIGHT", mBCVideoView.getHeight()).commit();
                }

                @Override
                public void onError(String s) {
                    Log.d("VideoActivity", "");
                }
            });
        } else {
            fetchVideoURL();
        }
    }

    private void fetchVideoURL() {
        String url = Constants.VIDEO_URL_1 + mVideoId + Constants.VIDEO_URL_2 + mToken;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String videoUrl = response.getString("FLVURL");
                            Log.d("VideoDetailsActivity", "Response::" + videoUrl);
                            mBCVideoView.setVideoURI(Uri.parse(videoUrl));
                            mBCVideoView.requestFocus();
                            VideoplazaPlugin vpPlugin = new VideoplazaPlugin();
                            vpPlugin.init(mBCVideoView);
                            getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).edit().putInt("VIDEO_HEIGHT", mBCVideoView.getHeight()).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        SetRequestQueue.getInstance(getApplicationContext()).getRequestQueue().add(jsonRequest);
    }

    public void display(RelatedVideo relatedVideo) {
        nRelatedVideo = relatedVideo;
        mVideoId = relatedVideo.getVideoId();
        setVideoTitle(relatedVideo.getVideoTitle());
        setShowName(relatedVideo.getShowName());
        setPublishedDate(relatedVideo.getPublishedDate());
        String color = relatedVideo.getColorCode();
        if (color.toLowerCase().equals("r")) {
            setTopBarColor(getResources().getColor(R.color.sony_red));
        } else if (color.toLowerCase().equals("g")) {
            setTopBarColor(getResources().getColor(R.color.sony_green));
        }else if (color.toLowerCase().equals("b")) {
            setTopBarColor(getResources().getColor(R.color.sony_blue));
        }else{
            setTopBarColor(getResources().getColor(R.color.sony_grey));
        }
    }

    private void setPublishedDate(String onAirDate) {
        mPublishedOn.setText(" " + onAirDate);
    }

    private void setCategory(String category) {
        mVideoCategory.setText(category);
    }

    private void setShowName(String showName) {
        mVideoShowName.setText(showName);
    }

    private void setVideoTitle(String showTitle) {
        mVideoTitle.setText(showTitle);
    }

    private void setTopBarColor(int color) {
        mVideoTopColorBar.setBackgroundColor(color);
    }

    @OnClick({R.id.back_arrow, R.id.btn_share, R.id.btn_video_menu, R.id.btn_play_centre, R.id.video_down_arow})
    public void OnClickListener(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;
            case R.id.btn_share:
                break;
            case R.id.btn_video_menu:
                break;
            case R.id.btn_play_centre:
                break;
            case R.id.video_down_arow:
                break;
        }
    }


    public void loadAdapter(ArrayList<RelatedVideo> mEpisodes) {
        mRelatedVideoAdapter = new RelatedVideoAdapter(VideoDetailsActivity.this, mEpisodes);
        mRelatedVideoList.setAdapter(mRelatedVideoAdapter);
        mRelatedVideoList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        RelatedVideo relatedVideo = ((RelatedVideo) mRelatedVideoAdapter.getItem(position));
        display(relatedVideo);
        playVideo();
        /*String videoTitle = relatedVideo.getVideoTitle();

        for (int i = 0; i < mAllVideos.size(); i++) {
            if(mRelatedVideos.size()>i && mRelatedVideos.get(i).equals(mAllVideos.get(i))){

            }else{
                if(!mAllVideos.get(i).getVideoTitle().equals(videoTitle)) {
                    mRelatedVideos.remove(position);
                    mRelatedVideos.add(position, mAllVideos.get(i));
                    return;
                }
            }*/
//        loadAdapter(mRelatedVideos);

//            for (int j = 0; j < mRelatedVideos.size(); j++) {
//                if (!mRelatedVideos.get(j).getVideoTitle().equals(mAllVideos.get(i).getVideoTitle())) {
//                    if (!mAllVideos.get(i).getVideoTitle().equals(videoTitle)) {
//                        mRelatedVideos.add(position, mAllVideos.get(i));
//                    }
//                }
//            }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pauseVideo();
    }

    private void pauseVideo() {
        getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("IS_PAUSED", true)
                .commit();
        mBCVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeVideo();
    }

    private void resumeVideo() {
        boolean isPaused = getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
                .getBoolean("IS_PAUSED", false);
        if (isPaused) {
            mBCVideoView.start();
        }
        getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("IS_PAUSED", false)
                .commit();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            goLandscape();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            goPortrait();
        }
    }

    private void goPortrait() {
        mBCVideoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE).getInt("VIDEO_HEIGHT", 0)));
        mVideoTitleLayout.setVisibility(View.VISIBLE);
        mRelatedVideoList.setVisibility(View.VISIBLE);
    }

    private void goLandscape() {
        mVideoTitleLayout.setVisibility(View.GONE);
        mRelatedVideoList.setVisibility(View.GONE);
        mBCVideoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }
}
