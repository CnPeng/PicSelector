package com.cnpeng.piclib.antutils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 程序路径类
 *
 * @author wanglin  2016.5.25
 */
public class PathUtil {

    public static String getPathSDCard() {
        String pathSDCard;
        if (SDCardUtil.isSDCardEnable()) {
            File file = Environment.getExternalStorageDirectory();
            pathSDCard = file == null ? null : file.getPath() + "/antLinker";
        } else {
            pathSDCard = null;
        }
        AntFileUtil.checkFilePath(pathSDCard, true);
        return pathSDCard;
    }

    //    public static String getPathCache() {
    //        String pathCache;
    //        if (SDCardUtil.isSDCardEnable()) {
    //            File file = AntLinkApplication.getAppContext().getExternalCacheDir();
    //            pathCache = file == null ? AntLinkApplication.getAppContext().getCacheDir().getPath() : file.getPath();
    //        } else {
    //            pathCache = AntLinkApplication.getAppContext().getCacheDir().getPath();
    //        }
    //        return pathCache;
    //    }
    //
    public static String getPathFile(Context context) {
        String pathCache;
        if (SDCardUtil.isSDCardEnable()) {
            File file = context.getApplicationContext().getExternalFilesDir(null);
            pathCache = file == null ? context.getApplicationContext().getCacheDir().getPath() : file.getPath();
        } else {
            pathCache = context.getApplicationContext().getFilesDir().getPath();
        }
        return pathCache;
    }
    //
    //    public static String getPathLog() {
    //        return getPathFile() + "/log";
    //    }
    //
    //    public static String getPathImageLoaderCache() {
    //        return getPathCache() + "/image_loader";
    //    }


}