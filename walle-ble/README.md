
# walle-ble
 低功耗蓝牙辅助库

### Gradle 引入库

```groovy
implementation 'cn.songhaiqing.walle.ble:walle-ble:1.0.4'
```

### 添加权限
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## 使用文档
### 打开扫描界面

![BleScan](https://github.com/HarlanSong/Walle/tree/master/images/bleScan.png)

```java
Intent intent = new Intent(this, DeviceScanActivity.class);
startActivityForResult(intent, REQUEST_BIND_DEVICE);
```
*REQUEST_BIND_DEVICE 自行定义回调常量（int）*

### 选择蓝牙设置成功回调,并连接设备

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode != RESULT_OK && REQUEST_BIND_DEVICE == requestCode) {
		String name = data.getStringExtra("name");
		String macAddress = data.getStringExtra("macAddress");
		Toast.makeText(this, "name:" + name + " macAddress:" + macAddress, Toast.LENGTH_LONG).show();
		BleUtil.connectDevice(this, name, macAddress);
	}
}
```

### 断开连接

```java
BleUtil.disConnect(this);
```

### 监听设备连接状态

```java
 private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WalleBleService.ACTION_CONNECTED_SUCCESS.equals(action)) {

            } else if (WalleBleService.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (WalleBleService.ACTION_DEVICE_RESULT.equals(action)) {
				String uuid = intent.getStringExtra("uuid");
				ArrayList<Integer> dataArray = intent.getIntegerArrayListExtra("data");
				byte[] srcData = intent.getByteArrayExtra("srcData");
            }
        }
    };
```

* ACTION_CONNECTED_SUCCESS 连接成功
* ACTION_GATT_DISCONNECTED 断开连接
* ACTION_DEVICE_RESULT 设备有数据返回，`uuid`为返回特征值UUID;`data`为解析后的数组。`srcData`为原数据；

### 发送命令到设备，并监听

```java
  BleUtil.broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes);
```
*  context 上下文
*  notifyServiceUUID 通知服务UUID
*  notifyCharacteristicUUID  通知特征值UUID
*  writeServiceUUID 写入服务UUID
*  writeCharacteristicUUID 写入特殊值UUID
*  bytes 命令内容

### 仅读取取设备数据

```java
BleUtil.broadcastReadBle(Context context, byte[] bytes, String serviceUUID,
                                        String characteristicUUID);
```

*  context 上下文
*  serviceUUID 服务UUID
*  characteristicUUID  特征值UUID

### 判断设备是否连接
```java
// 是否已经连接
BleUtil.bleConnected
// 已连接设备MAC地址
BleUtil.bleAddress
// 已连接设备名称
BleUtil.bleName
```

### 配置

```java
// Log前缀，默认为“Walle”
WalleConfig.setLogTag(String tag);
// 是否开户DEBUG模式，默认false
WalleConfig.setDebug(boolean isDebug);
```

##更新日志

**1.0.5(20180904)**
* 修复再次连接蓝牙设备慢后失败问题
* BleUtil添加已连接设备信息（mac地址、名称）

**1.0.4(20180903)**
* 删除项目自动生成的无用库，及删除测试模块代码。
* 引用的核心库升级至1.0.1。

