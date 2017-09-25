package com.dtston.demo.utils;

import android.widget.Toast;

import com.dtston.demo.ApplicationManager;

public class ToastUtils {
	
	private static Toast mToast = null;
	
	private static void initToast() {
		if (mToast == null) {
			synchronized (ToastUtils.class) {
				if (mToast == null) {
					mToast = Toast.makeText(ApplicationManager.getInstance().getApplicationContext(),
									"", Toast.LENGTH_SHORT);
				}
			}
		}
	}
	
	public static void showToast(int resId) {
		initToast();
		showToast(resId, mToast);
	}
	
	public static void showToast(String text) {
		initToast();
		showToast(text, mToast);
	}
	
	public static void showToast(String text, Toast toast) {
		if (toast != null) {
			toast.setText(text);
			toast.show();
		} else {
			Toast.makeText(ApplicationManager.getInstance().getApplicationContext(), 
					text, Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void showToast(int resId, Toast toast) {
		String text = ApplicationManager.getInstance().getApplicationContext()
				.getResources().getString(resId);
		if (toast != null) {
			toast.setText(text);
			toast.show();
		} else {
			Toast.makeText(ApplicationManager.getInstance().getApplicationContext(), 
					text, Toast.LENGTH_SHORT).show();
		}
	}
	
}
