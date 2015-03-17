package com.teli.sonyset.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import com.teli.sonyset.R;
import com.teli.sonyset.models.ShowDetail;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by naveen on 13/3/15.
 */
public class ExpandableListAdapterMenu extends BaseExpandableListAdapter {

    Context context;
    List<String> listDataHeader;
    HashMap<String,List<String>> listChildData;
    ArrayList<ShowDetail> showDetails;

    public ExpandableListAdapterMenu(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData, ArrayList<ShowDetail> showsDetails){
        this.context=context;
        this.listDataHeader=listDataHeader;
        this.listChildData=listChildData;
        this.showDetails=showsDetails;

    }


    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listChildData.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listChildData.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ImageView imageView;
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_groups, null);
        }
        View colorcode=(View) convertView.findViewById(R.id.colorcodemenu);
        imageView= (ImageView) convertView.findViewById(R.id.menuImage);
        ImageView expandableplusminus= (ImageView) convertView.findViewById(R.id.expandableplusminus);

        if(isExpanded) {
            colorcode.setVisibility(View.VISIBLE);
            convertView.setBackgroundColor(Color.parseColor("#000000"));
            expandableplusminus.setImageResource(R.drawable.btn_square_minus);
            if(groupPosition==0) {
                colorcode.setBackgroundResource(R.color.sony_red);
                imageView.setImageResource(R.drawable.shows_sel_b);

            }
            if(groupPosition==1) {
                colorcode.setBackgroundResource(R.color.sony_blue);
                imageView.setImageResource(R.drawable.schedule_sel_b);

            }
            if(groupPosition==2) {
                colorcode.setBackgroundResource(R.color.sony_green);
                imageView.setImageResource(R.drawable.episodes_sel_b);

            }
            if(groupPosition==3) {
                colorcode.setBackgroundResource(R.color.sony_red);
                imageView.setImageResource(R.drawable.video_sel_b);

            }if(groupPosition==4) {
                colorcode.setBackgroundResource(R.color.sony_green);
                imageView.setImageResource(R.drawable.misc_sel_b);

            }




        }else {
            convertView.setBackgroundColor(Color.parseColor("#191919"));
            colorcode.setVisibility(View.INVISIBLE);
            expandableplusminus.setImageResource(R.drawable.btn_square_plus);


            if (groupPosition == 2)
                expandableplusminus.setVisibility(View.INVISIBLE);
            else expandableplusminus.setVisibility(View.VISIBLE);

            if (groupPosition == 0) {
                imageView.setImageResource(R.drawable.shows_unsel_b);
            }

            if (groupPosition == 1) {
                imageView.setImageResource(R.drawable.schedule_unsel_b);

            }
            if (groupPosition == 2) {
                imageView.setImageResource(R.drawable.episodes_unsel_b);

            }
            if (groupPosition == 3) {
                imageView.setImageResource(R.drawable.video_unsel_b);
            }

            if (groupPosition == 4)
                imageView.setImageResource(R.drawable.misc_unsel_b);
        }

        SonyTextView lblListHeader = (SonyTextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_el, null);
        }

        SonyTextView tvNU= (SonyTextView) convertView.findViewById(R.id.tvUNdesc);
        if(groupPosition==0){

            tvNU.setText(showDetails.get(childPosition).getShowUpcomingNew());
            if(showDetails.get(childPosition).getShowUpcomingNew().equalsIgnoreCase("UPCOMING")) {
                tvNU.setBackgroundResource(R.drawable.rounded_text_box_red);
            }else if(showDetails.get(childPosition).getShowUpcomingNew().equalsIgnoreCase("NEW")) {
                tvNU.setBackgroundResource(R.drawable.rounded_text_box_green);
            }
        }else {
            tvNU.setVisibility(View.INVISIBLE);
        }

        SonyTextView txtListChild = (SonyTextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
