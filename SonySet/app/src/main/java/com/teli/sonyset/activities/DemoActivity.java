package com.teli.sonyset.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

import com.teli.sonyset.R;
import com.teli.sonyset.fragments.ShowFragment;


/**
 * Created by madhuri on 5/3/15.
 */
public class DemoActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo);

        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);

        ShowFragment fragment = new ShowFragment();
      //  VideoFragment fragment = new VideoFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
    }
}
