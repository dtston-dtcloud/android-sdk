package com.dtston.demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dtston.demo.ApplicationManager;
import com.dtston.demo.R;
import com.dtston.demo.adapter.LanDeviceAdapter;
import com.dtston.demo.dao.DeviceDao;
import com.dtston.demo.db.UserTable;
import com.dtston.demo.utils.ToastUtils;
import com.dtston.demo.view.PullDownListView;
import com.dtston.dtcloud.DeviceManager;
import com.dtston.dtcloud.push.DTConnectedDevice;
import com.dtston.dtcloud.push.DTIDeviceConnectCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
public class LanDeviceListActivity extends BaseActivity implements PullDownListView.OnPullDownListener {

    private static final int REQUEST_SCAN_ADD = 0x0001;
    public static final String EXTRA_MAC = "mac";
    public static final String EXTRA_TYPE = "dev_type" ;
    private final String TAG = this.getClass().getSimpleName();

    private View mLeftMenu;
    private PullDownListView mVDeviceList;
    private TextView mVEmptyTv;
    private LanDeviceAdapter mAdapter;

    private List<DTConnectedDevice> mConnectedDeviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_lan);
        initViews();
        initEvents();
        initData();
        onRefresh();
    }

    protected void initViews() {
        mLeftMenu = findViewById(R.id.leftMenu);
        mVDeviceList = (PullDownListView) findViewById(R.id.deviceList);

        mVEmptyTv = (TextView) findViewById(R.id.tv_empty);
        mVDeviceList.setEmptyView(mVEmptyTv);

        mVDeviceList.setPullLoadEnable(false);
        mVDeviceList.setPullRefreshEnable(true);
        mVDeviceList.setOnPullDownListener(this);
    }

    protected void initEvents() {
        mLeftMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftMenu:
                finish();
                break;
            default:
                break;
        }
    }

    private void initData() {
        mConnectedDeviceList.clear();
        mAdapter = new LanDeviceAdapter(this, mConnectedDeviceList);
        mVDeviceList.setAdapter(mAdapter);
        mVDeviceList.setOnItemClickListener(mAdapter);
        addDeviceBySearch();
    }

    private void addDeviceBySearch() {
        DeviceManager.startSearchDevice(0, new DTIDeviceConnectCallback() {
            @Override
            public void onSuccess(final DTConnectedDevice connectedDevice) {
                UserTable currentUser = ApplicationManager.getInstance().getCurrentUser();
                if(DeviceDao.getInstance().getDeviceByMac(currentUser.getUid(), connectedDevice.getMac()) != null) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (DTConnectedDevice dtConnectedDevice : mConnectedDeviceList) {
                            if(dtConnectedDevice.getMac().equals(connectedDevice.getMac())) {
                                return;
                            }
                        }
                        mConnectedDeviceList.add(connectedDevice);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                mVDeviceList.stopRefresh();
            }

            @Override
            public void onFail(int errcode, String errmsg) {
                ToastUtils.showToast("本地搜索失败！");
            }
        });
    }

    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onRefresh() {
        //DeviceManager.stopSearchDevice();
        mConnectedDeviceList.clear();
        addDeviceBySearch();
    }

    @Override
    public void onLoadMore() {

    }
}
