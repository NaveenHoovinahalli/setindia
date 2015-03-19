package com.teli.sonyset.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by madhuri on 5/3/15.
 */
public class SonyTextView extends TextView {
    public SonyTextView(Context context) {
        super(context);
        setFont();
    }

    public SonyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public SonyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    public void setFont(){
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "klavikaregular_plain_webfont.ttf");
        setTypeface(tf);
    }
}
