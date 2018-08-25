package cn.songhaiqing.walle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cn.songhaiqing.walle.gallery.GalleryActivity;
import cn.songhaiqing.walle.ui.activity.BaseActivity;

public class ChoosePhotoActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);
        initTitle(this, getString(R.string.func_choose_photo), null);
    }

    public void onGallery(View v) {
        startActivity(new Intent(this, GalleryActivity.class));
    }
}
