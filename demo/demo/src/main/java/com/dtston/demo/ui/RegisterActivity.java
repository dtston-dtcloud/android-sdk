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
import com.dtston.dtcloud.result.RegisterResult;

public class RegisterActivity extends BaseActivity {
	
	private static final int WHAT_CAPTCHA_TIME = 0x100;
	private static final int DELAY_CAPTCHA_TIME = 1*1000;
	
	private View mVRoot;
	private View mVBack;
	private View mVRegister;
	private EditText mVUserName;
	private EditText mVCaptcha;
	private EditText mVPassword;
	private View mVUserAgreement;
	private TextView mVGetCaptcha;
	
	private int mCaptchaTime = 60;
	
	private NetworkProgressDialog mVProgressDialog;
	private boolean captchaGetting = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
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
			case R.id.register:
				postRegister();
				break;
			case R.id.user_agreement:
				Intent userAgreementActivityIntent = new Intent(this, UserAgreementActivity.class);
				startActivity(userAgreementActivityIntent);
				break;
			default:
				break;
		}
	}

	@Override
	protected void initViews() {
		mVRoot = findViewById(R.id.root);
		mVBack = findViewById(R.id.back);
		mVRegister = findViewById(R.id.register);
		mVUserName = (EditText) findViewById(R.id.username);
		mVCaptcha = (EditText) findViewById(R.id.captcha);
		mVPassword = (EditText) findViewById(R.id.password);
		mVUserAgreement = findViewById(R.id.user_agreement);
		mVGetCaptcha = (TextView) findViewById(R.id.captchaBt);
	}

	@Override
	protected void initEvents() {
		InputMethodUtils.hideInputMethodWindow(this, mVRoot);
		mVBack.setOnClickListener(this);
		mVRegister.setOnClickListener(this);
		mVUserAgreement.setOnClickListener(this);
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
		if (captchaGetting) {
			return;
		}
		String userName = mVUserName.getText().toString().trim();
		if (TextUtils.isEmpty(userName)) {
			ToastUtils.showToast(R.string.input_phone_please);
			return;
		}
		boolean b = PatternUtils.isPhone(userName);
		if (b == false) {
			ToastUtils.showToast(R.string.input_correct_phone);
			return;
		}
		captchaGetting = true;
		
		showProgressDialog("正在获取验证码...");

		UserManager.getRegisterVcode(userName, new DTIOperateCallback() {
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
				captchaGetting = false;
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
	
	private void postRegister() {
		String userName = mVUserName.getText().toString().trim();
		String password = mVPassword.getText().toString();
		String vcode = mVCaptcha.getText().toString();
		
		if (TextUtils.isEmpty(userName)) {
			ToastUtils.showToast(R.string.input_phone_please);
			return;
		}
		boolean b = PatternUtils.isPhone(userName);
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
		
		showProgressDialog("正在注册...");

		UserManager.registerUser(userName, password, vcode, new DTIOperateCallback<RegisterResult>() {
			@Override
			public void onSuccess(RegisterResult registerResult, int code) {
				closeProgressDialog();
				String uid = registerResult.getData().getUid();
				String token = registerResult.getData().getToken();
				String time = registerResult.getData().getTime()+"";
				registerSuccess(uid, token, time);
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
