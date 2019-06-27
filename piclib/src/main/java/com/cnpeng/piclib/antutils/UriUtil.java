package com.cnpeng.piclib.antutils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * CnPeng 2019-06-27
 * 功用：URI 相关的工具类
 * 其他：
 */
public class UriUtil {
    private static final UriUtil OUR_INSTANCE = new UriUtil();

    public static UriUtil getInstance() {
        return OUR_INSTANCE;
    }

    private UriUtil() {
    }

    /**
     * CnPeng:2019-06-27 10:47 获取文件对象的 Uri
     */
    public Uri parseUri(Context pContext, File pFile) {
        Uri imageUri;
        String authority = pContext.getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(pContext, authority, pFile);
        } else {
            imageUri = Uri.fromFile(pFile);
        }
        return imageUri;
    }

}
