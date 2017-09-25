#SDK文档说明
##SDK Demo
点击下载应用 [SDK Demo](http://assist.dtston.com/sdk/android/sdk_demo_1.3.2.apk)，并用手机安装；
对应的工程源文件请点击下载 [Demo Project](http://assist.dtston.com/sdk/android/demo_project_1.3.2.zip)。
##开始
###1. 申请AppID 和 AppKey
登录应用申请网站，填写应用信息并提交审核，审核通过后，获取到AppID 和 AppKey。
![Alt text](./zhujiemian.jpg)

###2. 准备环境
（1）点击下载 [dtcloud-1.3.2.jar](http://assist.dtston.com/sdk/android/dtcloud-1.3.2.jar) 包，并将 dtcloud-1.3.2.jar 导入工程；
（2）在工程的AndroidManifest.xml里面填写应用的AppID 和 AppKey；
```java
<application android:label="@string/app_name"
        android:icon="@drawable/ic_launcher"
        android:theme="@style/AppTheme">
        
		<!-- 【必须】 请修改为APP的AccessId -->
        <meta-data
            android:name="DTCLOUD_APPID"
            android:value="50024" />
        <!-- 【必须】 请修改为APP的AccessKey -->
        <meta-data
            android:name="DTCLOUD_APPKEY"
            android:value="c909f4c64b81ac5a3f3587a590f39ce1" />
            
        ......
        ......
        
 </application>
```
###3. 初始化DtCloudManager
调用DtCloudManager的init()方法进行初始化
```java
DtCloudManager.init(this, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				//初始化成功
			}

			@Override
			public void onFail(Object errmsg, int errCode, String var3) {
				//初始化失败
			}
		});
```
错误信息(errCode)包含以下：
40001：应用校验未通过，确保AppID 和 AppKey是通过应用申请获取；
30000：应用校验失败，网络异常；
###4. 登录
```java
UserManager.loginUser(userName, password, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				JSONObject response = (JSONObject) var1;
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				
			}
		});
```
onSuccess()方法返回参数var1是一个JSON数据，包含下面信息 ：
errcode：返回码 ，0是成功，其余都是错误；
errmsg：错误信息；
###5. 注册设备回调接口
调用DeviceManager的registerDeviceMessageCallback()方法注册 **设备消息** 回调；
同时，在不需要的时候使用unregisterDeviceMessageCallback方法 **注销回调**；
```java
DeviceManager.registerDeviceMessageCallback(new DTIDeviceMessageCallback() {
			@Override
			public onMessageReceive(String mac, byte[] data) {
				//设备消息
			}
		});
```
调用DeviceManager的registerDeviceStateCallback()方法注册 **设备状态** 回调；
同时，在不需要的时候使用unregisterDeviceStateCallback()方法 **注销回调**；
```java
DeviceManager.registerDeviceStateCallback(new DTIDeviceStateCallback() {
			@Override
			public onDeviceOnlineNotice(DeviceState deviceState) {
				//设备在线
			}
			@Override
			public onDeviceOfflineNotice(DeviceState deviceState) {
				//设备离线
			}
		});
```
##用户接口：UserManager
###1. 用户登录
```java
UserManager.loginUser(userName, password, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				JSONObject response = (JSONObject) var1;
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				
			}
		});
```
onSuccess()方法返回参数var1是一个JSON数据，包含下面信息 ：
errcode：返回码 ，0是成功，其余都是错误；
errmsg：错误信息；
###2. 用户注册
```java
UserManager.registerUser(userName, password, vcode, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				JSONObject response = (JSONObject) var1;
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				
			}
		});
```
onSuccess()方法返回参数var1是一个JSON数据，包含下面信息 ：
errcode：返回码 ，0是成功，其余都是错误；
errmsg：错误信息；
###3. 获取注册验证码
```java
UserManager.getRegisterVcode(userName, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				JSONObject response = (JSONObject) var1;
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				
			}
		});
```
onSuccess()方法返回参数var1是一个JSON数据，包含下面信息 ：
errcode：返回码 ，0是成功，其余都是错误；
errmsg：错误信息；
###4. 获取重置密码验证码
```java
UserManager.getResetPasswordVcode(userName, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				JSONObject response = (JSONObject) var1;
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				
			}
		});
```
onSuccess()方法返回参数var1是一个JSON数据，包含下面信息 ：
errcode：返回码 ，0是成功，其余都是错误；
errmsg：错误信息；
###5. 重置密码
```java
UserManager.resetPassword(userName, password, vcode, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				JSONObject response = (JSONObject) var1;
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				
			}
		});
```
onSuccess()方法返回参数var1是一个JSON数据，包含下面信息 ：
errcode：返回码 ，0是成功，其余都是错误；
errmsg：错误信息；
###6. 注销用户
```java
UserManager.logoutUser();
```
##设备接口：DeviceManager
###1. 注册设备消息回调接口
```java
DeviceManager.registerDeviceMessageCallback(new DTIDeviceMessageCallback() {
			@Override
			public onMessageReceive(String mac, byte[] data) {
				//设备消息
			}
		});
```
###2. 注册设备状态回调接口
```java
DeviceManager.registerDeviceStateCallback(new DTIDeviceStateCallback() {
			@Override
			public onDeviceOnlineNotice(DeviceState deviceState) {
				//设备在线
			}
			@Override
			public onDeviceOfflineNotice(DeviceState deviceState) {
				//设备离线
			}
		});
```
###3、解除设备绑定
```java
DeviceManager.unbindDevice(String deviceMac，String dataId，new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				JSONObject response = (JSONObject) var1;
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				
			}
		});
```
**deviceMac**：设备MAC地址
**dataId**：设备数据ID；
**onSuccess()**方法返回参数var1是一个JSON数据，包含下面信息 ：
**errcode**：返回码 ，0是成功，400011是已解绑该设备，其余都是错误；
**errmsg**：错误信息；
###4、获取绑定设备
```java
DeviceManager.getBindDevices(new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				JSONObject response = (JSONObject) var1;
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				
			}
		});
```
**onSuccess()**方法返回参数var1是一个JSON数据，包含下面信息 ：
**errcode**：返回码 ，0是成功，400011是没有绑定设备数据，其余都是错误；
**errmsg**：错误信息；
**data**：绑定设备数据，是一个JSON数组JSONArray，包含
id：设备数据ID；
mac：设备MAC；
name：设备名称；
type：设备类型；
###5、发送消息
```java
DeviceManager.sendMessage(String deviceMac, byte[] msg, new DTIOperateCallback() {
			@Override
			public void onSuccess(Object var1, int var2) {
				//发送成功
			}

			@Override
			public void onFail(Object error, int var2, String var3) {
				//发送失败
			}
		});
```
**deviceMac**：设备MAC；
**msg**：设备发送数据；
###6、开始设备配对网络
```java
DeviceManager.startDeviceMatchingNetwork(Context context, int moduleType, int deviveType, 
	String deviveName, String ssid, String password, new DTIDeviceConnectCallback() {

			@Override
			public void onSuccess(DTConnectedDevice connectedDevice) {
				//入网成功，设备信息 DTConnectedDevice 
			}

			@Override
			public boolean onFail(int errcode, String errmsg {
				//入网失败
				return false;
			}
		});
```
**moduleType**，wifi模块类型，参见：
DeviceManager.WIFI_HF，      //汉枫模块
DeviceManager.WIFI_LX，      //乐鑫模块
DeviceManager.WIFI_QK，     //庆科模块
DeviceManager.WIFI_MWR，  //马威尔模块；
**deviveType**，设备类型；
**deviveName**；设备名称；
**ssid**：路由器ssid；
**password**：路由器密码；
**DTConnectedDevice**：入网设备，包含设备MAC、设备类型、设备数据ID；
###7、停止设备配对网络
```java
DeviceManager.stopDeviceMatchingNetwork();
```
配对网络结束后，需要调用这个方法去停止配网操作；
###8、获取设备状态
```java
DTDeviceState deviceState = DeviceManager.getDevicesState(String mac);
```
**DTDeviceState**，设备状态，其中 isOnline() 获取是否在线。
