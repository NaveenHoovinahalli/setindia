package com.teli.sonyset.views;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.brightcove.player.view.BrightcoveVideoView;

/**
 * Created by nith on 3/15/15.
 */
public class SonyVideoView extends BrightcoveVideoView {

    private PlayPauseListener mListener;
    private Uri uri;

    public SonyVideoView(Context context) {
        super(context);
    }

    public SonyVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SonyVideoView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public void setMediaStateListener(PlayPauseListener listener) {
        mListener = listener;
    }

    @Override
    public void pause() {
        super.pause();
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mListener != null) {
            mListener.onPlay();
        }
    }

    @Override
    public void setVideoURI (Uri uri)
    {
        super.setVideoURI(uri);
        this.uri = uri;
    }

    public Uri getVideoURI ()
    {
        return uri;
    }

    public interface PlayPauseListener {
        void onPlay();
        void onPause();
    }
}
