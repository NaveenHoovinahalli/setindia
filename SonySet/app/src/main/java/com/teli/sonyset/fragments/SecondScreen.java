package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.teli.sonyset.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sahana on 20/3/15.
 */
public class SecondScreen extends Fragment {

    @InjectView(R.id.welcome_txt)
    TextView welcomeText;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_second_screen,container,false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(),"klavikalight-plain-webfont.ttf");
        welcomeText.setTypeface(tf);

        fetchText();
        super.onActivityCreated(savedInstanceState);
    }

    private void fetchText() {

    }
}
