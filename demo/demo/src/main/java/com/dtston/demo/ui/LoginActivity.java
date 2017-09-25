package com.dtston.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.dtston.demo.ApplicationManager;
import com.dtston.demo.R;
import com.dtston.demo.dao.UserDao;
import com.dtston.demo.db.UserTable;
import com.dtston.demo.dialog.NetworkProgressDialog;
import com.dtston.demo.utils.Activitystack;
import com.dtston.demo.utils.Debugger;
import com.dtston.demo.utils.InputMethodUtils;
import com.dtston.demo.utils.PatternUtils;
import com.dtston.demo.utils.SharedPreferencesUtils;
import com.dtston.demo.utils.ToastUtils;
import com.dtston.dtcloud.UserManager;
import com.dtston.dtcloud.push.DTIOperateCallback;
import com.dtston.dtcloud.result.LoginResult;

public class LoginActivity extends BaseActivity {
	
	private View mVRoot;
	private View mVRegister;
	private View mVLogin;
	private EditText mVUserName;
	private EditText mVPassword;

	private NetworkProgressDialog mVProgressDialog;
	
	private View mVForgetPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initViews();
		initEvents();
		initData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.register:
				Intent registerIntent = new Intent(this, RegisterActivity.class);
				startActivity(registerIntent);
				break;
			case R.id.login:
				postLogin();
				break;
			default:
				break;
		}
	}

	@Override
	protected void initViews() {
		mVRoot = findViewById(R.id.root);
		mVRegister = findViewById(R.id.register);
		mVLogin = findViewById(R.id.login);
		mVUserName = (EditText) findViewById(R.id.username);
		mVPassword = (EditText) findViewById(R.id.password);
		mVForgetPassword = findViewById(R.id.forgetPassword);
	}

	@Override
	protected void initEvents() {
		InputMethodUtils.hideInputMethodWindow(this, mVRoot);
		mVRegister.setOnClickListener(this);
		mVLogin.setOnClickListener(this);
		mVForgetPassword.setOnClickListener(this);
	}
	
	private void initData() {
		String userName = SharedPreferencesUtils.getExitUserName(this);
		mVUserName.setText(userName);
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
	
	private void postLogin() {
		String userName = mVUserName.getText().toString().trim();
		String password = mVPassword.getText().toString();
		
		if (TextUtils.isEmpty(userName)) {
			ToastUtils.showToast(R.string.input_phone_please);
			return;
		}
		boolean b = PatternUtils.isPhone(userName);
		if (b == false) {
			ToastUtils.showToast(R.string.input_correct_phone);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			ToastUtils.showToast(R.string.input_password_please);
			return;
		}
		
		showProgressDialog("正在登录...");
		UserManager.loginUser(userName, password, new DTIOperateCallback<LoginResult>() {
//		UserManager.loginOtherUser(userName, new DTIOperateCallback() {
			@Override
			public void onSuccess(LoginResult var1, int var2) {
				closeProgressDialog();
				String uid = var1.getData().getUid();
				String token = var1.getData().getToken();
				String time = var1.getData().getTime()+"";
				loginSuccess(uid, token, time, "", UserTable.SOURCE_PHONE);
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				closeProgressDialog();
				ToastUtils.showToast(error.toString());
			}
		});
	}

	private void loginSuccess(String uid, String token, String time, String image, int source) {
		String userName = mVUserName.getText().toString().trim();

		boolean exist = true;
		UserTable currentUser = UserDao.getInstance().getUserByUid(uid);
		if (currentUser == null) {
			currentUser = new UserTable();
			exist = false;
		}
		
		currentUser.setUid(uid);
		currentUser.setUserName(userName);
		currentUser.setIcon(image);
		currentUser.setType(UserTable.TYPE_CURRENT_USER);
		currentUser.setSource(source);
		currentUser.setToken(token);
		
		if (exist) {
			int update = UserDao.getInstance().update(currentUser);
			Debugger.logD("login", "update id is " + currentUser.getId());
		} else {
			int insert = UserDao.getInstance().insert(currentUser);
			Debugger.logD("login", "insert id is " + currentUser.getId());
		}

		SharedPreferencesUtils.editExitUserName(this, currentUser.getUserName());
		ApplicationManager.getInstance().setCurrentUser(currentUser);
		Activitystack.finishAll();
		Intent mainIntent = new Intent(this, MainActivity.class);
		startActivity(mainIntent);
	}

}
