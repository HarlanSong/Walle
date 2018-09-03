package cn.songhaiqing.walle.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CameraActivity extends Activity {
    private final int REQUEST_CAMERA = 1;
    private final int REQUEST_CROP = 2;
    private File outputFile;

    private Uri cropUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        camera();
    }

    private void camera() {
        outputFile = new File(getExternalCacheDir(), "output.jpg");
        try {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this,
                    "cn.songhaiqing.walle.gallery.FileProvider", outputFile);
        } else {
            uri = Uri.fromFile(outputFile);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAMERA:
                crop();
                break;
            case REQUEST_CROP:
                saveCropImage();
                break;
        }
    }

    private void saveCropImage(){
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropUri));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void crop() {
            File cutFile = new File(getExternalCacheDir(), "crop.jpg"); //随便命名一个
          /*  if (cutFile.exists()) {
                cutFile.delete();
            }
            cutFile.createNewFile();*/
            Uri imageUri ;
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageUri = FileProvider.getUriForFile(this, "cn.songhaiqing.walle.gallery.FileProvider",
                        outputFile);
            } else {
                imageUri = Uri.fromFile(outputFile);
            }
            cropUri = Uri.fromFile(cutFile);
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (cropUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            }
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(intent, REQUEST_CROP);

    }
}
