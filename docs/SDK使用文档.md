### 一. 申请AppID和AppKey
登录应用申请网站，填写应用信息并提交审核，审核通过后，获取到AppID和AppKey。

### 二. 准备环境
#### 1.将 dtcloud-x.x.x.jar 导入工程<br>
#### 2.在工程的AndroidManifest.xml里面注册DTService
```Java
<application android:label="@string/app_name"
	android:icon="@drawable/icon" 	android:label="@string/app_name">
 
	......
	......

    <service android:name="com.dtston.dtcloud.push.DTService" />

</application>
```
#### 3.在工程的AndroidManifest.xml添加权限
```Java
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission  android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>
```
#### 4.配置应用版本号,不能小于100
```Java
android:versionCode="100"
```
* 版本号不能小于100，在AndroidManifest.xml或build.gradle配置

### 三. 初始化SDK
#### 1.设置AppID和AppKey
```Java
DtCloudManager.setAppInfo("AppID","AppKey", "AppKey");
```
#### 2.设置测试环境
```Java
DtCloudManager.setTestEnvironment(true);
```
#### 3.设置正式环境
```Java
DtCloudManager.setTestEnvironment(false);
```
#### 4.设置亚马逊服务器环境
```Java
DtCloudManager.setAmazonEnvironment();
```
* 测试环境、正式环境、亚马逊服务器环境只能设置一个。

#### 5.开启日志打印
```Java
DtCloudManager.setDebug(true);
```
* true为开启，false为关闭；

#### 6.DtCloudManager初始化
```Java
DtCloudManager.init(context, new DTIOperateCallback() {
    @Override
    public void onSuccess(Object var1, int var2) {
        System.out.println("初始化成功");
    }

    @Override
    public void onFail(Object errmsg, int errcode, String var3) {
        System.out.println("初始化错误：" + errmsg + ",errcode=" + 	errcode);
    }
});
```
* DtCloudManager的初始化，必须在放在初始化设置的最后。

### 四. 用户接口UserManager
#### 1.用户登录
```Java
UserManager.loginUser(userName, password, new DTIOperateCallback<LoginResult>() {
   @Override
   public void onSuccess(LoginResult loginResult, int var2) {
      //登录成功
      String uid = loginResult.getData().getUid();
      String token = loginResult.getData().getToken();
      String time = loginResult.getData().getTime()+"";
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //登录失败      
      ToastUtils.showToast(error.toString());
   }
});
```
#### 2.用户注册
```Java
UserManager.registerUser(userName, password, vcode, new DTIOperateCallback<RegisterResult>() {
   @Override
   public void onSuccess(RegisterResult registerResult, int i) {
      //注册失败
      String uid = registerResult.getData().getUid();
      String token = registerResult.getData().getToken();
      String time = registerResult.getData().getTime()+"";
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //注册失败
      ToastUtils.showToast(error.toString());
   }
});
```
#### 3.注销用户
```Java
UserManager.logoutUser();
```
* 用户退出时，必须要调用“注销用户”接口。

#### 4.获取注册验证码
```Java
UserManager.getRegisterVcode(userName, new DTIOperateCallback<BaseResult>() {
   @Override
   public void onSuccess(BaseResult baseResult, int var2) {
      //验证码获取成功
      ToastUtils.showToast("验证码获取成功");
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //验证码获取失败
      ToastUtils.showToast(error.toString());
   }
});
```
* 第一个参数userName：手机号码或邮箱地址;

#### 5.获取重置密码验证码
```Java
UserManager.getResetPasswordVcode(userName, new DTIOperateCallback<BaseResult>() {
   @Override
   public void onSuccess(BaseResult baseResult, int var2) {
      //验证码获取成功
      ToastUtils.showToast("验证码获取成功");
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //验证码获取失败
      ToastUtils.showToast(error.toString());
   }
});
```
* 第一个参数userName：手机号码或邮箱地址；

#### 6.重置密码
```Java
UserManager.resetPassword(userName, password，vcode，new DTIOperateCallback<BaseResult>() {
   @Override
   public void onSuccess(BaseResult baseResult, int var2) {
      //密码重置成功
      ToastUtils.showToast("密码重置成功");
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //密码重置失败
      ToastUtils.showToast(error.toString());
   }
});
```
* 第一个参数userName：手机号码或邮箱地址；
* 第二个参数password：新密码；
* 第三个参数vcode：验证码；

#### 7.修改密码
```Java
UserManager.changePassword(oldPassword, newPassword，new DTIOperateCallback<BaseResult>() {
   @Override
   public void onSuccess(BaseResult baseResult, int var2) {
      //密码修改成功
      ToastUtils.showToast("密码修改成功");
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //密码修改失败
      ToastUtils.showToast(error.toString());
   }
});
```
* 第一个参数oldPassword：旧密码；
* 第二个参数newPassword：新密码；

#### 8.获取用户信息
```Java
UserManager.getUserInfo(new DTIOperateCallback<UserInfoResult>() {
   @Override
   public void onSuccess(UserInfoResult userInfoResult, int var2) {
      //用户信息DataBean, userInfoResult.getData()
      //DataBean.uid, DataBean.image, DataBean.sex ......
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //用户信息获取失败
      ToastUtils.showToast(error.toString());
   }
});
```

#### 9.设置用户信息
```Java
UserManager.setUserInfo(nickName, sex, birthday, height, weight, 
new DTIOperateCallback<BaseResult>() {
   @Override
   public void onSuccess(BaseResult baseResult, int var2) {
      //修改成功
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //修改失败
      ToastUtils.showToast(error.toString());
   }
});
```
* 第一个参数nickName：用户昵称；
* 第二个参数sex：用户性别；
* 第三个参数birthday：用户生日；
* 第四个参数height：身高；
* 第五个参数weight：体重；
* 参数可以传null，表示不修改。

#### 10.修改用户头像
```Java
UserManager.changeUserAvatar(avatarFile, 
new DTIOperateCallback<BaseResult>() {
   @Override
   public void onSuccess(BaseResult baseResult, int var2) {
      //修改成功
   }

   @Override
   public void onFail(Object error, int var2, String var3) {
      //修改失败
      ToastUtils.showToast(error.toString());
   }
});
```
* 第一个参数avatarFile：头像图片文件的绝对路径；

### 五. 设备接口DeviceManager
#### 1.注册/注销设备消息回调接口
```Java
DeviceManager.registerDeviceMessageCallback(new DTIDeviceMessageCallback() {
    @Override
    public void onMessageReceive(String mac, String msgType, 
	byte[] data) {
	//设备消息回调
	//mac为设备mac地址，
	//msgType为消息类型，
	//data为消息体
    }
});
```
* 在不需要时，记得调用注销接口：DeviceManager.unregisterDeviceMessageCallback(this)。

#### 2.注册/注销设备状态回调接口
```Java
DeviceManager.registerDeviceStateCallback(new DTIDeviceStateCallback() {
    @Override
    public void onDeviceOnlineNotice(DTDeviceState 
	dtDeviceState) {
	//设备在线回调
	//远程在线状态：dtDeviceState.isRemoteOnline()
	//近场在线状态：dtDeviceState.isNearOnline()
	//在线状态：dtDeviceState.isOnline()
    }

    @Override
    public void onDeviceOfflineNotice(DTDeviceState 	dtDeviceState) {
	//设备离线回调
    }
});
```
* 在不需要时，记得调用注销接口：DeviceManager.unregisterDeviceStateCallback(this)。

#### 3.获取绑定设备
```Java
DeviceManager.getBindDevices(new DTIOperateCallback<UserDevicesResult>() {
    @Override
    public void onSuccess(UserDevicesResult userDevicesResult, int i) {
        if (userDevicesResult.getErrcode() == 0) {
	    //绑定设备数据
            List<UserDevicesResult.DataBean> data = 
		userDevicesResult.getData();
	} else if (userDevicesResult.getErrcode() == 400011) {
	    //没有绑定设备数据
    	    ToastUtils.showToast("没有绑定设备");
	}


    }

    @Override
    public void onFail(Object error, int i, String s) {
	//获取失败
    	ToastUtils.showToast(error.toString());
    }
});
```

#### 4.解除设备绑定
```Java
DeviceManager.unbindDevice(String deviceMac, String dataId, new DTIOperateCallback<BaseResult>() {
    @Override
    public void onSuccess(BaseResult result, int var2) {
        //设备解绑成功
    }

    @Override
    public void onFail(Object var1, int var2, String var3) {
        //设备解绑失败
    }
});
```

#### 5.发送消息
```Java
DeviceManager.sendMessage(String mac, String msgType, String msgBody, new DTIOperateCallback<>() {
    @Override
    public void onSuccess(Object o, int i) {
        //成功不会回调此方法
    }

    @Override
    public void onFail(Object error, int i, String s) {
	//发送失败
    	ToastUtils.showToast(error.toString());
    }
});
```
* 第一个参数mac：设备mac地址；
* 第二个参数msgType：消息类型，十六进制字符串，如1001或1002等；
* 第三个参数msgBody：消息体，十六进制字符串，如01或0201或020401等；

#### 6.设备开始配对网络
```Java
DeviceManager.startDeviceMatchingNetwork(this, moduleType, deviceType,
      deviceName, ssid, password, new DTIDeviceConnectCallback() {

         @Override
         public void onSuccess(DTConnectedDevice 
		connectedDevice) {
	     //配对成功回调
	     //设备mac：connectedDevice.getMac()
	     //设备类型：connectedDevice.getType()
	     //设备数据ID：connectedDevice.getDataId()
	     //设备ID：connectedDevice.getDeviceId()
         }

         @Override
         public void onFail(int errcode, String errmsg) {
            //配对失败
         }
      });
```
* 第一个参数moduleType，wifi模块类型
* DeviceManager.WIFI_HF， //汉枫模块
* DeviceManager.WIFI_LX， //乐鑫模块
* DeviceManager.WIFI_QK， //庆科模块
* DeviceManager.WIFI_MWR， //马威尔模块；
* 第二个参数deviveType，设备类型；
* 第三个参数deviveName；设备名称；
* 第四个参数ssid：路由器ssid；
* 第五个参数password：路由器密码；
* 第六个参数DTConnectedDevice：入网设备，包含设备MAC、设备类型、设备数据ID、、设备ID；

* 在不需要时，记得调用停止配网接口：DeviceManager.stopDeviceMatchingNetwork()。

#### 7.获取设备状态
```Java
DTDeviceState deviceState = DeviceManager.getDevicesState(deviceMac);
```
#### 8.固件版本检测
```Java
DeviceManager.firmwareUpgrade(deviceMac,DTFirmwareUpgradeResult.TYPE_CHECK_VERSION,
        new DTIOperateCallback < DTFirmwareUpgradeResult > ()
{
    @Override
    public void onSuccess ( final DTFirmwareUpgradeResult upgradeResult, int i){
        if (DTFirmwareUpgradeResult.RESULT_NO_NEW_VERSION == upgradeResult.getResult()) {
           //没有新版本
	   
        } else if (DTFirmwareUpgradeResult.RESULT_HAS_NEW_VERSION == upgradeResult.getResult()) {
	    //有新版本
           
        }
    }

    @Override
    public void onFail (Object o,int i, String s){
        //固件版本检测失败
        ToastUtils.showToast(o.toString());
    }
});
```
* 第一个参数为设备Mac；
* 第二个参数为固件操作类型，传DTFirmwareUpgradeResult.TYPE_CHECK_VERSION；
* 第三个参数为回调接口；

#### 9.固件升级
```Java
DeviceManager.firmwareUpgrade(deviceMac,DTFirmwareUpgradeResult.TYPE_UPGRADE,
        new DTIOperateCallback<DTFirmwareUpgradeResult>() {
    @Override
    public void onSuccess(final DTFirmwareUpgradeResult upgradeResult, int i) {
        if (DTFirmwareUpgradeResult.RESULT_DOWNLOAD_OK == upgradeResult.getResult()) {
            //固件升级成功
        } else if (DTFirmwareUpgradeResult.RESULT_DOWNLOAD_FAILED == upgradeResult.getResult()) {
            //固件升级失败
        } else if (DTFirmwareUpgradeResult.RESULT_DOWNLOAD_START == upgradeResult.getResult()
                || DTFirmwareUpgradeResult.RESULT_DOWNLOADING == upgradeResult.getResult()) {
            //正在下载固件
        }
    }

    @Override
    public void onFail(Object o, int i, String s) {
        //固件升级失败
    }
});
```
* 第一个参数为设备Mac；
* 第二个参数为固件操作类型，传DTFirmwareUpgradeResult.TYPE_UPGRADE；
* 第三个参数为回调接口；

#### 10.分享设备
```Java
DeviceManager.shareDevice(String dataId, String mobile, new DTIOperateCallback<BaseResult>() {
    @Override
    public void onSuccess(BaseResult baseResult, int i) {
        //分享成功
    }

    @Override
    public void onFail(Object error, int i, String s) {
        //分享失败
        ToastUtils.showToast(error.toString());
    }
});
```
* 第一个参数为设备数据ID；
* 第二个参数为分享给对方的手机号码；

#### 11.获取设备的用户
```Java
DeviceManager.getDeviceUsers(String deviceId, new DTIOperateCallback<DeviceUsersResult>() {
    @Override
    public void onSuccess(DeviceUsersResult deviceUsersResult, int i) {
	//设备用户数据        
	List<DeviceUsersResult.DataBean> data = 			 		deviceUsersResult.getData();
    }

    @Override
    public void onFail(Object error, int i, String s) {
	//获取失败
    	ToastUtils.showToast(error.toString());
    }
});
```
* 第一个参数为设备ID；

#### 12.删除设备的用户
```Java
DeviceManager.deleteDeviceUsers(String deviceId, String delUid, new DTIOperateCallback<BaseResult>() {
    @Override
    public void onSuccess(BaseResult result, int i) {
	//删除成功
    }

    @Override
    public void onFail(Object error, int i, String s) {
	//删除失败
        ToastUtils.showToast(error.toString());
    }
});
```
* 第一个参数为设备ID；
* 第二个参数为删除对方用户的uid；

#### 13.重命名设备
```Java
DeviceManager.renameDevice(String dataId, String deviceName, new DTIOperateCallback<BaseResult>() {
    @Override
    public void onSuccess(BaseResult baseResult, int i) {
        //重命名成功
    }

    @Override
    public void onFail(Object error, int i, String s) {
	//重命名失败
	ToastUtils.showToast(error.toString());
    }
});
```
