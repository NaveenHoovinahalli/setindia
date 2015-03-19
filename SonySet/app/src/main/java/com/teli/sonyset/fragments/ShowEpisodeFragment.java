package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.adapters.ShowEpisodeAdapter;
import com.teli.sonyset.models.ShowVideo;
import com.teli.sonyset.views.SonyTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    private Context mContext;
    private ArrayList<ShowVideo> episodes = new ArrayList<>();
    private ArrayList<String> brightCoveIds = new ArrayList<>();
    private ArrayList<String> brightCoveThumbnails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onActivityCreated( Bundle savedInstanceState) {
        if(!AndroidUtils.isNetworkOnline(mContext)){
            return;
        }

        episodes = ((ShowDetailsActivity)mContext).getEpisodes();
      /*  Log.d("EpisodeFragment", "episodes : " + episodes.toString());
        Log.d("EpisodeFragment", "episodes : " + episodes.get(0).getBrightCoveId());
        Log.d("EpisodeFragment", "episodes : " + episodes.get(0).getEpisodeNumber());
        Log.d("EpisodeFragment", "episodes : " + episodes.get(0).getDuration());
        Log.d("EpisodeFragment", "episodes : " + episodes.toString());*/

        if (episodes!=null && !episodes.toString().isEmpty() && episodes.size() != 0) {
            /*for (int i = 0; i < episodes.size(); i++) {
                  fetchBrightCoveID(episodes);
            }*/

            Log.d("EpisodeFragment", "episodes empty : " + episodes);
//            mNoContent.setVisibility(View.VISIBLE);
//            mNoContent.setText("No Episodes Found!");

        }else {
            Log.d("EpisodeFragment", "episodes empty : " + episodes);
            mNoContent.setVisibility(View.VISIBLE);
            mNoContent.setText("No Episodes Found!");
        }
        super.onActivityCreated(savedInstanceState);
    }

    private void fetchBrightCoveID(ArrayList<ShowVideo> s) {
      /*  Gson gson=new Gson();

        ArrayList<ShowVideo> episodes = gson.fromJson(s.toString(), new TypeToken<List<ShowVideo>>() {
        }.getType());
*/
        if (episodes!=null && !episodes.isEmpty()) {
            for (int i = 0; i < episodes.size(); i++) {
                brightCoveIds.add(episodes.get(i).getBrightCoveId());
            }

            for (int i = 0; i < brightCoveIds.size(); i++) {
                fetchThumbnail(brightCoveIds.get(i), s);
            }
        }
    }

    private void fetchThumbnail(String s, final ArrayList<ShowVideo> value) {

        String url = String.format(Constants.BRIGHT_COVE_THUMBNAIL, s);

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
                                brightCoveThumbnails.add((String) object.get("videoStillURL"));

                                if (brightCoveThumbnails.size() == brightCoveIds.size()){
                                    initAdapter(value , brightCoveThumbnails);
                                }
                                Log.d("pageScrolled", "brightCoveThumbnails" + brightCoveThumbnails.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        brightCoveThumbnails.add("null");
                        Log.d("pageScrolled", "Error" + error);
                    }
                });
        SetRequestQueue.getInstance(mContext).getRequestQueue().add(request);
    }

    private void initAdapter(ArrayList<ShowVideo> response, ArrayList<String> brightCoveThumbnails) {
        Gson gson=new Gson();

        ArrayList<ShowVideo> episodes = gson.fromJson(response.toString(), new TypeToken<List<ShowVideo>>() {
        }.getType());

        if (episodes!=null && !episodes.isEmpty()) {

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View headerView = layoutInflater.inflate(R.layout.fragment_header_item, null);

            headerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,300));

            ImageView episodeImage = (ImageView) headerView.findViewById(R.id.episode_iv);
            SonyTextView episodeTitle = (SonyTextView) headerView.findViewById(R.id.episode_title);
            SonyTextView episodeNumber = (SonyTextView) headerView.findViewById(R.id.episode_num);
            SonyTextView episodeTime = (SonyTextView) headerView.findViewById(R.id.episode_time);
            TextView colorCode = (TextView) headerView.findViewById(R.id.color_code_view);

            if (brightCoveThumbnails.size() != 0)
                if (!brightCoveThumbnails.get(0).equals("null")){
                    Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(0))).into(episodeImage);
                }else {
                    //  Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(0))).into(episodeImage);
                    episodeImage.setImageResource(R.drawable.ic_launcher);
                }

            //   episodeTitle.setText(episodes.get(0).getShowName());
            episodeNumber.setText(episodes.get(0).getEpisodeNumber());
            episodeTime.setText(episodes.get(0).getOnAirDate());

           /* String color = episodes.get(0).getColorCode();
            if (color.equals("R")){
                colorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
            }else if (color.equals("G")){
                colorCode.setBackgroundColor(Color.parseColor("#38A92C"));
            }else if (color.equals("B")){
                colorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
            }*/

//color code

            episodes.remove(0);
            brightCoveThumbnails.remove(0);
            ShowEpisodeAdapter adapter = new ShowEpisodeAdapter(mContext, episodes , brightCoveThumbnails);
            mEpisodeList.addHeaderView(headerView);
            mEpisodeList.setAdapter(adapter);
            mEpisodeList.setOnItemClickListener(this);

            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("EpisodeFragment", "clicked header");
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
