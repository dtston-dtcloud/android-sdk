package com.dtston.demo.ui;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dtston.demo.ApplicationManager;
import com.dtston.demo.R;
import com.dtston.demo.common.Constans;
import com.dtston.demo.dao.DeviceDao;
import com.dtston.demo.db.DeviceTable;
import com.dtston.demo.utils.Activitystack;
import com.dtston.demo.utils.SharedPreferencesUtils;
import com.dtston.dtcloud.DeviceManager;
import com.dtston.dtcloud.push.DTConnectedDevice;
import com.dtston.dtcloud.push.DTIDeviceConnectCallback;

public class DeviceConnectingActivity extends BaseActivity {

    private String TAG = getClass().getSimpleName();
	public static final String EXTRAS_SSID = "extras_ssid";
	public static String EXTRAS_DEVICE_TYPE="extras_device_type";
	public static final String EXTRAS_PASSWD = "extras_passwd";

	private View mVBack;
	private String mSsid;
	private String mPasswd;
	private int mModuleType;
	private int mDeviceType;
	private String mDeviceName;
	private String mMqttDomain = "api.ourslinks.com";
	private String mOtaDomain = "api.ourslinks.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_connecting);
		initViews();
		initEvents();
		startEasyLink();
//		startApSearch();
	//	DeviceManager.firmwareUpgrade();
	}

	@Override
	protected void initViews() {
		mVBack = findViewById(R.id.back);
		ImageView animDeviceConnecting = (ImageView) findViewById(R.id.device_connecting_anim);
		AnimationDrawable  animDrawable = (AnimationDrawable) animDeviceConnecting.getDrawable();
		animDrawable.start();
	}
	
	@Override
	protected void initEvents() {
		mVBack.setOnClickListener(this);
	}
	
	private void startEasyLink() {
		Bundle extras = getIntent().getExtras();
		mSsid = extras.getString(EXTRAS_SSID);
		mPasswd = extras.getString(EXTRAS_PASSWD);
		mModuleType = SharedPreferencesUtils.getWifiModule(this);
		DeviceTable currentDevice = ApplicationManager.getInstance().getCurrentControlDevice();
		mDeviceType = currentDevice.getType();
		mDeviceName = currentDevice.getDeviceName();
		mMqttDomain = "api.ourslinks.com";
		mOtaDomain = "api.ourslinks.com";

		if (DeviceManager.WIFI_AP_TYPE_THIRD == mModuleType) {
			startApConnect();
		} else {
			startDeviceMatching();
		}
	}
	
	@Override
	protected void onDestroy() {
		DeviceManager.stopDeviceMatchingNetwork();
		DeviceManager.stopApModeMatchingNetwork();
		//DeviceManager.stopApSearch();
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			default:
				break;
		}
	}

	private void startDeviceMatching() {
		DeviceManager.startDeviceMatchingNetwork(this, mModuleType, mDeviceType,
				mDeviceName, mSsid, mPasswd, new DTIDeviceConnectCallback() {

					@Override
					public void onSuccess(DTConnectedDevice connectedDevice) {
						DeviceTable currentControlDevice = ApplicationManager.getInstance().getCurrentControlDevice();
						currentControlDevice.setMac(connectedDevice.getMac());
						currentControlDevice.setDeviceName("设备" + connectedDevice.getMac());
						currentControlDevice.setType(Integer.valueOf(connectedDevice.getType()));
						currentControlDevice.setDataId(connectedDevice.getDataId());
						DeviceDao.getInstance().insert(currentControlDevice);
						ApplicationManager.getInstance().addDeviceList(currentControlDevice);

						Activitystack.finishOther(MainActivity.class.getSimpleName());
						if (Constans.isTestShuiSheng){
							startActivity(new Intent(DeviceConnectingActivity.this, DeviceControlForShuiShengActivity.class));
						}else {
							startActivity(new Intent(DeviceConnectingActivity.this, DeviceControlActivity.class));
						}

						finish();
					}

					@Override
					public void onFail(int errcode, String errmsg) {
						Log.d(TAG, "onFail errcode = " + errcode);
						Intent intent = new Intent(DeviceConnectingActivity.this, DeviceConnectedFailedActivity.class);
						startActivity(intent);
						finish();
					}
				});
	}

	private void startApConnect() {
		DeviceManager.startApModeMatchingNetwork(this, DeviceManager.WIFI_AP_TYPE_THIRD, mDeviceType, false, mDeviceName,
				mSsid, mPasswd, mMqttDomain, mOtaDomain, new DTIDeviceConnectCallback() {
					@Override
					public void onSuccess(DTConnectedDevice connectedDevice) {
						DeviceTable currentControlDevice = ApplicationManager.getInstance().getCurrentControlDevice();
						currentControlDevice.setMac(connectedDevice.getMac());
						currentControlDevice.setDeviceName("设备" + connectedDevice.getMac());
						currentControlDevice.setType(Integer.valueOf(connectedDevice.getType()));
						currentControlDevice.setDataId(connectedDevice.getDataId());
						DeviceDao.getInstance().insert(currentControlDevice);
						ApplicationManager.getInstance().addDeviceList(currentControlDevice);

						Activitystack.finishOther(MainActivity.class.getSimpleName());
						if (Constans.isTestShuiSheng){
							startActivity(new Intent(DeviceConnectingActivity.this, DeviceControlForShuiShengActivity.class));
						}else {
							startActivity(new Intent(DeviceConnectingActivity.this, DeviceControlActivity.class));
						}

						finish();
					}

					@Override
					public void onFail(int errcode, String errmsg) {
						Log.d(TAG, "onFail errcode = " + errcode);
						Intent intent = new Intent(DeviceConnectingActivity.this, DeviceConnectedFailedActivity.class);
						startActivity(intent);
						finish();
					}
				});
	}

	private void startApSearch() {
		DeviceManager.startApSearch(new DTIDeviceConnectCallback() {
			@Override
			public void onSuccess(DTConnectedDevice connectedDevice) {
				System.out.println("startApSearch ok");
				DeviceTable currentControlDevice = ApplicationManager.getInstance().getCurrentControlDevice();
				currentControlDevice.setMac(connectedDevice.getMac());
				currentControlDevice.setDeviceName("设备" + connectedDevice.getMac());
				currentControlDevice.setType(Integer.valueOf(connectedDevice.getType()));
				currentControlDevice.setDataId(connectedDevice.getDataId());
				DeviceDao.getInstance().insert(currentControlDevice);

				Activitystack.finishOther(MainActivity.class.getSimpleName());
				if (Constans.isTestShuiSheng){
					startActivity(new Intent(DeviceConnectingActivity.this, DeviceControlForShuiShengActivity.class));
				}else {
					startActivity(new Intent(DeviceConnectingActivity.this, DeviceControlActivity.class));
				}
				finish();
			}

			@Override
			public void onFail(int i, String s) {
				System.out.println("startApSearch failed, s = " + s);
			}
		});
	}
	
}
