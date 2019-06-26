package com.cnpeng.piclib.antutils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by yxj on 17/9/6.
 */

public class ConverSionTool {
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px(Context context, double dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getDpSizeData(Context context, float pxValue) {//获得单位是dp的数值
        int dipSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, pxValue, context.getResources()
                        .getDisplayMetrics());
        return dipSize;
    }

    /**
     * 返回手机的屏幕分辨率的String
     */
    public static String getDpiType(Context context) {//获得单位是dp的数值
        float xdpi = context.getResources().getDisplayMetrics().xdpi;
//		0dpi ~ 120dpi	ldpi
//		120dpi ~ 160dpi	mdpi
//		160dpi ~ 240dpi	hdpi
//		240dpi ~ 320dpi	xhdpi
//		320dpi ~ 480dpi	xxhdpi
//		480dpi ~ 640dpi	xxxhdpi
        if (xdpi < 240) return "hdpi";
        if (xdpi < 320) return "xhdpi";
        if (xdpi < 480) return "xxhdpi";
        if (xdpi < 640) return "xxxhdpi";
        return "hdpi";
    }

    /**
     * 返回手机的屏幕分辨率的String
     */
    public static String getDpiType2(Context context) {//获得单位是dp的数值
        float xdpi = context.getResources().getDisplayMetrics().xdpi;
//		0dpi ~ 120dpi	ldpi
//		120dpi ~ 160dpi	mdpi
//		160dpi ~ 240dpi	hdpi
//		240dpi ~ 320dpi	xhdpi
//		320dpi ~ 480dpi	xxhdpi
//		480dpi ~ 640dpi	xxxhdpi
        if (xdpi < 240) return "@1.5x";
        if (xdpi < 320) return "@2x";
        if (xdpi < 480) return "@3x";
        return "@4x";
    }
}
