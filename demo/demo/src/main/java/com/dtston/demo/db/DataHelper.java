package com.dtston.demo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dtston.demo.utils.Debugger;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DataHelper extends OrmLiteSqliteOpenHelper {
	
	private static final String DATABASE_NAME = "sdkdemo.db";
	private static final int DATABASE_VERSION = 1;
	
	private static DataHelper mInstance;
	
	public static DataHelper getInstance(Context context) {
		if (mInstance == null) {
			synchronized (DataHelper.class) {
				if (mInstance == null) {
					mInstance = new DataHelper(context);
				}
			}
		}
		return mInstance;
	}
	
	public static DataHelper getHelper() {
		return mInstance;
		
	}

	private DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTable(connectionSource, UserTable.class);
			TableUtils.createTable(connectionSource, DeviceTable.class);
		} catch (Throwable e) {
			Debugger.logD(DataHelper.class.getName(), "database onCreate failed : " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
//		int nextVersion = oldVersion + 1;
//		switch (nextVersion) {
//		case 2:
//			try {
//				TableUtils.createTable(connectionSource, UserMessageTable.class);
//			} catch (Throwable e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	@Override
	public void close() {
		super.close();
	}

}
