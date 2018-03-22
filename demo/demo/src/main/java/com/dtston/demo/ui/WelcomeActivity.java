package com.dtston.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.dtston.demo.ApplicationManager;
import com.dtston.demo.R;
import com.dtston.demo.db.UserTable;

public class WelcomeActivity extends BaseActivity {

	private static final int MSG_WHAT_LOGIN = 100;
	private static final int MSG_DELAYED_LOGIN = 3 * 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDate();
	}
	
	private void initDate() {
		UserTable currentUser = ApplicationManager.getInstance().getCurrentUser();
		if (currentUser == null) {
			setContentView(R.layout.activity_welcome);
			mHandler.sendEmptyMessageDelayed(MSG_WHAT_LOGIN, MSG_DELAYED_LOGIN);
		} else {
			Intent mainIntent = new Intent(this, MainActivity.class);
			startActivity(mainIntent);
			finish();
		}
	}
	
	@Override
	public void onClick(View v) {
		
	}

	@Override
	protected void initViews() {
		
	}

	@Override
	protected void initEvents() {
		
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isFinishing()) {
				return;
			}
			if (MSG_WHAT_LOGIN == msg.what) {
				startLoginActivity();
			}
		}
	};
	
	private void startLoginActivity() {
		Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}
