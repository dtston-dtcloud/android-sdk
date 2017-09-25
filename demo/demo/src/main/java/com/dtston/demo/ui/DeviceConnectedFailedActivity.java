package com.dtston.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dtston.demo.R;

public class DeviceConnectedFailedActivity extends BaseActivity {
	
	private View mVBack;
	private Button mBtReconnected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_connected_failed);
		initViews();
		initEvents();
	}

	@Override
	protected void initViews() {
		mVBack = findViewById(R.id.back);
		mBtReconnected = (Button) findViewById(R.id.bt_reconnected);
	}

	@Override
	protected void initEvents() {
		mVBack.setOnClickListener(this);
		mBtReconnected.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.bt_reconnected:
				Intent intent = new Intent(this, DeviceConnectionActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
		}
	}

}
