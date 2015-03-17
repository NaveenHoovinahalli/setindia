package com.teli.sonyset.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.teli.sonyset.activities.LandingActivity;
import com.teli.sonyset.fragments.MyFragment;
import com.teli.sonyset.views.HorizontalLinearLayout;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private HorizontalLinearLayout cur = null;
    private HorizontalLinearLayout next = null;
    private LandingActivity context;
    private FragmentManager fm;
    MyFragment myFragment;
    private float scale;

    public MyPagerAdapter(LandingActivity context, FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        int count = position;
        position = position % LandingActivity.PAGES;
        myFragment = (MyFragment) myFragment.newInstance(context, position, 0.5f, count);
        return myFragment;
    }

    @Override
    public int getCount() {
        return LandingActivity.PAGES * LandingActivity.LOOPS;
    }

    public MyFragment getFragment(int key) {
        return null;
    }

    public void setSelection(int position) {
    }

    public void unSetSelection(int position) {
    }

}
