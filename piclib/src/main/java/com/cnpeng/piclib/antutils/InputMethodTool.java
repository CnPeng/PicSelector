package com.cnpeng.piclib.antutils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class InputMethodTool {
    /**
     * // 隐藏软键盘,软键盘在的时候是隐藏，软键盘不在的时候是显示
     */
    public static void hideInputManager(Context ct) {
        try {
            View curFocusView = ((Activity) ct).getCurrentFocus();
            if (null == curFocusView) {
                return;
            }
            IBinder windowToken = curFocusView.getWindowToken();
            InputMethodManager inputMethodManager = ((InputMethodManager) ct.getSystemService(Context.INPUT_METHOD_SERVICE));
            if (null != windowToken && null != inputMethodManager) {
                inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            Log.e("hide inputManager", "hideInputManager Catch error,skip it!", e);
        }
    }

    public static void inputMethodShowOrHideWindow(final EditText edittext) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm;
                imm = (InputMethodManager) edittext.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 40);
    }

    public static void inputMethodHide(final EditText edittext) {
        // 隐藏软键盘
        InputMethodManager imm = (InputMethodManager) edittext.getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }

    public static void inputMethodShow(EditText edittext) {
        inputMethodHide(edittext);
        inputMethodShowOrHideWindow(edittext);
    }
}
