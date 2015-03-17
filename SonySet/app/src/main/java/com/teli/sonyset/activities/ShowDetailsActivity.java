package com.teli.sonyset.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.Utils.SonyRequest;
import com.teli.sonyset.fragments.CastFragment;
import com.teli.sonyset.fragments.ConceptFragment;
import com.teli.sonyset.fragments.ShowDetailHomeFragment;
import com.teli.sonyset.fragments.ShowEpisodeFragment;
import com.teli.sonyset.fragments.SynopsisFragment;
import com.teli.sonyset.models.Cast;
import com.teli.sonyset.models.Concept;
import com.teli.sonyset.models.ConceptValue;
import com.teli.sonyset.models.ShowMain;
import com.teli.sonyset.models.ShowVideo;
import com.teli.sonyset.models.Synopsis;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.views.CustomTabLayout;
import com.teli.sonyset.views.SonyTextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

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
    SonyTextView mShowTitle;

    @InjectView(R.id.noContent)
    SonyTextView mNoContent;

    @InjectView(R.id.show_time)
    SonyTextView mShowTime;

    @InjectView(R.id.show_logo)
    ImageView mShowLogo;

    @InjectView(R.id.colorCodeText)
    TextView mColorCode;

    @InjectView(R.id.custom_tab_layout)
    CustomTabLayout mTabLayout;

 /*   @InjectView(R.id.carousel)
    Carousel mCarousel;*/

   /* @InjectView(R.id.titlestrip)
    PagerTitleStrip titleStrip;*/

    @InjectView(R.id.settingDesc)
    LinearLayout settingLayout;

    private NavigationAdapter mPagerAdapter;
    public static final String SHOW_ID = "show_id";
    public static final String SHOW_COLOR_CODE = "show_clor_code";
    private static final double SLIDE_DURATION_FACTOR = 3.0;

    private static ArrayList<Cast> casts = new ArrayList<>();
    private ArrayList<Synopsis> synopsises = new ArrayList<>();
    private ArrayList<ShowVideo> episodes = new ArrayList<>();
    private String banner;
    private String title;
    private ArrayList<Video> promos = new ArrayList<>();
    private Concept concept;
    private Concept timeConcept;
    private ArrayList<ConceptValue> timeConcepts = new ArrayList<>();
    private String time;
    private String colorCode;
    private String showLogo;
    //  private ImageAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_show_details);
        ButterKnife.inject(this);


        if(!AndroidUtils.isNetworkOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry! No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if(getIntent().hasExtra(SHOW_ID)){

            String showId =   getIntent().getStringExtra(SHOW_ID);
            String countryId = SonyDataManager.init(this).getCountryId();

            String url = String.format(Constants.SHOW_DETAILS,showId,countryId,
                    AndroidUtils.getScreenWidth(this),AndroidUtils.getScreenHeight(this));
            fetchShowDetails(url);
        }else {
            mNoContent.setVisibility(View.VISIBLE);
            mNoContent.setText("No Content Available!");
        }

        if (getIntent().hasExtra(SHOW_COLOR_CODE)){
            colorCode =  getIntent().getStringExtra(SHOW_COLOR_CODE);
            Log.d("ShowDetailsAcivity","colorcode" + colorCode);
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
            }

        Picasso.with(this).load(Uri.parse(banner)).placeholder(R.drawable.place_holder).into(mShowImage);
        Picasso.with(this).load(Uri.parse(showLogo)).into(mShowLogo);
        mShowTitle.setText(title);

        timeConcepts = timeConcept.getValues();
        for (int i = 0; i < timeConcepts.size() ; i++){
            time = timeConcepts.get(i).getValue();
        }

        mShowTime.setText(time);
        mShowTime.setTextColor(Color.parseColor("#767676"));

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

        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mBottomPager.setAdapter(mPagerAdapter);
        //   mBottomPager.setScrollDurationFactor(SLIDE_DURATION_FACTOR);
        mBottomPager.setOnPageChangeListener(this);
        mBottomPager.setCurrentItem(2);
        mTabLayout.setViewPager(mBottomPager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

  /*  private void setUpcarousel() {
        mCarousel.setType(100);
        mCarousel.setInfiniteScrollEnabled(true);
        mCarousel.setItemRearrangeEnabled(true);
        adapter1 = new ImageAdapter(this);
        mCarousel.setAdapter(adapter1);
       // mCarousel.setCenterPosition(2);
        mCarousel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id = view.getId();
                Toast.makeText(ShowDetailsActivity.this, "clicked" + i, Toast.LENGTH_SHORT).show();
                Log.d("MyActivity","item clicked" + i);

              //  mPager.setCurrentItem(i);
            }
        });

        mCarousel.setOnItemSelectionUpdatedListener(new Carousel.OnItemSelectionUpdatedListener() {
            @Override
            public void onItemSelectionUpdated(AdapterView<?> adapterView, View view, int i) {
                Log.d("MyActivity","item selection update" + i);
                //  carousel.setCenterPosition(i-1);
              //  mPager.setCurrentItem(i);
            }
        });
    }*/

  /*  public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private int[] musicCover = { R.drawable.ic_launcher, R.drawable.cal,
                R.drawable.filter, R.drawable.star, R.drawable.tv};

        public ImageAdapter(Context c)
        {
            mContext = c;
        }

        @Override
        public int getCount() {
            return musicCover.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return musicCover[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.carousel_item, parent, false);
                view.setLayoutParams(new Carousel.LayoutParams(250, 250));

                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.itemImage);

                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)view.getTag();
            holder.imageView.setImageResource(musicCover[position]);

            return view;
        }

        private class ViewHolder {
            ImageView imageView;
        }
    }*/

    /*public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private String[] names = {"CAST","CONCEPT","HOME","EPISODES","SYNOPSIS"};
        private int[] musicCover = { R.drawable.ic_launcher, R.drawable.cal,
                R.drawable.filter, R.drawable.star, R.drawable.tv};

        public ImageAdapter(Context c)
        {
            mContext = c;
        }

        @Override
        public int getCount() {
            return musicCover.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return musicCover[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.carousel_item_text, parent, false);
                view.setLayoutParams(new Carousel.LayoutParams(250, 250));

                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.home_image);
                holder.mTitle = (TextView)view.findViewById(R.id.cast_title);

                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)view.getTag();
            holder.imageView.setImageResource(musicCover[position]);

            holder.mTitle.setText(names[position]);
            return view;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView mTitle;
        }
    }*/

    private static class NavigationAdapter extends FragmentStatePagerAdapter {

        private String[] names = {"CAST","CONCEPT","HOME","EPISODES","SYNOPSIS"};
        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new Fragment();

            //  final int pattern = position % 5;
            switch (position) {

                case 0:
                    f = new CastFragment();
                    break;

                case 1:
                    f = new ConceptFragment();
                    break;

                case 2:
                    f = new ShowDetailHomeFragment();
                    break;

                case 3:
                    f = new ShowEpisodeFragment();
                    break;

                case 4:
                    f = new SynopsisFragment();
                    break;

            }
            return f;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //    final int pattern = position % 5;
            return names[position];
        }

    }

    public ArrayList<Cast> getCasts(){
        return casts;
    }

    public ArrayList<Synopsis> getSynopsises(){
        return synopsises;
    }

    public ArrayList<ShowVideo> getEpisodes(){
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
            R.id.menuEpisodes,R.id.menuVideos/*,R.id.menuNews,R.id.menuFeedback*/})
    public void menuItemClicked(View view){

        switch (view.getId()){
            case R.id.menuConcept :
                loadPager(1);
                break;

            case R.id.menuCast :
                loadPager(0);
                break;

            case R.id.menuSynopsis :
                loadPager(4);
                break;

            case R.id.menuEpisodes :
                loadPager(3);
                break;

            case R.id.menuVideos :
                loadPager(2);
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
}
