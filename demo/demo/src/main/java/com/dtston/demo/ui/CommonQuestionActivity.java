package com.dtston.demo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dtston.demo.R;
import com.dtston.demo.utils.SharedPreferencesUtils;

/**
 * Created by Administrator on 2017/11/24.
 */

public class CommonQuestionActivity extends BaseActivity{

    private View mVBack;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_question);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mVBack = findViewById(R.id.back);
        webview = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = webview.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        String appId = SharedPreferencesUtils.getAppId(this);
        webview.loadUrl("http://testfiles.ourslinks.com/html/help/app/help.html?product_id="+ appId);
    }

    @Override
    protected void initEvents() {
        mVBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

}
