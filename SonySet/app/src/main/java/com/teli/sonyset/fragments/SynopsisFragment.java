package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.adapters.SynopsisAdapter;
import com.teli.sonyset.models.Synopsis;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by madhuri on 12/3/15.
 */
public class SynopsisFragment extends Fragment {

    @InjectView(R.id.synopsis_list)
    ListView mListView;

    @InjectView(R.id.noContent)
    SonyTextView noContent;

    private Context mContext;
    private ArrayList<Synopsis> synopsises = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_synopsis_list,null);
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

        synopsises =  ((ShowDetailsActivity)mContext).getSynopsises();

        if (synopsises!=null && !synopsises.toString().isEmpty() && synopsises.size() != 0) {
            SynopsisAdapter adapter = new SynopsisAdapter(mContext, synopsises);
            mListView.setAdapter(adapter);
        }else {
            noContent.setVisibility(View.VISIBLE);
            noContent.setText("No Synopsis Found!");
        }

        super.onActivityCreated(savedInstanceState);
    }
}
