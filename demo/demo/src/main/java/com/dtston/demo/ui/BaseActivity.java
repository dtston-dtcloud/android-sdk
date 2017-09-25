package com.dtston.demo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.Window;

import com.dtston.demo.utils.Activitystack;
import com.dtston.demo.utils.Debugger;

public abstract class BaseActivity extends Activity implements OnClickListener {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Activitystack.addActivity(this);
		Debugger.logD("Activity", "onCreate: " + getClass().getSimpleName());
	}
	
	protected abstract void initViews();
	
	protected abstract void initEvents();

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Activitystack.removeActivity(this);
		Debugger.logD("Activity", "onDestroy: " + getClass().getSimpleName());
	}
	
	@Override
	public void finish() {
		super.finish();
	}
	
}
