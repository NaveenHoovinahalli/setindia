package com.teli.sonyset.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by sahana on 24/3/15.
 */
public class SonyEtMediumKalavika  extends EditText {

    public SonyEtMediumKalavika(Context context) {
        super(context);
        setFont();
    }

    public SonyEtMediumKalavika(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public SonyEtMediumKalavika(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    public void setFont(){
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "klavikamedium_plain_webfont.ttf");
        setTypeface(tf);
    }

}
