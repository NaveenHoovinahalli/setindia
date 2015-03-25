package com.teli.sonyset.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.teli.sonyset.R;
import com.teli.sonyset.Utils.AndroidUtils;
import com.teli.sonyset.views.SonyTextView;
import com.zedo.androidsdk.ZedoAndroidSdk;
import com.zedo.androidsdk.utils.AdRenderer;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by madhuri on 15/3/15.
 */
public class WebViewActivity extends Activity {

    public static final String WEB_URL = "web_url";
    public static final String WEB_TEXT_HEADER = "header_text";
    @InjectView(R.id.webview)
    WebView mWebview;

    @InjectView(R.id.webProgress)
    ProgressBar mProgressBar;

    @InjectView(R.id.rlheader)
    RelativeLayout relativeLayout;

    @InjectView(R.id.textHeading)
    SonyTextView mTextHeading;

    Map<String, String> aliasMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_webview);
        ButterKnife.inject(this);

        if(!AndroidUtils.isNetworkOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry! No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        aliasMap = new LinkedHashMap<String, String>();
        aliasMap.put("banner", "c406d19s1");
        ZedoAndroidSdk.init(getApplicationContext(), "1408", aliasMap);

        if (getIntent().hasExtra(WEB_TEXT_HEADER)){
            relativeLayout.setVisibility(View.VISIBLE);
            mTextHeading.setText(getIntent().getStringExtra(WEB_TEXT_HEADER));
        }

        if(getIntent().hasExtra(WEB_URL)){
            String url = getIntent().getStringExtra(WEB_URL);
            mWebview.loadUrl(url);
            // mWebview.loadUrl("http://www.google.com");
            mWebview.setWebChromeClient(new MyWebChromeClient());
            mWebview.setWebViewClient(new MyWebviewClient());
        }

        super.onCreate(savedInstanceState);
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyWebviewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }

    @OnClick(R.id.backBtn)
    public void backPressed(){
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdRenderer.showStickyFooter(this, "banner",AdRenderer.ANIMATION_SLIDE_FROM_BOTTOM);
    }
}
