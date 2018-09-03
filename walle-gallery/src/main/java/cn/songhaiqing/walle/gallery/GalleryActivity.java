package cn.songhaiqing.walle.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.GridView;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;
import cn.songhaiqing.walle.ui.activity.BaseActivity;

public class GalleryActivity extends BaseActivity {

    private GridView gvImage;
    private List<GalleryImage> galleryImages;
    private final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.Ext.init(getApplication());
        x.Ext.setDebug(true);
        setContentView(R.layout.walle_grallery_activity_gallery);
        initTitle(this, getString(R.string.walle_gallery_title_gallery), null);
        gvImage = findViewById(R.id.gv_image);
        galleryImages = queryImage();
        gvImage.setAdapter(new ImageAdapter(this, galleryImages));
    }

    private boolean validPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            }
            return false;
        }
        return true;
    }

    private List<GalleryImage> queryImage() {
        List<GalleryImage> images = new ArrayList<>();
        if (!validPermission()) {
            return images;
        }
        Uri uri = MediaStore.Images.Media.getContentUri("external");
        if (uri == null) {
            return images;
        }
        String[] projection = new String[]{MediaStore.Files.FileColumns._ID, // id
                MediaStore.Files.FileColumns.DATA, // 文件路径
                MediaStore.Files.FileColumns.SIZE, // 文件大小
                MediaStore.Files.FileColumns.DATE_MODIFIED}; // 修改日期
        Cursor cursor =  getContentResolver().query(uri, projection, null, null, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");
        if (cursor == null) {
            return images;
        }
        try {
            if (cursor.moveToFirst()) {
                int pathIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int sizeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                //int modifyIdx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED);
                do {
                    String path = cursor.getString(pathIndex);
                    GalleryImage image = new GalleryImage();
                    image.setOriginalPath(path);
                    image.setSize(cursor.getLong(sizeIndex));
                    images.add(image);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return images;
    }

}
