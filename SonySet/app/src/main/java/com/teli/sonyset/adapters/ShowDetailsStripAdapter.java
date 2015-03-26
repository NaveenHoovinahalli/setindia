package com.teli.sonyset.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.teli.sonyset.activities.ShowDetailsActivity;
import com.teli.sonyset.fragments.ShowDetailsStripFragment;
import com.teli.sonyset.views.HorizontalLinearLayout;

public class ShowDetailsStripAdapter extends FragmentPagerAdapter {

	private HorizontalLinearLayout cur = null;
	private HorizontalLinearLayout next = null;
	private Activity context;
	private FragmentManager fm;
	private String mColor;

	public ShowDetailsStripAdapter(Activity context, FragmentManager fm, String color) {
		super(fm);
		this.fm = fm;
		this.context = context;
        this.mColor = color;
	}

	@Override
	public Fragment getItem(int position){
        int count = position;
        position = position % ShowDetailsActivity.PAGES;
        return ShowDetailsStripFragment.newInstance(context, position, 0.5f, count, mColor);
	}

	@Override
	public int getCount()
	{
		return ShowDetailsActivity.PAGES * ShowDetailsActivity.LOOPS;
	}

}