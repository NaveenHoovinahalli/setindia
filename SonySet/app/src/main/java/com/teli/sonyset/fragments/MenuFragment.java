package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.korovyansk.android.slideout.SlideoutHelper;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.activities.LandingActivity;
import com.teli.sonyset.activities.MenuActivity;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.activities.WebViewActivity;
import com.teli.sonyset.adapters.ExpandableListAdapterMenu;
import com.teli.sonyset.models.ShowDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MenuFragment extends Fragment implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener, ExpandableListView.OnGroupExpandListener {

    @InjectView(R.id.lvExp)
    ExpandableListView expListViev;

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ExpandableListAdapterMenu listAdapter;

    ArrayList<ShowDetail> showsDetails;
    private Context mContext;
    private SlideoutHelper slideOut;
    private int lastExpandedPosition = -1;

    @InjectView(R.id.bottomsocial)
    RelativeLayout socialButtonRL;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.menu_list,null);

        ButterKnife.inject(this,view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        if(width==540){
            RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) socialButtonRL.getLayoutParams();
            relativeParams.setMargins(30, 0, 0, 0);  // left, top, right, bottom
            socialButtonRL.setLayoutParams(relativeParams);
        }

//        socialButtonRL

//        Log.d("Menu","Screen hight"+height+"width"+width);
        //888 540


        fetchValues();
        setListView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       slideOut = ((MenuActivity)mContext).getSlideoutHelper();
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    private void setListView() {
        listAdapter=new ExpandableListAdapterMenu(getActivity(), listDataHeader, listDataChild,showsDetails);
        expListViev.setAdapter(listAdapter);
        expListViev.setOnChildClickListener(this);
        expListViev.setOnGroupClickListener(this);
        expListViev.setOnGroupExpandListener(this);
//        expListViev.setGroupIndicator(null);
    }

    private void fetchValues() {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("SHOWS");
        listDataHeader.add("SCHEDULE");
        listDataHeader.add("FULL EPISODES");
        listDataHeader.add("VIDEOS");
        listDataHeader.add("MISCELLANEOUS");
        listDataHeader.add("TEST");
       /* listDataHeader.add("About SET");
        listDataHeader.add("Contact Us ");
        listDataHeader.add("Terms of Use ");
        listDataHeader.add("Privacy Policy ");
        listDataHeader.add("Disclaimer");*/

        String showsResponse= SonyDataManager.init(getActivity()).getShows();
        Gson gson=new Gson();
        List<String> shows = new ArrayList<String>();

        if(!showsResponse.isEmpty()) {
            showsDetails = gson.fromJson(showsResponse, new TypeToken<List<ShowDetail>>() {
            }.getType());

            for(int i=0;i<showsDetails.size();i++) {
                // Adding child data
                shows.add(showsDetails.get(i).getShowTitle());
            }
        }

        List<String> schedule = new ArrayList<String>();
        schedule.add("SD");
        schedule.add("HD");

        List<String> videos= new ArrayList<String>();
        videos.add("PROMOS");
        videos.add("PRECAPS");

        List<String> episodes=new ArrayList<String>();

        List<String> miscellaneous = new ArrayList<>();
        miscellaneous.add("About SET");
        miscellaneous.add("Contact Us ");
        miscellaneous.add("Terms of Use ");
        miscellaneous.add("Privacy Policy ");
        miscellaneous.add("Disclaimer");

        List<String> test = new ArrayList<>();

        listDataChild.put(listDataHeader.get(0), shows); // Header, Child data
        listDataChild.put(listDataHeader.get(1), schedule);
        listDataChild.put(listDataHeader.get(2), episodes);
        listDataChild.put(listDataHeader.get(3),videos);
        listDataChild.put(listDataHeader.get(4),miscellaneous);
        listDataChild.put(listDataHeader.get(5),test);

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Log.d("ChildPosition",+groupPosition+"--"+childPosition);

        if(groupPosition==0){
            //call show details
            Log.d("Menu","SHow id"+showsDetails.get(childPosition).getShowId());
            Log.d("Menu","SHow Color"+showsDetails.get(childPosition).getColorCode());
            slideOut.close();

            Intent intent = new Intent(mContext, ShowDetailsActivity.class);
            intent.putExtra(ShowDetailsActivity.SHOW_ID,showsDetails.get(childPosition).getShowId());
            intent.putExtra(ShowDetailsActivity.SHOW_COLOR_CODE,showsDetails.get(childPosition).getColorCode());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        if(groupPosition==1){
            if(childPosition==0)
            // Toast.makeText(getActivity(),"SD",Toast.LENGTH_SHORT).show();
            {
                slideOut.close();
                Intent intent = new Intent(mContext,LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.OPEN_IS_SD,true);
                startActivity(intent);
            }
            else{
                slideOut.close();
                Intent intent = new Intent(mContext,LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.OPEN_IS_HD,true);
                startActivity(intent);
            }
              //  Toast.makeText(getActivity(),"HD",Toast.LENGTH_SHORT).show();
        }

        if(groupPosition==3){
            if(childPosition==0){
                slideOut.close();
                Intent intent = new Intent(mContext,LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.OPEN_PROMOS,true);
                startActivity(intent);
            }else{
                slideOut.close();
                Intent intent = new Intent(mContext,LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.OPEN_PRECAPS,true);
                startActivity(intent);
            }
        }

        if (groupPosition == 4){
            if (childPosition == 0){
                slideOut.close();
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_URL,SonyDataManager.init(mContext).getMenuItemUrl("About SET"));
                intent.putExtra(WebViewActivity.WEB_TEXT_HEADER,"About SET");
                mContext.startActivity(intent);
            }

            if (childPosition == 1){
                slideOut.close();
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_URL,SonyDataManager.init(mContext).getMenuItemUrl("Contact Us "));
                intent.putExtra(WebViewActivity.WEB_TEXT_HEADER,"Contact Us ");
                mContext.startActivity(intent);
            }

            if (childPosition == 2){
                slideOut.close();
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_URL,SonyDataManager.init(mContext).getMenuItemUrl("Terms of Use "));
                intent.putExtra(WebViewActivity.WEB_TEXT_HEADER,"Terms of Use ");
                mContext.startActivity(intent);
            }

            if (childPosition == 3){
                slideOut.close();
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_URL,SonyDataManager.init(mContext).getMenuItemUrl("Privacy Policy "));
                intent.putExtra(WebViewActivity.WEB_TEXT_HEADER,"Privacy Policy ");
                mContext.startActivity(intent);
            }


            if (childPosition == 4){
                slideOut.close();
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_URL,SonyDataManager.init(mContext).getMenuItemUrl("Disclaimer"));
                intent.putExtra(WebViewActivity.WEB_TEXT_HEADER,"Disclaimer");
                mContext.startActivity(intent);
            }
        }
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

       if(groupPosition==2){
           slideOut.close();
           Intent intent = new Intent(mContext,LandingActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
           intent.putExtra(Constants.OPEN_EPISODES,true);
           startActivity(intent);
       }

        return false;
    }


    @OnClick({R.id.btnFacebook,R.id.btnTwitter,R.id.btnYoutube})
    public void onSocialMedia(View view){
        switch (view.getId()){
            case R.id.btnFacebook:
                Uri uri=Uri.parse("https://www.facebook.com/sonytelevision");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                break;
            case R.id.btnTwitter:
                Uri uri2=Uri.parse("https://twitter.com/SonyTV");
                Intent intent2 = new Intent(Intent.ACTION_VIEW,uri2);
                startActivity(intent2);
                break;
            case R.id.btnYoutube:
                Uri uri3=Uri.parse("http://www.youtube.com/setindia");
                Intent intent3 = new Intent(Intent.ACTION_VIEW,uri3);
                startActivity(intent3);
                break;


        }
    }


    @Override
    public void onGroupExpand(int groupPosition) {

        if(lastExpandedPosition != -1 && groupPosition != lastExpandedPosition){
            expListViev.collapseGroup(lastExpandedPosition);
        }
        lastExpandedPosition=groupPosition;

    }
}
