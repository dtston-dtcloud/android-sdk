package com.dtston.demo.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.dtston.demo.ApplicationManager;
import com.dtston.demo.R;
import com.dtston.demo.common.Constans;
import com.dtston.demo.db.DeviceTable;
import com.dtston.demo.dialog.ChoiceListDialog;
import com.dtston.demo.dialog.ConfirmDialogWithoutTitle;
import com.dtston.demo.dialog.NetworkProgressDialog;
import com.dtston.demo.thirdmodify.ShuiShengHelper;
import com.dtston.demo.utils.InputMethodUtils;
import com.dtston.demo.utils.StringUtils;
import com.dtston.demo.utils.ToastUtils;
import com.dtston.dtcloud.DeviceManager;
import com.dtston.dtcloud.push.DTDeviceState;
import com.dtston.dtcloud.push.DTFirmwareUpgradeResult;
import com.dtston.dtcloud.push.DTIDeviceMessageCallback;
import com.dtston.dtcloud.push.DTIDeviceStateCallback;
import com.dtston.dtcloud.push.DTIOperateCallback;
import com.dtston.dtcloud.push.DTProtocolVersion;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.dtston.demo.common.Constans.isTestGprs;

public class DeviceControlActivity extends BaseActivity implements DTIDeviceMessageCallback, DTIDeviceStateCallback {
    private static final String TAG                    = "DeviceControlActivity";
    public static final  String EXTRA_GPRS_HTTP_FLAG   = "extra_gprs_http";
    public static final  int    WHAT_INTERVAL_SEND_CMD = 1;
    private View     mVRoot;
    private View     mVBack;
    private TextView mVTitle;
    private View     mVControlRoot;
    private View     mVNonControl;
    private TextView mVState;
    private EditText mVType;
    private EditText mVCmd;
    private TextView mVSend;
    private TextView mVResult;
    private View     mVClear;
    private TextView shuishengReset;
    private View     interval_root;
    private EditText time_interval;
    private View     tcp_count_root;
    private TextView tvTcpCount;
    private TextView tvDataSouce;
    private TextView proVersionTv;
    private TextView firmware_upgrade;

    private DeviceTable mCurrentDevice;

    private StringBuffer     mResultBuffer = new StringBuffer();
    private SimpleDateFormat mFormatter    = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    private MyHandler sendIntervalHandler;
    private int       interval;
    private ChoiceListDialog mChoiceListDialog;

    private NetworkProgressDialog mVProgressDialog;

    private int tcpSendTotalCount = 0; //发送总次数
    private int tcpSendOkCount = 0; //发送成功次数
    private int tcpReceiveTotalCount = 0; //接收总次数

    private static final String PROTOCOL_TYPE_SECOND = "第二套";
    private static final String PROTOCOL_TYPE_THIRD = "第三套";
    private static final String PROTOCOL_TYPE_THIRD_ALIVE = "第三套长连接";
    private static final String PROTOCOL_TYPE_HTTP_GPRS = "HTTP GPRS";
    private static final String PROTOCOL_TYPE_MQTT_GPRS = "MQTT GPRS";

    private static final String[] protocolMenuList = new String[]{PROTOCOL_TYPE_SECOND, PROTOCOL_TYPE_THIRD,
            PROTOCOL_TYPE_THIRD_ALIVE, PROTOCOL_TYPE_HTTP_GPRS, PROTOCOL_TYPE_MQTT_GPRS};
    private String currentProtocol = PROTOCOL_TYPE_THIRD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentDevice = ApplicationManager.getInstance().getCurrentControlDevice();
        setContentView(R.layout.activity_control);
        DeviceManager.registerDeviceStateCallback(this);
        DeviceManager.registerDeviceMessageCallback(this);
        initViews();
        initEvents();
        if (mCurrentDevice == null) return;
        mVTitle.setText("(" + mCurrentDevice.getMac() + ")");

        sendIntervalHandler = new MyHandler(this);
    }

    @Override
    protected void initViews() {
        mVRoot = findViewById(R.id.root);
        mVBack = findViewById(R.id.back);
        mVTitle = (TextView) findViewById(R.id.title);
        mVControlRoot = findViewById(R.id.control_root);
        mVNonControl = findViewById(R.id.nonControl);
        mVState = (TextView) findViewById(R.id.state);
        mVType = (EditText) findViewById(R.id.msg_type);
        mVCmd = (EditText) findViewById(R.id.cmd);
        mVSend = (TextView) findViewById(R.id.send);
        mVResult = (TextView) findViewById(R.id.result);
        mVClear = findViewById(R.id.clear);
        shuishengReset = (TextView) findViewById(R.id.shui_sheng_reset_filter);
        tvDataSouce = (TextView) findViewById(R.id.tv_data_source);
        proVersionTv = (TextView) findViewById(R.id.proVersion);
        firmware_upgrade = (TextView) findViewById(R.id.firmware_upgrade);

        proVersionTv.setText(currentProtocol);
        if (Constans.isTestShuiSheng) {
            shuishengReset.setVisibility(View.VISIBLE);
        }
        initIntervalSendCmd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeviceManager.unregisterDeviceStateCallback(this);
        DeviceManager.unregisterDeviceMessageCallback(this);
        sendIntervalHandler.removeMessages(WHAT_INTERVAL_SEND_CMD);
    }

    @Override
    protected void initEvents() {
        InputMethodUtils.hideInputMethodWindow(this, mVRoot);
        mVBack.setOnClickListener(this);
        mVSend.setOnClickListener(this);
        mVClear.setOnClickListener(this);
        proVersionTv.setOnClickListener(this);
        shuishengReset.setOnClickListener(this);
        firmware_upgrade.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentDevice == null) mCurrentDevice = new DeviceTable();
        DTDeviceState currentDeviceState = DeviceManager.getDevicesState(mCurrentDevice.getMac());
        if ((currentDeviceState != null && currentDeviceState.isOnline()) || isTestGprs) {
            mVControlRoot.setVisibility(View.VISIBLE);
            mVNonControl.setVisibility(View.GONE);
        } else {
            mVControlRoot.setVisibility(View.GONE);
            mVNonControl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.send:
                checkEdittextState();
//                fuckTest();
                break;
            case R.id.clear:
                clear();
                break;
            case R.id.shui_sheng_reset_filter:
                ShuiShengHelper.resetFilter(mCurrentDevice.getMac());
                break;
            case R.id.proVersion:
                showProtocolVersionDialog();
                break;
            case R.id.firmware_upgrade:
                firmwareCheck(mCurrentDevice);
                break;
            default:
                break;
        }
    }

    private void fuckTest() {
        DeviceManager.sendMessage(mCurrentDevice.getMac(), "100f", "00",dtiOperateCallback );
//        DeviceManager.sendMessage(mCurrentDevice.getMac(), "100a", "00",dtiOperateCallback );
    }

    private void clear() {
        mResultBuffer.delete(0, mResultBuffer.length());
        mVResult.setText("");
    }

    private void initIntervalSendCmd() {
        if (Constans.isIntervalSend) {
            interval_root = findViewById(R.id.interval_root);
            time_interval = (EditText) findViewById(R.id.time_interval);
            interval_root.setVisibility(View.VISIBLE);

            tcp_count_root = findViewById(R.id.tcp_count_root);
            tvTcpCount = (TextView) findViewById(R.id.tv_tcp_count);
            tcp_count_root.setVisibility(View.VISIBLE);
        }
    }

    private void sendInterval() {


    }

    private void checkEdittextState() {
        String msgType  = mVType.getText().toString();
        String uartData = mVCmd.getText().toString();
        if (uartData.length() % 2 != 0) {
            ToastUtils.showToast("16进制字符必须为双数");
            return;
        }
//        msgType = "2258";
//        uartData = "1212121212";
        if (mCurrentDevice == null) mCurrentDevice = new DeviceTable();
//        mCurrentDevice.setMac("123456789012345");
        if (StringUtils.isEmpty(msgType) || msgType.length() != 4) {
            ToastUtils.showToast("功能码长度为4");
            return;
        }
        if (Constans.isIntervalSend) {
            String intervalStr = time_interval.getText().toString();
            if (TextUtils.isEmpty(intervalStr)) {
                ToastUtils.showToast("间隔时间不能为空");
                return;
            }
            if ("发送".equals(mVSend.getText())) {
                tcpSendTotalCount = 0;
                tcpReceiveTotalCount = 0;
                try {
                    interval = Integer.parseInt(intervalStr);
                    if (interval != 0) {
                        sendIntervalHandler.sendEmptyMessageDelayed(WHAT_INTERVAL_SEND_CMD, interval);
                        mVSend.setText("停止发送");
                    }
                    send();

                } catch (Exception e) {
                    ToastUtils.showToast("请输入数字");
                }
            } else {
                mVSend.setText("发送");
                sendIntervalHandler.removeMessages(WHAT_INTERVAL_SEND_CMD);
            }
        } else {
            send();
        }

    }

    private void send() {
        Log.d(TAG, "send() called");

        String msgType  = mVType.getText().toString();
        String uartData = mVCmd.getText().toString();
        String mac      = mCurrentDevice.getMac();
        if (currentProtocol.equals(PROTOCOL_TYPE_HTTP_GPRS)) {
            DeviceManager.sendGprsHttpMessage(mCurrentDevice.getMac(), msgType, uartData,dtiOperateCallback);
        } else if (currentProtocol.equals(PROTOCOL_TYPE_THIRD_ALIVE)) {
            DeviceManager.sendAliveMessage(mCurrentDevice.getMac(), msgType, uartData, getProtocolVersion(), dtiOperateCallback);
        } else {
            DeviceManager.sendMessage(mCurrentDevice.getMac(), msgType, uartData, getProtocolVersion(), dtiOperateCallback);
        }

    }

    public void receiveData(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mResultBuffer.length() > 0) {
                    mResultBuffer.append("\n");
                }
                String strTime = mFormatter.format(new Date());
//				mResultBuffer.append(strTime + "  ");
                mResultBuffer.append(data);
                mVResult.setText(mResultBuffer.toString());
            }
        });
    }

    public void deviceOffline() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isTestGprs) return;
                mVControlRoot.setVisibility(View.GONE);
                mVNonControl.setVisibility(View.VISIBLE);
                mVState.setText("设备离线了");
            }
        });
    }

    public void deviceOnline() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVControlRoot.setVisibility(View.VISIBLE);
                mVNonControl.setVisibility(View.GONE);
                mResultBuffer.delete(0, mResultBuffer.length());
                mVResult.setText("");
            }
        });
    }


    @Override
    public void onMessageReceive(String mac, String msgType, byte[] msgBody) {
        if (mCurrentDevice.getMac().equals(mac)) {
            String msg = StringUtils.bytesToHexString(msgBody);
            receiveData(msgType + " : " + msg);
            tvDataSouce.setText("");
        }
    }

    public void onMessageReceive(String mac, String msgType, byte[] msgBody, String msgSource) {
        if (mCurrentDevice.getMac().equals(mac)) {
            String msg = StringUtils.bytesToHexString(msgBody);
            receiveData(msgSource + " --- " +msgType + " : " + msg);
            tvDataSouce.setText(msgSource);
        }
    }

    @Override
    public void onDeviceOnlineNotice(DTDeviceState deviceState) {
        deviceOnline();
    }

    @Override
    public void onDeviceOfflineNotice(DTDeviceState deviceState) {
        deviceOffline();
    }

    private static class MyHandler extends Handler {
        private WeakReference<DeviceControlActivity> paretant;

        public MyHandler(DeviceControlActivity activity) {
            paretant = new WeakReference<DeviceControlActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceControlActivity parentActivity = paretant.get();
            if (parentActivity != null && !parentActivity.isFinishing()) {
                switch (msg.what) {
                    case WHAT_INTERVAL_SEND_CMD:
                        parentActivity.send();
                        sendEmptyMessageDelayed(WHAT_INTERVAL_SEND_CMD, parentActivity.interval);
                        break;
                }
            }
        }
    }

    private DTIOperateCallback dtiOperateCallback = new DTIOperateCallback() {

        @Override
        public void onSuccess(Object data, int code) {
            Log.d(TAG, "onSuccess() called with: data = [" + data + "], code = [" + code + "]");
            //ToastUtils.showToast("发送成功");
            tcpSendTotalCount++;
            tcpReceiveTotalCount++;
            updateTcpCount();
        }

        @Override
        public void onFail(Object error, int code, String info) {
            Log.d(TAG, "onFail() called with: error = [" + error + "], code = [" + code + "], info = [" + info + "]");
            ToastUtils.showToast(error.toString());
            tcpSendTotalCount++;
            updateTcpCount();
        }
    };

    private void updateTcpCount() {
        if (tvTcpCount != null) {
            tvTcpCount.setText("tcp发送" + tcpSendTotalCount + "次，接收" + tcpReceiveTotalCount + "次");
        }
    }

    private void showProtocolVersionDialog() {
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                currentProtocol = protocolMenuList[position];
                proVersionTv.setText(currentProtocol);
            }
        };
        mChoiceListDialog = new ChoiceListDialog(this, Arrays.asList(protocolMenuList), onItemClickListener);

        mChoiceListDialog.setTitle("添加设备");
        mChoiceListDialog.show();
    }

    //固件版本检测
    private void firmwareCheck(final DeviceTable deviceTable) {
        showProgressDialog("正在检查固件版本");
        DeviceManager.firmwareUpgrade(deviceTable.getMac(), DTFirmwareUpgradeResult.TYPE_CHECK_VERSION, getProtocolVersion(),
                new DTIOperateCallback<DTFirmwareUpgradeResult>() {
                    @Override
                    public void onSuccess(final DTFirmwareUpgradeResult upgradeResult, int i) {
                        closeProgressDialog();
                        if (DTFirmwareUpgradeResult.RESULT_NO_NEW_VERSION == upgradeResult.getResult()) {
                            ToastUtils.showToast("已是最新固件版本");
                        } else if (DTFirmwareUpgradeResult.RESULT_HAS_NEW_VERSION == upgradeResult.getResult()) {
                            showFirmwareUpgradeDialog(deviceTable);
                        }
                    }

                    @Override
                    public void onFail(Object o, int i, String s) {
                        closeProgressDialog();
                        ToastUtils.showToast("固件版本检查失败: " + o.toString());
                    }
                });
    }

    private int getProtocolVersion() {
        int protocolVersion = DTProtocolVersion.TYPE_THIRD;
        if (currentProtocol.equals(PROTOCOL_TYPE_SECOND)) {
            protocolVersion = DTProtocolVersion.TYPE_SECOND;
        }
        return protocolVersion;
    }

    private void showProgressDialog(String text) {
        closeProgressDialog();
        mVProgressDialog = new NetworkProgressDialog(this, false, false);
        mVProgressDialog.setProgressText(text);
        mVProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (null != mVProgressDialog && mVProgressDialog.isShowing()) {
            mVProgressDialog.cancel();
        }
        mVProgressDialog = null;
    }
    private void showFirmwareUpgradeDialog(final DeviceTable deviceTable) {
        ConfirmDialogWithoutTitle dialog = new ConfirmDialogWithoutTitle(this, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog("正在下载固件");
                firmwarmUpgrade(deviceTable);
            }
        });
        dialog.setContent("是否更新固件");
        dialog.show();
    }

    //固件升级
    private void firmwarmUpgrade(DeviceTable deviceTable) {
        DeviceManager.firmwareUpgrade(deviceTable.getMac(), DTFirmwareUpgradeResult.TYPE_UPGRADE, getProtocolVersion(),
                new DTIOperateCallback<DTFirmwareUpgradeResult>() {
                    @Override
                    public void onSuccess(final DTFirmwareUpgradeResult upgradeResult, int i) {
                        if (DTFirmwareUpgradeResult.RESULT_DOWNLOAD_OK == upgradeResult.getResult()) {
                            closeProgressDialog();
                            ToastUtils.showToast("固件升级成功");
                        } else if (DTFirmwareUpgradeResult.RESULT_DOWNLOAD_FAILED == upgradeResult.getResult()) {
                            closeProgressDialog();
                            ToastUtils.showToast("固件升级失败");
                        } else if (DTFirmwareUpgradeResult.RESULT_DOWNLOAD_START == upgradeResult.getResult()
                                || DTFirmwareUpgradeResult.RESULT_DOWNLOADING == upgradeResult.getResult()) {
                            ToastUtils.showToast("正在下载固件");
                        }
                    }

                    @Override
                    public void onFail(Object o, int i, String s) {
                        closeProgressDialog();
                        ToastUtils.showToast("固件升级失败: " + s);
                    }
                });
    }

}
