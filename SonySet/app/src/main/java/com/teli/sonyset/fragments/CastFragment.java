package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.adapters.CastAdapter;
import com.teli.sonyset.models.Cast;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by madhuri on 12/3/15.
 */
public class CastFragment extends Fragment {

  /*  @InjectView(R.id.linear_listview)
    LinearLayout mLinearLayout;
*/


    @InjectView(R.id.cast_lv)
    ListView mCastListView;

    @InjectView(R.id.noContent)
    SonyTextView mNoContent;

    private Context mContext;
    private ArrayList<Cast> casts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cast,null);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        if(!AndroidUtils.isNetworkOnline(mContext)){
            return;
        }
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        casts =  ((ShowDetailsActivity)mContext).getCasts();

        Log.d("CastFragment","casts" + casts);

        if (casts!=null && !casts.toString().isEmpty() && casts.size() != 0) {
            CastAdapter adapter = new CastAdapter(mContext, casts);
            mCastListView.setAdapter(adapter);
        }else {
            mNoContent.setVisibility(View.VISIBLE);
            mNoContent.setText("No Cast Found!");
        }

        /*for (int i = 0 ; i<casts.size() ; i++){

            Log.d("CastFragment","casts:: "+  i);

            LayoutInflater inflater = null;
            inflater = (LayoutInflater) mContext.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mLinearView = inflater.inflate(R.layout.fragment_cast_detail, null);

            ImageView castIv = (ImageView) mLinearView.findViewById(R.id.cast_iv);
            SonyTextView castTitle = (SonyTextView) mLinearView.findViewById(R.id.cast_title);
            final SonyTextView castDetail = (SonyTextView) mLinearView.findViewById(R.id.cast_detail);
            ImageView downImage = (ImageView) mLinearView.findViewById(R.id.down_image);
            final LinearLayout linearLayout = (LinearLayout) mLinearView.findViewById(R.id.linear_layout);

            Picasso.with(mContext).load(Uri.parse(casts.get(i).getThumbnail())).into(castIv);

            castTitle.setText(casts.get(i).getName());
            castDetail.setText(casts.get(i).getDescription());
            mLinearView.setTag(i);

            downImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    castDetail.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                }
            });
            mLinearLayout.addView(mLinearView);
        }*/
        super.onActivityCreated(savedInstanceState);
    }

}
