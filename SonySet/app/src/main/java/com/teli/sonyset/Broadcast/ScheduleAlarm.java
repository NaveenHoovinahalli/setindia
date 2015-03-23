package com.teli.sonyset.Broadcast;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.teli.sonyset.R;
import com.teli.sonyset.Utils.SonyDataManager;
import com.teli.sonyset.activities.LandingActivity;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by naveen on 12/3/15.
 */
public class ScheduleAlarm extends BroadcastReceiver {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager powerManager=(PowerManager) context.getSystemService(context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock=powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"SONY");

        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Toast.makeText(context, "AlarmStarted", Toast.LENGTH_SHORT).show();
        Log.d("Alarm","AlarmStarted");
        String showName=intent.getStringExtra("KEY");


        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(showName);
        builder.setContentText("will start in 5 min");
        builder.setTicker("SONY");
        builder.setNumber(1);

        Log.d("Alarm","intent extra"+intent.getStringExtra("KEY"));
        Log.d("Alarm","Intent extra"+intent.getIntExtra("ID",0));

        int id=intent.getIntExtra("ID",0);

        SonyDataManager.init(context).removeSharedPrefrence(String.valueOf(id));

//        Intent notification=new Intent(context, LandingActivity.class);
//
//        TaskStackBuilder stackBuilder= TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(LandingActivity.class);
//        stackBuilder.addNextIntent(notification);
//
//        notificationManager.notify(1,builder.build());

        Intent childIntent=new Intent(context, LandingActivity.class);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,id,childIntent,0);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        Notification notification=builder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager.notify(1,notification);


    }




    public void setAlarm(Context context, int id, String showName, String scheduleItemDateTime){


        long showtime= Long.parseLong(scheduleItemDateTime)*1000;
        long schedule=showtime-30000;

        Calendar cal2=Calendar.getInstance();
        cal2.setTimeInMillis(schedule);

        Log.d("Year","Year"+cal2.get(Calendar.YEAR));
        Log.d("Year","Month"+cal2.get(Calendar.MONTH));
        Log.d("Year","Date"+cal2.get(Calendar.DATE));

        Log.d("Year","Hour"+cal2.get(Calendar.HOUR_OF_DAY));
        Log.d("Year","Minute"+cal2.get(Calendar.MINUTE));
        Log.d("Year","Second"+cal2.get(Calendar.SECOND));


        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(schedule);
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context,ScheduleAlarm.class);
        intent.putExtra("KEY",showName);
        intent.putExtra("ID",id);

        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);


    }

    public void cancelAlarm(Context context,int id){

        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context,ScheduleAlarm.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);


    }

    private void setNewAlarm(Context context) {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

        Calendar currentCalender=Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

        cal.set(Calendar.YEAR,currentCalender.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, currentCalender.get(Calendar.MONTH));
        cal.set(Calendar.DATE,currentCalender.get(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY,8);
        cal.set(Calendar.MINUTE,1);
        cal.set(Calendar.SECOND,1);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context,ScheduleAlarm.class);
//        intent.putExtra("NAME",myname);
//        intent.putExtra("MESSAGE",mymessage);
//        intent.setAction("BIRTHDAY");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
    }



}
