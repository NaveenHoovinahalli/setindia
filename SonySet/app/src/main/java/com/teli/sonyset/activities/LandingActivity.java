package com.teli.sonyset.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.korovyansk.android.slideout.SlideoutActivity;
import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SetRequestQueue;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.Utils.SonyRequest;
import com.teli.sonyset.adapters.MyPagerAdapter;
import com.teli.sonyset.fragments.EpisodeFragment;
import com.teli.sonyset.fragments.ExclusiveFragment;
import com.teli.sonyset.fragments.Schedule;
import com.teli.sonyset.fragments.ShowFragment;
import com.teli.sonyset.fragments.VideoFragment;
import com.teli.sonyset.models.DetectedAudio;
import com.teli.sonyset.models.NowPlayingResponse;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.services.BackgroundService;
import com.teli.sonyset.views.BusProvider;
import com.teli.sonyset.views.CirclePageIndicator;
import com.teli.sonyset.views.MotionViewPager;
import com.teli.sonyset.views.SonyTextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by madhuri on 11/3/15.
 */
public class LandingActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.topPager)
    MotionViewPager mTopPager;

    @InjectView(R.id.circleIndicator)
    CirclePageIndicator circlePageIndicator;

    @InjectView(R.id.bottomPager)
    ViewPager bottomPager;

    @InjectView(R.id.secondScreen)
    ImageView mSecondScreen;

    public final static int PAGES = 5;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 0.75f;
    public final static float SMALL_SCALE = 0.75f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    public MyPagerAdapter adapter;
    public ViewPager pager;

    private ArrayList<String> brightCoveIds = new ArrayList<String>();
    private static final double SLIDE_DURATION_FACTOR = 3.0;
    private static final long SLIDE_INTERVAL = 4000;
    private Timer timer1;
    private ArrayList<Video> promos = new ArrayList<Video>();
    private NavigationAdapter mPagerAdapter;
    private HashMap<Integer, String> thumbnailsBrightCove = new HashMap<>();
    private ArrayList<String> brightCoveList = new ArrayList<>();
    private String countryCode;
    private DetectedAudio mDetectedAudio;
    private ResponseReceiver receiver;
    private String programLink;
    private String countryId;
    private View oldView;
    private DetectedAudio detectedAudio;
    private int i;
    private int count;
    private boolean isPausedBool;
    private Runnable mUpdateResults;
    private Handler mHandler1 = new Handler();
    private Handler mHandler = new Handler();
    private int[] IMAGE_IDS = {
            R.drawable.ripple_1, R.drawable.ripple_2, R.drawable.ripple_3, R.drawable.ripple_4,
            R.drawable.ripple_5, R.drawable.ripple_6, R.drawable.ripple_7, R.drawable.ripple_8,
            R.drawable.ripple_9, R.drawable.ripple_10, R.drawable.ripple_11, R.drawable.ripple_12,
            R.drawable.ripple_13, R.drawable.ripple_14, R.drawable.ripple_15, R.drawable.ripple_16
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_landing);
        ButterKnife.inject(this);
        BusProvider.getInstance().register(this);
        pager = (ViewPager) findViewById(R.id.myviewpager);
        adapter = new MyPagerAdapter(this, this.getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(horizontalListener);
        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(15);
        pager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.loopPagerMargin));

        countryId = SonyDataManager.init(this).getCountryId();


        if (!AndroidUtils.isNetworkOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Sorry! No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if (countryId != null)
            fetchPromos();

        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(), LandingActivity.this);
        bottomPager.setAdapter(mPagerAdapter);
//        bottomPager.setScrollDurationFactor(SLIDE_DURATION_FACTOR);
        bottomPager.setOnPageChangeListener(bottomPagerListener);
        bottomPager.setOffscreenPageLimit(15);
        bottomPager.setCurrentItem(FIRST_PAGE);

        if (getIntent().hasExtra(Constants.OPEN_IS_HD)) {
            bottomPager.setCurrentItem(1);
            SonyDataManager.init(this).saveHdIsFromMenu(true);
        } else if (getIntent().hasExtra(Constants.OPEN_IS_SD)) {
            bottomPager.setCurrentItem(1);
        } else if (getIntent().hasExtra(Constants.OPEN_PRECAPS)) {
            SonyDataManager.init(this).savePrecapsIsFromMenu(true);
            bottomPager.setCurrentItem(3);
        } else if (getIntent().hasExtra(Constants.OPEN_PROMOS)) {
            bottomPager.setCurrentItem(3);
        } else if (getIntent().hasExtra(Constants.OPEN_EPISODES)) {
            bottomPager.setCurrentItem(0);
        }

        receiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackgroundService.MY_ACTION);
        registerReceiver(receiver, intentFilter);

        countryCode = SonyDataManager.init(this).getConutryCode();

        Log.d("LandingAcitivity", "countrycode" + countryCode);
        if (countryCode != null && !countryCode.isEmpty())
            if (!countryCode.equals("in")) {
                return;
            } else {
                Log.d("LandingAcitivity", "countrycode india" + countryCode);
                checkForSecondScreen();
            }
        super.onCreate(savedInstanceState);
    }
/*
    @Subscribe
    public void getMessage(String s){

        Log.d("bus provider","boolean value" + s);

       *//* if (b){
            Log.d("bus provider","boolean value" + b);
            new SonyPostRequest().execute();
        }*//*
    }*/

    private void checkForSecondScreen() {
        String url = String.format(Constants.URL_ON_OFF_STATE, "android");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            try {
                                String onState = response.getString("on_state");
                                if (onState.equals("true")) {
                                    startServiceForDetection();
                                } else {
                                    // layoutMyLayout.mTvBlank.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SplashScreenResponse", "Error::" + error.toString());
            }
        }
        );
        SetRequestQueue.getInstance(this).getRequestQueue().add(request);
    }

    private void startServiceForDetection() {
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
    }

    private void fetchPromos() {

        String url = String.format(Constants.ALL_PROMOS, countryId);
        Log.d("MyActivity", "JsonArray Url" + url);

        com.teli.sonyset.Utils.SonyRequest request = new SonyRequest(this, url) {
            @Override
            public void onResponse(JSONArray s) {
                Log.d("MyActivity", "Response" + s);

                if (s != null && !s.toString().isEmpty()) {
                    initAdapter(s);
                }
            }

            @Override
            public void onError(final String e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MyActivity", "ERROR" + e);
                    }
                });
            }
        };
        request.execute();
    }

    private void initAdapter(JSONArray jsonArray) {

        Gson gson = new Gson();

        promos = gson.fromJson(jsonArray.toString(), new TypeToken<List<Video>>() {
        }.getType());
        if (promos != null && !promos.isEmpty()) {
            for (int i = 0; i < promos.size(); i++) {
                brightCoveIds.add(promos.get(i).getBrightCoveId());
            }

            for (int i = 0; i < brightCoveIds.size(); i++) {
                Log.d("pageScrolled", "brightCoveIds" + brightCoveIds.get(i));
                fetchThumbnail(promos.get(i).getBrightCoveId(), promos, i);
            }
        }
    }

    @OnClick(R.id.menu_button)
    public void onMenuClicked(View view) {

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        SlideoutActivity.prepare(LandingActivity.this, R.id.mainRelativeLayour, width);
        startActivity(new Intent(LandingActivity.this,
                MenuActivity.class));

        overridePendingTransition(0, 0);

//
//        if(!isMenuOpen) {
//            menuBtn.setImageResource(R.drawable.close_btn);
//            isMenuOpen=true;
//            Toast.makeText(this,"close",Toast.LENGTH_SHORT).show();
//        }else {
//            menuBtn.setImageResource(R.drawable.sony_menu);
//            isMenuOpen=false;
//            Toast.makeText(this,"Open",Toast.LENGTH_SHORT).show();
//        }

    }


    private void fetchThumbnail(String s, final ArrayList<Video> promos, final int i) {

        String url = String.format(Constants.BRIGHT_COVE_THUMBNAIL, s);
        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyActivity", "Thumbnail" + response);

                        if (response != null) {
                            try {
                                JSONObject object = new JSONObject(response.toString());

                                thumbnailsBrightCove.put(i, (String) object.get("videoStillURL"));
                                // brightCoveThumbnails.add(i,(String) object.get("videoStillURL"));

                                if (thumbnailsBrightCove.size() == brightCoveIds.size()) {
                                    setTopPager(promos, thumbnailsBrightCove);

                                    brightCoveList = new ArrayList<String>(thumbnailsBrightCove.values());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pageScrolled", "Error" + error);
                        thumbnailsBrightCove.put(i, "null");
                    }
                });
        SetRequestQueue.getInstance(this).getRequestQueue().add(request);
    }

    private void setTopPager(ArrayList<Video> promos, HashMap<Integer, String> brightCoveThumbnails) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(promos, brightCoveThumbnails);
        mTopPager.setAdapter(adapter);
        mTopPager.setScrollDurationFactor(SLIDE_DURATION_FACTOR);
        circlePageIndicator.setViewPager(mTopPager);
        mTopPager.setOnPageChangeListener(this);
        handler.postDelayed(slidePagerRunnable, SLIDE_INTERVAL);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        circlePageIndicator.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                handler.removeCallbacks(slidePagerRunnable);
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                handler.postDelayed(slidePagerRunnable, SLIDE_INTERVAL);
                break;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private HashMap<Integer, String> brightCoveThumbnails = new HashMap<>();
        private ArrayList<Video> promos = new ArrayList<Video>();

        ViewPagerAdapter() {

        }

        public ViewPagerAdapter(ArrayList<Video> promos, HashMap<Integer, String> brightCoveThumbnails) {
            this.brightCoveThumbnails = brightCoveThumbnails;
            this.promos = promos;

            inflater = (LayoutInflater) LandingActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return promos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            final View itemView = inflater.inflate(R.layout.activity_promo, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.promo_iv);

            if (!brightCoveThumbnails.get(position).equals("null")) {
                Picasso.with(LandingActivity.this).load(Uri.parse(brightCoveThumbnails.get(position))).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.place_holder);
            }

            SonyTextView promoTitle = (SonyTextView) itemView.findViewById(R.id.promo_title);
            promoTitle.setText(promos.get(position).getShowTitle());

            SonyTextView promoName = (SonyTextView) itemView.findViewById(R.id.promo_name);
            promoName.setText(promos.get(position).getShowName());

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Toast.makeText(LandingActivity.this, "clicked image", Toast.LENGTH_SHORT).show();

                    callVideoActivity(view, position, promos);
                }
            });
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void callVideoActivity(View view, int position, ArrayList<Video> promos) {

        Intent intent = new Intent(this, VideoDetailsActivity.class);
        intent.putExtra(Constants.POSITION, "" + position);
        intent.putExtra(Constants.EPISODE_RESPONSE, promos);
        intent.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveList);
        intent.putExtra(Constants.IS_PROMO, true);

        startActivity(intent);
    }

    private Handler handler = new Handler();
    private Runnable slidePagerRunnable = new Runnable() {
        @Override
        public void run() {
            mTopPager.setCurrentItem(mTopPager.getCurrentItem() + 1, true);
            circlePageIndicator.setCurrentItem(mTopPager.getCurrentItem());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!promos.isEmpty())
            handler.postDelayed(slidePagerRunnable, SLIDE_INTERVAL);
    }

    private static class NavigationAdapter extends FragmentStatePagerAdapter {

//        private static final String[] TITLES = new String[]{"Episodes", "Schedule", "Shows", "Exclusives", "Videos", "Extra"};

        private FragmentManager fm;
        Activity mContext;

        public NavigationAdapter(FragmentManager fm, Activity context) {
            super(fm);
            this.fm = fm;
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;
            Log.d("LandingActivity", "AdapterPosition" + position);
            position = position % 5;
//            final int pattern = position % 5;
            switch (position) {

                case 0:
                    f = new ShowFragment();
                    break;
                case 1:
                    f = new ExclusiveFragment();
                    break;
                case 2:
                    f = new VideoFragment();
                    break;
                case 3:
                    f = new EpisodeFragment();
                    break;
                case 4:
                    f = new Schedule();
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return 5 * LOOPS;
        }

       /* @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 6){

            }
            final int pattern = position % 6;
            return TITLES[pattern];
        }*/
    }

    @OnClick(R.id.secondScreen)
    public void secondScreenClicked() {

        if (programLink != null && !programLink.isEmpty()) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(WebViewActivity.WEB_URL, programLink);
            startActivity(intent);
        }/*else {
            if(!AndroidUtils.isNetworkOnline(getApplicationContext())){
                return;
            }

            new SonyPostRequest(detectedAudio.getId()).execute();
        }*/
    }

    public class SonyPostRequest extends AsyncTask<String, Void, JSONObject> {

        String channelId;

        public SonyPostRequest(String id) {
            this.channelId = id;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            Log.d("bus provider", "boolean value do in background");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL_NOW_PLAYING);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                nameValuePairs.add(new BasicNameValuePair("channel_id", channelId));
                nameValuePairs.add(new BasicNameValuePair("uid", AndroidUtils.getDeviceImei(LandingActivity.this)));
                nameValuePairs.add(new BasicNameValuePair("phone_build", AndroidUtils.getDeviceManufacturer()));
                nameValuePairs.add(new BasicNameValuePair("phone_model", AndroidUtils.getDeviceModel()));
                nameValuePairs.add(new BasicNameValuePair("os_type", "android"));
                nameValuePairs.add(new BasicNameValuePair("os_version", AndroidUtils.getOsVersionName()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = (HttpResponse) httpclient.execute(httppost);

                if (response != null) {
                    Log.d("MainActivity", "Landing response n do in background" + response.getStatusLine().toString());

                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity1 = response.getEntity();

                        if (entity1 != null) {
                            InputStream instream1 = null;
                            try {
                                instream1 = entity1.getContent();
                                JSONObject jsonArray = convertInputStreamToJSONArray(instream1);
                                instream1.close();

                                return jsonArray;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ShowFragment", "EXCEPTION" + e);
            }
            return null;
        }

        private JSONObject convertInputStreamToJSONArray(InputStream inputStream)
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
            if (s != null) {
                Log.d("MainActivity", "Landing response in pot execute" + s.toString());

                NowPlayingResponse nowPlayingResponse = new Gson().fromJson(s.toString(), NowPlayingResponse.class);
                programLink = nowPlayingResponse.getProgramLink();
            }
            super.onPostExecute(s);
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle bundle = intent.getExtras();
                if (bundle != null && !bundle.isEmpty()) {
                    String response = bundle.getString(BackgroundService.RESPONSE_KEY);

                    if (response != null && !response.isEmpty())

                        detectedAudio = new Gson().fromJson(response.toString(), DetectedAudio.class);

                    final int delay = 4000;
                    final Timer timer = new Timer();
                    i = 0;
                    count = 1;


                    final Runnable mStartAnimation = new Runnable() {
                        public void run() {
                            isPausedBool = false;
                        }
                    };

                    mUpdateResults = new Runnable() {
                        public void run() {

                            if (!isPausedBool) {
                                if (i > 15) {
                                    i = 0;
                                    count++;
                                }

                                if (count == 3) {
                                    isPausedBool = true;
                                    count = 1;
                                    i = 0;
                                    mHandler1.postDelayed(mStartAnimation, 5000);
                                }

                                mSecondScreen.setBackgroundResource(IMAGE_IDS[i]);
                                i++;
                            }
                        }
                    };

                    timer.scheduleAtFixedRate(new TimerTask() {

                        public void run() {
                            mHandler.post(mUpdateResults);
                        }

                    }, delay, 100);

                    if (!AndroidUtils.isNetworkOnline(getApplicationContext())) {
                        return;
                    }

                    new SonyPostRequest(detectedAudio.getId()).execute();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onPause() {

        //  unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        Intent stopService = new Intent(LandingActivity.this, BackgroundService.class);
        stopService(stopService);
        unregisterReceiver(receiver);
        super.onDestroy();
    }


    ViewPager.OnPageChangeListener horizontalListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
//            Log.d("MainActivity","PageSelected::" + pager.getCurrentItem()%5);

//            bottomPager.setCurrentItem(pager.getCurrentItem() % 5);
            bottomPager.setCurrentItem(pager.getCurrentItem());
            adapter.selectedItem(pager.getCurrentItem());

        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

    public void setSelectedIten(int position) {
        pager.setCurrentItem(position);
    }

    ViewPager.OnPageChangeListener bottomPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {
            Log.d("MainActivity", "PageScrolled::" + i);
        }

        @Override
        public void onPageSelected(int i) {
//            Log.d("MainActivity","PageSelected::" + pager.getCurrentItem()%5);
            Log.d("MainActivity", "PageSelected::" + i);
            pager.setCurrentItem(bottomPager.getCurrentItem());
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            Log.d("MainActivity", "PageScrollStateChanged::" + i);
        }
    };
}






