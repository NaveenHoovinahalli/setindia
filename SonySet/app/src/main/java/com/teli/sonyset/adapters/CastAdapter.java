package com.teli.sonyset.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.teli.sonyset.R;
import com.teli.sonyset.models.Cast;
import com.teli.sonyset.views.ExpandableTextView;
import com.teli.sonyset.views.SonyTextView;

import java.util.ArrayList;

/**
 * Created by madhuri on 12/3/15.
 */
public class CastAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Cast> casts = new ArrayList<>();
    private ExpandableTextView mDetail;
    private SonyTextView mTitle;
    private ImageView mImageView;
    private ImageView mDownImage;
    //   private ViewHolder viewHolder;

    public CastAdapter(Context mContext, ArrayList<Cast> casts) {
        this.mContext = mContext;
        this.casts = casts;
    }

    @Override
    public int getCount() {
        return casts.size();
    }

    @Override
    public Object getItem(int i) {
        return casts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       // if (view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_cast_detail,null);
            //  viewHolder = new ViewHolder();
            mDetail = (ExpandableTextView) view.findViewById(R.id.cast_detail);
            mTitle = (SonyTextView) view.findViewById(R.id.cast_title);
            mImageView = (ImageView) view.findViewById(R.id.cast_iv);
            mDownImage = (ImageView) view.findViewById(R.id.down_image);
            //view.setTag(viewHolder);
      //  }/*else {
        //   viewHolder = (ViewHolder) view.getTag();
      //  }*/

        mDetail.setText(casts.get(i).getDescription());
        mTitle.setText(casts.get(i).getName());

        Picasso.with(mContext).load(Uri.parse(casts.get(i).getThumbnail())).placeholder(R.drawable.place_holder_circle).into(mImageView);
        mDownImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* mDetail.setMaxLines(Integer.MAX_VALUE);
                mDownImage.setVisibility(View.GONE);*/
               // mDetail.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });

        return view;
    }

    /*public class ViewHolder{
        ImageView mImageView;
        SonyTextView mTitle;
        SonyTextView mDetail;
        ImageView mDownImage;
    }*/
}
