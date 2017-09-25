package com.dtston.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

public class InputMethodUtils {
	
	/**
	 * 点击空白处隐藏输入法
	 * @param activity
	 * @param view
	 */
	public static void hideInputMethodWindow(final Activity activity, View rootView) {
		rootView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				 if (activity == null 
						 || activity.getCurrentFocus() == null) {
					 return false;
				 }
				 return imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
			}
		});
	}
	
	/**
	 * 显示隐藏输入法
	 * 
	 * @param isShown
	 */
	public static void showOrHideInputMethod(Activity activity, View editText, boolean isShown) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (isShown) {
			if (editText != null) {
				editText.requestFocus();
				editText.requestFocusFromTouch();
				imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
			}
		} else {
			if (activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
			}
			if (editText != null) {
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			}
		}
	}
	
}
