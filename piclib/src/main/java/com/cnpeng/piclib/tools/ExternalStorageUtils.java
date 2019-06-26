package com.cnpeng.piclib.tools;

import java.text.DecimalFormat;

/**
 * Created by Android on 2016/4/17.
 * <p>
 * 自定义的获取以及处理文件大小的工具类
 */
public class ExternalStorageUtils {
    /**
     * size的类型转换  B ->KB ->MB ->GB
     */
    public static String converSize(Long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        if (size > (1024 * 1024 * 1024)) {
            return df.format(size / 1024 / 1024 / 1024) + "GB";
        } else if (size > (1024 * 1024)) {
            return df.format(size / 1024 / 1024) + "MB";
        } else if (size > 1024) {
            return df.format(size / 1024) + "KB";
        } else {
            return new DecimalFormat("#").format(size) + "B";
        }
    }

    /**
     * size的类型转换  B ->KB ->MB ->GB
     */
    public static String converSize(Double size) {
        DecimalFormat df = new DecimalFormat("#.00");
        if (size > (1024 * 1024 * 1024)) {
            return df.format(size / 1024 / 1024 / 1024) + "GB";
        } else if (size > (1024 * 1024)) {
            return df.format(size / 1024 / 1024) + "MB";
        } else if (size > 1024) {
            return df.format(size / 1024) + "KB";
        } else {
            return new DecimalFormat("#").format(size) + "B";
        }
    }

}
