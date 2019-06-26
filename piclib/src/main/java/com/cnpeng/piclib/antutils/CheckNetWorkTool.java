package com.cnpeng.piclib.antutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetWorkTool {
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()) {
                return true;
            }
//            } else {
//                //网络未连接
//                ToastUtil.toastShort("网络断开啦...sdlf;af;afja;dfja;fjad;slk");
//            }
        }
        return false;
    }
}
