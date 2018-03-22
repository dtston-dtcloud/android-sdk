package com.dtston.demo.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dtston.demo.ApplicationManager;
import com.dtston.demo.R;
import com.dtston.demo.adapter.DeviceManagerAdapter;
import com.dtston.demo.dao.DeviceDao;
import com.dtston.demo.dao.UserDao;
import com.dtston.demo.db.DeviceTable;
import com.dtston.demo.db.UserTable;
import com.dtston.demo.dialog.ChoiceListDialog;
import com.dtston.demo.dialog.ConfirmDialogWithoutTitle;
import com.dtston.demo.utils.Activitystack;
import com.dtston.demo.utils.Debugger;
import com.dtston.demo.utils.DownloadFileTask;
import com.dtston.demo.utils.FileUtil;
import com.dtston.demo.utils.SharedPreferencesUtils;
import com.dtston.demo.utils.ToastUtils;
import com.dtston.demo.view.PullDownListView;
import com.dtston.dtcloud.DeviceManager;
import com.dtston.dtcloud.UserManager;
import com.dtston.dtcloud.push.DTConnectedDevice;
import com.dtston.dtcloud.push.DTDeviceState;
import com.dtston.dtcloud.push.DTIDeviceConnectCallback;
import com.dtston.dtcloud.push.DTIDeviceStateCallback;
import com.dtston.dtcloud.push.DTIOperateCallback;
import com.dtston.dtcloud.result.AppVersionResult;
import com.dtston.dtcloud.result.BaseResult;
import com.dtston.dtcloud.result.UserDevicesResult;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/8/12.
 */
public class MainActivity extends BaseActivity implements DTIDeviceStateCallback,
        PullDownListView.OnPullDownListener {

    private static final int REQUEST_SCAN_ADD = 0x0001;
    public static final String EXTRA_MAC = "mac";
    public static final String EXTRA_TYPE = "dev_type";
    private final String TAG = this.getClass().getSimpleName();

    private View mVAdd;
    private View mVLeftMenu;
    private PullDownListView mVDeviceList;
    private LinearLayout mVEmptyView;
    private DeviceManagerAdapter mAdapter;

    private Button mVAddDevice;
    private List<DeviceTable> mDeviceList = new ArrayList<>();
    private ChoiceListDialog mChoiceListDialog;
    private List<String> mAddDeviceMethods = new ArrayList<>();
    private ChoiceListDialog mMenuListDialog;
    private List<String> mMenus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manager);
        initViews();
        initEvents();
        initData();
        onRefresh();
        DeviceManager.registerDeviceStateCallback(this);

        //极光
        String registrationID = JPushInterface.getRegistrationID(this);
        if (!TextUtils.isEmpty(registrationID))
            DeviceManager.uploadJpushToken(registrationID, new DTIOperateCallback<BaseResult>() {
                @Override
                public void onSuccess(BaseResult baseResult, int i) {
                    System.out.println("JPush uploud token success");
                }

                @Override
                public void onFail(Object o, int i, String s) {
                    System.out.println("JPush uploud token failed");
                }
            });
    }

    protected void initViews() {
        mVAdd = findViewById(R.id.ivAdd);
        mVLeftMenu = findViewById(R.id.leftMenu);
        mVDeviceList = (PullDownListView) findViewById(R.id.deviceList);

        mVEmptyView = (LinearLayout) findViewById(R.id.empty);
        mVAddDevice = (Button) mVEmptyView.findViewById(R.id.addDevice);
        LinearLayout loadingView = (LinearLayout) findViewById(R.id.loading);
        mVDeviceList.setEmptyView(loadingView);

        mVDeviceList.setPullLoadEnable(false);
        mVDeviceList.setPullRefreshEnable(true);
        mVDeviceList.setOnPullDownListener(this);
    }

    protected void initEvents() {
        mVAdd.setOnClickListener(this);
        mVLeftMenu.setOnClickListener(this);
        mVAddDevice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAdd:
            case R.id.addDevice:
//                gprsDataHandlerTest();
                showAddDeviceDialog();
                break;
            case R.id.leftMenu:
                showMenuDialog();
            default:
                break;
        }
    }

    private void initData() {
        mAddDeviceMethods.add("本地配网添加");
        mAddDeviceMethods.add("本地搜索添加");
        mAddDeviceMethods.add("扫描添加");
        mAddDeviceMethods.add("gprs添加");
        mAddDeviceMethods.add("WiFi设备添加");
        mAdapter = new DeviceManagerAdapter(this, mDeviceList);
        mVDeviceList.setAdapter(mAdapter);
        mVDeviceList.setOnItemClickListener(mAdapter);
        mVDeviceList.setOnItemLongClickListener(mAdapter);

        mMenus.add("常见问题");
        mMenus.add("用户反馈");
        mMenus.add("APP版本检查");
        mMenus.add("退出");
    }

    public void onResume() {
        super.onResume();
        loadDevices();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void addDeviceByMatch() {
        long time = System.currentTimeMillis();
        String deviceName = "设备" + time;
        String mac = "-1" + time;
        int type = 1101;

        UserTable currentUser = ApplicationManager.getInstance().getCurrentUser();
        DeviceTable deviceTable = new DeviceTable();
        deviceTable.setBind(DeviceTable.BIND_NON);
        deviceTable.setDeviceName(deviceName);
        deviceTable.setMac(mac);
        deviceTable.setType(type);
        deviceTable.setUid(currentUser.getUid());

        ApplicationManager.getInstance().setCurrentControlDevice(deviceTable);
        Intent intent = new Intent(this, DeviceConnectionActivity.class);
        startActivity(intent);
    }

    private void addDeviceBySearch() {
        Intent intent = new Intent(this, LanDeviceListActivity.class);
        startActivity(intent);
    }

    private void addDeviceByScan() {
//        Intent intent = new Intent(this, ScanActivity.class);
//        startActivityForResult(intent, REQUEST_SCAN_ADD);
    }

    private void showAddDeviceDialog() {
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        addDeviceByMatch();
                        break;
                    case 1:
                        addDeviceBySearch();
                        break;
                    case 2:
                        addDeviceByScan();
//                        addGprs();
                        break;
                    case 3:
                        showGprsDialog();
                        break;
                    case 4:
                        showWiFiDeviceDialog();
                        break;
                    default:
                        break;
                }
            }
        };
        mChoiceListDialog = new ChoiceListDialog(this, mAddDeviceMethods, onItemClickListener);
        mChoiceListDialog.setTitle("添加设备");
        mChoiceListDialog.show();
    }

    private void showMenuDialog() {
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, CommonQuestionActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                        break;
                    case 2:
                        checkAppUpdate();
                        break;
                    case 3:
                        showQuitDialog();
                        break;
                    default:
                        break;
                }
            }
        };
        mMenuListDialog = new ChoiceListDialog(this, mMenus, onItemClickListener);
        mMenuListDialog.show();
    }

    private void addGprs() {
        devMac = "862180034610813";//862180034610813
        devType = 2288;
    }

    private void setUserInfo() {
        String nickName = "test";
        UserManager.setUserInfo(nickName, null, null, null, null, new DTIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                System.out.println("setUserInfo onSuccess : " + o.toString());
            }

            @Override
            public void onFail(Object o, int i, String s) {
                System.out.println("setUserInfo onFail : " + o.toString());
            }
        });
    }

    private void changeUserAvatar(String filePath) {
        if (filePath.startsWith("file:///")) {
            filePath = filePath.substring("file://".length());
        }
        //filePath = "/storage/emulated/0/11.png";
        try {
            UserManager.changeUserAvatar(filePath, new DTIOperateCallback() {
                @Override
                public void onSuccess(Object o, int i) {
                    System.out.println("changeUserAvatar onSuccess : " + o.toString());
                }

                @Override
                public void onFail(Object o, int i, String s) {
                    System.out.println("changeUserAvatar onFail : " + o.toString());
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void shareDevice(String dataId, String mobile) {
        DeviceManager.shareDevice(dataId, mobile, new DTIOperateCallback() {
            @Override
            public void onSuccess(Object var1, int var2) {
                System.out.println("onSuccess : " + var1.toString());
            }

            @Override
            public void onFail(Object var1, int var2, String var3) {
                System.out.println("onFail : " + var1.toString());
            }
        });
    }

    private void renameDevice(String dataId, String name) {
        DeviceManager.renameDevice(dataId, name, new DTIOperateCallback() {
            @Override
            public void onSuccess(Object var1, int var2) {
                System.out.println("onSuccess : " + var1.toString());
            }

            @Override
            public void onFail(Object var1, int var2, String var3) {
                System.out.println("onFail : " + var1.toString());
            }
        });
    }

    private void postGetBindingDevice() {
        DeviceManager.getBindDevices(new DTIOperateCallback<UserDevicesResult>() {
            @Override
            public void onSuccess(UserDevicesResult var1, int var2) {
                if (isFinishing()) {
                    return;
                }
                if (var1.getErrcode() == 0) {
                    DeviceTable deviceTable = null;
                    UserTable currentUser = ApplicationManager.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();
                    JSONObject deviceInfo;
                    String name;
                    String mac;
                    String type;
                    String deviceId;
                    String dataId;
                    List<UserDevicesResult.DataBean> data = var1.getData();
                    int count = data.size();
                    for (int i = 0; i < count; i++) {
                        name = data.get(i).getName();
                        mac = data.get(i).getMac();
                        type = data.get(i).getType();
                        deviceId = data.get(i).getDevice_id();
                        dataId = data.get(i).getId();

                        boolean exist = true;
                        deviceTable = DeviceDao.getInstance().getDeviceByMac(uid, mac);
                        if (deviceTable == null) {
                            deviceTable = new DeviceTable();
                            deviceTable.setMac(mac);
                            exist = false;
                        }
                        deviceTable.setBind(DeviceTable.BIND_HAS);
                        deviceTable.setUid(currentUser.getUid());
                        deviceTable.setType(Integer.valueOf(type));
                        deviceTable.setDeviceId(deviceId);
                        deviceTable.setDeviceName(name);
                        deviceTable.setDataId(dataId);
                        if (exist) {
                            int update = DeviceDao.getInstance().update(deviceTable);
                            Debugger.logD(TAG, "deviceTable update id is " + update);
                        } else {
                            int insert = DeviceDao.getInstance().insert(deviceTable);
                            Debugger.logD(TAG, "deviceTable insert id is " + insert);
                        }
                    }
                } else if (var1.getErrcode() == 400011) {
                    ToastUtils.showToast("没有绑定设备");
                }

                loadDevices();
            }

            @Override
            public void onFail(Object var1, int var2, String var3) {
                String msg = null;
//                if (var1 instanceof NoConnectionError) {
//                    msg = "刷新失败，请检查网络是否可用";
//                } else if (var1 instanceof TimeoutError) {
//                    msg = "刷新失败，请检查网络是否可用";
//                } else {
//                    msg = "刷新失败,网络繁忙,请稍候再试";
//                }
                ToastUtils.showToast(var1.toString());
                loadDevices();
            }
        });
    }

    private void loadDevices() {
        UserTable currentUser = ApplicationManager.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getUid() == null) {
            finish();
            return;
        }
        List<DeviceTable> deviceTables = DeviceDao.getInstance().getDeviceByUid(currentUser.getUid());
        if (deviceTables != null) {
            mDeviceList.clear();
            mDeviceList.addAll(deviceTables);
        }
        mAdapter.notifyDataSetChanged();
        mVDeviceList.setEmptyView(mVEmptyView);
        mVDeviceList.stopRefresh();

        ApplicationManager.getInstance().setDeviceList(mDeviceList);
    }

    @Override
    public void onDestroy() {
        DeviceManager.unregisterDeviceStateCallback(this);
        super.onDestroy();
    }

    @Override
    public void onDeviceOnlineNotice(DTDeviceState deviceState) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onDeviceOnlineNotice: mac:" + deviceState.getMac() + " state:" + deviceState.isOnline());
        stateRefresh();
    }

    @Override
    public void onDeviceOfflineNotice(DTDeviceState deviceState) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onDeviceOfflineNotice: mac:"+deviceState.getMac()+" state:"+deviceState.isOnline());
        stateRefresh();
    }

    private void stateRefresh(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onRefresh() {
        postGetBindingDevice();
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }

    private void showQuitDialog() {
        ConfirmDialogWithoutTitle confirmDialogWithoutTitle = new ConfirmDialogWithoutTitle(this, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quit();
            }
        });
        confirmDialogWithoutTitle.setContent("是否退出账号");
        confirmDialogWithoutTitle.show();
    }

    private void quit() {
        UserTable currentUser = ApplicationManager.getInstance().getCurrentUser();
        currentUser.setType(UserTable.TYPE_OTHER_USER);
        int update = UserDao.getInstance().update(currentUser);
        Debugger.logD("quit", "quit update id is " + update);

        UserManager.logoutUser();

        SharedPreferencesUtils.editExitUserName(this, currentUser.getUserName());
        ApplicationManager.getInstance().setCurrentUser(null);
        Activitystack.finishAll();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void showDeviceNameEditDlg() {
        final EditText et = new EditText(this);

        new AlertDialog.Builder(this).setTitle("输入设备名")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String deviceName = et.getText().toString();
                        if (deviceName.equals("")) {
                            Toast.makeText(getApplicationContext(), "设备名不能为空！" + deviceName, Toast.LENGTH_LONG).show();
                        } else {
                            DeviceManager.bindGprsDevBySearch(devMac, devType, deviceName, new DTIDeviceConnectCallback() {
                                @Override
                                public void onSuccess(DTConnectedDevice connectedDevice) {
                                    DeviceTable currentControlDevice = ApplicationManager.getInstance().getCurrentControlDevice();
                                    if (currentControlDevice == null)
                                        currentControlDevice = new DeviceTable();
                                    currentControlDevice.setMac(connectedDevice.getMac());
                                    currentControlDevice.setDeviceName(deviceName);
                                    currentControlDevice.setType(Integer.valueOf(connectedDevice.getType()));
                                    currentControlDevice.setDataId(connectedDevice.getDataId());
                                    DeviceDao.getInstance().insert(currentControlDevice);
                                    loadDevices();
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFail(int errcode, String errmsg) {
                                    ToastUtils.showToast(errmsg);
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void showGprsDialog() {
        View view = View.inflate(this, R.layout.dialog_gprs_add, null);
        final EditText etImei = (EditText) view.findViewById(R.id.tv_imei);
        final EditText etType = (EditText) view.findViewById(R.id.tv_type);
        final EditText etName = (EditText) view.findViewById(R.id.tv_name);
//        etImei.setText("112233445566778");
//        etType.setText("2291");
        new AlertDialog.Builder(this).setTitle("提示")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String imeiStr = etImei.getText().toString();
                        final String typeStr = etType.getText().toString();
                        final String deviceName = etName.getText().toString();
                        if (imeiStr.equals("")) {
                            Toast.makeText(getApplicationContext(), "imei不能为空！" + deviceName, Toast.LENGTH_LONG).show();
                        } else if (typeStr.equals("")) {
                            Toast.makeText(getApplicationContext(), "设备类型不能为空！" + deviceName, Toast.LENGTH_LONG).show();
                        } else if (deviceName.equals("")) {
                            Toast.makeText(getApplicationContext(), "设备名不能为空！" + deviceName, Toast.LENGTH_LONG).show();
                        } else {
                            DeviceManager.bindGprsDevBySearch(imeiStr, Integer.parseInt(typeStr), deviceName, new DTIDeviceConnectCallback() {
                                @Override
                                public void onSuccess(DTConnectedDevice connectedDevice) {
                                    DeviceTable currentControlDevice = ApplicationManager.getInstance().getCurrentControlDevice();
                                    UserTable currentUser = ApplicationManager.getInstance().getCurrentUser();
                                    if (currentUser == null) return;
                                    if (currentControlDevice == null)
                                        currentControlDevice = new DeviceTable();
                                    currentControlDevice.setMac(connectedDevice.getMac());
                                    currentControlDevice.setDeviceName(deviceName);
                                    currentControlDevice.setType(Integer.valueOf(connectedDevice.getType()));
                                    currentControlDevice.setDataId(connectedDevice.getDataId());
                                    currentControlDevice.setBind(DeviceTable.BIND_HAS);
                                    currentControlDevice.setUid(currentUser.getUid());
                                    currentControlDevice.setDeviceId(connectedDevice.getDeviceId());
                                    DeviceDao.getInstance().insert(currentControlDevice);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadDevices();
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }

                                @Override
                                public void onFail(int errcode, String errmsg) {
                                    ToastUtils.showToast(errmsg);
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(false)
                .show();

    }

    public void showWiFiDeviceDialog() {
        View view = View.inflate(this, R.layout.dialog_wifi_add, null);
        final EditText etMac = (EditText) view.findViewById(R.id.tv_mac);
        final EditText etType = (EditText) view.findViewById(R.id.tv_type);
        final EditText etName = (EditText) view.findViewById(R.id.tv_name);
        new AlertDialog.Builder(this).setTitle("提示")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String macStr = etMac.getText().toString().toUpperCase();
                        final String typeStr = etType.getText().toString();
                        final String deviceName = etName.getText().toString();
                        if (macStr.equals("") || macStr.length() != 12) {
                            Toast.makeText(getApplicationContext(), "设备MAC格式不对，12位字符串！" + deviceName, Toast.LENGTH_LONG).show();
                        } else if (typeStr.equals("")) {
                            Toast.makeText(getApplicationContext(), "设备类型不能为空！" + deviceName, Toast.LENGTH_LONG).show();
                        } else if (deviceName.equals("")) {
                            Toast.makeText(getApplicationContext(), "设备名不能为空！" + deviceName, Toast.LENGTH_LONG).show();
                        } else {
                            DeviceManager.bindDeviceByMatchingNetwork(macStr, typeStr, "192.168.1.1", deviceName, new DTIDeviceConnectCallback() {
                                @Override
                                public void onSuccess(DTConnectedDevice connectedDevice) {
                                    DeviceTable currentControlDevice = ApplicationManager.getInstance().getCurrentControlDevice();
                                    UserTable currentUser = ApplicationManager.getInstance().getCurrentUser();
                                    if (currentUser == null) return;
                                    if (currentControlDevice == null)
                                        currentControlDevice = new DeviceTable();
                                    currentControlDevice.setMac(connectedDevice.getMac());
                                    currentControlDevice.setDeviceName(deviceName);
                                    currentControlDevice.setType(Integer.valueOf(connectedDevice.getType()));
                                    currentControlDevice.setDataId(connectedDevice.getDataId());
                                    currentControlDevice.setBind(DeviceTable.BIND_HAS);
                                    currentControlDevice.setUid(currentUser.getUid());
                                    currentControlDevice.setDeviceId(connectedDevice.getDeviceId());
                                    DeviceDao.getInstance().insert(currentControlDevice);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadDevices();
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }

                                @Override
                                public void onFail(int errcode, String errmsg) {
                                    ToastUtils.showToast(errmsg);
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(false)
                .show();

    }

    private String devMac = "";
    private int devType = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN_ADD) {
            if (RESULT_OK == resultCode) {
                devMac = data.getStringExtra(EXTRA_MAC);
                devType = data.getIntExtra(EXTRA_TYPE, 0);
                showDeviceNameEditDlg();
            } else {

            }
        }

    }

    private AlertDialog adApkUpdate;
    private void checkAppUpdate() {
        DeviceManager.checkAppVersion(new DTIOperateCallback<AppVersionResult>() {
            @Override
            public void onSuccess(final AppVersionResult appVersionResult, int i) {
                if (1 == appVersionResult.getData().getUpdate()) {
                    ToastUtils.showToast(R.string.is_the_latest_version);
                } else {
                    String updateContent = appVersionResult.getData().getReason().replace("\\n", "\n");
                    adApkUpdate = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.discover_new_version)
                            .setMessage(updateContent)
                            .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adApkUpdate.cancel();
                                }
                            })
                            .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adApkUpdate.cancel();
                                    apkUrl = appVersionResult.getData().getUrl_direct();
                                    requestPermissions();
                                }
                            })
                            .setCancelable(false)
                            .create();
                    adApkUpdate.show();
                }
            }

            @Override
            public void onFail(Object o, int i, String s) {
                ToastUtils.showToast(s);
            }
        });
    }

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOAD_APK = 1;
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOAD_APK);
        } else {
            downloadApk();
        }
    }

    private String apkUrl;
    private ProgressDialog pdApkDownload;
    private DownloadFileTask downloadFileTask;
    private void downloadApk() {
        downloadFileTask = new DownloadFileTask();
        downloadFileTask.setListener(new DownloadFileTask.OnDownListener() {
            @Override
            public void onProgress(int progress) {
                pdApkDownload.setProgress(progress);
            }

            @Override
            public void onResult(String saveFilePath) {
                pdApkDownload.cancel();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File(saveFilePath)),"application/vnd.android.package-archive");
                startActivity(intent);
            }

            @Override
            public void onError(int error) {
                pdApkDownload.cancel();
                if (error == DownloadFileTask.ERROR_FILE_NOT_FOUND) {
                    ToastUtils.showToast("没有发现下载文件，请稍候再试");
                } else {
                    ToastUtils.showToast("网络错误，请稍候再试");
                }
            }
        });

        pdApkDownload = new ProgressDialog(this);
        pdApkDownload.setMax(100);
        pdApkDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdApkDownload.setIcon(null);
        pdApkDownload.setMessage(getString(R.string.downloading));
        pdApkDownload.setCancelable(false);
        pdApkDownload.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pdApkDownload.cancel();
                downloadFileTask.cancel();
            }
        });
        pdApkDownload.show();

        File dirFile= FileUtil.createFileDir(FileUtil.DOWNLOAD_PATH);
        File file = new File(dirFile, FileUtil.APK_NAME);
        downloadFileTask.execute(apkUrl, file.getAbsolutePath());
    }

}
