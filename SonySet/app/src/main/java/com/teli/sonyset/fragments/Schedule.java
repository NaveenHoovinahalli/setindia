package com.teli.sonyset.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.Utils.Constants;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.Utils.SonyRequest;
import com.teli.sonyset.adapters.ScheduleAdapter;
import com.teli.sonyset.models.ScheduleDayDetail;
import com.teli.sonyset.views.SonyTextView;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by naveen on 11/3/15.
 */
public class Schedule extends Fragment {

    @InjectView(R.id.listSchedule)
    ListView listSchedule;

    @InjectView(R.id.tvhd)
    SonyTextView hdBtn;

    @InjectView(R.id.tvsd)
    SonyTextView sdBtn;

    @InjectView(R.id.lldate1)
    LinearLayout date1;

    @InjectView(R.id.lldate2)
    LinearLayout date2;

    @InjectView(R.id.lldate3)
    LinearLayout date3;

    @InjectView(R.id.lldate4)
    LinearLayout date4;

    @InjectView(R.id.lldate5)
    LinearLayout date5;

    @InjectView(R.id.lldate6)
    LinearLayout date6;

    @InjectView(R.id.lldate7)
    LinearLayout date7;

    @InjectView(R.id.day1)
    SonyTextView day11;

    @InjectView(R.id.dayN1)
    SonyTextView dayN11;

    @InjectView(R.id.day2)
    SonyTextView day2;
    @InjectView(R.id.dayN2)
    SonyTextView dayN2;

    @InjectView(R.id.day3)
    SonyTextView day3;
    @InjectView(R.id.dayN3)
    SonyTextView dayN3;

    @InjectView(R.id.day4)
    SonyTextView day4;
    @InjectView(R.id.dayN4)
    SonyTextView dayN4;

    @InjectView(R.id.day5)
    SonyTextView day5;
    @InjectView(R.id.dayN5)
    SonyTextView dayN5;

    @InjectView(R.id.day6)
    SonyTextView day6;
    @InjectView(R.id.dayN6)
    SonyTextView dayN6;

    @InjectView(R.id.day7)
    SonyTextView day7;
    @InjectView(R.id.dayN7)
    SonyTextView dayN7;


    ListAdapter adapter;

    @InjectView(R.id.scheduleProgress)
    ProgressBar mPbar;

    ArrayList<ScheduleDayDetail> days;

    View previousView;
    SonyTextView previousdateView;
    SonyTextView previousNameView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_schedule,null);
        ButterKnife.inject(this,view);

        long currentDateTime = System.currentTimeMillis();
        Log.d("currenttime",""+currentDateTime);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(AndroidUtils.isNetworkOnline(mContext))
            if (SonyDataManager.init(mContext).getSavedHdIsFromMenu()){
                setHD();
            }else {
                setSD();
            }
    }


    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    private void setSD() {
       String cId=SonyDataManager.init(getActivity()).getCountryId();



        String url = String.format(Constants.SCHEDULE_SD, cId);

        Log.d("URLSCHEDULE","url-"+url);

        sdBtn.setBackgroundColor(Color.parseColor("#4A67D6"));
        hdBtn.setBackgroundColor(Color.parseColor("#323232"));
        sdBtn.setTextColor(Color.parseColor("#ffffff"));
        hdBtn.setTextColor(Color.parseColor("#848484"));

        fetchValues(url);
    }

    private void setHD() {

        String cId= SonyDataManager.init(getActivity()).getCountryId();

        String url = String.format(Constants.SCHEDULE_HD,cId);
        Log.d("URLSCHEDULE","url-"+url);

        hdBtn.setBackgroundColor(Color.parseColor("#4A67D6"));
        sdBtn.setBackgroundColor(Color.parseColor("#323232"));
        hdBtn.setTextColor(Color.parseColor("#ffffff"));
        sdBtn.setTextColor(Color.parseColor("#848484"));

        fetchValues(url);
    }

    private void fetchValues(String url) {

        mPbar.setVisibility(View.VISIBLE);

        SonyRequest request = new SonyRequest(getActivity() , url) {
            @Override
            public void onResponse(JSONArray s) {
                Log.d("ShowFragment", "Fragment Response" + s);
                mPbar.setVisibility(View.GONE);
                if(s!=null && !s.toString().isEmpty())
                    initAdapter(s);
            }

            @Override
            public void onError(String e) {
                Log.d("ShowFragment", "Fragment Exception" + e);

                if (getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPbar.setVisibility(View.GONE);
                        }
                    });
            }
        };
        request.execute();
    }

    private void initAdapter(JSONArray json) {

        Gson gson=new Gson();

        days = gson.fromJson(json.toString(), new TypeToken<List<ScheduleDayDetail>>() {
        }.getType());

        if(days.size()>0){
            if(previousNameView!=null && previousdateView!=null) {
                previousView.setBackgroundColor(Color.parseColor("#323232"));
                previousdateView.setTextColor(Color.parseColor("#848484"));
                previousNameView.setTextColor(Color.parseColor("#848484"));
            }
            date1.setVisibility(View.VISIBLE);
            date1.setBackgroundColor(Color.parseColor("#000000"));
            day11.setTextColor(Color.parseColor("#496BB4"));
            dayN11.setTextColor(Color.parseColor("#ffffff"));
            previousView=date1;
            previousdateView=day11;
            previousNameView=dayN11;

            setlinearlayout1();
            setList(0);
        }else {
            Toast.makeText(getActivity(),"Sorry...Data not found",Toast.LENGTH_SHORT).show();
        }
    }

    private void setList(int i) {
        adapter=new ScheduleAdapter(getActivity(),days.get(i).getScheduleDetailsArrayList());
        listSchedule.setAdapter(adapter);


    }

    private void setlinearlayout1() {
        int noOfDays= days.size();
        if(noOfDays>6) {
            date7.setVisibility(View.VISIBLE);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(days.get(6).getDate())*1000);
            Log.d("Year","Month"+calendar.get(Calendar.MONTH));
            Log.d("Year","Date"+calendar.get(Calendar.DATE));

            day7.setText(calendar.get(Calendar.DATE)+"");
            dayN7.setText((calendar.get(Calendar.MONTH) + "").toUpperCase());


        }
        if(noOfDays>5) {

            date6.setVisibility(View.VISIBLE);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(days.get(5).getDate())*1000);
            day6.setText(calendar.get(Calendar.DATE)+"");
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(calendar.getTime());
            dayN6.setText((month_name+"").toUpperCase());
        }
        if(noOfDays>4) {
            date5.setVisibility(View.VISIBLE);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(days.get(4).getDate())*1000);
            day5.setText(calendar.get(Calendar.DATE)+"");
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(calendar.getTime());
            dayN5.setText((month_name+"").toUpperCase());
        }
        if(noOfDays>3) {
            date4.setVisibility(View.VISIBLE);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(days.get(3).getDate())*1000);
            day4.setText(calendar.get(Calendar.DATE)+"");
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(calendar.getTime());
            dayN4.setText((month_name+"").toUpperCase());
        }
        if(noOfDays>2) {
            date3.setVisibility(View.VISIBLE);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(days.get(2).getDate())*1000);
            day3.setText(calendar.get(Calendar.DATE)+"");
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(calendar.getTime());
            dayN3.setText((month_name+"").toUpperCase());
        }
        if(noOfDays>1) {
            date2.setVisibility(View.VISIBLE);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(days.get(1).getDate())*1000);
            day2.setText(calendar.get(Calendar.DATE)+"");
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(calendar.getTime());
            dayN2.setText((month_name+"").toUpperCase());
        }
        if(noOfDays>0) {
            date1.setVisibility(View.VISIBLE);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(days.get(0).getDate())*1000);
            day11.setText(calendar.get(Calendar.DATE)+"");

            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(calendar.getTime());
            dayN11.setText((month_name+"").toUpperCase());
        }

        for(int i=0;i<days.size();i++){
            Log.d("Schedule",""+ getDate(Long.parseLong(days.get(i).getDate())));
        }

    }

    @OnClick({R.id.tvsd,R.id.tvhd})
    public void setHdSD(View view){
        switch (view.getId()){
            case R.id.tvsd:
                setSD();
                break;
            case R.id.tvhd:
                setHD();
                break;
        }
    }

    @OnClick({R.id.lldate1,R.id.lldate2,R.id.lldate3,R.id.lldate4,R.id.lldate5,R.id.lldate6,R.id.lldate7})
    public  void onClickOfDates(View view){
        setbagroundColor(view);
        switch (view.getId()){
            case R.id.lldate1:
                setList(0);
                setPreviousView();
                date1.setBackgroundColor(Color.parseColor("#000000"));
                day11.setTextColor(getResources().getColor(R.color.sony_blue));
                dayN11.setTextColor(Color.parseColor("#ffffff"));
                previousView=view;
                previousdateView=day11;
                previousNameView=dayN11;
                break;
            case R.id.lldate2:
                setList(1);
                setPreviousView();
                date2.setBackgroundColor(Color.parseColor("#000000"));
                day2.setTextColor(getResources().getColor(R.color.sony_blue));
                dayN2.setTextColor(Color.parseColor("#ffffff"));
                previousView=view;
                previousdateView=day2;
                previousNameView=dayN2;
                break;
            case R.id.lldate3:
                setList(2);
                setPreviousView();
                date3.setBackgroundColor(Color.parseColor("#000000"));
                day3.setTextColor(getResources().getColor(R.color.sony_blue));
                dayN3.setTextColor(Color.parseColor("#ffffff"));
                previousView=view;
                previousdateView=day3;
                previousNameView=dayN3;
                break;
            case R.id.lldate4:
                setList(3);
                setPreviousView();
                date4.setBackgroundColor(Color.parseColor("#000000"));
                day4.setTextColor(getResources().getColor(R.color.sony_blue));
                dayN4.setTextColor(Color.parseColor("#ffffff"));
                previousView=view;
                previousdateView=day4;
                previousNameView=dayN4;
                break;
            case R.id.lldate5:
                setList(4);
                setPreviousView();
                date5.setBackgroundColor(Color.parseColor("#000000"));
                day5.setTextColor(getResources().getColor(R.color.sony_blue));
                dayN5.setTextColor(Color.parseColor("#ffffff"));
                previousView=view;
                previousdateView=day5;
                previousNameView=dayN5;
                break;
            case R.id.lldate6:
                setList(5);
                setPreviousView();
                date6.setBackgroundColor(Color.parseColor("#000000"));
                day6.setTextColor(getResources().getColor(R.color.sony_blue));
                dayN6.setTextColor(Color.parseColor("#ffffff"));
                previousView=view;
                previousdateView=day6;
                previousNameView=dayN6;
                break;case R.id.lldate7:
                setList(6);
                setPreviousView();
                date7.setBackgroundColor(Color.parseColor("#000000"));
                day7.setTextColor(getResources().getColor(R.color.sony_blue));
                dayN7.setTextColor(Color.parseColor("#ffffff"));
                previousView=view;
                previousdateView=day7;
                previousNameView=dayN7;
                break;

        }

    }

    private void setbagroundColor(View view) {

        view.setBackgroundColor(Color.parseColor("#4A67D6"));
        if(previousView!=null)
            previousView.setBackgroundColor(Color.parseColor("#191919"));
        previousView=view;

    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);

//        java.util.Date time123=new java.util.Date((long)time*1000);

//        Fri Mar 13 00:00:00 GMT+05:30 2015
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time*1000);

        Log.d("Year","Year"+calendar.get(Calendar.YEAR));
        Log.d("Year","Month"+calendar.get(Calendar.MONTH));
        Log.d("Year","Date"+calendar.get(Calendar.DATE));

        Log.d("Year","Hour"+calendar.get(Calendar.HOUR_OF_DAY));
        Log.d("Year","Minute"+calendar.get(Calendar.MINUTE));
        Log.d("Year","Second"+calendar.get(Calendar.SECOND));

        return "date";
    }

    public void setPreviousView(){
        if(previousNameView!=null && previousdateView!=null) {
            previousView.setBackgroundColor(Color.parseColor("#191919"));
            previousdateView.setTextColor(Color.parseColor("#848484"));
            previousNameView.setTextColor(Color.parseColor("#848484"));
        }
    }
}
