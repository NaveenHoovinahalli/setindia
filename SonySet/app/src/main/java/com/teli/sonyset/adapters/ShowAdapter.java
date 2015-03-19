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
import com.teli.sonyset.models.ShowDetail;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;

/**
 * Created by madhuri on 5/3/15.
 */
public class ShowAdapter extends BaseAdapter {

    private ArrayList<ShowDetail> shows =new ArrayList<ShowDetail>();
    Context mContext;

    public ShowAdapter(Context context, ArrayList<ShowDetail> shows) {
        this.shows = shows;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return shows.size();
    }

    @Override
    public Object getItem(int i) {
        return shows.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_show_item,null);
            viewHolder = new ViewHolder();
            viewHolder.showImage = (ImageView) view.findViewById(R.id.show_iv);
            viewHolder.mColorCode = (TextView) view.findViewById(R.id.color_code_view);
            viewHolder.showLogo = (ImageView) view.findViewById(R.id.show_logo_iv);
            viewHolder.showTitle = (SonyTextView) view.findViewById(R.id.show_name);
            viewHolder.showDate = (TextView) view.findViewById(R.id.show_time);
            viewHolder.upcomingNew = (ImageView) view.findViewById(R.id.upcoming_new_tv);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Picasso.with(mContext).load(Uri.parse(shows.get(i).getBanner())).into(viewHolder.showImage);
        Picasso.with(mContext).load(Uri.parse(shows.get(i).getShowLogo())).into(viewHolder.showLogo);

        viewHolder.showTitle.setText(shows.get(i).getShowTitle());

        Log.d("ShowAdapter", "show name" + shows.get(i).getShowTitle());

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "sonyregularplain.ttf");
        viewHolder.showDate.setTypeface(tf);

        viewHolder.showDate.setText(shows.get(i).getShowDate());
       // viewHolder.upcomingNew.setText(shows.get(i).getShowUpcomingNew());

        if (shows.get(i).getShowUpcomingNew().equalsIgnoreCase("UPCOMING")){
            viewHolder.upcomingNew.setImageResource(R.drawable.upcoming);
        }else if (shows.get(i).getShowUpcomingNew().equalsIgnoreCase("New")){
            viewHolder.upcomingNew.setImageResource(R.drawable.newnew);
        }

        String color = shows.get(i).getColorCode();
        if (color.equals("R")){
            viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#CD2E2E"));
        }else if (color.equals("G")){
            viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#38A92C"));
        }else if (color.equals("B")){
            viewHolder.mColorCode.setBackgroundColor(Color.parseColor("#4A67D6"));
        }

        return view;
    }

    private class ViewHolder{
        ImageView showImage;
        TextView mColorCode;
        ImageView showLogo;
        SonyTextView showTitle;
        TextView showDate;
        ImageView upcomingNew;
    }
}
