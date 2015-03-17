package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.adapters.ShowAdapter;
import com.teli.sonyset.models.ShowDetail;
import com.teli.sonyset.Utils.SonyRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by madhuri on 4/3/15.
 */
public class ShowFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.shows_lv)
    ListView mListview;

    @InjectView(R.id.show_progress)
    ProgressBar mPbar;
    private Context mContext;
    private String countryId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shows,null);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!AndroidUtils.isNetworkOnline(mContext)){
            return;
        }

        countryId  = SonyDataManager.init(mContext).getCountryId();

        if(countryId != null && !countryId.isEmpty())
            fetchAllShows();

        if (!AndroidUtils.isNetworkOnline(getActivity()))
            return;

    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    private void fetchAllShows() {

        String url = String.format(Constants.ALL_SHOWS, countryId, AndroidUtils.getScreenWidth(mContext), AndroidUtils.getScreenHeight(mContext));

        SonyRequest request = new SonyRequest(mContext , url) {
            @Override
            public void onResponse(JSONArray s) {
                Log.d("ShowFragment", "Fragment Response" + s);
                mPbar.setVisibility(View.GONE);

                if (s!=null && !s.toString().isEmpty())
                    initAdapter(s);
            }

            @Override
            public void onError(String e) {
                Log.d("ShowFragment", "Fragment Exception" + e);
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

    private void initAdapter(JSONArray json) {

        Gson gson=new Gson();

        ArrayList<ShowDetail> shows = gson.fromJson(json.toString(), new TypeToken<List<ShowDetail>>() {
        }.getType());

        if (shows!=null && !shows.isEmpty()) {
            SonyDataManager.init(mContext).setShows(json.toString());
            ShowAdapter adapter = new ShowAdapter(getActivity(), shows);
            mListview.setAdapter(adapter);
            mListview.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ShowDetail item = (ShowDetail) adapterView.getItemAtPosition(i);
        String showId = item.getShowId();
        Intent intent = new Intent(mContext, ShowDetailsActivity.class);
        intent.putExtra(ShowDetailsActivity.SHOW_ID,showId);
        intent.putExtra(ShowDetailsActivity.SHOW_COLOR_CODE,item.getColorCode());
        startActivity(intent);
    }
}
