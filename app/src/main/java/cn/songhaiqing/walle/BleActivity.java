package cn.songhaiqing.walle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.songhaiqing.walle.ble.activity.DeviceScanActivity;
import cn.songhaiqing.walle.ble.service.WalleBleService;
import cn.songhaiqing.walle.ble.utils.BleUtil;

public class BleActivity extends Activity implements View.OnClickListener {
    private final int REQUEST_BIND_DEVICE = 1;

    private Button btnBleOption;
    private Button btnScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WalleBleService.ACTION_CONNECTED_SUCCESS);
        intentFilter.addAction(WalleBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(WalleBleService.ACTION_DEVICE_RESULT);

        registerReceiver(bleReceiver, intentFilter);

        btnBleOption = findViewById(R.id.btn_ble_option);
        btnScan = findViewById(R.id.btn_scan);
        btnBleOption.setOnClickListener(this);
        btnScan.setOnClickListener(this);
    }

    private BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WalleBleService.ACTION_CONNECTED_SUCCESS.equals(action)) {

            } else if (WalleBleService.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (WalleBleService.ACTION_DEVICE_RESULT.equals(action)) {

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (REQUEST_BIND_DEVICE == requestCode) {
            String name = data.getStringExtra("name");
            String macAddress = data.getStringExtra("macAddress");
            Toast.makeText(this, "name:" + name + " macAddress:" + macAddress, Toast.LENGTH_LONG).show();

            BleUtil.connectDevice(this, name, macAddress);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bleReceiver);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.btn_scan:
                Intent intent = new Intent(this, DeviceScanActivity.class);
                startActivityForResult(intent, REQUEST_BIND_DEVICE);
                break;
            case R.id.btn_ble_option:
                BleUtil.disConnect(this);
                break;
        }
    }
}