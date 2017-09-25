package com.dtston.demo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtston.demo.ApplicationManager;
import com.dtston.demo.R;
import com.dtston.demo.common.Constans;
import com.dtston.demo.db.DeviceTable;
import com.dtston.demo.dialog.ChoiceListDialog;
import com.dtston.demo.utils.InputMethodUtils;
import com.dtston.demo.utils.SharedPreferencesUtils;
import com.dtston.demo.utils.WifiUtils;
import com.dtston.dtcloud.DeviceManager;
import com.dtston.dtcloud.DtCloudManager;
import com.dtston.dtcloud.net.NetworkStateObserver;

import java.util.ArrayList;
import java.util.List;

public class DeviceConnectionActivity extends BaseActivity implements NetworkStateObserver {
	
	private View mVRoot;
	private View mVBack;
	private View mVWifiModuleRoot;
	private TextView mVWifiModuleName;
	private TextView mVWifiSsid;
	private EditText mEtPassword;
	private EditText mEtProductType;
	private EditText mEtProductName;

	private Button mBtnNext;
	private Button mBtOpenWifi;
	private LinearLayout mLlSsidAndPasswd;
	private LinearLayout mLlWifiNot;

	private ImageView mVShowPassword;
	
	private boolean isShowPassword = false;
	
	private List<String> mWifiModuleList = new ArrayList<String>();//WiFi模块列表
	private ChoiceListDialog mChoiceListDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_connection);
		initViews();
		initEvents();
		initWifiModuleData();
		DtCloudManager.addNetworkStateObserver(this);
		initForShuishengkeji();
	}
	
	private void initWifiModuleData() {
		mWifiModuleList.add("汉枫");
		mWifiModuleList.add("乐鑫");
		mWifiModuleList.add("庆科");
		mWifiModuleList.add("马威尔");
		mWifiModuleList.add("6060");

		int module = SharedPreferencesUtils.getWifiModule(this);
		String moduleName = "汉枫";
		switch (module) {
			case DeviceManager.WIFI_QK:
				moduleName = "庆科";
				break;
			case DeviceManager.WIFI_HF:
				moduleName = "汉枫";
				break;
			case DeviceManager.WIFI_LX:
				moduleName = "乐鑫";
				break;
			case DeviceManager.WIFI_MWR:
				moduleName = "马威尔";
				break;
			case DeviceManager.WIFI_6060:
				moduleName = "6060";
				break;
			default:
				break;
		}
		mVWifiModuleName.setText(moduleName);
	}

	private void  initForShuishengkeji(){
		if (Constans.isTestShuiSheng){
			SharedPreferencesUtils.editWifiModule(this,DeviceManager.WIFI_LX);
			mVWifiModuleName.setText("乐鑫");
			mEtProductType.setText("2290");
			mEtProductName.setText("水圣科技");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initWifi();
	}
	
	@Override
	protected void onDestroy() {
		DtCloudManager.removeNetworkStateObserver(this);
		super.onDestroy();
	}
	
	@Override
	protected void initViews() {
		mVRoot = findViewById(R.id.root);
		mVBack = findViewById(R.id.back);
		
		mVWifiModuleRoot = findViewById(R.id.wifi_module_root);
		mVWifiModuleName = (TextView) findViewById(R.id.module_name);
		
		mLlSsidAndPasswd = (LinearLayout) findViewById(R.id.ll_ssid_passwd);
		mLlWifiNot = (LinearLayout) findViewById(R.id.ll_wifi_not);
		mBtOpenWifi = (Button) findViewById(R.id.bt_open_wifi);
		mBtnNext = (Button) findViewById(R.id.btn_next);

		mVWifiSsid  = (TextView) findViewById(R.id.et_wifi_ssid);
		mEtPassword  = (EditText) findViewById(R.id.et_password);
		mEtProductType  = (EditText) findViewById(R.id.et_product_type);
		mEtProductName  = (EditText) findViewById(R.id.et_product_name);
		mVShowPassword = (ImageView) findViewById(R.id.show_password);
	}
	
	private void initWifi() {
		String ssid = WifiUtils.getWifiSSID(this);
		Boolean isWifi = WifiUtils.isWifiConnected(this);
		if (ssid != null && isWifi) {
			mLlSsidAndPasswd.setVisibility(View.VISIBLE);
			mLlWifiNot.setVisibility(View.GONE);
			mBtOpenWifi.setVisibility(View.GONE);
			mBtnNext.setVisibility(View.VISIBLE);
			mVWifiSsid.setText(ssid);
			mEtPassword.requestFocus();
			getPassword(ssid);
		} else {
			mLlSsidAndPasswd.setVisibility(View.GONE);
			mLlWifiNot.setVisibility(View.VISIBLE);
			mBtOpenWifi.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initEvents() {
		InputMethodUtils.hideInputMethodWindow(this, mVRoot);
		mVBack.setOnClickListener(this);
		mVWifiModuleRoot.setOnClickListener(this);
		mBtOpenWifi.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mVShowPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShowPassword) {
					mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mVShowPassword.setImageResource(R.drawable.icon_eyes_close);
				} else {
					mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					mVShowPassword.setImageResource(R.drawable.icon_eyes);
				}
				Editable etext = mEtPassword.getText();
				Selection.setSelection(etext, etext.length());
				isShowPassword = !isShowPassword;
			}
		});
		mEtPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				changeEditDelete(s.toString());
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.wifi_module_root:
				showWifiModuleDialog();
				break;
			case R.id.bt_open_wifi:
				WifiUtils.openWifiSettingActivity(this);
				break;
			case R.id.btn_next:
				connectDevice();
				break;
			default:
				break;
		}
	}

	private void connectDevice() {
		String ssid = mVWifiSsid.getText().toString().trim();
		String password = mEtPassword.getText().toString();
		String productType = mEtProductType.getText().toString();
		String name = mEtProductName.getText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, R.string.input_wifi_password_please, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(productType)) {
			Toast.makeText(this, R.string.input_product_type_please, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, R.string.input_product_name_please, Toast.LENGTH_SHORT).show();
			return;
		}
		rememberPassword(ssid, password);

		DeviceTable currentDevice = ApplicationManager.getInstance().getCurrentControlDevice();
		int type = Integer.valueOf(mEtProductType.getText().toString().trim());
		currentDevice.setType(type);
		currentDevice.setDeviceName(name);
		Bundle extras = new Bundle();
		extras.putString(DeviceConnectingActivity.EXTRAS_SSID, ssid);
		extras.putString(DeviceConnectingActivity.EXTRAS_PASSWD, password);
		Intent intent = new Intent(DeviceConnectionActivity.this, DeviceConnectingActivity.class);
		intent.putExtras(extras);
		startActivity(intent);
	}
	
	private void rememberPassword(String ssid, String password) {
		SharedPreferencesUtils.rememberPassword(this, ssid, password);
	}
	
	private void getPassword(String ssid) {
		String password = SharedPreferencesUtils.getPassword(this, ssid);
		mEtPassword.setText(password);
		changeEditDelete(password);
	}
	
	private void changeEditDelete(String editText) {
		if (editText != null && editText.length() > 0) {
			mVShowPassword.setVisibility(View.VISIBLE);
		} else {
			mVShowPassword.setVisibility(View.VISIBLE);
		}
	}

	private void showWifiModuleDialog() {
		OnItemClickListener onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int module = DeviceManager.WIFI_HF;
				switch (position) {
					case 0:
						module = DeviceManager.WIFI_HF;
						break;
					case 1:
						module = DeviceManager.WIFI_LX;
						break;
					case 2:
						module = DeviceManager.WIFI_QK;
						break;
					case 3:
						module = DeviceManager.WIFI_MWR;
						break;
					case 4:
						module = DeviceManager.WIFI_6060;
						break;
					default:
						break;
				}
				SharedPreferencesUtils.editWifiModule(DeviceConnectionActivity.this, module);
				mVWifiModuleName.setText(mWifiModuleList.get(position));
			}
		};
		mChoiceListDialog = new ChoiceListDialog(this, mWifiModuleList, onItemClickListener);
		mChoiceListDialog.setTitle("选择WiFi模块");
		mChoiceListDialog.show();
	}

	@Override
	public void onNetworkStateReceive(Context context, Intent intent) {
		initWifi();
	}
}
