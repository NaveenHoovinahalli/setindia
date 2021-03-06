package com.teli.sonyset.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.SonySet;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.Utils.SonyRequest;
import com.teli.sonyset.adapters.ShowDetailsStripAdapter;
import com.teli.sonyset.fragments.CastFragment;
import com.teli.sonyset.fragments.ConceptFragment;
import com.teli.sonyset.fragments.ShowDetailHomeFragment;
import com.teli.sonyset.fragments.ShowEpisodeFragment;
import com.teli.sonyset.fragments.SynopsisFragment;
import com.teli.sonyset.models.Cast;
import com.teli.sonyset.models.Concept;
import com.teli.sonyset.models.ConceptValue;
import com.teli.sonyset.models.ShowMain;
import com.teli.sonyset.models.Synopsis;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.views.SonyTextView;
import com.zedo.androidsdk.ZedoAndroidSdk;
import com.zedo.androidsdk.utils.AdRenderer;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by madhuri on 12/3/15.
 */
public class ShowDetailsActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.bottomPager)
    ViewPager mBottomPager;

    @InjectView(R.id.progress)
    ProgressBar mprogress;

    @InjectView(R.id.show_image)
    ImageView mShowImage;

    @InjectView(R.id.show_title)
    TextView mShowTitle;

    @InjectView(R.id.noContent)
    SonyTextView mNoContent;

    @InjectView(R.id.show_time)
    TextView mShowTime;

    @InjectView(R.id.show_logo)
    ImageView mShowLogo;

    @InjectView(R.id.colorCodeText)
    TextView mColorCode;

    @InjectView(R.id.topLayout)
    RelativeLayout mTopLayout;

    public ShowDetailsStripAdapter adapter;
    public ViewPager pager;
    private int previousItem;
    private boolean isFirst;

    public final static int PAGES = 5;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 0.75f;
    public final static float SMALL_SCALE = 0.75f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

 /*   @InjectView(R.id.carousel)
    Carousel mCarousel;*/

   /* @InjectView(R.id.titlestrip)
    PagerTitleStrip titleStrip;*/

    @InjectView(R.id.settingDesc)
    LinearLayout settingLayout;

    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout mSlideLayout;

    @InjectView(R.id.dragView)
    RelativeLayout mDragview;

    private NavigationAdapter mPagerAdapter;
    public static final String SHOW_ID = "show_id";
    public static final String SHOW_COLOR_CODE = "show_clor_code";
    private static final double SLIDE_DURATION_FACTOR = 3.0;

    private static ArrayList<Cast> casts = new ArrayList<>();
    private ArrayList<Synopsis> synopsises = new ArrayList<>();
    private ArrayList<Video> episodes = new ArrayList<>();
    private String banner;
    private String title;
    private ArrayList<Video> promos = new ArrayList<>();
    private Concept concept;
    private Concept timeConcept;
    private ArrayList<ConceptValue> timeConcepts = new ArrayList<>();
    private String time;
    private String colorCode;
    private String showLogo;
    private Tracker t;
    private String nid;
    private String showId;
    private String countryId;
    Map<String, String> aliasMap;
    //  private ImageAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_show_details1);
        ButterKnife.inject(this);

        t = ((SonySet) this.getApplication()).getTracker(
                SonySet.TrackerName.APP_TRACKER);
        t.setScreenName(Constants.SHOW_DETAILS_SCREEN);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        if(!AndroidUtils.isNetworkOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry! No Internet Connection", Toast.LENGTH_SHORT).show();
            mprogress.setVisibility(View.GONE);
            return;
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        Log.d("Screen", "width" + width);
        if (width == 540) {
            ViewGroup.LayoutParams params = mSlideLayout.getLayoutParams();
            params.height = 850;
            mSlideLayout.setLayoutParams(params);

            ViewGroup.LayoutParams params1 =  mDragview.getLayoutParams();
            params1.height = 807;
            mDragview.setLayoutParams(params1);
        }

        aliasMap = new LinkedHashMap<String, String>();
        aliasMap.put("banner", "c406d19s1");
        ZedoAndroidSdk.init(getApplicationContext(), "1408", aliasMap);

        if (getIntent().hasExtra(SHOW_COLOR_CODE)){
            colorCode =  getIntent().getStringExtra(SHOW_COLOR_CODE);
            Log.d("ShowDetailsAcivity","colorcode" + colorCode);
        }

        if(getIntent().hasExtra(SHOW_ID)){

            pager = (ViewPager) findViewById(R.id.myviewpager);
            adapter = new ShowDetailsStripAdapter(this, this.getSupportFragmentManager(), colorCode);
            pager.setAdapter(adapter);
            pager.setOnPageChangeListener(horizontalListener);
            pager.setCurrentItem(FIRST_PAGE);
            pager.setOffscreenPageLimit(15);
            pager.setPageMargin(((int) getResources().getDimension(R.dimen.loopPagerMargin)));

            showId =   getIntent().getStringExtra(SHOW_ID);
            countryId = SonyDataManager.init(this).getCountryId();

            String url = String.format(Constants.SHOW_DETAILS,showId,countryId,
                    AndroidUtils.getScreenWidth(this),AndroidUtils.getScreenHeight(this));
            fetchShowDetails(url);
        }else {
            mNoContent.setVisibility(View.VISIBLE);
            mNoContent.setText("No Content Available!");
            mprogress.setVisibility(View.GONE);
            mTopLayout.setVisibility(View.GONE);
        }

        super.onCreate(savedInstanceState);
    }

    private void fetchShowDetails(String url) {

        SonyRequest request = new SonyRequest(this,url) {
            @Override
            public void onResponse(JSONArray s) {
                Log.d("ShowDetails","Response" + s);
                mprogress.setVisibility(View.GONE);

                if (s!=null && !s.toString().isEmpty())
                    initAdapter(s);
            }

            @Override
            public void onError(String e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mprogress.setVisibility(View.GONE);
                    }
                });
                Log.d("ShowDetails","Error" + e);
            }
        };
        request.execute();
    }

    private void initAdapter(JSONArray s) {
        Gson gson=new Gson();

        ArrayList<ShowMain> showDetails = gson.fromJson(s.toString(), new TypeToken<List<ShowMain>>() {
        }.getType());


        if (showDetails!=null && !showDetails.isEmpty() && showDetails.size() != 0)
            for (int i = 0 ; i<showDetails.size();i++){
                banner = showDetails.get(i).getBanner();
                showLogo = showDetails.get(i).getShowLogo();
                timeConcept = showDetails.get(i).getTimeConcept();
                title = showDetails.get(i).getTitle();
                casts = showDetails.get(i).getCasts();
                synopsises = showDetails.get(i).getSynopsises();
                episodes = showDetails.get(i).getEpisodess();
                promos = showDetails.get(i).getPromos();
                concept = showDetails.get(i).getConcept();

                nid = showDetails.get(i).getNid();

                SonyDataManager.init(this).saveShowTitle(title);
                SonyDataManager.init(this).saveShowNid(nid);
                SonyDataManager.init(this).saveShowId(showId);
            }

        Picasso.with(this).load(Uri.parse(banner)).placeholder(R.drawable.place_holder).into(mShowImage);
        Picasso.with(this).load(Uri.parse(showLogo)).into(mShowLogo);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "klavikabold_bold_webfont.ttf");
        mShowTitle.setTypeface(tf);
        mShowTitle.setText(title);

        timeConcepts = timeConcept.getValues();
        for (int i = 0; i < timeConcepts.size() ; i++){
            time = timeConcepts.get(i).getValue();
        }

        Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "klavikamedium_plain_webfont.ttf");
        mShowTime.setTypeface(tf1);
        mShowTime.setText(time);
        mShowTime.setTextColor(Color.parseColor("#BABABA"));

        if (colorCode!=null && !colorCode.isEmpty() && !colorCode.equals("null")){

            if (colorCode.equalsIgnoreCase("r")){
                mColorCode.setBackgroundColor(getResources().getColor(R.color.sony_red));
            }else if (colorCode.equalsIgnoreCase("g")){
                mColorCode.setBackgroundColor(getResources().getColor(R.color.sony_green));
            }else if (colorCode.equalsIgnoreCase("b")){
                mColorCode.setBackgroundColor(getResources().getColor(R.color.sony_blue));
            }
        }

        // setUpcarousel();

        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(), ShowDetailsActivity.this);
        mBottomPager.setAdapter(mPagerAdapter);
        //   mBottomPager.setScrollDurationFactor(SLIDE_DURATION_FACTOR);
        mBottomPager.setOnPageChangeListener(this);
        mBottomPager.setCurrentItem(FIRST_PAGE);
        mBottomPager.setOffscreenPageLimit(15);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pager.setCurrentItem(mBottomPager.getCurrentItem());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class NavigationAdapter extends FragmentStatePagerAdapter {

        FragmentManager fm;
        Activity context;

        private String[] names = {"CAST","CONCEPT","HOME","EPISODES","SYNOPSIS"};
        public NavigationAdapter(FragmentManager fm, Activity context) {
            super(fm);
            this.fm = fm;
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;

            //  final int pattern = position % 5;
            position = position % 5;
            switch (position) {

                case 0:
                    f = new ShowDetailHomeFragment();
                    trackClicks("home");
                    break;

                case 1:
                    f = new ShowEpisodeFragment();
                    trackClicks("episode");
                    break;

                case 2:
                    f = new SynopsisFragment();
                    trackClicks("synopsis");
                    break;

                case 3:
                    f = new CastFragment();
                    trackClicks("cast");
                    break;

                case 4:
                    f = new ConceptFragment();
                    trackClicks("concept");
                    break;

            }
            return f;
        }

        @Override
        public int getCount() {
            return 5 * LOOPS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //    final int pattern = position % 5;
            return names[position];
        }

    }

    private void trackClicks(String tab) {
        t.send(new HitBuilders.AppViewBuilder().build());
        t.send(new HitBuilders.EventBuilder()
                .setCategory(Constants.CLICK)
                .setAction("clicked "+tab)
                .setLabel(Constants.SHOW_DETAILS_SCREEN)
                .build());
    }

    public ArrayList<Cast> getCasts(){
        return casts;
    }

    public ArrayList<Synopsis> getSynopsises(){
        return synopsises;
    }

    public ArrayList<Video> getEpisodes(){
        return episodes;
    }

    public ArrayList<Video> getPromos(){
        return promos;
    }

    public Concept getConcept(){
        return concept;
    }

    public String getColorCode(){
        return colorCode;
    }

    @OnClick({R.id.btnBack/*,R.id.btnAlarm,*/,R.id.btnSetting})
    public void topButtons(View view){

        switch (view.getId()){
            case R.id.btnBack:
                super.onBackPressed();
                break;
            /*case R.id.btnAlarm:
                break;*/
            case R.id.btnSetting:
                if(settingLayout.getVisibility()==View.VISIBLE)
                    settingLayout.setVisibility(View.INVISIBLE);
                else
                    settingLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick({R.id.menuConcept,R.id.menuCast,R.id.menuSynopsis,
            R.id.menuEpisodes,R.id.menuVideos,R.id.menuFeedback/*,R.id.menuNews,R.id.menuFeedback*/})
    public void menuItemClicked(View view){

        switch (view.getId()){
            case R.id.menuConcept :
                loadPager(2504);
                break;

            case R.id.menuCast :
                loadPager(2503);
                break;

            case R.id.menuSynopsis :
                loadPager(2502);
                break;

            case R.id.menuEpisodes :
                loadPager(2501);
                break;

            case R.id.menuVideos :
                loadPager(2500);
                break;

            case R.id.menuFeedback :

                if(settingLayout.getVisibility()==View.VISIBLE)
                    settingLayout.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(ShowDetailsActivity.this,FeedbackActivity.class);
                startActivity(intent);
                break;

           /* case R.id.menuVideos :
                if(settingLayout.getVisibility()==View.VISIBLE)
                    settingLayout.setVisibility(View.INVISIBLE);

                mBottomPager.setCurrentItem(1);
                break;*/

        }
    }

    public void loadPager(int i){
        if(settingLayout.getVisibility()==View.VISIBLE)
            settingLayout.setVisibility(View.INVISIBLE);

        mBottomPager.setCurrentItem(i);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    ViewPager.OnPageChangeListener horizontalListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {}

        @Override
        public void onPageSelected(int i) {
            try {
                if (isFirst) {
                    Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.myviewpager + ":" + previousItem);

                    if (previousFragment.getView() != null) {
                        View previousView = previousFragment.getView();
                        LinearLayout previousLayout = (LinearLayout) previousView.findViewById(R.id.strip_item);
                        LinearLayout pDividerLayout = (LinearLayout) previousView.findViewById(R.id.divider);
                        pDividerLayout.setVisibility(View.VISIBLE);
//                        previousLayout.setSelected(false);
                    }

                    Fragment xFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.myviewpager + ":" + (pager.getCurrentItem() - 1));

                    if (xFragment.getView() != null) {
                        View xView = xFragment.getView();
                        LinearLayout xDividerLayout = (LinearLayout) xView.findViewById(R.id.divider);
                        xDividerLayout.setVisibility(View.VISIBLE);
                    }

                    Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.myviewpager + ":" + pager.getCurrentItem());

                    if (currentFragment.getView() != null) {
                        View currentView = currentFragment.getView();
                        LinearLayout currentLayout = (LinearLayout) currentView.findViewById(R.id.strip_item);
                        LinearLayout cDividerLayout = (LinearLayout) currentView.findViewById(R.id.divider);
                        cDividerLayout.setVisibility(View.GONE);
//                        currentLayout.setSelected(true);
                    }

                    int nextItem = pager.getCurrentItem() + 1;
                    Fragment nextFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.myviewpager + ":" + nextItem);
                    View nextView = nextFragment.getView();
                    LinearLayout nDividerLayout = (LinearLayout) nextView.findViewById(R.id.divider);
                    nDividerLayout.setVisibility(View.GONE);

                    int secondItem = pager.getCurrentItem() + 2;
                    Fragment secondFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.myviewpager + ":" + secondItem);
                    View secondView = secondFragment.getView();
                    LinearLayout sDividerLayout = (LinearLayout) secondView.findViewById(R.id.divider);
                    sDividerLayout.setVisibility(View.VISIBLE);
                }


            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

            isFirst = true;
            previousItem = pager.getCurrentItem();
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };

    public void setSelectedIten(int position) {
        pager.setCurrentItem(position);
        mBottomPager.setCurrentItem(position);
    }

    public void setBottomPagerToConcept(){
        mBottomPager.setCurrentItem(2504);
    }

    public void setBottomPagerToCast(){
        mBottomPager.setCurrentItem(2503);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdRenderer.showStickyFooter(this, "banner", AdRenderer.ANIMATION_SLIDE_FROM_BOTTOM);
    }
}
