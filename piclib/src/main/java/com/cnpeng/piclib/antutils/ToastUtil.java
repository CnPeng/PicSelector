package com.cnpeng.piclib.antutils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast封装提示
 *
 * @author wanglin  2016.5.25
 */
public class ToastUtil {

    private static Toast toast;    // 提示框，静态全局

    /**
     * 普通提示
     *
     * @param strRes 显示信息
     */
    public static void toastShort(int strRes, Context context) {
        toastShort(strRes, 0, context);
    }

    public static void toastShort(int strRes, int gravity, Context context) {
        if (null == context.getApplicationContext()) {
            return;
        }
        toastShow(context.getApplicationContext().getString(strRes), Toast.LENGTH_SHORT, null, gravity, context);
    }

    /**
     * 普通提示
     *
     * @param strRes 显示信息
     */
    public static void toastLong(int strRes, Context context) {
        toastLong(strRes, 0, context);
    }

    /**
     * 普通提示
     *
     * @param strRes 显示信息
     */
    public static void toastLong(int strRes, int gravity, Context context) {
        if (null == context.getApplicationContext()) {
            return;
        }
        toastShow(context.getApplicationContext().getString(strRes), Toast.LENGTH_LONG, null, gravity,context);
    }

    /**
     * 普通提示，   Toast.LENGTH_SHORT
     *
     * @param strInfo 显示信息
     */
    public static void toastShort(String strInfo, int gravity, Context context) {
        if (null == context.getApplicationContext()) {
            return;
        }
        if (!TextUtils.isEmpty(strInfo)) {
            toastShow(strInfo, Toast.LENGTH_SHORT, null, gravity,context);
        }
    }

    public static void toastShort(String strInfo, Context context) {
        toastShort(strInfo, 0, context);
    }

    public static void toastShortTop(String strInfo, Context context) {
        if (null == context.getApplicationContext()) {
            return;
        }
        if (!TextUtils.isEmpty(strInfo)) {
            toastShow(strInfo, Toast.LENGTH_SHORT, null, Gravity.TOP,context);
        }
    }

    /**
     * 普通提示，   Toast.LENGTH_SHORT
     *
     * @param strInfo 显示信息
     */
    public static void toastLong(String strInfo, int gravity, Context context) {
        if (null == context.getApplicationContext()) {
            return;
        }
        if (!TextUtils.isEmpty(strInfo)) {
            toastShow(strInfo, Toast.LENGTH_LONG, null, gravity,context);
        }
    }

    public static void toastLong(String strInfo, Context context) {
        toastLong(strInfo, 0, context);
    }

    /**
     * 设置信息显示
     *
     * @param message   显示信息
     * @param nKeepTime 显示时间
     * @param able      显示的图片
     */
    public static void toastShow(String message, int nKeepTime, Drawable able, int gravity, Context context) {
        ToastInfo toastInfo = new ToastInfo();
        toastInfo.msg = message;
        toastInfo.nKeepTime = nKeepTime;
        toastInfo.able = able;
        toastInfo.gravity = gravity;
        ToastHandler handler = new ToastHandler(Looper.getMainLooper(), context);
        Message msg = handler.obtainMessage();
        msg.arg1 = 0;
        msg.obj = toastInfo;
        handler.sendMessage(msg);
    }

    /**
     * 显示Toast，必须主线程调用
     *
     * @param strInfo   显示信息
     * @param nKeepTime 显示时间
     * @param able      显示的图片
     */
    private static void ToastShowWithDrawable(String strInfo, int nKeepTime, Drawable able, int gravity, Context context) {
        try {
            if (null == context.getApplicationContext()) {
                return;
            }

            if (nKeepTime < 0) {
                nKeepTime = Toast.LENGTH_SHORT;
            }

            Context appContext = context.getApplicationContext();

            if (toast != null) {
                //共用的同一个toast，如果先设置在top上显示，下次就回去不去了，所以需要写到下面去
                if ((gravity == 0 || gravity == Gravity.BOTTOM) && toast.getGravity() == Gravity.TOP) {
                    gravity = Gravity.BOTTOM;
                    toast.setGravity(gravity, 0, DisplayUtil.dip2px(appContext, 45));
                } else if (gravity == Gravity.TOP && toast.getGravity() == Gravity.BOTTOM) {
                    gravity = Gravity.TOP;
                    toast.setGravity(gravity, 0, DisplayUtil.dip2px(appContext, 45));
                }
                toast.setText(strInfo);
            } else {
                toast = Toast.makeText(appContext, strInfo, nKeepTime);
                if (gravity == 0) {
                    gravity = Gravity.BOTTOM;
                }
                toast.setGravity(gravity, 0, DisplayUtil.dip2px(appContext, 45));
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 子线程与主线程的通讯
     * 使子线程也可以通过通知主线程调用Toast
     */
    static class ToastHandler extends Handler {
        Context context;

        ToastHandler(Looper mainLooper, Context context) {
            super(mainLooper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg != null && msg.arg1 == 0 && msg.obj != null) {
                ToastInfo info = (ToastInfo) msg.obj;
                ToastShowWithDrawable(info.msg, info.nKeepTime, info.able, info.gravity, context);
            }
        }
    }

    /**
     * Toast信息携带类
     */
    private static class ToastInfo {
        String   msg;
        int      nKeepTime;
        Drawable able;
        int      gravity;
    }
}
