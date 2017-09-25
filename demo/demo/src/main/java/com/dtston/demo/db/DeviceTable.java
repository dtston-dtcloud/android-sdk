package com.dtston.demo.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "device")
public class DeviceTable implements Serializable {
	
	/**
	 * 未绑定
	 */
	public static final int BIND_NON = 1;
	
	/**
	 * 已绑定
	 */
	public static final int BIND_HAS = 2;

	public static final String ID = "id";
	public static final String DEVICE_NAME = "deviceName";
	public static final String MAC = "mac";
	public static final String UID = "uid";
	public static final String BIND = "bind";
	public static final String TYPE = "type";
	public static final String IMAGE = "image";
	public static final String DEVICE_ID = "deviceId";
	public static final String DATA_ID = "dataId";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField
	private String deviceName;

	@DatabaseField
	private String mac;
	
	@DatabaseField
	private String uid;
	
	@DatabaseField
	private int bind;
	
	@DatabaseField
	private int type = 0;

	@DatabaseField
	private String image = "1";
	
	@DatabaseField
	private String deviceId = "";

	@DatabaseField
	private String dataId = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getBind() {
		return bind;
	}

	public void setBind(int bind) {
		this.bind = bind;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	
}
