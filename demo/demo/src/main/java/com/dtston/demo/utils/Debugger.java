package com.dtston.demo.utils;

import android.util.Log;

public class Debugger {
	
	public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int NO_LOG = 7;
	
	private static int mLevel = VERBOSE;

	public static void logD(String tag, String msg) {
		if (mLevel < INFO) {
			Log.d(tag, msg);
		}
	}
	
}
