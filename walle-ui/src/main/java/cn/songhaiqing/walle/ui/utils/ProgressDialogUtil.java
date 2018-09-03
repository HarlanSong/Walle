package cn.songhaiqing.walle.ui.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {
    private static ProgressDialog  progressDialog;

    public static void show(Context context,int messageId){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(messageId));
        progressDialog.show();
    }

    public static void dismiss(){
        if(progressDialog == null || !progressDialog.isShowing()){
            return;
        }
        progressDialog.dismiss();
        progressDialog = null;
    }
}
