package com.dtston.demo.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.dtston.demo.R;
import com.dtston.demo.dialog.NetworkProgressDialog;
import com.dtston.demo.utils.InputMethodUtils;
import com.dtston.demo.utils.ToastUtils;
import com.dtston.dtcloud.UserManager;
import com.dtston.dtcloud.push.DTIOperateCallback;
import com.dtston.dtcloud.result.BaseResult;

public class FeedbackActivity extends BaseActivity {
	
	private View mVRoot;
	private View mVBack;
	private View mVSubmit;
	private EditText mVFeedback;


	private NetworkProgressDialog mVProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initViews();
		initEvents();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
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
		mVFeedback = (EditText) findViewById(R.id.feedback);
	}

	@Override
	protected void initEvents() {
		InputMethodUtils.hideInputMethodWindow(this, mVRoot);
		mVBack.setOnClickListener(this);
		mVSubmit.setOnClickListener(this);
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
	

	private void postSubmit() {
		String userName = mVFeedback.getText().toString().trim();

		if (TextUtils.isEmpty(userName)) {
			ToastUtils.showToast("请输入反馈内容");
			return;
		}
		
		showProgressDialog("正在提交...");

		UserManager.userFeedback(userName, "1001", null, new DTIOperateCallback<BaseResult>() {
			@Override
			public void onSuccess(BaseResult baseResult, int code) {
				closeProgressDialog();
				ToastUtils.showToast("提交成功");
				finish();
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				//提交失败
				closeProgressDialog();
				ToastUtils.showToast(error.toString());
			}
		});
	}

}
