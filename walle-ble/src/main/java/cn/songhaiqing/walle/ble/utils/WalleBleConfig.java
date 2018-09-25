package cn.songhaiqing.walle.ble.utils;


public class WalleBleConfig {
    private static boolean debug = false;
    private static String LOG_TAG = "WalleBle ";

    public static void setLogTag(String tag) {
        if (tag == null) {
            return;
        }
        LOG_TAG = tag;
    }

    public static String getLogTag() {
        return LOG_TAG;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean isDebug) {
        debug = isDebug;
    }
}
