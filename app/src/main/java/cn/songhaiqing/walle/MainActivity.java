package cn.songhaiqing.walle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.songhaiqing.walle.core.WalleConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WalleConfig.setDebug(true);
    }

    public void onBle(View view) {
        Intent intent = new Intent(this, BleActivity.class);
        startActivity(intent);
    }
}
