## 申请AppID和AppKey
登录应用申请网站，填写应用信息并提交审核，审核通过后，获取到AppID和AppKey。

## 准备环境
### 1.将 dtcloud-x.x.x.jar 导入工程<br>
### 2.在工程的AndroidManifest.xml里面注册DTService
```Java
<application android:label="@string/app_name"
	android:icon="@drawable/icon" 	android:label="@string/app_name">
 
	......
	......

    <service android:name="com.dtston.dtcloud.push.DTService" />

</application>
```
### 3.在工程的AndroidManifest.xml添加权限
```Java
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission  android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>
```
### 4.配置应用版本号,不能小于100
```Java
android:versionCode="100"
```
##### 注：版本号不能小于100，在AndroidManifest.xml或build.gradle配置

## 初始化SDK
### 1.设置AppID和AppKey
```Java
DtCloudManager.setAppInfo("AppID","AppKey", "AppKey");
```
### 2.设置测试环境
```Java
DtCloudManager.setTestEnvironment(true);
```
### 3.设置正式环境
```Java
DtCloudManager.setTestEnvironment(false);
```
### 4.设置亚马逊服务器环境
```Java
DtCloudManager.setAmazonEnvironment();
```
##### 注：测试环境、正式环境、亚马逊服务器环境只能设置一个。
### 5.开启日志打印
```Java
DtCloudManager.setDebug(true);
```
##### true为开启，false为关闭；
### 6.DtCloudManager初始化
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
##### 注：DtCloudManager的初始化，必须在放在初始化设置的最后。
