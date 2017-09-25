package com.dtston.demo.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")
public class UserTable {
	
	/**
	 * 当前用户
	 */
	public static final int TYPE_CURRENT_USER = 1;
	
	/**
	 * 其他用户
	 */
	public static final int TYPE_OTHER_USER = 2;
	
	/**
	 * 手机
	 */
	public static final int SOURCE_PHONE = 1;
	
	/**
	 * QQ
	 */
	public static final int SOURCE_QQ = 2;
	
	public static final String ID = "id";
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String UID = "uid";
	public static final String TOKEN = "token";
	public static final String TYPE = "type";
	public static final String ICON = "icon";
	public static final String SOURCE = "source";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField
	private String userName;
	
	@DatabaseField
	private String password;
	
	@DatabaseField
	private String uid;
	
	@DatabaseField
	private String token;
	
	@DatabaseField
	private int type;
	
	@DatabaseField
	private String icon;
	
	@DatabaseField
	private int source = SOURCE_PHONE;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public String getCmdUser() {
		if (source == SOURCE_QQ) {
			return "qq" + uid;
		}
		return userName;
	}
	
	public String getNick() {
		if (source == SOURCE_QQ) {
			return "qq" + uid;
		}
		return userName;
	}
	
}
