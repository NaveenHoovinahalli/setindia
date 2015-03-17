package com.teli.sonyset.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.teli.sonyset.activities.LandingActivity;
import com.teli.sonyset.fragments.MyFragment;
import com.teli.sonyset.views.HorizontalLinearLayout;

public class MyPagerAdapter extends FragmentPagerAdapter {

	private HorizontalLinearLayout cur = null;
	private HorizontalLinearLayout next = null;
	private LandingActivity context;
	private FragmentManager fm;
	private float scale;

	public MyPagerAdapter(LandingActivity context, FragmentManager fm) {
		super(fm);
		this.fm = fm;
		this.context = context;
	}

	@Override
	public Fragment getItem(int position)
	{
        position = position % LandingActivity.PAGES;
        return MyFragment.newInstance(context, position, 0.5f);
	}

	@Override
	public int getCount()
	{
		return LandingActivity.PAGES * LandingActivity.LOOPS;
	}

}
