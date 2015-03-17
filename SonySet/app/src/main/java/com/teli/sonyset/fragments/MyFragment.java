package com.teli.sonyset.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.teli.sonyset.R;
import com.teli.sonyset.activities.LandingActivity;
import com.teli.sonyset.views.HorizontalLinearLayout;

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

    public static Fragment newInstance(LandingActivity context, int pos,
                                       float scale, int count) {
        mPosition = pos;
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putInt("count", count);
        b.putFloat("scale", scale);
        Fragment fragment = Fragment.instantiate(context, MyFragment.class.getName(), b);
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
        rootLayout.setTag(pos);
        strip_container = (LinearLayout) linearLayout.findViewById(R.id.strip_item);
        strip_container.setTag(pos);
        strip_container.setBackgroundResource(images[pos]);
       /* View view = (View) linearLayout.findViewById(R.id.strip);
        TextView tv = (TextView) linearLayout.findViewById(R.id.text);
        ImageView iv = (ImageView) linearLayout.findViewById(R.id.imageview);
        tv.setText(names[pos]);
        iv.setImageResource(images[pos]);*/
       /* oldPos = pos;
        previousView = strip_container;*/
        if(pos == 0){
//            strip_container.setSelected(true);
            previousView = strip_container;
        }

        strip_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Toast.makeText(getActivity(), "Position" + rootLayout.getChildCount(), Toast.LENGTH_SHORT).show();
                ((LandingActivity)getActivity()).setSelectedIten(mCount);
                for(int i=0; i<rootLayout.getChildCount(); i++){
                    View mView = rootLayout.getChildAt(i);
                    mView.setSelected(false);
                }
//                strip_container.setSelected(true);
               // previousView.setBackgroundResource(images[oldPos]);
            }
        });

        HorizontalLinearLayout root = (HorizontalLinearLayout) linearLayout.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return linearLayout;
    }

    public void setSelected(int pos) {

        Log.d("MyFragment","setSelected" + pos);


    }

    public LinearLayout getFragmentLayout() {
        if (linearLayout != null)
            return linearLayout;

        return null;
    }
}

//LightGrey #474747
//Black #1e1e1e



