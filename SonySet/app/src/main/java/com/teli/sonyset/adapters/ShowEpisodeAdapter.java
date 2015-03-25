package com.teli.sonyset.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
 * Created by madhuri on 12/3/15.
 */
public class ShowEpisodeAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Video> episodes = new ArrayList<>();
    ArrayList<Video> episodesOld = new ArrayList<>();
    ArrayList<String> brightCoveThumbnails = new ArrayList<>();
    ArrayList<String> brightCoveThumbnailsOld = new ArrayList<>();
    private ViewHolder viewHolder;

    public ShowEpisodeAdapter(Context mContext, ArrayList<Video> episodes, ArrayList<String> brightCoveThumbnails) {
        this.mContext = mContext;
        this.episodes = episodes;
        this.brightCoveThumbnails = brightCoveThumbnails;

        /*for (int i = 1 ; i<episodesOld.size();i++){
           this.episodes.add(episodesOld.get(i));
        }

        for (int i = 1 ; i<brightCoveThumbnailsOld.size();i++){
           this.brightCoveThumbnails.add(brightCoveThumbnailsOld.get(i));
        }*/
    }

    @Override
    public int getCount() {
        Log.d("ShowEpisodeAdapter","Get count::" +episodes.size());
        return episodes.size();
    }

    @Override
    public Object getItem(int i) {
        return episodes.get(i );
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_episode_item,null);

            viewHolder = new ViewHolder();
            viewHolder.mColorCode = (TextView) view.findViewById(R.id.color_code_view);
            viewHolder.episodeImage = (ImageView) view.findViewById(R.id.episode_iv);
            viewHolder.episodeTime = (SonyTextView) view.findViewById(R.id.episode_time);
            viewHolder.episodeNum = (TextView) view.findViewById(R.id.episode_num);
            viewHolder.episodeTitle = (TextView) view.findViewById(R.id.episode_title);
            viewHolder.duration = (SonyTextView) view.findViewById(R.id.duration);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
            view.forceLayout();
        }

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "klavikamedium_plain_webfont.ttf");
        viewHolder.episodeTitle.setTypeface(tf);
        viewHolder.episodeTitle.setText(episodes.get(i).getShowName());

        if (!episodes.get(i).getOnAirDate().isEmpty()) {
            viewHolder.episodeTime.setText(episodes.get(i).getOnAirDate());
            viewHolder.episodeTime.setBackgroundResource(R.drawable.rounded_text_box);
        }

        viewHolder.duration.setText(episodes.get(i).getDuration());
        viewHolder.duration.setBackgroundColor(Color.parseColor("#000000"));

        if (!brightCoveThumbnails.get(i).equals("null")){
            Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(i))).into(viewHolder.episodeImage);
        }else {
            viewHolder.episodeImage.setImageResource(R.drawable.place_holder);
        }

        String color = episodes.get(i).getColorCode();
        //  Log.d("EpisodeAdapter", "color code" + color);

        if (color!=null && !color.isEmpty()) {
            if (color.equalsIgnoreCase("null"))
                if (color.equalsIgnoreCase("r")) {
                    viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
                } else if (color.equalsIgnoreCase("g")) {
                    viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#38A92C"));
                } else if (color.equalsIgnoreCase("b")) {
                    viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
                }
        }

        viewHolder.episodeNum.setTypeface(tf);
        viewHolder.episodeNum.setText(episodes.get(i).getEpisodeNumber());
        return view;
    }

    private class ViewHolder{
        TextView mColorCode;
        ImageView episodeImage;
        SonyTextView episodeTime;
        TextView episodeNum;
        TextView episodeTitle;
        SonyTextView duration;
    }
}
