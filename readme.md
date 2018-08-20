## Walle 是Android开发库.

* walle-core 核心库
* walle-ble 低功耗蓝牙
* walle-ui  UI
* walle-gallery 图片选择器（待开发）

### walle-core

**使用**
```
implementation 'cn.songhaiqing.walle.core:walle-core:1.0.0'
```

### walle-ble

`walle-ble`通过主要功能在WalleBleService中完成，通过广播与service通讯。

#### 引入
```
implementation 'cn.songhaiqing.walle.ble:walle-ble:1.0.2'
```

#### 文档

**打开扫描界面**

![扫描设备](https://github.com/HarlanSong/Walle/tree/master/images/bleScan.jpg)

```java
Intent intent = new Intent(this, DeviceScanActivity.class);
startActivityForResult(intent, REQUEST_BIND_DEVICE);
```
*REQUEST_BIND_DEVICE 自行定义回调常量（int）*

**选择蓝牙设置成功回调,并连接设备**

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

**断开连接**

```java
BleUtil.disConnect(this);
```

**监听设备连接状态**

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

**发送命令到设备，并监听**

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



**仅读取取设备数据**

```java
BleUtil.broadcastReadBle(Context context, byte[] bytes, String serviceUUID,
                                        String characteristicUUID);
```

*  context 上下文
*  serviceUUID 服务UUID
*  characteristicUUID  特征值UUID

**判断设备是否连接**
```java
BleUtil.bleConnected
```

**配置**

```java
// Log前缀，默认为“Walle”
WalleConfig.setLogTag(String tag);
// 是否开户DEBUG模式，默认false
WalleConfig.setDebug(boolean isDebug);
```