package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.models.Concept;
import com.teli.sonyset.models.ConceptValue;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by madhuri on 12/3/15.
 */
public class ConceptFragment extends Fragment {


    @InjectView(R.id.linearLayout)
    LinearLayout mLinearLayout;

    private Context mContext;
    private Concept mConcept;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concept,null);
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        if(!AndroidUtils.isNetworkOnline(mContext)){
            return;
        }

        mConcept = ((ShowDetailsActivity)mContext).getConcept();

        if (mConcept!=null && !mConcept.toString().isEmpty()){
            ArrayList<ConceptValue> concepts = mConcept.getValues();

            for (int i = 0 ; i <concepts.size() ; i++){

                String concept = concepts.get(i).getValue();
                SonyTextView textView = new SonyTextView(mContext);
                textView.setText(concept);
                textView.setTextColor(Color.parseColor("#767676"));

                mLinearLayout.addView(textView);
            }
        }
        super.onActivityCreated(savedInstanceState);
    }
}
