package com.dtston.demo;

import android.os.Build;

import com.dtston.demo.utils.Activitystack;
import com.dtston.demo.utils.FileUtil;
import com.dtston.demo.utils.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

public class AppUncaughtExceptionHandler implements UncaughtExceptionHandler{
	
	public final static String LINE_END = "\r\n";
	
	private static AppUncaughtExceptionHandler mInstance;
	private static UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
	
	private AppUncaughtExceptionHandler() {
		mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	public static AppUncaughtExceptionHandler getInstance() {
		if (null == mInstance) {
			synchronized (AppUncaughtExceptionHandler.class) {
				if (null == mInstance) {
					mInstance = new AppUncaughtExceptionHandler();
				}
			}
		}
		return mInstance;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		String name = thread.getName();
		String errorLog = obtainErrorLog(ex);
		saveLog(name, errorLog);
		mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
	}
	
	public static String obtainErrorLog(Throwable ex) {
		StackTraceElement[] arr = ex.getStackTrace();
		final StringBuffer sbLog = new StringBuffer(ex.toString());
		final String lineSeperator = "-------------------------------\n\n";
		sbLog.append(LINE_END + LINE_END);
		sbLog.append("--------- Stack trace ---------\n\n");
		for (int i = 0; i < arr.length; i++) {
			sbLog.append("    ");
			sbLog.append(arr[i].toString());
			sbLog.append(LINE_END);
		}

		Throwable cause = ex.getCause();
		if (cause != null) {
			sbLog.append(lineSeperator);
			sbLog.append("--------- Cause ---------\n\n");
			sbLog.append(cause.toString());
			sbLog.append(LINE_END + LINE_END);
			arr = cause.getStackTrace();
			for (int i = 0; i < arr.length; i++) {
				sbLog.append("    ");
				sbLog.append(arr[i].toString());
				sbLog.append(LINE_END);
			}
		}
		sbLog.append(lineSeperator);
		sbLog.append("--------- Device ---------\n\n");
		sbLog.append("Brand: ");
		sbLog.append(Build.BRAND);
		sbLog.append(LINE_END);
		sbLog.append("Device: ");
		sbLog.append(Build.DEVICE);
		sbLog.append(LINE_END);
		sbLog.append("Model: ");
		sbLog.append(Build.MODEL);
		sbLog.append(LINE_END);
		sbLog.append("Id: ");
		sbLog.append(Build.ID);
		sbLog.append(LINE_END);
		sbLog.append("Product: ");
		sbLog.append(Build.PRODUCT);
		sbLog.append(LINE_END);
		sbLog.append(lineSeperator);
		sbLog.append("--------- Firmware ---------\n\n");
		sbLog.append("SDK: ");
		sbLog.append(Build.VERSION.SDK);
		sbLog.append(LINE_END);
		sbLog.append("Release: ");
		sbLog.append(Build.VERSION.RELEASE);
		sbLog.append(LINE_END);
		sbLog.append("Incremental: ");
		sbLog.append(Build.VERSION.INCREMENTAL);
		sbLog.append(LINE_END);
		sbLog.append(lineSeperator);
		sbLog.append("--------- Activity Stack Info ---------\n\n");
		for(int i=0 ; i< Activitystack.getActivityList().size() ; i++){
			sbLog.append(i+"-"+Activitystack.getActivityList().get(i).getClass().getSimpleName());
			sbLog.append(LINE_END);
		}
		sbLog.append("--------- App Info ---------\n\n");
		sbLog.append("Version code:");
		sbLog.append(StringUtils.getVersionCode());
		sbLog.append(LINE_END);
		sbLog.append("Version name:");
		sbLog.append(StringUtils.getVersionName());
		return sbLog.toString();
	}
	
	public static String obtainBasicInfoLog(String info) {
		final String lineSeperator = "-------------------------------\n\n";
		StringBuffer sbLog;
		if (StringUtils.isEmpty(info)) {
			sbLog = new StringBuffer();
		} else {
			sbLog = new StringBuffer(info);
			sbLog.append(LINE_END + LINE_END);
		}
		sbLog.append("--------- Device ---------\n\n");
		sbLog.append("Brand: ");
		sbLog.append(Build.BRAND);
		sbLog.append(LINE_END);
		sbLog.append("Device: ");
		sbLog.append(Build.DEVICE);
		sbLog.append(LINE_END);
		sbLog.append("Model: ");
		sbLog.append(Build.MODEL);
		sbLog.append(LINE_END);
		sbLog.append("Id: ");
		sbLog.append(Build.ID);
		sbLog.append(LINE_END);
		sbLog.append("Product: ");
		sbLog.append(Build.PRODUCT);
		sbLog.append(LINE_END);
		sbLog.append(lineSeperator);
		sbLog.append("--------- Firmware ---------\n\n");
		sbLog.append("SDK: ");
		sbLog.append(Build.VERSION.SDK);
		sbLog.append(LINE_END);
		sbLog.append("Release: ");
		sbLog.append(Build.VERSION.RELEASE);
		sbLog.append(LINE_END);
		sbLog.append("Incremental: ");
		sbLog.append(Build.VERSION.INCREMENTAL);
		sbLog.append(LINE_END);
		sbLog.append(lineSeperator);
		sbLog.append("--------- Activity Stack Info ---------\n\n");
		for(int i=0 ; i< Activitystack.getActivityList().size() ; i++){
			sbLog.append(i+"-"+Activitystack.getActivityList().get(i).getClass().getSimpleName());
			sbLog.append(LINE_END);
		}
		sbLog.append("--------- App Info ---------\n\n");
		sbLog.append("Version code:");
		sbLog.append(StringUtils.getVersionCode());
		sbLog.append(LINE_END);
		sbLog.append("Version name:");
		sbLog.append(StringUtils.getVersionName());
		return sbLog.toString();
	}

	public static void saveLog(String logName,String log) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			File file = FileUtil.createFileDir(FileUtil.LOG_PATH);
			String logFilePath = file.getAbsolutePath() + File.separator + logName +"_"+System.currentTimeMillis() + ".dt";
			File logFile = new File(logFilePath);
			logFile.createNewFile();
			fw = new FileWriter(logFile, true);
			pw = new PrintWriter(fw);
			pw.println("------------------------"
					+ (new Date().toLocaleString())
					+ "------------------------");
			pw.print(log);
			pw.print(LINE_END + LINE_END);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
				pw = null;
			}
			if (fw != null) {
				try {
					fw.close();
					fw = null;
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

}
