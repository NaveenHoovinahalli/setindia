package com.teli.sonyset.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.teli.sonyset.activities.LandingActivity;
import com.teli.sonyset.fragments.MyFragment;
import com.teli.sonyset.views.HorizontalLinearLayout;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private HorizontalLinearLayout cur = null;
    private HorizontalLinearLayout next = null;
    private Activity context;
    private FragmentManager fm;

    public MyPagerAdapter(Activity context, FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        int count = position;
        position = position % LandingActivity.PAGES;
        return MyFragment.newInstance(context, position, 0.5f, count);
    }

    @Override
    public int getCount() {
//        return LandingActivity.PAGES * LandingActivity.LOOPS;
        return LandingActivity.PAGES;
    }
}
