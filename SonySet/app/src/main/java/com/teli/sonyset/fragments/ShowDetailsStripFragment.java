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

public class ShowDetailsStripFragment extends Fragment {

    public static int mPosition;
    private LinearLayout linearLayout;
    int[] images = new int[]{
            R.drawable.shows_unsel,
            R.drawable.exclusive_unsel,
            R.drawable.videos_unsel,
            R.drawable.episodes_unsel,
            R.drawable.schedule_unsel

    };

    String[] names = new String[]{
            "Home",
            "EPISODES",
            "SYNOPSIS",
            "CAST",
            "CONCEPT"
    };
    private int pos;
    private LinearLayout strip_container;
    private int mCount;

    public static Fragment newInstance(Activity context, int pos,
                                       float scale, int count) {
        mPosition = pos;
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putInt("count", count);
        b.putFloat("scale", scale);
        return Fragment.instantiate(context, ShowDetailsStripFragment.class.getName(), b);
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
        strip_container = (LinearLayout) linearLayout.findViewById(R.id.strip_item);
        strip_container.setTag(pos);
        View view = (View) linearLayout.findViewById(R.id.strip);
        TextView tv = (TextView) linearLayout.findViewById(R.id.text);
        ImageView iv = (ImageView) linearLayout.findViewById(R.id.imageview);
        tv.setText(names[pos]);
        iv.setImageResource(images[pos]);
        if(pos==0){
            strip_container.setSelected(true);
        }
        strip_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Position" + pos, Toast.LENGTH_SHORT).show();
                ((ShowDetailsActivity)getActivity()).setSelectedIten(mCount);
            }
        });


        HorizontalLinearLayout root = (HorizontalLinearLayout) linearLayout.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return linearLayout;
    }

    private void setSelected(View view) {

    }

    public LinearLayout getFragmentLayout() {
        if (linearLayout != null)
            return linearLayout;

        return null;
    }
}

//LightGrey #474747
//Black #1e1e1e



