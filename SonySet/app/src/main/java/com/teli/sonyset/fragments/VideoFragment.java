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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SetRequestQueue;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.Utils.SonyRequest;
import com.teli.sonyset.activities.VideoDetailsActivity;
import com.teli.sonyset.adapters.VideoAdapter;
import com.teli.sonyset.models.BrightCoveThumbnail;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.views.SonyTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import jp.co.recruit_mp.android.widget.HeaderFooterGridView;

/**
 * Created by madhuri on 10/3/15.
 */
public class VideoFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.videos_gv)
    HeaderFooterGridView mGridView;

    @InjectView(R.id.videobar)
    ProgressBar videoBar;

    @InjectView(R.id.promos)
    TextView mPromos;

    @InjectView(R.id.precaps)
    TextView mPrecaps;

    @InjectView(R.id.topColor1)
    TextView mColorTop1;

    @InjectView(R.id.topColor2)
    TextView mColorTop2;

    private Context mContext;
    private ArrayList<String> brightCoveIds = new ArrayList<String>();
    private HashMap<Integer,String> thumbnailsBrightCove = new HashMap<>();

    View oldHeaderView;
    private ArrayList<Video> videosOld = new ArrayList<>();
    private HashMap<Integer, String> brightCoveThumbnailsOld = new HashMap<>();
    private String type = "promo";
    private ArrayList<String> brightCoveListOld = new ArrayList<>();
    private ArrayList<Video> promos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video,null);
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

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "klavikamedium_plain_webfont.ttf");
        mPrecaps.setTypeface(tf);
        mPromos.setTypeface(tf);

        if(!AndroidUtils.isNetworkOnline(mContext)){
            return;
        }

        if (!SonyDataManager.init(mContext).getPrecapsIsFromMenu(true)) {
            SonyDataManager.init(mContext).savePrecapsIsFromMenu(false);
            String url = String.format(Constants.ALL_PROMOS, 1);
            mPromos.setBackgroundColor(Color.parseColor("#191919"));
            mColorTop1.setBackgroundColor(Color.parseColor("#2D2D2D"));
            fetchData(url);
        }else {
            precaps();
        }
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick({R.id.promos, R.id.precaps})
    public void clicked(View view){

        String url = "";

        switch (view.getId()){
            case R.id.promos :
                promos();
                break;

            case R.id.precaps:
                precaps();
                break;
        }
    }

    public void promos(){
        String url = String.format(Constants.ALL_PROMOS, 1);
        videoBar.setVisibility(View.VISIBLE);
        mPromos.setBackgroundColor(Color.parseColor("#191919"));
        mPrecaps.setBackgroundColor(Color.parseColor("#323232"));

        mColorTop1.setBackgroundColor(Color.parseColor("#2D2D2D"));
        mColorTop2.setBackgroundColor(Color.parseColor("#737373"));

        fetchData(url);
        type = "promo";
    }

    public void precaps(){
        SonyDataManager.init(mContext).savePrecapsIsFromMenu(true);
        String url = String.format(Constants.ALL_VIDEOS, 1);
        mPrecaps.setBackgroundColor(Color.parseColor("#191919"));
        mPromos.setBackgroundColor(Color.parseColor("#323232"));

        mColorTop1.setBackgroundColor(Color.parseColor("#737373"));
        mColorTop2.setBackgroundColor(Color.parseColor("#2D2D2D"));
        videoBar.setVisibility(View.VISIBLE);
        fetchData(url);
        type = "video";
    }

    private void fetchData(String url) {
        SonyRequest request = new SonyRequest(mContext,url) {
            @Override
            public void onResponse(JSONArray s) {
                Log.d("VideoFragment", "Promo response::" + s);
                if (s!=null && !s.toString().isEmpty())
                    initAdapter(s);
            }

            @Override
            public void onError(String e) {
                Log.d("VideoFragment", "Promo error::" + e);
                if (getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoBar.setVisibility(View.GONE);
                        }
                    });
            }
        };
        request.execute();
    }

    private void initAdapter(JSONArray jsonArray) {
        brightCoveIds.clear();

        Gson gson=new Gson();

        promos = gson.fromJson(jsonArray.toString(), new TypeToken<List<Video>>() {
        }.getType());

        if (promos!=null && !promos.isEmpty()){
            for (int i = 0;i<promos.size();i++){
                brightCoveIds.add(promos.get(i).getBrightCoveId());
            }

            for (int i = 0;i < brightCoveIds.size();i++){
                Log.d("pageScrolled", "brightCoveIds" + brightCoveIds.get(i));
                fetchThumbnail(promos.get(i).getBrightCoveId() , jsonArray , i);
            }
        }
    }

    private void fetchThumbnail(String s, final JSONArray jsonArray, final int i) {

        String url = String.format(Constants.BRIGHT_COVE_THUMBNAIL, s);
        final JsonObjectRequest request = new JsonObjectRequest(url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyActivity", "Thumbnail" + response);

                        if (response != null && !response.toString().isEmpty()) {
                            BrightCoveThumbnail brightCoveThumbnail = new Gson().fromJson(response.toString(), BrightCoveThumbnail.class);

                            if (!brightCoveThumbnail.getVideoStillUrl().isEmpty()) {
                                thumbnailsBrightCove.put(i, brightCoveThumbnail.getVideoStillUrl());
                            } else {
                                thumbnailsBrightCove.put(i, "null");
                            }

                            if (thumbnailsBrightCove.size() == brightCoveIds.size()) {

                                Log.d("MyActivity", "equal size");
                                setAdapter(jsonArray, thumbnailsBrightCove);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("pageScrolled", "Error" + error);

                      //  thumbnailsBrightCove.put(i,"null");
                    }
                });
        SetRequestQueue.getInstance(mContext).getRequestQueue().add(request);
    }

    private void setAdapter(JSONArray jsonArray, HashMap<Integer, String> brightCoveThumbnails) {
        Gson gson=new Gson();

        ArrayList<Video> videos = gson.fromJson(jsonArray.toString(), new TypeToken<List<Video>>() {
        }.getType());

        videoBar.setVisibility(View.GONE);

        if (videos!=null && !videos.isEmpty()) {

            mGridView.removeHeaderView(oldHeaderView);

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View headerView = layoutInflater.inflate(R.layout.video_fragment_header_item, null);

            headerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.videoImageheaderheight)));

            ImageView episodeImage = (ImageView) headerView.findViewById(R.id.episode_iv);

            SonyTextView episodeTitle = (SonyTextView) headerView.findViewById(R.id.episode_title);
            TextView episodeShowName = (TextView) headerView.findViewById(R.id.episode_showName);

            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "klavikamedium_plain_webfont.ttf");
            episodeShowName.setTypeface(tf);

            TextView colorCode = (TextView) headerView.findViewById(R.id.color_code_view);
            if (brightCoveThumbnails.size() != 0)
                if (!brightCoveThumbnails.get(0).equals("null")){
                    Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(0))).placeholder(R.drawable.place_holder).into(episodeImage);
                }else {
                    episodeImage.setImageResource(R.drawable.place_holder);
                }

            episodeTitle.setText(videos.get(0).getShowTitle());
            episodeShowName.setText(videos.get(0).getShowName());

            String color = videos.get(0).getColorCode();
            if (color.equals("R")){
                colorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
            }else if (color.equals("G")){
                colorCode.setBackgroundColor(Color.parseColor("#38A92C"));
            }else if (color.equals("B")){
                colorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
            }
            oldHeaderView = headerView;

            videosOld = videos;

            ArrayList<String> brightCoveList = new ArrayList<String>(brightCoveThumbnails.values());

            for (int i = 0; i<brightCoveList.size();i++)
                brightCoveListOld.add(brightCoveList.get(i));

            videos.remove(0);
            brightCoveList.remove(0);

            VideoAdapter adapter = new VideoAdapter(mContext, videos , brightCoveList);
            mGridView.addHeaderView(headerView);
            adapter.notifyDataSetChanged();
            mGridView.setAdapter(adapter);

            mGridView.setOnItemClickListener(this);

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent openVideoActivity = new Intent(mContext, VideoDetailsActivity.class);
                    openVideoActivity.putExtra(Constants.POSITION, "" + 0);
                    openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, promos);
                    openVideoActivity.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveListOld);

                    if (type.equals("promo"))
                        openVideoActivity.putExtra(Constants.IS_PROMO, true);
                    startActivity(openVideoActivity);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("EpisodeFragment", "item clicked");

        i = i-1;

        Intent openVideoActivity = new Intent(mContext, VideoDetailsActivity.class);
        openVideoActivity.putExtra(Constants.POSITION, "" + i);
        openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, promos);
        openVideoActivity.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveListOld);

        if (type.equals("promo"))
            openVideoActivity.putExtra(Constants.IS_PROMO, true);
        startActivity(openVideoActivity);
    }
}

