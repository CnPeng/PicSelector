package com.cnpeng.piclib.antutils;

import android.util.Log;

/**
 * Created by yxj on 17/8/2.
 */

public class IntegerUtil {
    public static int parseInt(String str) {
        int i = 0;
        if (str != null) {
            try {
                i = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                Log.d(IntegerUtil.class.getName(), "解析数值有问题");
            }
        }
        return i;
    }
}
