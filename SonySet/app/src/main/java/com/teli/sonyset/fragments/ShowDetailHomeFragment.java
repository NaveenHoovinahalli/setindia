package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SetRequestQueue;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.activities.VideoDetailsActivity;
import com.teli.sonyset.models.Cast;
import com.teli.sonyset.models.Concept;
import com.teli.sonyset.models.ConceptValue;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.views.SonyTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
    SonyTextView mFirstShowTopTitle;

    @InjectView(R.id.episode_title)
    SonyTextView mFirstShowTitle;

    @InjectView(R.id.about_details)
    SonyTextView mAboutDetails;

    @InjectView(R.id.duration)
    SonyTextView mDuration;

    @InjectView(R.id.cast_layout)
    LinearLayout mCastLayout;

    @InjectView(R.id.noContent)
    SonyTextView noContent;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    @InjectView(R.id.cast_iv1)
    CircularImageView mCastIv1;

    @InjectView(R.id.cast_iv2)
    CircularImageView mCastIv2;

    @InjectView(R.id.cast_iv3)
    CircularImageView mCastIv3;

    @InjectView(R.id.color_code_view)
    SonyTextView mFirstColorCode;

    private Context mContext;
    private ArrayList<Video> promos = new ArrayList<>();
    private ArrayList<String> brightCoveIds = new ArrayList<>();
    private HashMap<Integer,String> thumbnailsBrightCove = new HashMap<>();

    private Concept concept;
    private ArrayList<ConceptValue> conceptValues = new ArrayList<>();
    private ArrayList<Cast> casts = new ArrayList<>();
    private String mColorcode;
    private ArrayList<Video> valueOld = new ArrayList<>();
    private HashMap<Integer, String> brightCoveThumbnailsOld = new HashMap<>();
    private ArrayList<String> brightCoveListOld = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(!AndroidUtils.isNetworkOnline(mContext)){
            return;
        }

        promos.clear();
        promos = ((ShowDetailsActivity)mContext).getPromos();
        concept = ((ShowDetailsActivity)mContext).getConcept();
        casts = ((ShowDetailsActivity)mContext).getCasts();
        mColorcode = ((ShowDetailsActivity)mContext).getColorCode();

        if (concept!=null && !concept.toString().isEmpty()){
            conceptValues = concept.getValues();

            for (int i = 0; i < conceptValues.size() ; i++){
                mAboutDetails.setText(conceptValues.get(i).getValue());
            }
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
        }

        if (promos!=null && !promos.isEmpty() && promos.size()!=0){
            fetchBrightCoveID(promos);
        }else {
            noContent.setVisibility(View.VISIBLE);
            noContent.setText("No Cast Found!");
        }
        super.onActivityCreated(savedInstanceState);
    }

    private void fetchBrightCoveID(ArrayList<Video> s) {

        Log.d("TestActivity","response" + s.toString());
        if (promos!=null && !promos.isEmpty()) {
            for (int i = 0; i < promos.size(); i++) {
                brightCoveIds.add(promos.get(i).getBrightCoveId());
                Log.d("TestActivity","brightcoveId" + promos.get(i).getBrightCoveId());
                Log.d("TestActivity","brightcoveId" + promos.get(i).getShowTitle());
                Log.d("TestActivity","brightcoveIds" + brightCoveIds);
            }

            for (int i = 0; i < brightCoveIds.size(); i++) {
                fetchThumbnail(brightCoveIds.get(i), s , i);
            }
        }
    }

    private void fetchThumbnail(String s, final ArrayList<Video> value, final int i) {

        String url = String.format(Constants.BRIGHT_COVE_THUMBNAIL, s);
        Log.d("EpisodeFragment","url" + url);
        Log.d("EpisodeFragment", "thumbnails : " + url);
        final JsonObjectRequest request = new JsonObjectRequest(url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyActivity", "Thumbnail" + response);

                        if (response != null) {
                            try {
                                JSONObject object = new JSONObject(response.toString());
                                //  brightCoveThumbnails.add((String) object.get("videoStillURL"));
                                thumbnailsBrightCove.put(i,(String) object.get("videoStillURL"));
                                if (thumbnailsBrightCove.size() == brightCoveIds.size()){
                                    initAdapter(value , thumbnailsBrightCove);
                                }
                                Log.d("pageScrolled", "brightCoveThumbnails" + thumbnailsBrightCove.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        thumbnailsBrightCove.put(i, "null");
                        Log.d("pageScrolled", "Error" + error);
                    }
                });
        SetRequestQueue.getInstance(mContext).getRequestQueue().add(request);
    }

    private void initAdapter(final ArrayList<Video> value, HashMap<Integer, String> brightCoveThumbnails) {
        Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(0))).into(mFirstShowImageView);
        mFirstShowTime.setText(value.get(0).getOnAirDate());
        mFirstShowTitle.setText(value.get(0).getShowTitle());
        mFirstShowTopTitle.setText(value.get(0).getShowName());
        mDuration.setText(value.get(0).getDuration());

        if(mColorcode!=null && !mColorcode.isEmpty() && !mColorcode.equalsIgnoreCase("null"))
            if (mColorcode.equals("R")){
                mFirstColorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
            }else if (mColorcode.equals("G")){
                mFirstColorCode.setBackgroundColor(Color.parseColor("#38A92C"));
            }else if (mColorcode.equals("B")){
                mFirstColorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
            }

        mFirstShowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadVideoActivity(brightCoveIds.get(0) , 0);
            }
        });

        valueOld = value;
        brightCoveThumbnailsOld = brightCoveThumbnails;

        ArrayList<String> brightCoveList = new ArrayList<String>(brightCoveThumbnails.values());

        for (int i = 0 ; i<brightCoveList.size();i++)
            brightCoveListOld.add(brightCoveList.get(i));

        value.remove(0);
        brightCoveList.remove(0);

        mProgress.setVisibility(View.GONE);

        for (int i = 0; i<value.size();i = i+2){
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.dynamic_layout, null);

            final ImageView promoIv = (ImageView) view.findViewById(R.id.episode_iv);
            SonyTextView promoTime = (SonyTextView) view.findViewById(R.id.episode_time);
            SonyTextView promoNum = (SonyTextView) view.findViewById(R.id.episode_num);
            SonyTextView promoTitle = (SonyTextView) view.findViewById(R.id.episode_title);
            TextView colorCode = (TextView) view.findViewById(R.id.color_code_view);
            SonyTextView duration = (SonyTextView) view.findViewById(R.id.duration);

            final ImageView promoIv1 = (ImageView) view.findViewById(R.id.episode_iv1);
            SonyTextView promoTime1 = (SonyTextView) view.findViewById(R.id.episode_time1);
            SonyTextView promoNum1 = (SonyTextView) view.findViewById(R.id.episode_num1);
            SonyTextView promoTitle1 = (SonyTextView) view.findViewById(R.id.episode_title1);
            TextView colorCode1 = (TextView) view.findViewById(R.id.color_code_view1);
            SonyTextView duration1 = (SonyTextView) view.findViewById(R.id.duration);

            Picasso.with(mContext).load(Uri.parse(brightCoveList.get(i))).placeholder(R.drawable.place_holder).into(promoIv);
            promoTitle.setText(value.get(i).getShowTitle());
            promoTime.setText(value.get(i).getOnAirDate());
            promoTime.setBackgroundResource(R.drawable.rounded_text_box);
            promoNum.setText(value.get(i).getEpisodeNumber());
            duration.setText(value.get(i).getDuration());

            promoIv.setTag(i);

            if (i+1 < value.size()) {
                Picasso.with(mContext).load(Uri.parse(brightCoveList.get(i + 1))).placeholder(R.drawable.place_holder).into(promoIv1);
                promoTitle1.setText(value.get(i + 1).getShowTitle());
                promoTime1.setText(value.get(i + 1).getOnAirDate());
                duration1.setText(value.get(i+1).getDuration());

                promoTime1.setBackgroundResource(R.drawable.rounded_text_box);
                promoNum1.setText(value.get(i + 1).getEpisodeNumber());

                if (mColorcode!=null && !mColorcode.isEmpty() && !mColorcode.equalsIgnoreCase("null")) {
                   colorCode1.setVisibility(View.VISIBLE);
                    if (mColorcode.equalsIgnoreCase("R")) {
                        colorCode1.setBackgroundColor(Color.parseColor("#CD2E2E"));
                    } else if (mColorcode.equalsIgnoreCase("G")) {
                        colorCode1.setBackgroundColor(Color.parseColor("#38A92C"));
                    } else if (mColorcode.equalsIgnoreCase("B")) {
                        colorCode1.setBackgroundColor(Color.parseColor("#4A67D6"));
                    }
                }
                promoIv1.setTag(i+1);
            }

            if (mColorcode!=null && !mColorcode.isEmpty() && !mColorcode.equalsIgnoreCase("null")) {
                colorCode.setVisibility(View.VISIBLE);
                if (mColorcode.equalsIgnoreCase("R")) {
                    colorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
                } else if (mColorcode.equalsIgnoreCase("G")) {
                    colorCode.setBackgroundColor(Color.parseColor("#38A92C"));
                } else if (mColorcode.equalsIgnoreCase("B")) {
                    colorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
                }
            }
            mShowsImages.addView(view);
            // view.setTag(i);

            promoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = (int) promoIv.getTag();

                    Log.d("ShowDetailsFragment","onClicklistener" + i);

                    loadVideoActivity(value.get(i).getBrightCoveId() , i);
                }
            });

            if (promoIv1!=null)
                promoIv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int i = (int) promoIv1.getTag();

                        Log.d("ShowDetailsFragment","onClicklistener" + i);

                        loadVideoActivity(value.get(i).getBrightCoveId() , i);
                    }
                });
        }
    }

    public void loadVideoActivity(String brightCoveId , int i){
        Intent openVideoActivity = new Intent(mContext, VideoDetailsActivity.class);
        openVideoActivity.putExtra(Constants.POSITION, "" + i);
        openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, promos);
        openVideoActivity.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveListOld);
        openVideoActivity.putExtra(Constants.IS_PROMO, true);
        startActivity(openVideoActivity);

        Log.d("TestActivity","brightCoveThumbnailsOld" + brightCoveThumbnailsOld);
        Log.d("TestActivity","brightCoveThumbnailsOld" + brightCoveListOld);
        Log.d("TestActivity","brightCoveThumbnailsOld" + brightCoveIds);

    }
}
