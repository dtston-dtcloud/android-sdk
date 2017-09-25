package com.dtston.demo.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.dtston.demo.db.UserTable;


public class UserDao extends BaseDao<UserTable, Integer> {
	
	private static UserDao mInstance;
	
	public static UserDao getInstance() {
		if (mInstance == null) {
			synchronized (UserDao.class) {
				if (mInstance == null) {
					mInstance = new UserDao();
				}
			}
		}
		return mInstance;
	}
	
	private UserDao() {
		super();
		try {
			mDao = getHelper().getDao(UserTable.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public UserTable getCurrentUser() {
		Map<String, Object> where = new HashMap<String, Object>();
		where.put(UserTable.TYPE, UserTable.TYPE_CURRENT_USER);
		return super.queryForFirstWhere(where);
	}
	
	public UserTable getUserByUid(String uid) {
		Map<String, Object> where = new HashMap<String, Object>();
		where.put(UserTable.UID, uid);
		return super.queryForFirstWhere(where);
	}
	
}
