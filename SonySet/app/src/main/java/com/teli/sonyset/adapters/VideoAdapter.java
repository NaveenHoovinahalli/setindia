package com.teli.sonyset.adapters;

/**
 * Created by madhuri on 11/3/15.
 */

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
import java.util.List;

/**
 * Created by madhuri on 6/3/15.
 */
public class VideoAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Video> episodes = new ArrayList<Video>();
    List<String> brightCoveThumbnails = new ArrayList<>();

    public VideoAdapter(Context mContext, ArrayList<Video> episodes, List<String> brightCoveThumbnails) {
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
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_video_item,null);

            viewHolder = new ViewHolder();
            viewHolder.mColorCode = (TextView) view.findViewById(R.id.color_code_view);
            viewHolder.episodeImage = (ImageView) view.findViewById(R.id.episode_iv);
            viewHolder.episodeTitle = (SonyTextView) view.findViewById(R.id.episode_title);
            viewHolder.episodeShowName = (TextView) view.findViewById(R.id.episode_show_name);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
            view.forceLayout();
        }

        viewHolder.episodeTitle.setText(episodes.get(i).getShowTitle());
        viewHolder.episodeShowName.setText(episodes.get(i).getShowName());

        if (!brightCoveThumbnails.get(i).equals("null")){
            Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(i))).placeholder(R.drawable.place_holder).into(viewHolder.episodeImage);
        }else {
            viewHolder.episodeImage.setImageResource(R.drawable.ic_launcher);
        }

        String color = episodes.get(i).getColorCode();
        Log.d("EpisodeAdapter", "color code" + color);

        if (color!=null && !color.isEmpty()) {
            if (color.toLowerCase().equals("r")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
            } else if (color.toLowerCase().equals("g")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#38A92C"));
            } else if (color.toLowerCase().equals("b")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
            }
        }

        return view;
    }

    private class ViewHolder{
        TextView mColorCode;
        ImageView episodeImage;
        SonyTextView episodeTitle;
        TextView episodeShowName;
    }
}

