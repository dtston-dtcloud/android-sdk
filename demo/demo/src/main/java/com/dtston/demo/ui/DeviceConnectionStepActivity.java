package com.dtston.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dtston.demo.R;

public class DeviceConnectionStepActivity extends BaseActivity {
	
	private View mVBack;
	private View mVNext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_connection_step);
		initViews();
		initEvents();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.btn_next:
				Intent intent = new Intent(this, DeviceConnectionActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	@Override
	protected void initViews() {
		mVBack = findViewById(R.id.back);
		mVNext = findViewById(R.id.btn_next);
	}

	@Override
	protected void initEvents() {
		mVBack.setOnClickListener(this);
		mVNext.setOnClickListener(this);
	}

}
