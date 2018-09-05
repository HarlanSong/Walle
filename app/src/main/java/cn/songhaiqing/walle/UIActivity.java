package cn.songhaiqing.walle;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import cn.songhaiqing.walle.ui.activity.BaseActivity;
import cn.songhaiqing.walle.ui.view.AlterDialog;
import cn.songhaiqing.walle.ui.view.ConfirmDialog;

public class UIActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        initTitle(this, getString(R.string.func_ui),null);
    }

    public void onConfirm(View view){
        ConfirmDialog.Builder dialog = new ConfirmDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title);
        dialog.setMessage(R.string.dialog_message);
        dialog.setPositiveButton(R.string.dialog_btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.create().show();
    }

    public void onAlter(View view){
        AlterDialog.Builder dialog = new AlterDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title);
        dialog.setMessage(R.string.dialog_message);
        dialog.setPositiveButton(R.string.dialog_btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.create().show();
    }
}
