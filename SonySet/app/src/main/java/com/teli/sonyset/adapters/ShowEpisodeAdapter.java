package com.teli.sonyset.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.models.ShowVideo;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;

/**
 * Created by madhuri on 12/3/15.
 */
public class ShowEpisodeAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<ShowVideo> episodes = new ArrayList<>();
    ArrayList<String> brightCoveThumbnails = new ArrayList<>();
    private ViewHolder viewHolder;

    public ShowEpisodeAdapter(Context mContext, ArrayList<ShowVideo> episodes, ArrayList<String> brightCoveThumbnails) {
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

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_episode_item,null);

            viewHolder = new ViewHolder();
            viewHolder.mColorCode = (TextView) view.findViewById(R.id.color_code_view);
            viewHolder.episodeImage = (ImageView) view.findViewById(R.id.episode_iv);
            viewHolder.episodeTime = (SonyTextView) view.findViewById(R.id.episode_time);
            viewHolder.episodeNum = (SonyTextView) view.findViewById(R.id.episode_num);
            viewHolder.episodeTitle = (SonyTextView) view.findViewById(R.id.episode_title);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
            view.forceLayout();
        }
       // viewHolder.episodeTitle.setText(episodes.get(i).getShowName());
        viewHolder.episodeTime.setText(episodes.get(i).getOnAirDate());

        if (!brightCoveThumbnails.get(i).equals("null")){
            Picasso.with(mContext).load(Uri.parse(brightCoveThumbnails.get(i))).into(viewHolder.episodeImage);
        }else {
            viewHolder.episodeImage.setImageResource(R.drawable.ic_launcher);
        }

      //  String color = episodes.get(i).getColorCode();
      //  Log.d("EpisodeAdapter", "color code" + color);

       /* if (color!=null && !color.isEmpty()) {
            if (color.equals("R")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
            } else if (color.equals("G")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#38A92C"));
            } else if (color.equals("B")) {
                viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
            }
        }*/

        viewHolder.episodeNum.setText(episodes.get(i).getEpisodeNumber());
        return view;
    }

    private class ViewHolder{
        TextView mColorCode;
        ImageView episodeImage;
        SonyTextView episodeTime;
        SonyTextView episodeNum;
        SonyTextView episodeTitle;
    }
}
