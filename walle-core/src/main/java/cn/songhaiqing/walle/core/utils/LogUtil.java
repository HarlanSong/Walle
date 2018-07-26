package cn.songhaiqing.walle.core.utils;

import android.util.Log;
import cn.songhaiqing.walle.core.WalleConfig;

public class LogUtil {
    public static void d(String tag, String message) {
        if (WalleConfig.isDebug()) {
            Log.d(tag, WalleConfig.getLogTag() + message);
        }
    }

    public static void i(String tag, String message) {
        if (WalleConfig.isDebug()) {
            Log.i(tag, WalleConfig.getLogTag() + message);
        }
    }

    public static void w(String tag, String message) {
        if (WalleConfig.isDebug()) {
            Log.w(tag, WalleConfig.getLogTag() + message);
        }
    }

    public static void e(String tag, String message) {
        if (WalleConfig.isDebug()) {
            Log.e(tag, WalleConfig.getLogTag() + message);
        }
    }
}
