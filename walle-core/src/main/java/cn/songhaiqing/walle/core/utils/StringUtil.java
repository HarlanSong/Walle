package cn.songhaiqing.walle.core.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harlan on 11/28/2017.
 */

public class StringUtil {


    /**
     * byte[] --> 十六进制的字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexStr(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(" " + 0 + hv);
            } else {
                stringBuilder.append(" " + hv);
            }
        }
        return stringBuilder.toString().trim();
    }

    public static List<Integer> bytesToArrayList(byte[] bytes){
        List<Integer> datas = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            datas.add(bytes[i] & 0xff);
        }
        return datas;
    }
}
