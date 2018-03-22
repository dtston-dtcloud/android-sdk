package com.dtston.demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dtston.demo.R;
import com.dtston.demo.utils.SharedPreferencesUtils;
import com.dtston.demo.utils.StringUtils;
import com.dtston.demo.utils.ToastUtils;

/**
 * Created by Administrator on 2017/11/27.
 */

public class SettingActivity extends BaseActivity {

    private View mVBack;
    private EditText mVAppId;
    private EditText mVAppKey;
    private View mVSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mVBack = findViewById(R.id.back);
        mVAppId = (EditText) findViewById(R.id.app_id);
        mVAppKey = (EditText) findViewById(R.id.app_key);
        mVSubmit = findViewById(R.id.submit);

        String appId = SharedPreferencesUtils.getAppId(this);
        String appKey = SharedPreferencesUtils.getAppKey(this);
        mVAppId.setText(appId);
        mVAppKey.setText(appKey);
    }

    @Override
    protected void initEvents() {
        mVBack.setOnClickListener(this);
        mVSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                submit();
                break;
            default:
                break;
        }
    }

    private void submit() {
        String appId = mVAppId.getText().toString();
        String appKey = mVAppKey.getText().toString();
        if (StringUtils.isEmpty(appId)) {
            ToastUtils.showToast("请输入APP ID");
            return;
        }
        if (StringUtils.isEmpty(appKey)) {
            ToastUtils.showToast("请输入APP Key");
            return;
        }
        SharedPreferencesUtils.editAppId(this, appId);
        SharedPreferencesUtils.editAppKey(this, appKey);
        finish();
    }

}
