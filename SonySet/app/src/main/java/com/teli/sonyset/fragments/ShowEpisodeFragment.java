package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SetRequestQueue;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.activities.VideoDetailsActivity;
import com.teli.sonyset.adapters.ShowEpisodeAdapter;
import com.teli.sonyset.models.BrightCoveThumbnail;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.views.SonyTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.co.recruit_mp.android.widget.HeaderFooterGridView;

/**
 * Created by madhuri on 12/3/15.
 */
public class ShowEpisodeFragment extends Fragment implements AdapterView.OnItemClickListener {


    @InjectView(R.id.episodes_gv)
    HeaderFooterGridView mEpisodeList;

    @InjectView(R.id.noContent)
    SonyTextView mNoContent;

    @InjectView(R.id.exclusiveProgress)
    ProgressBar mExclusiveProgress;

    private Context mContext;
    private ArrayList<Video> episodes = new ArrayList<>();
    private ArrayList<String> brightCoveIds = new ArrayList<>();
    private HashMap<Integer,String> thumbnailsBrightCove = new HashMap<>();
    private ArrayList<String> brightCoveListOld = new ArrayList<>();
    private ArrayList<Video> episodesOld = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_episode_list,null);
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

        episodes = ((ShowDetailsActivity)mContext).getEpisodes();

        if (episodes!=null && !episodes.toString().isEmpty() && episodes.size() != 0) {
            fetchBrightCoveID(episodes);
        }else {
            mExclusiveProgress.setVisibility(View.GONE);
            Log.d("EpisodeFragment", "episodes empty : " + episodes);
            mNoContent.setVisibility(View.VISIBLE);
            mNoContent.setText("No Episodes Found!");
        }
        super.onActivityCreated(savedInstanceState);
    }

    private void fetchBrightCoveID(ArrayList<Video> s) {

        if (episodes!=null && !episodes.isEmpty()) {
            for (int i = 0; i < episodes.size(); i++) {
                brightCoveIds.add(episodes.get(i).getBrightCoveId());
            }

            for (int i = 0; i < brightCoveIds.size(); i++) {
                fetchThumbnail(brightCoveIds.get(i),s ,i);
            }
        }
    }

    private void fetchThumbnail(String s, final ArrayList<Video> value, final int i) {

        String url = String.format(Constants.BRIGHT_COVE_EPISODE_THUMBNAIL, s);

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
                      //  thumbnailsBrightCove.put(i, "null");
                        Log.d("pageScrolled", "Error" + error);
                    }
                });
        SetRequestQueue.getInstance(mContext).getRequestQueue().add(request);
    }

    private void initAdapter(ArrayList<Video> response, HashMap<Integer, String> brightCoveThumbnails) {
        mExclusiveProgress.setVisibility(View.GONE);
        if (episodes!=null && !episodes.isEmpty()) {

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View headerView = layoutInflater.inflate(R.layout.fragment_header_item, null);

            headerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.promoHeight)));

            ImageView episodeImage = (ImageView) headerView.findViewById(R.id.episode_iv);
            TextView episodeTitle = (TextView) headerView.findViewById(R.id.episode_title);
            SonyTextView episodeNumber = (SonyTextView) headerView.findViewById(R.id.episode_num);
            SonyTextView episodeTime = (SonyTextView) headerView.findViewById(R.id.episode_time);
            TextView colorCode = (TextView) headerView.findViewById(R.id.color_code_view);

            if (brightCoveThumbnails.size() != 0)
                if (!brightCoveThumbnails.get(0).equals("null")){
                    Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(0))).into(episodeImage);
                }else {
                    episodeImage.setImageResource(R.drawable.place_holder);
                }

            episodeTitle.setText(episodes.get(0).getShowName());
            episodeNumber.setText(episodes.get(0).getEpisodeNumber());
            episodeTime.setText(episodes.get(0).getOnAirDate());

            String color = episodes.get(0).getColorCode();

            if (!color.equalsIgnoreCase("null"))
                if (color.equalsIgnoreCase("R")){
                    colorCode.setBackgroundColor(mContext.getResources().getColor(R.color.sony_red));
                }else if (color.equalsIgnoreCase("G")){
                    colorCode.setBackgroundColor(mContext.getResources().getColor(R.color.sony_green));
                }else if (color.equalsIgnoreCase("B")){
                    colorCode.setBackgroundColor(mContext.getResources().getColor(R.color.sony_blue));
                }

            ArrayList<String> brightCoveList = new ArrayList<String>(brightCoveThumbnails.values());
            for (int i =0 ;i<brightCoveList.size();i++){
                brightCoveListOld.add(brightCoveList.get(i));
            }
//color code
            for (int i = 0 ; i<episodes.size();i++){
                episodesOld.add(episodes.get(i));
            }

            episodes.remove(0);
            brightCoveList.remove(0);

            ShowEpisodeAdapter adapter = new ShowEpisodeAdapter(mContext, episodes , brightCoveList);
            mEpisodeList.addHeaderView(headerView);
            mEpisodeList.setAdapter(adapter);
            mEpisodeList.setOnItemClickListener(this);

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("EpisodeFragment", "clicked header");

                    Intent openVideoActivity = new Intent(mContext, VideoDetailsActivity.class);
                    openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, episodesOld);
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
        openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, episodes);
        openVideoActivity.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveListOld);
        openVideoActivity.putExtra(Constants.IS_EPISODE, true);
        startActivity(openVideoActivity);
    }
}
