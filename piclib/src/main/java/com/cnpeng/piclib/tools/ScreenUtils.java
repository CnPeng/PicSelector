package com.cnpeng.piclib.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

/**
 * Created by dee on 15/11/19.
 */
public class ScreenUtils {
    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels - getStatusBarHeight(context);
    }

    /**
     * CnPeng:2019-07-23 11:34 获取完整的高度，包括状态栏、底部虚拟导航栏
     *
     * 参考链接：https://blog.csdn.net/Kikitious_Du/article/details/78584326
     * getRealMetrics(metric) 得到的是包含底部导航和状态栏的完整屏幕高度
     * getMetrics(metric) 得到的是不包含底部虚拟导航栏的高度
     * 上述两个函数中的 metric.heightPixels 相减就能得到底部虚拟导航的高度
     */
    public static int getFullScreenHeight(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        //        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }


    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
