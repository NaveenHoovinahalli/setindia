package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.teli.sonyset.adapters.EpisodeAdapter;
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
import jp.co.recruit_mp.android.widget.HeaderFooterGridView;

/**
 * Created by madhuri on 5/3/15.
 */
public class EpisodeFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.episodes_gv)
    HeaderFooterGridView mGridView;

    @InjectView(R.id.pBar)
    ProgressBar mPbar;

    private Context mContext;
    private ArrayList<String> brightCoveIds = new ArrayList<String>();
    private HashMap<Integer, String> brightCoveThumbnailsOld = new HashMap<>();
    private ArrayList<Video> episodesOld = new ArrayList<>();
    private HashMap<Integer,String> thumbnailsBrightCove = new HashMap<>();
    private ArrayList<String> brightCoveListOld = new ArrayList<>();
    private String countryId;
    private ArrayList<Video> episodesFirst = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_episode,null);
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
        super.onActivityCreated(savedInstanceState);

        if (!AndroidUtils.isNetworkOnline(mContext)) {
            return;
        }

        countryId = SonyDataManager.init(mContext).getCountryId();

        if (countryId!=null && !countryId.isEmpty())
            fetchEpisodes();
    }

    private void fetchEpisodes() {

        String url = String.format(Constants.ALL_EPISODES,countryId );
        Log.d("EpisodeFragment", "Url :" + url);
        SonyRequest request = new SonyRequest(mContext,url) {
            @Override
            public void onResponse(JSONArray s) {
                Log.d("ShowFragment", "Fragment Response" + s);

                fetchBrightCoveID(s);
            }

            @Override
            public void onError(String e) {
                if (getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPbar.setVisibility(View.GONE);
                        }
                    });
            }
        };
        request.execute();
    }

    private void fetchBrightCoveID(JSONArray s) {
        Gson gson=new Gson();

        episodesFirst = gson.fromJson(s.toString(), new TypeToken<List<Video>>() {
        }.getType());

        if (episodesFirst!=null && !episodesFirst.isEmpty()) {
            for (int i = 0; i < episodesFirst.size(); i++) {
                brightCoveIds.add(episodesFirst.get(i).getBrightCoveId());
            }

            for (int i = 0; i < brightCoveIds.size(); i++) {
                fetchThumbnail(brightCoveIds.get(i), s , i);
            }
        }
    }

    private void fetchThumbnail(String s, final JSONArray value, final int i) {

        String url = String.format(Constants.BRIGHT_COVE_THUMBNAIL, s);

        Log.d("EpisodeFragment", "thumbnails : " + url);
        final JsonObjectRequest request = new JsonObjectRequest(url,
                null,
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
                                initAdapter(value, thumbnailsBrightCove);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // thumbnailsBrightCove.put(i,"null");
                        Log.d("pageScrolled", "Error" + error);
                    }
                });
        SetRequestQueue.getInstance(mContext).getRequestQueue().add(request);
    }

    private void initAdapter(JSONArray response, HashMap<Integer, String> brightCoveThumbnails) {
        Gson gson=new Gson();

        final ArrayList<Video> episodes = gson.fromJson(response.toString(), new TypeToken<List<Video>>() {
        }.getType());

        mPbar.setVisibility(View.GONE);

        if (episodes!=null && !episodes.isEmpty()) {

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View headerView = layoutInflater.inflate(R.layout.fragment_header_item, null);

            headerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.videoImageheaderheight)));

            ImageView episodeImage = (ImageView) headerView.findViewById(R.id.episode_iv);
            TextView episodeTitle = (TextView) headerView.findViewById(R.id.episode_title);
            SonyTextView episodeNumber = (SonyTextView) headerView.findViewById(R.id.episode_num);
            SonyTextView episodeTime = (SonyTextView) headerView.findViewById(R.id.episode_time);
            TextView colorCode = (TextView) headerView.findViewById(R.id.color_code_view);
            TextView duration = (TextView) headerView.findViewById(R.id.duration);

            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "klavikamedium_plain_webfont.ttf");
            episodeTitle.setTypeface(tf);

            if (brightCoveThumbnails.size() != 0)
                if (!brightCoveThumbnails.get(0).equals("null")){
                    Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(0))).placeholder(R.drawable.place_holder).into(episodeImage);
                }else {
                    //  Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(0))).into(episodeImage);
                    episodeImage.setImageResource(R.drawable.place_holder);
                }

            episodeTitle.setText(episodes.get(0).getShowName());

            episodeTime.setText(episodes.get(0).getOnAirDate());

            if (!episodes.get(0).getDuration().isEmpty()){
                duration.setText(episodes.get(0).getDuration());
            }else {
                duration.setVisibility(View.GONE);
            }

            if (!episodes.get(0).getEpisodeNumber().isEmpty()){
                episodeTime.setText(episodes.get(0).getEpisodeNumber());
            }else {
                episodeTime.setVisibility(View.GONE);
            }

            String color = episodes.get(0).getColorCode();
            if (!color.equalsIgnoreCase("null"))
                if (color.toLowerCase().equalsIgnoreCase("r")){
                    colorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
                }else if (color.toLowerCase().equalsIgnoreCase("g")){
                    colorCode.setBackgroundColor(Color.parseColor("#38A92C"));
                }else if (color.toLowerCase().equalsIgnoreCase("b")){
                    colorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
                }

            episodesOld = episodes;
            brightCoveThumbnailsOld = brightCoveThumbnails;

            ArrayList<String> brightCoveList = new ArrayList<String>(brightCoveThumbnails.values());
            //brightCoveListOld = brightCoveList;

            for (int i =0 ;i<brightCoveList.size();i++){
                brightCoveListOld.add(brightCoveList.get(i));
            }

            episodes.remove(0);
            brightCoveList.remove(0);

            EpisodeAdapter adapter = new EpisodeAdapter(mContext, episodes , brightCoveList);
            mGridView.addHeaderView(headerView);
            mGridView.setAdapter(adapter);
            mGridView.setOnItemClickListener(this);

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("EpisodeFragment", "clicked header");
                    Log.d("EpisodeFragment", "clicked header getBrightCoveId" + episodesOld.get(0).getBrightCoveId());

                    Intent openVideoActivity = new Intent(mContext, VideoDetailsActivity.class);
                    openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, episodesFirst);
                    openVideoActivity.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveListOld);
                    openVideoActivity.putExtra(Constants.IS_EPISODE, true);
                    openVideoActivity.putExtra(Constants.POSITION, "" + 0);
                    startActivity(openVideoActivity);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Video item = (Video) adapterView.getItemAtPosition(i);
        Log.d("EpisodeFragment", "OnItemClicked");
        Log.d("EpisodeFragment", "OnItemClicked getBrightCoveId" + item.getBrightCoveId());
        i = i-1;
        Intent openVideoActivity = new Intent(mContext, VideoDetailsActivity.class);
        openVideoActivity.putExtra(Constants.POSITION, "" + i);
        openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, episodesFirst);
        openVideoActivity.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveListOld);
        openVideoActivity.putExtra(Constants.IS_EPISODE, true);
        startActivity(openVideoActivity);
    }
}
