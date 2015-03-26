package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SetRequestQueue;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.activities.VideoDetailsActivity;
import com.teli.sonyset.models.BrightCoveThumbnail;
import com.teli.sonyset.models.Cast;
import com.teli.sonyset.models.Concept;
import com.teli.sonyset.models.ConceptValue;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.views.SonyTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by madhuri on 12/3/15.
 */

public class ShowDetailHomeFragment extends Fragment {

    @InjectView(R.id.shows_gv)
    LinearLayout mShowsImages;

    @InjectView(R.id.first_iv)
    RelativeLayout mFirstImage;

    @InjectView(R.id.episode_iv)
    ImageView mFirstShowImageView;

    @InjectView(R.id.episode_time)
    SonyTextView mFirstShowTime;

    @InjectView(R.id.episode_top_title)
    TextView mFirstShowTopTitle;

    @InjectView(R.id.episode_title)
    TextView mFirstShowTitle;

    @InjectView(R.id.about_details)
    TextView mAboutDetails;

    @InjectView(R.id.duration)
    SonyTextView mDuration;

    @InjectView(R.id.cast_layout)
    LinearLayout mCastLayout;

    @InjectView(R.id.noContent)
    SonyTextView noContent;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    @InjectView(R.id.cast_iv1)
    CircleImageView mCastIv1;

    @InjectView(R.id.cast_iv2)
    CircleImageView mCastIv2;

    @InjectView(R.id.cast_iv3)
    CircleImageView mCastIv3;

    @InjectView(R.id.color_code_view)
    SonyTextView mFirstColorCode;

    @InjectView(R.id.cast_text)
    TextView mCastTextBox;

    @InjectView(R.id.view_all_btn)
    ImageView mViewAllBtn;

    @InjectView(R.id.read_more_text)
    TextView mReadMoreText;

    @InjectView(R.id.read_more_btn)
    ImageView mReadMoreBtn;

    private Context mContext;
    private ArrayList<Video> promos = new ArrayList<>();
    private ArrayList<Video> promosOld = new ArrayList<>();
    private ArrayList<String> brightCoveIds = new ArrayList<>();
    private HashMap<Integer,String> thumbnailsBrightCove = new HashMap<>();

    private Concept concept;
    private ArrayList<ConceptValue> conceptValues = new ArrayList<>();
    private ArrayList<Cast> casts = new ArrayList<>();
    private String mColorcode;
    private ArrayList<Video> valueOld = new ArrayList<>();
    private HashMap<Integer, String> brightCoveThumbnailsOld = new HashMap<>();
    private ArrayList<String> brightCoveListOld = new ArrayList<>();
    private String firstShowTopTitle;
    private String firstShowSubTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_show_detail_home,null);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        if(!AndroidUtils.isNetworkOnline(mContext)){
            return;
        }

        promos = ((ShowDetailsActivity)mContext).getPromos();
        concept = ((ShowDetailsActivity)mContext).getConcept();
        casts = ((ShowDetailsActivity)mContext).getCasts();
        mColorcode = ((ShowDetailsActivity)mContext).getColorCode();

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "klavikalight-plain-webfont.ttf");
        mReadMoreText.setTypeface(tf);
        mCastTextBox.setTypeface(tf);

        if (concept!=null && !concept.toString().isEmpty()){
            conceptValues = concept.getValues();

            for (int i = 0; i < conceptValues.size() ; i++){
                mAboutDetails.setText(conceptValues.get(i).getValue());
            }
        }else {
            mReadMoreText.setVisibility(View.GONE);
            mReadMoreBtn.setVisibility(View.GONE);
        }

        if (casts!=null && !casts.toString().isEmpty() && casts.size() !=0 ){
            if (casts.size() == 1){
                Picasso.with(mContext).load(Uri.parse(casts.get(0).getThumbnail())).placeholder(R.drawable.place_holder_circle).into(mCastIv1);
            }else if (casts.size() == 2){
                Picasso.with(mContext).load(Uri.parse(casts.get(0).getThumbnail())).placeholder(R.drawable.place_holder_circle).into(mCastIv1);
                Picasso.with(mContext).load(Uri.parse(casts.get(1).getThumbnail())).placeholder(R.drawable.place_holder_circle).into(mCastIv2);
            }else if (casts.size() >= 3){
                Picasso.with(mContext).load(Uri.parse(casts.get(0).getThumbnail())).placeholder(R.drawable.place_holder_circle).into(mCastIv1);
                Picasso.with(mContext).load(Uri.parse(casts.get(1).getThumbnail())).placeholder(R.drawable.place_holder_circle).into(mCastIv2);
                Picasso.with(mContext).load(Uri.parse(casts.get(3).getThumbnail())).placeholder(R.drawable.place_holder_circle).into(mCastIv3);
            }
        }else {
            mCastTextBox.setVisibility(View.GONE);
            mViewAllBtn.setVisibility(View.GONE);
        }

        if (promos!=null && !promos.isEmpty() && promos.size()!=0){
            fetchBrightCoveID(promos);
        }else {
            mFirstImage.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            /*noContent.setVisibility(View.VISIBLE);
            noContent.setText("No Details Found!");*/
        }
        super.onActivityCreated(savedInstanceState);
    }

    private void fetchBrightCoveID(ArrayList<Video> s) {

        if (promos!=null && !promos.isEmpty() && promos.size()!=0) {

            Log.d("ShowdetailHomeFragmetn", "value" + promos.get(0).getShowTitle());
            Log.d("ShowdetailHomeFragmetn", "value" + promos.get(0).getShowName());

            firstShowTopTitle = promos.get(0).getShowName();
            firstShowSubTitle = promos.get(0).getShowTitle();

            for (int i = 0 ; i <promos.size();i++){
                promosOld.add(promos.get(i));
            }

            for (int i = 0; i < promos.size(); i++) {
                brightCoveIds.add(promos.get(i).getBrightCoveId());
            }

            for (int i = 0; i < brightCoveIds.size(); i++) {
                fetchThumbnail(brightCoveIds.get(i), s , i);
            }
        }
    }

    private void fetchThumbnail(String s, final ArrayList<Video> value, final int i) {

        String url = String.format(Constants.BRIGHT_COVE_THUMBNAIL, s);
        final JsonObjectRequest request = new JsonObjectRequest(url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null && !response.toString().isEmpty()) {
                            BrightCoveThumbnail brightCoveThumbnail = new Gson().fromJson(response.toString(), BrightCoveThumbnail.class);

                            if (!brightCoveThumbnail.getVideoStillUrl().isEmpty()) {

                                thumbnailsBrightCove.put(i, brightCoveThumbnail.getVideoStillUrl());
                            } else {
                                thumbnailsBrightCove.put(i, "null");
                            }

                            if (thumbnailsBrightCove.size() == brightCoveIds.size()) {
                                initAdapter(value, thumbnailsBrightCove);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         thumbnailsBrightCove.put(i, "null");
                        if (thumbnailsBrightCove.size() == brightCoveIds.size()) {
                            initAdapter(value, thumbnailsBrightCove);
                        }
                        Log.d("pageScrolled", "Error" + error);
                    }
                });
        SetRequestQueue.getInstance(mContext).getRequestQueue().add(request);
    }

    private void initAdapter(final ArrayList<Video> value, HashMap<Integer, String> brightCoveThumbnails) {
        Typeface subTitleTypeface = Typeface.createFromAsset(mContext.getAssets(), "klavikamedium_plain_webfont.ttf");
        Typeface topTitleTypeface = Typeface.createFromAsset(mContext.getAssets(), "klavikaregular_plain_webfont.ttf");

        if (promos!=null && !promos.toString().isEmpty() && promos.size()!=0) {
            Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(0))).into(mFirstShowImageView);

            if (!promos.get(0).getOnAirDate().isEmpty()) {
                mFirstShowTime.setBackgroundResource(R.drawable.rounded_text_box);
                mFirstShowTime.setText(promos.get(0).getOnAirDate());
            }

            mFirstShowTitle.setTypeface(subTitleTypeface);
            mFirstShowTitle.setText(firstShowSubTitle);

            mFirstShowTopTitle.setTypeface(topTitleTypeface);
            mFirstShowTopTitle.setText(firstShowTopTitle);

            mDuration.setText(promos.get(0).getDuration());
            mDuration.setBackgroundColor(Color.parseColor("#000000"));
            Log.d("ShowdetailHomeFragmetn", "value first " + promos.get(0).getShowTitle());
            Log.d("ShowdetailHomeFragmetn", "value first" + promos.get(0).getShowName());

            if (mColorcode != null && !mColorcode.isEmpty() && !mColorcode.equalsIgnoreCase("null"))
                if (mColorcode.equalsIgnoreCase("r")) {
                    mFirstColorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
                } else if (mColorcode.equalsIgnoreCase("g")) {
                    mFirstColorCode.setBackgroundColor(Color.parseColor("#38A92C"));
                } else if (mColorcode.equalsIgnoreCase("b")) {
                    mFirstColorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
                }

            if(mFirstShowImageView!=null) {
                mFirstShowImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadVideoActivity(brightCoveIds.get(0), 0);
                    }
                });
            }

            valueOld = value;
            brightCoveThumbnailsOld = brightCoveThumbnails;

          /*  for(int i =0 ; i < value.size();i++){
               valueOld.add(value.get(i));
            }*/

            ArrayList<String> brightCoveList = new ArrayList<String>(brightCoveThumbnails.values());

            for (int i = 0; i < brightCoveList.size(); i++)
                brightCoveListOld.add(brightCoveList.get(i));

           // value.remove(0);
          //  brightCoveList.remove(0);

            mProgress.setVisibility(View.GONE);

            for (int i = 1; i < value.size() - 1; i = i + 2) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View view = layoutInflater.inflate(R.layout.dynamic_layout, null);

                final ImageView promoIv = (ImageView) view.findViewById(R.id.episode_iv);
                SonyTextView promoTime = (SonyTextView) view.findViewById(R.id.episode_time);
                TextView promoNum = (TextView) view.findViewById(R.id.episode_num);
                TextView promoTitle = (TextView) view.findViewById(R.id.episode_title);
                TextView colorCode = (TextView) view.findViewById(R.id.color_code_view);
                SonyTextView duration = (SonyTextView) view.findViewById(R.id.duration);

                final ImageView promoIv1 = (ImageView) view.findViewById(R.id.episode_iv1);
                SonyTextView promoTime1 = (SonyTextView) view.findViewById(R.id.episode_time1);
                TextView promoNum1 = (TextView) view.findViewById(R.id.episode_num1);
                TextView promoTitle1 = (TextView) view.findViewById(R.id.episode_title1);
                TextView colorCode1 = (TextView) view.findViewById(R.id.color_code_view1);
                SonyTextView duration1 = (SonyTextView) view.findViewById(R.id.duration);

                Picasso.with(mContext).load(Uri.parse(brightCoveList.get(i))).placeholder(R.drawable.place_holder).into(promoIv);
                promoTitle.setTypeface(subTitleTypeface);
                promoTitle.setText(value.get(i).getShowTitle());

                if (!value.get(i).getOnAirDate().isEmpty()) {
                    promoTime.setText(value.get(i).getOnAirDate());
                    promoTime.setBackgroundResource(R.drawable.rounded_text_box);
                }

                promoNum.setTypeface(topTitleTypeface);
                promoNum.setText(value.get(i).getShowName());

                duration.setText(value.get(i).getDuration());
                duration.setBackgroundColor(Color.parseColor("#000000"));

                promoIv.setTag(i);

                if (i + 1 < value.size() - 1) {
                    Picasso.with(mContext).load(Uri.parse(brightCoveList.get(i + 1))).placeholder(R.drawable.place_holder).into(promoIv1);
                    promoTitle1.setTypeface(subTitleTypeface);
                    promoTitle1.setText(value.get(i + 1).getShowTitle());

                    if (!value.get(i+1).getOnAirDate().isEmpty()) {
                        promoTime1.setText(value.get(i + 1).getOnAirDate());
                        promoTime1.setBackgroundResource(R.drawable.rounded_text_box);
                    }

                    promoNum1.setTypeface(topTitleTypeface);
                    promoNum1.setText(value.get(i + 1).getEpisodeNumber());

                    duration1.setText(value.get(i + 1).getDuration());
                    duration.setBackgroundColor(Color.parseColor("#000000"));

                    if (mColorcode != null && !mColorcode.isEmpty() && !mColorcode.equalsIgnoreCase("null")) {
                        colorCode1.setVisibility(View.VISIBLE);
                        if (mColorcode.equalsIgnoreCase("r")) {
                            colorCode1.setBackgroundColor(Color.parseColor("#CD2E2E"));
                        } else if (mColorcode.equalsIgnoreCase("g")) {
                            colorCode1.setBackgroundColor(Color.parseColor("#38A92C"));
                        } else if (mColorcode.equalsIgnoreCase("b")) {
                            colorCode1.setBackgroundColor(Color.parseColor("#4A67D6"));
                        }
                    }
                    promoIv1.setTag(i + 1);
                }

                if (mColorcode != null && !mColorcode.isEmpty() && !mColorcode.equalsIgnoreCase("null")) {
                    colorCode.setVisibility(View.VISIBLE);
                    if (mColorcode.equalsIgnoreCase("r")) {
                        colorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
                    } else if (mColorcode.equalsIgnoreCase("g")) {
                        colorCode.setBackgroundColor(Color.parseColor("#38A92C"));
                    } else if (mColorcode.equalsIgnoreCase("b")) {
                        colorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
                    }
                }
                mShowsImages.addView(view);
                // view.setTag(i);

                promoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int i = (int) promoIv.getTag();

                        Log.d("ShowDetailsFragment", "onClicklistener" + i);

                        loadVideoActivity(value.get(i).getBrightCoveId(), i);
                    }
                });

                if (promoIv1 != null)
                    promoIv1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int i = (int) promoIv1.getTag();

                            Log.d("ShowDetailsFragment", "onClicklistener" + i);

                            loadVideoActivity(value.get(i).getBrightCoveId(), i);
                        }
                    });
            }
        }
    }

    public void loadVideoActivity(String brightCoveId , int i){
        Intent openVideoActivity = new Intent(mContext, VideoDetailsActivity.class);
        openVideoActivity.putExtra(Constants.POSITION, "" + i);
        openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, promosOld);
        openVideoActivity.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveListOld);
        openVideoActivity.putExtra(Constants.IS_PROMO, true);
        startActivity(openVideoActivity);
    }

    @OnClick(R.id.read_more_btn)
    public void readMoreClicked(){
        ((ShowDetailsActivity)mContext).setBottomPagerToConcept();
    }


    @OnClick(R.id.view_all_btn)
    public void viewAllClicked(){
        ((ShowDetailsActivity)mContext).setBottomPagerToCast();
    }


}
