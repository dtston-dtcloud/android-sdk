package com.dtston.demo.dialog;

import com.dtston.demo.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NetworkProgressDialog extends ProgressDialog {	
	
	private ProgressBar mVProgressBar;
	private TextView mVProgressText=null;
	private String mText;

	public NetworkProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}	
	
	//返回键点击是否取消
	public NetworkProgressDialog(Context context,boolean isReturnKey) {		
		super(context);
		this.setCancelable(isReturnKey);
		// TODO Auto-generated constructor stub
	}
	
	//其他区域是否可以点击取消
	public NetworkProgressDialog(Context context,boolean isReturnKey,boolean isOutside) {
		super(context);
		this.setCancelable(isReturnKey);
		this.setCanceledOnTouchOutside(isOutside);
		// TODO Auto-generated constructor stub
	}
	
	public void setProgressText(String text){
		this.mText = text;
		if (mVProgressText != null) {
			mVProgressText.setText(mText);		
		}
	}
	
	public void setProgressText(int resid){	
		this.mText = getContext().getResources().getString(resid);
		if (mVProgressText != null) {
			mVProgressText.setText(mText);
		}
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_dialog);
		initView();
	}
	
	private void initView(){
		mVProgressBar=(ProgressBar)findViewById(R.id.progressBars);
		mVProgressText=(TextView)findViewById(R.id.progressText);
		setProgressText(mText);
	}

}
