package cn.songhaiqing.walle.core;


public class WalleConfig {
    private static boolean debug = false;
    private static String LOG_TAG = "Walle";

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
