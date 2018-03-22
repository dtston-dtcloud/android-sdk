package com.dtston.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dtston.demo.ApplicationManager;
import com.dtston.demo.R;
import com.dtston.demo.dao.UserDao;
import com.dtston.demo.db.UserTable;
import com.dtston.demo.dialog.NetworkProgressDialog;
import com.dtston.demo.utils.Activitystack;
import com.dtston.demo.utils.Debugger;
import com.dtston.demo.utils.InputMethodUtils;
import com.dtston.demo.utils.PatternUtils;
import com.dtston.demo.utils.ToastUtils;
import com.dtston.dtcloud.UserManager;
import com.dtston.dtcloud.push.DTIOperateCallback;
import com.dtston.dtcloud.result.BaseResult;

public class ForgetPasswordActivity extends BaseActivity {
	
	private static final int WHAT_CAPTCHA_TIME = 0x100;
	private static final int DELAY_CAPTCHA_TIME = 1*1000;
	
	private View mVRoot;
	private View mVBack;
	private View mVSubmit;
	private EditText mVUserName;
	private EditText mVCaptcha;
	private EditText mVPassword;
	private TextView mVGetCaptcha;
	
	private int mCaptchaTime = 60;
	
	private NetworkProgressDialog mVProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		initViews();
		initEvents();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.captchaBt:
				postCaptcha();
				break;
			case R.id.submit:
				postSubmit();
				break;
			default:
				break;
		}
	}

	@Override
	protected void initViews() {
		mVRoot = findViewById(R.id.root);
		mVBack = findViewById(R.id.back);
		mVSubmit = findViewById(R.id.submit);
		mVUserName = (EditText) findViewById(R.id.username);
		mVCaptcha = (EditText) findViewById(R.id.captcha);
		mVPassword = (EditText) findViewById(R.id.password);
		mVGetCaptcha = (TextView) findViewById(R.id.captchaBt);
	}

	@Override
	protected void initEvents() {
		InputMethodUtils.hideInputMethodWindow(this, mVRoot);
		mVBack.setOnClickListener(this);
		mVSubmit.setOnClickListener(this);
		mVGetCaptcha.setOnClickListener(this);
	}
	
	private void showProgressDialog(String text) {
		closeProgressDialog();
		mVProgressDialog = new NetworkProgressDialog(this, true, false);
		mVProgressDialog.setProgressText(text);
		mVProgressDialog.show();
	}
	
	private void closeProgressDialog() {
		if (null != mVProgressDialog && mVProgressDialog.isShowing()) {
			mVProgressDialog.cancel();
		}
		mVProgressDialog = null;
	}
	
	private void postCaptcha() {
		String userName = mVUserName.getText().toString().trim();
		if (TextUtils.isEmpty(userName)) {
			ToastUtils.showToast(R.string.input_phone_please);
			return;
		}
		boolean b = PatternUtils.isPhone(userName) || PatternUtils.isEmail(userName);
		if (b == false) {
			ToastUtils.showToast(R.string.input_correct_phone);
			return;
		}
		mVGetCaptcha.setEnabled(false);
		
		showProgressDialog("正在获取验证码...");

		UserManager.getResetPasswordVcode(userName, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				closeProgressDialog();
				mVGetCaptcha.setEnabled(false);
				mCaptchaTime = 60;
				mVGetCaptcha.setText(getCaptchaTimeText(mCaptchaTime));
				mHandler.sendEmptyMessageDelayed(WHAT_CAPTCHA_TIME, DELAY_CAPTCHA_TIME);
				ToastUtils.showToast("验证码获取成功");
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				mVGetCaptcha.setEnabled(true);
				closeProgressDialog();
				ToastUtils.showToast(error.toString());
			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == WHAT_CAPTCHA_TIME) {
				mCaptchaTime --;
				if (mCaptchaTime > 0) {
					mVGetCaptcha.setText(getCaptchaTimeText(mCaptchaTime));
					mHandler.sendEmptyMessageDelayed(WHAT_CAPTCHA_TIME, DELAY_CAPTCHA_TIME);
				} else {
					mVGetCaptcha.setEnabled(true);
					mVGetCaptcha.setText(getString(R.string.get_captcha));
				}
			}
		};
	};
	
	private String getCaptchaTimeText(int time) {
		return getString(R.string.get_again) + "(" + time + ")";
	}
	
	private void postSubmit() {
		String userName = mVUserName.getText().toString().trim();
		String password = mVPassword.getText().toString();
		String vcode = mVCaptcha.getText().toString();
		
		if (TextUtils.isEmpty(userName)) {
			ToastUtils.showToast(R.string.input_phone_please);
			return;
		}
		boolean b = PatternUtils.isPhone(userName) || PatternUtils.isEmail(userName);
		if (b == false) {
			ToastUtils.showToast(R.string.input_correct_phone);
			return;
		}
		if (TextUtils.isEmpty(vcode)) {
			ToastUtils.showToast(R.string.input_captcha_please);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			ToastUtils.showToast(R.string.input_password_please);
			return;
		}
		if (false == PatternUtils.isCharacterByLetterOrNumber(password)) {
			ToastUtils.showToast(R.string.password_format_incorrect);
			return;
		}
		
		showProgressDialog("正在提交...");

		UserManager.resetPassword(userName, password, vcode, new DTIOperateCallback<BaseResult>() {
			@Override
			public void onSuccess(BaseResult baseResult, int code) {
				closeProgressDialog();
				ToastUtils.showToast("修改成功");
				finish();
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				//注册失败
				closeProgressDialog();
				ToastUtils.showToast(error.toString());
			}
		});
	}

	private void registerSuccess(String uid, String token, String time) {
		String userName = mVUserName.getText().toString().trim();

		boolean exist = true;
		UserTable currentUser = UserDao.getInstance().getUserByUid(uid);
		if (currentUser == null) {
			currentUser = new UserTable();
			exist = false;
		}
		
		currentUser.setUid(uid);
		currentUser.setUserName(userName);
		currentUser.setType(UserTable.TYPE_CURRENT_USER);
		currentUser.setToken(token);
		
		if (exist) {
			int update = UserDao.getInstance().update(currentUser);
			Debugger.logD("register", "update id is " + currentUser.getId());
		} else {
			int insert = UserDao.getInstance().insert(currentUser);
			Debugger.logD("register", "insert id is " + currentUser.getId());
		}
		
		ApplicationManager.getInstance().setCurrentUser(currentUser);
		Activitystack.finishAll();
		
		Intent managerIntent = new Intent(this, DeviceConnectionActivity.class);
		startActivity(managerIntent);
	}
	
}
