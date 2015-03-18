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
    private int pos;
    private LinearLayout strip_container;
    static ArrayList<Fragment> fragments = new ArrayList<>();
    private int mCount;

    public static Fragment newInstance(Activity context, int pos,
                                       float scale, int count) {
        mPosition = pos;
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putInt("count", count);
        b.putFloat("scale", scale);
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
        strip_container = (LinearLayout) linearLayout.findViewById(R.id.strip_item);
        strip_container.setTag(pos);
        TextView tv = (TextView) linearLayout.findViewById(R.id.text);
        ImageView iv = (ImageView) linearLayout.findViewById(R.id.imageview);
        tv.setText(names[pos]);

        if(pos==0){
            strip_container.setSelected(true);
            tv.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);
            iv.setImageResource(R.drawable.home);
        }
        strip_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Position" + pos, Toast.LENGTH_SHORT).show();
                ((ShowDetailsActivity)getActivity()).setSelectedIten(mCount);
                for(int i=0; i<fragments.size();i++){
                    View v = fragments.get(i).getView();
                    if(v!=null){
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



