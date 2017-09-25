package com.dtston.demo.dao;

import com.dtston.demo.db.DeviceTable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DeviceDao extends BaseDao<DeviceTable, Integer> {
	
	private static DeviceDao mInstance;
	
	public static DeviceDao getInstance() {
		if (mInstance == null) {
			synchronized (DeviceDao.class) {
				if (mInstance == null) {
					mInstance = new DeviceDao();
				}
			}
		}
		return mInstance;
	}
	
	private DeviceDao() {
		super();
		try {
			mDao = getHelper().getDao(DeviceTable.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<DeviceTable> getDeviceByUid(String uid) {
		Map<String, Object> where = new HashMap<String, Object>();
		where.put(DeviceTable.UID, uid);
		return super.queryWhere(where);
	}
	
	public DeviceTable getDeviceByMac(String uid, String mac) {
		Map<String, Object> where = new HashMap<String, Object>();
		where.put(DeviceTable.UID, uid);
		where.put(DeviceTable.MAC, mac);
		List<DeviceTable> devices = super.queryWhere(where);
		if (devices != null && devices.size()>0) {
			return devices.get(0);
		}
		return null;
	}

}
