package com.teli.sonyset.adapters;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.activities.VideoDetailsActivity;
import com.teli.sonyset.models.RelatedVideo;
import com.teli.sonyset.models.Video;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;

/**
 * Created by Nitish Kulkarni on 2/8/15.
 */
public class RelatedVideoAdapter extends BaseAdapter {

    Activity mContext;
    ArrayList<RelatedVideo> mEpisodes;

    public RelatedVideoAdapter(Activity context, ArrayList<RelatedVideo> episodes) {
        mContext = context;
        mEpisodes = episodes;
    }

    @Override
    public int getCount() {
        return mEpisodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mEpisodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.related_video_list_item, null, true);
            viewHolder = new ViewHolder();
//            viewHolder.relatedVideoItemLayout = (RelativeLayout) view.findViewById(R.id.related_video_item_layout);
            viewHolder.relatedVideoItemColor = (View) view.findViewById(R.id.itemColor);
            viewHolder.thumbnail = (ImageView) view.findViewById(R.id.related_thumbnail);
            viewHolder.thumbnailTitle = (SonyTextView) view.findViewById(R.id.video_title);
            viewHolder.thumbnailShowName = (SonyTextView) view.findViewById(R.id.show_name);
            viewHolder.thumbnailCategory = (SonyTextView) view.findViewById(R.id.category);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.with(mContext).load(Uri.parse(mEpisodes.get(position).getThumbnailUrl())).placeholder(R.drawable.sony_logo).into(viewHolder.thumbnail);
        viewHolder.thumbnailTitle.setText(mEpisodes.get(position).getVideoTitle());
        viewHolder.thumbnailShowName.setText(mEpisodes.get(position).getShowName());
        viewHolder.thumbnailCategory.setText(mEpisodes.get(position).getCategory());
        String color = mEpisodes.get(position).getColorCode();
        Log.d("RelatedVideoAdapter",position + "Color::" + color);
        if (color.toLowerCase().equals("r")) {
            viewHolder.relatedVideoItemColor.setBackgroundColor(mContext.getResources().getColor(R.color.sony_red));
        } else if (color.toLowerCase().equals("g")) {
            viewHolder.relatedVideoItemColor.setBackgroundColor(mContext.getResources().getColor(R.color.sony_green));
        }if (color.toLowerCase().equals("b")) {
            viewHolder.relatedVideoItemColor.setBackgroundColor(mContext.getResources().getColor(R.color.sony_blue));
        }

        return view;
    }

    class ViewHolder {
        private ImageView thumbnail;
        private SonyTextView thumbnailTitle;
        private SonyTextView thumbnailShowName;
        private SonyTextView thumbnailCategory;
        private RelativeLayout relatedVideoItemLayout;
        private View relatedVideoItemColor;
    }
}
