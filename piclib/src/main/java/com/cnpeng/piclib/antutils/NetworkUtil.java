package com.cnpeng.piclib.antutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络连接工具类
 *
 * @author wanglin  2016.5.25
 */
public class NetworkUtil {

    /**
     * 判断wifi是否连接
     */
    public static boolean isWifiConnect(Context context) {
        return getConnectedType(context) == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断移动数据是否连接
     */
    public static boolean isMobileConnect(Context context) {
        return getConnectedType(context) == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        return getConnectedType(context) != -1;
    }

    /**
     * 获取当前网络连接的类型信息, -1：没有网络, 0：移动网络, 1：WIFI网络, 2：wap网络, 3：net网络
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

//    public static String GetNetworkType() {
//        String strNetworkType = "";
//        ConnectivityManager mConnectivityManager = (ConnectivityManager) AntLinkApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                strNetworkType = "WIFI";
//            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//                String _strSubTypeName = networkInfo.getSubtypeName();
//
//
//                // TD-SCDMA   networkType is 17
//                int networkType = networkInfo.getSubtype();
//                switch (networkType) {
//                    case TelephonyManager.NETWORK_TYPE_GPRS:
//                    case TelephonyManager.NETWORK_TYPE_EDGE:
//                    case TelephonyManager.NETWORK_TYPE_CDMA:
//                    case TelephonyManager.NETWORK_TYPE_1xRTT:
//                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
//                        strNetworkType = "2G";
//                        break;
//                    case TelephonyManager.NETWORK_TYPE_UMTS:
//                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                    case TelephonyManager.NETWORK_TYPE_HSDPA:
//                    case TelephonyManager.NETWORK_TYPE_HSUPA:
//                    case TelephonyManager.NETWORK_TYPE_HSPA:
//                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
//                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
//                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
//                        strNetworkType = "3G";
//                        break;
//                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
//                        strNetworkType = "4G";
//                        break;
//                    default:
//                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
//                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
//                            strNetworkType = "3G";
//                        } else {
//                            strNetworkType = _strSubTypeName;
//                        }
//
//                        break;
//                }
//
//            }
//        }
//        return strNetworkType;
//    }

    /**
     * 获取本机Ip地址， 可以获取Gprs和wifi的
     */
    public String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

}
