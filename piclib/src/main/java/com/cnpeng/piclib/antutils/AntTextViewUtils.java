package com.cnpeng.piclib.antutils;

import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 作者：CnPeng
 * 时间：2018/7/10
 * 功用：TextView相关的工具类
 * 其他：
 */
public class AntTextViewUtils {

    private static String TAG = "AntTextViewUtils";

    /**
     * 作者：CnPeng
     * 时间：2018/7/10 下午4:23
     * 功用：控制最大行数的展示
     * 说明：先判断map中是否已经存在该键值，如果存在，根据其状态控制textView的最大行数；如果不存在，设置视图树监听，判断是否超出最大行数
     *
     * @param textView 需要对最大行数作出控制的TextView
     * @param maxLines 最大行数
     * @param map      存储是否已经判断过是否需要收起的值.必须是一个成员变量
     * @param key      存储时的关键字
     */
    public static void handleTvMaxLine(@NonNull final TextView textView, final int maxLines, @NonNull final Map<Object, Boolean> map, final Object key, final OverMaxLinesListener listener) {
        boolean isKeyExist = map.containsKey(key);
        boolean expandableStatus = false;
        final int maxLinesToSet = maxLines <= 0 ? 3 : maxLines;
        // Log.i(TAG, "handleTvMaxLine");
        if (isKeyExist) {
            expandableStatus = map.get(key);
            if (expandableStatus) {
                textView.setMaxLines(maxLinesToSet);
            }
            if (null != listener) {
                listener.overMaxLines(expandableStatus);
                //Log.i(TAG, "handleTvMaxLine——触发监听1");
            }
        } else {
            // CnPeng 2018/7/10 下午5:56 此处使用preDrawListener，不要用 onGloableLayout,因为gloableLayout并不是每一次都触发
            textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int realLines = textView.getLineCount();
                    if (realLines > maxLinesToSet) {
                        textView.setMaxLines(maxLinesToSet);
                        map.put(key, true);
                    } else {
                        map.put(key, false);
                    }

                    if (null != listener) {
                        listener.overMaxLines(realLines > maxLinesToSet);
                        // Log.i(TAG, "handleTvMaxLine——触发监听2");
                    }

                    textView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }


    /**
     * 作者：CnPeng
     * 时间：2018/7/10 下午4:59
     * 功用：TextView的行数超过最大行数限制时的监听器
     */
    public interface OverMaxLinesListener {
        /**
         * 超过最大行数
         */
        void overMaxLines(boolean overMaxLine);
    }

}
