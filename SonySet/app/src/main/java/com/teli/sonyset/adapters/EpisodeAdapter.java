package com.teli.sonyset.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;

/**
 * Created by madhuri on 6/3/15.
 */
public class EpisodeAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Video> episodes = new ArrayList<Video>();
    ArrayList<String> brightCoveThumbnails = new ArrayList<String>();

    public EpisodeAdapter(Context mContext, ArrayList<Video> episodes, ArrayList<String> brightCoveThumbnails) {
        this.mContext = mContext;
        this.episodes = episodes;
        this.brightCoveThumbnails = brightCoveThumbnails;
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

    @Override
    public Object getItem(int i) {
        return episodes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      //  i = i + 1;

        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_episode_item,null);

            viewHolder = new ViewHolder();
            viewHolder.mColorCode = (TextView) view.findViewById(R.id.color_code_view);
            viewHolder.episodeImage = (ImageView) view.findViewById(R.id.episode_iv);
            viewHolder.episodeTime = (SonyTextView) view.findViewById(R.id.episode_time);
            viewHolder.episodeNum = (SonyTextView) view.findViewById(R.id.episode_num);
            viewHolder.episodeTitle = (SonyTextView) view.findViewById(R.id.episode_title);
            viewHolder.mDuration = (SonyTextView) view.findViewById(R.id.duration);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
            view.forceLayout();
        }

        viewHolder.episodeTitle.setText(episodes.get(i).getShowName());
        viewHolder.episodeTime.setText(episodes.get(i).getOnAirDate());
        viewHolder.mDuration.setText(episodes.get(i).getDuration());

        if (!brightCoveThumbnails.get(i).equals("null")){
            Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(i))).placeholder(R.drawable.place_holder).into(viewHolder.episodeImage);
        }else {
            viewHolder.episodeImage.setImageResource(R.drawable.place_holder);
        }


        if (!episodes.get(0).getDuration().isEmpty()){
            viewHolder.mDuration.setText(episodes.get(0).getDuration());
        }else {
            viewHolder.mDuration.setVisibility(View.GONE);
        }

        if (!episodes.get(0).getEpisodeNumber().isEmpty()){
            viewHolder.episodeTime.setText(episodes.get(0).getEpisodeNumber());
        }else {
            viewHolder.episodeTime.setVisibility(View.GONE);
        }

        String color = episodes.get(i).getColorCode();
        Log.d("EpisodeAdapter", "color code" + color);

        if (color!=null && !color.isEmpty()) {
            if (color.equals("R")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
            } else if (color.equals("G")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#38A92C"));
            } else if (color.equals("B")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
            }
        }

        viewHolder.episodeNum.setText(episodes.get(i).getEpisodeNumber());
        return view;
    }

    private class ViewHolder{
        TextView mColorCode;
        ImageView episodeImage;
        SonyTextView episodeTime;
        SonyTextView episodeNum;
        SonyTextView episodeTitle;
        SonyTextView mDuration;
    }
}
