package com.teli.sonyset.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.teli.sonyset.R;
import com.teli.sonyset.activities.LandingActivity;
import com.teli.sonyset.views.HorizontalLinearLayout;

import java.util.ArrayList;

public class MyFragment extends Fragment {

    public static int mPosition;
    private LinearLayout linearLayout;
    int[] images = new int[]{
            R.drawable.strip_selector_show,
            R.drawable.strip_selector_exclusive,
            R.drawable.strip_selector_video,
            R.drawable.strip_selector_episode,
            R.drawable.strip_selector_schedule
    };

    String[] names = new String[]{
            "SHOWS",
            "EXCLUSIVES",
            "VIDEOS",
            "EPISODES",
            "SCHEDULE"
    };

    private int pos;
    private LinearLayout strip_container;
    private HorizontalLinearLayout rootLayout;
    private int mCount;
    private View previousView;
    private int oldPos;

    static ArrayList<Fragment> fragments = new ArrayList<>();
    private int previousItem;

    public static Fragment newInstance(LandingActivity context, int pos,
                                       float scale, int count) {
        mPosition = pos;
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putInt("count", count);
        b.putFloat("scale", scale);
        Fragment fragment = Fragment.instantiate(context, MyFragment.class.getName(), b);
        fragments.add(fragment);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        linearLayout = (LinearLayout)
                inflater.inflate(R.layout.horiz_bar_fragment, container, false);

        pos = this.getArguments().getInt("pos");
        mCount = this.getArguments().getInt("count");
        rootLayout = (HorizontalLinearLayout) linearLayout.findViewById(R.id.root);
        rootLayout.setTag(mCount);
        strip_container = (LinearLayout) linearLayout.findViewById(R.id.strip_item);
        strip_container.setTag(pos);
        strip_container.setBackgroundResource(images[pos]);
        if (pos == 0) {
            strip_container.setSelected(true);
            previousItem = 0;
        }

        strip_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LandingActivity) getActivity()).setSelectedItem(mCount);
                for (int i = 0; i < fragments.size(); i++) {
                    View v = fragments.get(i).getView();
                    if (v != null) {
                        LinearLayout layout = (LinearLayout) v.findViewById(R.id.strip_item);
                        layout.setSelected(false);
                    }
                }

                strip_container.setSelected(true);
            }
        });

        HorizontalLinearLayout root = (HorizontalLinearLayout) linearLayout.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return linearLayout;
    }

    public void selectLayout(int position) {
    }

    public void unSelectLayout(int position) {
//        for (int i = 0; i < fragments.size(); i++) {
//            View v = fragments.get(i).getView();
//            if (v != null) {
//                LinearLayout layout = (LinearLayout) v.findViewById(R.id.strip_item);
//                if(i!=position) {
//                    layout.setSelected(false);
//                }else if(i==position){
//                    layout.setSelected(true);
//                }
//            }
//        }
    }

    public LinearLayout getFragmentLayout() {
        if (linearLayout != null)
            return linearLayout;
        return null;
    }
}

//LightGrey #474747
//Black #1e1e1e



