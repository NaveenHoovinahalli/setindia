package com.teli.sonyset.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.teli.sonyset.Broadcast.ScheduleAlarm;
import com.teli.sonyset.R;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.models.ScheduleDetails;
import com.teli.sonyset.views.SonyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by naveen on 12/3/15.
 */
public class ScheduleAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<ScheduleDetails> scheduleDetails;
    Typeface tf;
    public   boolean isReminderSet=false;


    public ScheduleAdapter(Context context, ArrayList<ScheduleDetails> scheduleDetailsArrayList) {
        this.context = context;
        this.scheduleDetails=scheduleDetailsArrayList;

    }


    @Override
    public int getCount() {
        return scheduleDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        isReminderSet=false;



        tf = Typeface.createFromAsset(context.getAssets(), "klavikamedium_plain_webfont.ttf");


        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_schedule_item, null);
        final ImageView reminder = (ImageView) view.findViewById(R.id.setReminder);
        SonyTextView showTiming= (SonyTextView) view.findViewById(R.id.showTiming);
        ImageView showThumbnail= (ImageView) view.findViewById(R.id.imageThumbnail);
        SonyTextView title= (SonyTextView) view.findViewById(R.id.showTitle);
        View colorLine=view.findViewById(R.id.colorCode);

        showTiming.setTypeface(tf);

        if(scheduleDetails.get(position).getColourcode()!=null && !scheduleDetails.get(position).getColourcode().equals("null") && !scheduleDetails.get(position).getColourcode().isEmpty()){
            Log.d("Color",""+scheduleDetails.get(position).getColourcode());
            String color=scheduleDetails.get(position).getColourcode();
            if(color.equals("R")){
                colorLine.setBackgroundResource(R.color.sony_red);

            }else if(color.equals("G")){
                colorLine.setBackgroundResource(R.color.sony_green);

            }else if(color.equals("B")){
                colorLine.setBackgroundResource(R.color.sony_blue);

            }
        }

        setShowTimingView(showTiming,position);


        String showName;
        showName=scheduleDetails.get(position).getTitle();
        if(showName.isEmpty())
            showName=scheduleDetails.get(position).getProgramTitle();
        title.setText(showName);
        Log.d("Title",""+showName);


        if(!scheduleDetails.get(position).getNid().equals("0"))
            if(!scheduleDetails.get(position).getUri().equals("null") && !scheduleDetails.get(position).getUri().isEmpty())
                Picasso.with(context).load(Uri.parse(scheduleDetails.get(position).getUri())).placeholder(R.drawable.place_holder)
                        .into(showThumbnail);

//        /load(Uri.parse(scheduleDetails.get(position).getUri()))

        final int episodeId= Integer.parseInt(scheduleDetails.get(position).getEpisodeScheduleId());

        if(!scheduleDetails.get(position).getNid().equals("0") && !scheduleDetails.get(position).getTitle().equalsIgnoreCase("teleshopping")) {
            showThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callShowDetails(position);
                }
            });
            showTiming.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callShowDetails(position);
                }
            });
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callShowDetails(position);
                }
            });

        }else {
           // Toast.makeText(context,"No Show details found!",Toast.LENGTH_SHORT).show();
        }

        int savedValue=  SonyDataManager.init(context).getScheduledId(String.valueOf(episodeId));
        if(scheduleDetails.get(position).getNowShowing().equals("yes")){
            reminder.setImageResource(R.drawable.now_playing);
            showTiming.setTextColor(Color.parseColor("#CA2E2E"));
        }
        else if(savedValue!=0) {
            reminder.setImageResource(R.drawable.reminder_set);
            isReminderSet=true;
            Log.d("Adapter", "saved position" + position + "---" + savedValue);
            reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if(isReminderSet){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setCancelable(true);
                    dialog.setTitle("Do you want to cancel the reminder?");
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SonyDataManager.init(context).removeSharedPrefrence(String.valueOf(episodeId));
                            reminder.setImageResource(R.drawable.set_reminder);
                            ScheduleAlarm alarm = new ScheduleAlarm();
                            alarm.cancelAlarm(context, episodeId);
                        }
                    });
                    dialog.show();
//                }
                }
            });

        }else {
            if(System.currentTimeMillis()>(Long.parseLong(scheduleDetails.get(position).getScheduleItemDateTime())*1000)){
                reminder.setVisibility(View.INVISIBLE);

            }else {
                reminder.setImageResource(R.drawable.set_reminder);
                final String finalShowName = showName;
                reminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(!isReminderSet){
                            isReminderSet=true;
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setCancelable(true);
                        dialog.setTitle("Do you want to set the reminder for this show?");
                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SonyDataManager.init(context).saveScheduledId(String.valueOf(episodeId), episodeId);
                                reminder.setImageResource(R.drawable.reminder_set);
                                ScheduleAlarm alarm = new ScheduleAlarm();
                                alarm.setAlarm(context, episodeId, finalShowName, scheduleDetails.get(position).getScheduleItemDateTime());
                                Log.d("Adapter", "Position" + position);
                            }
                        });
                        dialog.show();
//                    }


                    }
                });
            }
        }
        return view;
    }


    private void setShowTimingView(SonyTextView showTiming,int i) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((Long.parseLong(scheduleDetails.get(i).getScheduleItemDateTime()))*1000);
        Log.d("Year", "Hour" + cal.get(Calendar.HOUR_OF_DAY));
        Log.d("Year", "Minute" + cal.get(Calendar.MINUTE));
        Log.d("Year","Day"+cal.get(Calendar.DATE));
        int hour=cal.get(Calendar.HOUR_OF_DAY);

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String dateforrow = dateFormat.format(cal.getTime());
        Log.d("Date","format"+dateforrow);
        showTiming.setText(dateforrow);

        java.util.Date time123=new java.util.Date((Long.parseLong(scheduleDetails.get(i).getScheduleItemDateTime())*1000));
        Log.d("Year",""+time123);

    }

    private void callShowDetails(int position) {

        Intent intent = new Intent(context , ShowDetailsActivity.class);
        intent.putExtra(ShowDetailsActivity.SHOW_ID,scheduleDetails.get(position).getNid());
        intent.putExtra(ShowDetailsActivity.SHOW_COLOR_CODE,scheduleDetails.get(position).getColourcode());
        context.startActivity(intent);
    }
}
