package com.dtston.demo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dtston.demo.R;

public class UserAgreementActivity extends BaseActivity {
	
	private View mVBack;
	private WebView mVWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_agreement);
		initViews();
		initEvents();
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

	@Override
	protected void initViews() {
		mVBack = findViewById(R.id.back);
		mVWebView = (WebView) findViewById(R.id.webview);
		
		WebSettings webSettings = mVWebView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        mVWebView.setBackgroundColor(Color.TRANSPARENT);  //  WebView 背景透明效果 
        mVWebView.loadUrl("file:///android_asset/clause.html");
	}

	@Override
	protected void initEvents() {
		mVBack.setOnClickListener(this);
	}
	
}
