package com.dtston.demo.utils;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;

public class Activitystack {
	
	private static CopyOnWriteArrayList<Activity> mActivityList = new CopyOnWriteArrayList<Activity>();
	
	public static CopyOnWriteArrayList<Activity> getActivityList() {
		return mActivityList;
	}

	public static void addActivity(Activity activity) {
		mActivityList.add(activity);
	}
	
	public static void removeActivity(Activity activity) {
		mActivityList.remove(activity);
	}
	
	public static Activity getFirstActivity() {
		Activity activity = null;
		mActivityList.remove(activity);
		if (!mActivityList.isEmpty()) {
			activity = mActivityList.get(mActivityList.size()-1);
		}
		return activity;
	}
	
	public static void finishAll() {
		try {
			ArrayList<Activity> finishList = new ArrayList<Activity>();
			int size = mActivityList.size();
			for (int i=0; i<size; i++) {
				finishList.add(mActivityList.get(i));
			}
			for (Activity activity : finishList) {
				if(activity != null && !activity.isFinishing()) {
					activity.finish();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	public static void finishOther(String activitySimpleName) {
		try {
			ArrayList<Activity> finishList = new ArrayList<Activity>();
			int size = mActivityList.size();
			for (int i=0; i<size; i++) {
				Activity activity = mActivityList.get(i);
				if(activity != null
						&& false == activity.getClass().getSimpleName().equals(activitySimpleName)) {
					finishList.add(mActivityList.get(i));
				}
			}
			for (Activity activity : finishList) {
				if(activity != null && !activity.isFinishing()) {
					activity.finish();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void finishActvtity(String activitySimpleName) {
		try {
			ArrayList<Activity> finishList = new ArrayList<Activity>();
			int size = mActivityList.size();
			for (int i=0; i<size; i++) {
				Activity activity = mActivityList.get(i);
				if(activity != null
						&& activity.getClass().getSimpleName().equals(activitySimpleName)) {
					finishList.add(mActivityList.get(i));
				}
			}
			for (Activity activity : finishList) {
				if(activity != null && !activity.isFinishing()) {
					activity.finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
