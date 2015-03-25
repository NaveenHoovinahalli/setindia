package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.teli.sonyset.Utils.SonyRequest;
import com.teli.sonyset.activities.LandingActivity;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.adapters.ShowAdapter;
import com.teli.sonyset.models.ShowDetail;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shows,null);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
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
//            mListview.setEnabled(false);
            mListview.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_POINTER_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_POINTER_UP:
//                            ((LandingActivity)getActivity()).expandView();
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;

                        /*case MotionEvent.EDGE_RIGHT:
//                            ((LandingActivity)getActivity()).expandView();
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;*/
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
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

    public void enableTouch(){
        mListview.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return false; // Indicates that this has been handled by you and will not be forwarded further.
                }
                return false;
            }
        });
    }

    public void disbleTouch(){
        mListview.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true; // Indicates that this has been handled by you and will not be forwarded further.
                }
                return false;
            }
        });
    }
}
