package com.teli.sonyset.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teli.sonyset.R;
import com.teli.sonyset.activities.LandingActivity;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.views.HorizontalLinearLayout;

import java.util.ArrayList;

public class ShowDetailsStripFragment extends Fragment {

    public static int mPosition;
    private LinearLayout linearLayout;

    String[] names = new String[]{
            "Home",
            "EPISODES",
            "SYNOPSIS",
            "CAST",
            "CONCEPT"
    };

    int[] blue = new int[]{
            R.drawable.show_detail_home_blue_selector,
            R.drawable.show_detail_episodes_blue_selector,
            R.drawable.show_detail_synopsis_blue_selector,
            R.drawable.show_detail_cast_blue_selector,
            R.drawable.show_detail_concept_blue_selector
    };

    int[] red = new int[]{
            R.drawable.show_detail_home_red_selector,
            R.drawable.show_detail_episodes_red_selector,
            R.drawable.show_detail_synopsis_red_selector,
            R.drawable.show_detail_cast_red_selector,
            R.drawable.show_detail_concept_red_selector
    };

    int[] green = new int[]{
            R.drawable.show_detail_home_green_selector,
            R.drawable.show_detail_episodes_green_selector,
            R.drawable.show_detail_synopsis_green_selector,
            R.drawable.show_detail_cast_green_selector,
            R.drawable.show_detail_concept_green_selector
    };

    private int pos;
    private LinearLayout strip_container;
    static ArrayList<Fragment> fragments = new ArrayList<>();
    private int mCount;
    private String mColor;

    public static Fragment newInstance(Activity context, int pos,
                                       float scale, int count, String color) {
        mPosition = pos;
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putInt("count", count);
        b.putFloat("scale", scale);
        b.putString("color", color);
        Fragment fragment = Fragment.instantiate(context, ShowDetailsStripFragment.class.getName(), b);
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
                inflater.inflate(R.layout.show_details_bar_fragment, container, false);
        pos = this.getArguments().getInt("pos");
        mCount = this.getArguments().getInt("count");
        mColor = this.getArguments().getString("color");
        strip_container = (LinearLayout) linearLayout.findViewById(R.id.strip_item);
        if (mColor.toLowerCase().equals("b"))
            strip_container.setBackgroundResource(blue[pos]);
        else if (mColor.toLowerCase().equals("g"))
            strip_container.setBackgroundResource(green[pos]);
        else
        strip_container.setBackgroundResource(red[pos]);

        strip_container.setTag(pos);

        if (mCount == 2500) {
            strip_container.setSelected(true);
        }

        strip_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Position" + pos, Toast.LENGTH_SHORT).show();
                ((ShowDetailsActivity) getActivity()).setSelectedIten(mCount);
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
}

//LightGrey #474747
//Black #1e1e1e



