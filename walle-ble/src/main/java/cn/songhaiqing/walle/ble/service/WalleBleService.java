package cn.songhaiqing.walle.ble.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import cn.songhaiqing.walle.ble.utils.BleUtil;
import cn.songhaiqing.walle.core.utils.LogUtil;
import cn.songhaiqing.walle.core.utils.StringUtil;

/**
 * 蓝牙连接基础类
 */
public class WalleBleService extends Service {
    private final String TAG = getClass().getName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public static final String ACTION_READ_BLE = "cn.songhaiqing.walle.ble.ACTION_READ_BLE";
    public static final String ACTION_WRITE_BLE = "cn.songhaiqing.walle.ble.ACTION_WRITE_BLE";
    public static final String ACTION_CONNECT_DEVICE = "cn.songhaiqing.walle.ble.ACTION_CONNECT_DEVICE";
    public static final String ACTION_DISCONNECT_DEVICE = "cn.songhaiqing.walle.ble.ACTION_DISCONNECT_DEVICE";

    public final static String ACTION_GATT_CONNECTED = "cn.songhaiqing.walle.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "cn.songhaiqing.walle.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "cn.songhaiqing.walle.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_CONNECTED_SUCCESS = "cn.songhaiqing.walle.ble.ACTION_CONNECTED_SUCCESS";
    public final static String ACTION_EXECUTED_SUCCESSFULLY = "cn.songhaiqing.walle.ble.ACTION_EXECUTED_SUCCESSFULLY";
    public final static String ACTION_EXECUTED_FAILED = "cn.songhaiqing.walle.ble.ACTION_EXECUTED_FAILED";
    public final static String ACTION_DEVICE_RESULT = "cn.songhaiqing.walle.ble.ACTION_DEVICE_RESULT";

    public final static String EXTRA_DATA = "EXTRA_DATA";
    public final static String EXTRA_DATA_NOTIFY_SERVICE_UUID = "EXTRA_DATA_NOTIFY_SERVICE_UUID";
    public final static String EXTRA_DATA_NOTIFY_CHARACTERISTIC_UUID = "EXTRA_DATA_NOTIFY_CHARACTERISTIC_UUID";
    public final static String EXTRA_DATA_WRITE_SERVICE_UUID = "EXTRA_DATA_WRITE_SERVICE_UUID";
    public final static String EXTRA_DATA_WRITE_CHARACTERISTIC_UUID = "EXTRA_DATA_WRITE_CHARACTERISTIC_UUID";
    public final static String EXTRA_DATA_READ_SERVICE_UUID = "EXTRA_DATA_READ_SERVICE_UUID";
    public final static String EXTRA_DATA_READ_CHARACTERISTIC_UUID = "EXTRA_DATA_READ_CHARACTERISTIC_UUID";

    private BluetoothGattCharacteristic notifyBluetoothGattCharacteristic;

    private Handler handler;

    private boolean operationDone = true;
    private boolean artificialDisconnect = true;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "on create");
        handler = new Handler();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_BLE);
        intentFilter.addAction(ACTION_WRITE_BLE);
        intentFilter.addAction(ACTION_CONNECT_DEVICE);
        intentFilter.addAction(ACTION_DISCONNECT_DEVICE);
        registerReceiver(broadcastReceiver, intentFilter);
        initialize();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(TAG, "BroadcastReceiver Action:" + action);
            if (ACTION_DISCONNECT_DEVICE.equals(action)) {
                disconnect();
            } else if (ACTION_CONNECT_DEVICE.equals(action)) {
                String address = intent.getStringExtra(EXTRA_DATA);
                connect(address);
            } else if (ACTION_READ_BLE.equals(action)) {
                String serviceUUID = intent.getStringExtra(EXTRA_DATA_READ_SERVICE_UUID);
                String characteristicUUID = intent.getStringExtra(EXTRA_DATA_READ_CHARACTERISTIC_UUID);
                readBluetooth(serviceUUID, characteristicUUID);
            } else if (ACTION_WRITE_BLE.equals(action)) {
                String notifyServiceUUID = intent.getStringExtra(EXTRA_DATA_NOTIFY_SERVICE_UUID);
                String notifyCharacteristicUUID = intent.getStringExtra(EXTRA_DATA_NOTIFY_CHARACTERISTIC_UUID);
                String writeServiceUUID = intent.getStringExtra(EXTRA_DATA_WRITE_SERVICE_UUID);
                String writeCharacteristicUUID = intent.getStringExtra(EXTRA_DATA_WRITE_CHARACTERISTIC_UUID);
                byte[] data = intent.getByteArrayExtra(EXTRA_DATA);
                writeBluetooth(notifyServiceUUID, notifyCharacteristicUUID, writeServiceUUID, writeCharacteristicUUID, data);
            }
        }
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtil.d(TAG, "onConnectionStateChange status:" + status + " newSate:" + newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                LogUtil.i(TAG, "成功连接设备 ,name:" + gatt.getDevice().getName() + " address:" + gatt.getDevice().getAddress());
                LogUtil.d(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                Intent intent = new Intent(intentAction);
                sendBroadcast(intent);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED && status != 133) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                BleUtil.bleConnected = false;
                LogUtil.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
                if (!artificialDisconnect && !TextUtils.isEmpty(mBluetoothDeviceAddress)) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!artificialDisconnect && !TextUtils.isEmpty(mBluetoothDeviceAddress)) {
                                LogUtil.d(TAG, "Start automatically reconnecting.");
                                connect(mBluetoothDeviceAddress);
                            }
                        }
                    }, 5000);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LogUtil.d(TAG, "onServicesDiscovered status:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS && isConnected()) {
                BleUtil.bleConnected = true;
                broadcastUpdate(ACTION_CONNECTED_SUCCESS);
            } else {
                LogUtil.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            LogUtil.d(TAG, "onCharacteristicRead Characteristic UUID : " + characteristic.getUuid().toString());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothUpdate(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            LogUtil.d(TAG, "onCharacteristicChanged Characteristic UUID : " + characteristic.getUuid().toString());
            bluetoothUpdate(characteristic);
        }
    };

    public boolean isConnected() {
        return mConnectionState == STATE_CONNECTED;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                LogUtil.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            LogUtil.w(TAG, "Bluetooth unavailable");
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            LogUtil.w(TAG, "Bluetooth not turned on");
            return false;
        }

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter == null) {
            LogUtil.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    private boolean connect(final String address) {
        if (!initialize()) {
            return false;
        }
        LogUtil.d(TAG, "开始连接手环：" + address);
        artificialDisconnect = false;
        if (mBluetoothAdapter == null || address == null) {
            LogUtil.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            if (!initialize()) {
                return false;
            }
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            LogUtil.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                checkConnectStatus();
                return true;
            } else {
                checkConnectStatus();
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            LogUtil.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        LogUtil.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        checkConnectStatus();
        return true;
    }

    private void checkConnectStatus() {
        if (artificialDisconnect || isConnected()) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!artificialDisconnect && !isConnected() && !TextUtils.isEmpty(mBluetoothDeviceAddress)) {
                    connect(mBluetoothDeviceAddress);
                }
            }
        }, 10000);
    }

    private void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtil.w(TAG, "BluetoothAdapter not initialized (disconnect)");
            return;
        }
        mBluetoothGatt.disconnect();
        artificialDisconnect = true;
        notifyBluetoothGattCharacteristic = null;
    }

    private void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(final BluetoothGattCharacteristic characteristic, final int retryNumber) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtil.w(TAG, "BluetoothAdapter not initialized(readCharacteristic)");
            return;
        }
        boolean status = mBluetoothGatt.readCharacteristic(characteristic);
        if (status) {
            LogUtil.d(TAG, "Bluetooth read success");
            if (!operationDone) {
                broadcastUpdate(ACTION_EXECUTED_SUCCESSFULLY);
                operationDone = true;
            }
            return;
        }
        final int maxRetryNumber = 4;
        if (retryNumber <= maxRetryNumber) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readCharacteristic(characteristic, retryNumber + 1);
                }
            }, 2000);
        } else if (!operationDone) {
            broadcastUpdate(ACTION_EXECUTED_FAILED);
            operationDone = true;
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private void bluetoothUpdate(BluetoothGattCharacteristic characteristic) {
        String uuid = characteristic.getUuid().toString();
        String dataUINT16Str = StringUtil.bytesToHexStr(characteristic.getValue());
        ArrayList<Integer> dataArray = new ArrayList<>(StringUtil.bytesToArrayList(characteristic.getValue())) ;
        LogUtil.i(TAG, "Result Data:" + dataUINT16Str + " size:" + dataArray.size());
        Intent intent = new Intent(ACTION_DEVICE_RESULT);
        intent.putExtra("uuid",uuid);
        intent.putExtra("data",dataArray);
        intent.putExtra("srcData",characteristic.getValue());
        sendBroadcast(intent);
    }

    protected void readBluetooth(final String serviceUUID, final String characteristicUUID) {
        if (!isConnected()) {
            LogUtil.w(TAG, "Bluetooth  not connected");
            return;
        }
        BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (bluetoothGattService == null) {
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(characteristicUUID));
        if (bluetoothGattCharacteristic == null) {
            return;
        }
        readCharacteristic(bluetoothGattCharacteristic, 0);
    }

    private void writeAndNotify(String notifyServiceUUID, String notifyCharacteristicUUID, final String writeServiceUUID,
                                final String writeCharacteristicUUID, final byte[] writeData) {
        if (!isConnected()) {
            LogUtil.w(TAG, "Bluetooth  not connected");
            return;
        }
        BluetoothGattService bluetoothGattServiceNotify = mBluetoothGatt.getService(UUID.fromString(notifyServiceUUID));
        if (bluetoothGattServiceNotify == null) {
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristicNotify = bluetoothGattServiceNotify.getCharacteristic(UUID.fromString(notifyCharacteristicUUID));
        if (bluetoothGattCharacteristicNotify == null) {
            return;
        }
        if (notifyBluetoothGattCharacteristic != null && notifyBluetoothGattCharacteristic.getUuid().equals(bluetoothGattCharacteristicNotify.getUuid())) {
            BluetoothGattCharacteristic bluetoothGattCharacteristicWrite = mBluetoothGatt.getService(UUID.fromString(writeServiceUUID))
                    .getCharacteristic(UUID.fromString(writeCharacteristicUUID));
            bluetoothGattCharacteristicWrite.setValue(writeData);
            writeCharacteristic(bluetoothGattCharacteristicWrite, 0);
            return;
        } else if (notifyBluetoothGattCharacteristic != null) {
            mBluetoothGatt.setCharacteristicNotification(notifyBluetoothGattCharacteristic, false);
            notifyBluetoothGattCharacteristic = null;
        }
        mBluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristicNotify, true);
        notifyBluetoothGattCharacteristic = bluetoothGattCharacteristicNotify;
        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristicNotify.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean status = mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
            if (!status) {
                LogUtil.e(TAG, "Change notification status to enable failed");
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothGattCharacteristic bluetoothGattCharacteristicWrite = mBluetoothGatt.getService(UUID.fromString(writeServiceUUID))
                        .getCharacteristic(UUID.fromString(writeCharacteristicUUID));
                bluetoothGattCharacteristicWrite.setValue(writeData);
                writeCharacteristic(bluetoothGattCharacteristicWrite, 0);
            }
        }, 2000);
    }

    protected void writeBluetooth(final String notifyServiceUUID, final String notifyCharacteristicUUID,
                                  final String writeServiceUUID, final String writeCharacteristicUUID, final byte[] content) {
        LogUtil.d(TAG, "Write Data:" + StringUtil.bytesToHexStr(content));
        final int maxLength = 20;
        if (content.length <= maxLength) {
            writeAndNotify(notifyServiceUUID, notifyCharacteristicUUID, writeServiceUUID, writeCharacteristicUUID, content);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                int length = content.length / maxLength;
                for (int i = 0; i < length; i++) {
                    if (i > 0) {
                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] byteTag = new byte[maxLength];
                    System.arraycopy(content, i * maxLength, byteTag, 0, maxLength);
                    writeAndNotify(notifyServiceUUID, notifyCharacteristicUUID, writeServiceUUID, writeCharacteristicUUID, byteTag);
                }
                int lastLength = content.length % maxLength;
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (lastLength != 0) {
                    byte[] byteTag = new byte[lastLength];
                    System.arraycopy(content, length * maxLength, byteTag, 0, lastLength);
                    writeAndNotify(notifyServiceUUID, notifyCharacteristicUUID, writeServiceUUID, writeCharacteristicUUID, byteTag);
                }
            }
        }.start();
    }

    private void writeCharacteristic(final BluetoothGattCharacteristic bluetoothGattCharacteristic, final int retryNumber) {
        boolean status = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        final int maxRetryNumber = 3;
        if (status) {
            LogUtil.d(TAG, "Bluetooth write success");
            if (!operationDone) {
                broadcastUpdate(ACTION_EXECUTED_SUCCESSFULLY);
                operationDone = true;
            }
            return;
        }
        LogUtil.e(TAG, "Bluetooth write failed , retryNumber:" + retryNumber);
        if (retryNumber <= maxRetryNumber) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    writeCharacteristic(bluetoothGattCharacteristic, retryNumber + 1);
                }
            }, 1000);
        } else if (!operationDone) {
            broadcastUpdate(ACTION_EXECUTED_FAILED);
            operationDone = true;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        close();
    }
}