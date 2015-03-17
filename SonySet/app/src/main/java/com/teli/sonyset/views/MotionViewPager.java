package com.teli.sonyset.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

public class MotionViewPager extends LoopViewPager {
    private boolean isPagingEnabled;

    public MotionViewPager(Context context) {
        super(context);
        postInitViewPager();
        this.isPagingEnabled=true;
    }

    public MotionViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
        this.isPagingEnabled=true;
    }

    private ScrollerCustomDuration mScroller = null;

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new ScrollerCustomDuration(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        mScroller.setScrollDurationFactor(scrollFactor);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    public void setPagingEnabled(boolean b){
        this.isPagingEnabled = b;
    }

}
