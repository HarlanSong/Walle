package cn.songhaiqing.walle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.songhaiqing.walle.core.WalleConfig;
import cn.songhaiqing.walle.core.utils.AppUtil;
import cn.songhaiqing.walle.gallery.GalleryActivity;
import cn.songhaiqing.walle.ui.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WalleConfig.setDebug(true);
        initTitle(null, getString(R.string.app_name), null);
    }

    public void onBle(View view) {
        startActivity(new Intent(this, BleActivity.class));
    }

    public void onChoosePhoto(View view) {
        startActivity(new Intent(this, ChoosePhotoActivity.class));
    }

    public void onUI(View view) {
        startActivity(new Intent(this, UIActivity.class));
    }
}
