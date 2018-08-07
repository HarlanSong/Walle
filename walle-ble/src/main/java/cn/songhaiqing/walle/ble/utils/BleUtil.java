package cn.songhaiqing.walle.ble.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import cn.songhaiqing.walle.ble.service.WalleBleService;
import cn.songhaiqing.walle.core.utils.LogUtil;

public class BleUtil {
    public static boolean bleConnected = false;

    public static boolean connectDevice(final Context context, String name, final String address) {
        Intent intent = new Intent(context,WalleBleService.class);
        context.startService(intent);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    LogUtil.e("BleUtil", e.getMessage());
                }
                Intent intent = new Intent(WalleBleService.ACTION_CONNECT_DEVICE);
                intent.putExtra(WalleBleService.EXTRA_DATA, address);
                context.sendBroadcast(intent);
            }
        }.start();
        return true;
    }

    public static void disConnect(Context context){
        Intent intent = new Intent(WalleBleService.ACTION_DISCONNECT_DEVICE);
        context.sendBroadcast(intent);
    }

    public static void broadcastReadBle(Context context, byte[] bytes, String serviceUUID,
                                        String characteristicUUID) {
        if(!bleConnected){
            return ;
        }
        Intent intent = new Intent(WalleBleService.ACTION_READ_BLE);
        intent.putExtra(WalleBleService.EXTRA_DATA_READ_SERVICE_UUID, serviceUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA_READ_CHARACTERISTIC_UUID, characteristicUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA, bytes);
        context.sendBroadcast(intent);
    }

    public static void broadcastWriteBle(Context context, String notifyServiceUUID,
                                         String notifyCharacteristicUUID, String writeServiceUUID,
                                         String writeCharacteristicUUID, byte[] bytes) {
        if(!bleConnected){
            return ;
        }
        Intent intent = new Intent(WalleBleService.ACTION_WRITE_BLE);
        intent.putExtra(WalleBleService.EXTRA_DATA_NOTIFY_SERVICE_UUID, notifyServiceUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA_NOTIFY_CHARACTERISTIC_UUID, notifyCharacteristicUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA_WRITE_SERVICE_UUID, writeServiceUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA_WRITE_CHARACTERISTIC_UUID, writeCharacteristicUUID);
        intent.putExtra(WalleBleService.EXTRA_DATA, bytes);
        context.sendBroadcast(intent);
    }
}
