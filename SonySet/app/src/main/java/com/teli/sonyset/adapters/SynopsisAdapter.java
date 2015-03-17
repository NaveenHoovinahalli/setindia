package com.teli.sonyset.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.teli.sonyset.R;
import com.teli.sonyset.models.Synopsis;
import com.teli.sonyset.views.SonyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by madhuri on 12/3/15.
 */
public class SynopsisAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Synopsis> synopsises = new ArrayList<>();
    private SonyTextView episodeNumber;
    private SonyTextView date;
    private SonyTextView episodeDetail;
    private ImageView fullEpisode;
    private ImageView fullDetails;

    public SynopsisAdapter(Context mContext, ArrayList<Synopsis> synopsises) {
        this.mContext = mContext;
        this.synopsises = synopsises;
    }

    @Override
    public int getCount() {
        return synopsises.size();
    }

    @Override
    public Object getItem(int i) {
        return synopsises.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        //  if (view == null){

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_synopsis,null);

        episodeNumber = (SonyTextView) view.findViewById(R.id.episode_number);
        date = (SonyTextView) view.findViewById(R.id.date);
        episodeDetail = (SonyTextView) view.findViewById(R.id.episode_detail);
        fullEpisode = (ImageView) view.findViewById(R.id.full_episode);
        fullDetails = (ImageView) view.findViewById(R.id.full_details);
        //  }

        String myString = synopsises.get(i).getTitle();
        String upperString = myString.substring(0,1).toUpperCase() + myString.substring(1);
        episodeNumber.setText(upperString);

        if (synopsises.get(i).getDate() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(synopsises.get(i).getDate() * 1000);

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            String weekDay = dayFormat.format(calendar.getTime());

            Log.d("SynopsisAdapter", "Weekday" + weekDay);

            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            String monthName = month_date.format(calendar.getTime());

            Log.d("SynopsisAdapter", "Weekday" + calendar.get(Calendar.DATE));

            int dateNum = calendar.get(Calendar.DATE);
            Log.d("SynopsisAdapter", "month" + monthName);

            date.setText("(" + weekDay + "," + monthName + dateNum + ")");
        }
        episodeDetail.setText(synopsises.get(i).getText());

        fullEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //video detail
                Toast.makeText(mContext, "Data not available", Toast.LENGTH_SHORT).show();
                /*Intent openVideoActivity = new Intent(mContext, VideoDetailsActivity.class);
                openVideoActivity.putExtra(Constants.VIDEO_ID, synopsises.get(i).getBrightCoveId());
                openVideoActivity.putExtra(Constants.EPISODE_RESPONSE, episodesOld);
                openVideoActivity.putStringArrayListExtra(Constants.EPISODE_THUMBNAILS, brightCoveThumbnailsOld);
                openVideoActivity.putExtra(Constants.IS_EPISODE, true);

                mContext.startActivity(openVideoActivity);*/
            }
        });

        fullDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*fullDetails.setLayoutParams(
                        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));*/

                episodeDetail.setMaxLines(Integer.MAX_VALUE);
                fullDetails.setVisibility(View.GONE);
            }
        });

        return view;
    }
}
