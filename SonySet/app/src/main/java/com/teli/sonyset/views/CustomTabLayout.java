package com.teli.sonyset.views;

/**
 * Created by madhuri on 16/3/15.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teli.sonyset.R;

/**
 * Created by rajesh on 27/5/14.
 */
public class CustomTabLayout extends HorizontalScrollView {

    private static final float TAB_VIEW_TEXT_SIZE_SP = 14;
    private static final float TAB_VIEW_PADDING_DIPS = 8;
    private static final int TITLE_OFFSET = 100;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private ViewPager mViewPager;
    private LinearLayout mTabStrip;
    private Resources mResources;

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);

        mResources = context.getResources();

        mTabStrip = new LinearLayout(context);
        mTabStrip.setBackgroundColor(Color.parseColor("#323232"));
        mTabStrip.setOrientation(LinearLayout.HORIZONTAL);
        mTabStrip.setBackgroundColor(Color.GRAY);
        mTabStrip.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        mTabStrip.setWeightSum(5f);
        mTabStrip.setDividerDrawable(getResources().getDrawable(R.drawable.divider));
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(internalPageChangeListener);
            populateTabs();
            scrollToTab(mViewPager.getCurrentItem());
        }
    }

    private void populateTabs() {
        PagerAdapter adapter = mViewPager.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {

            if (i == 2) {
                LinearLayout linearLayout = getMiddleImageView(adapter);
                linearLayout.setOnClickListener(mOnTabClickListener);
                mTabStrip.addView(linearLayout);
            } else {
                LinearLayout linearLayout = getView(adapter , i);
                linearLayout.setOnClickListener(mOnTabClickListener);
                mTabStrip.addView(linearLayout);
            }
        }
    }

    private LinearLayout getView(PagerAdapter adapter, int i){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.parseColor("#303030"));
        TextView colorTextView = new TextView(getContext());

        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.BOTTOM);
        textView.setTextColor(Color.WHITE);
        textView.setText(adapter.getPageTitle(i));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);

        if (i == 0){
            textView.setPadding(padding + 10, padding , padding + 10,  padding);
        }else if (i == 1){
            textView.setPadding(padding + 10, padding, padding + 10, padding);
        }else {
            textView.setPadding(padding, padding, padding, padding);
        }
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,1f));

        linearLayout.addView(textView);

        return linearLayout;
    }

    private LinearLayout getMiddleImageView(PagerAdapter adapter) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.home);
        int padding1 = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        imageView.setPadding(padding1, padding1, padding1, padding1);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        linearLayout.addView(imageView);
        return linearLayout;
    }

    private TextView createTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.BOTTOM);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,1f));
        return textView;
    }

    private ImageView createTabImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.home);
        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        return imageView;
    }

    private ViewPager.OnPageChangeListener internalPageChangeListener
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener
                        .onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
            scrollToTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    int prevTab = -1;

    public void scrollToTab(int tab) {
        if (prevTab > -1) {
            LinearLayout child = (LinearLayout) mTabStrip.getChildAt(prevTab);
            child.setBackgroundColor(Color.parseColor("#303030"));
        }

        LinearLayout child = (LinearLayout) mTabStrip.getChildAt(tab);
        child.setBackgroundColor(Color.parseColor("#323232"));
        int left = child.getLeft() - TITLE_OFFSET;
        smoothScrollTo(left, 0);
        prevTab = tab;
    }

    private OnClickListener mOnTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    };
}

