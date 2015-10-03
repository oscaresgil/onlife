package com.objective4.app.onlife.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.objective4.app.onlife.R;

public class ActivityPrivacyPolicy extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        WebView myWebView;
        myWebView = (WebView) findViewById(R.id.webview_privacy);
        myWebView.loadUrl(getString(R.string.privacy_policy_url));
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }
}
