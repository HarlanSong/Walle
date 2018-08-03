package cn.songhaiqing.walle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.songhaiqing.walle.core.WalleConfig;
import cn.songhaiqing.walle.ui.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WalleConfig.setDebug(true);
        initTitle(null,getString(R.string.app_name),null);
    }

    public void onBle(View view) {
        Intent intent = new Intent(this, BleActivity.class);
        startActivity(intent);
    }
}
