package com.cnpeng.piclib.antwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cnpeng.piclib.antutils.ActivitiesCollector;
import com.cnpeng.piclib.antutils.ActivitiesOnResumeCollector;
import com.cnpeng.piclib.antutils.NetworkUtil;

/**
 * 功用：所有Activity的基类
 * 说明：
 * 2018/4/23 下午5:21  CnPeng 增加网络变化的广播监听
 */
public class TitleActivity extends AppCompatActivity {

    /**
     * 作者：CnPeng
     * 时间：2018/5/6 下午10:36
     * 功用：关键字--XX功能的关键词,这也是与后台约定的，不要随便改
     */
    public final String KEY_KEY_WORD          = "keyword";
    /**
     * 值：网络类型--wifi网络
     */
    public final String VALUE_NET_TYPE_WIFI   = "WiFi";
    /**
     * 值：网络类型--移动网络（非WiFi网络，包含2G /3G /4G 等）
     */
    public final String VALUE_NET_TYPE_MOBILE = "Mobile";


    //public class TitleActivity extends FragmentActivity {
    public  String                mCurNetType = "";
    public  boolean               mIsNetWorkAvailable;
    private NetworkChangeReceiver mNetworkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 凡是继承此活动的都回添加到管理器中（每开一个activity就加进集合）
        ActivitiesCollector.addActivity(this);

        checkNetWorkOnResume();
        initNetWorkChangeReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activity销毁的时候，就从activity的集合中删除
        ActivitiesCollector.removeActivity(this);

        unregisterReceiver(mNetworkChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //activity失去焦点的时候，从有焦点activity的集合中删除
        ActivitiesOnResumeCollector.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkNetWorkOnResume();

        //activity获取焦点后，就添加到获取焦点的activity的集合
        ActivitiesOnResumeCollector.addActivity(this);
    }

    private void checkNetWorkOnResume() {

        if (!NetworkUtil.isNetworkAvailable(TitleActivity.this)) {
            mIsNetWorkAvailable = false;
            netWrokUnAvailableOnResume();
        } else {
            mCurNetType = NetworkUtil.getConnectedType(this) == 1 ? VALUE_NET_TYPE_WIFI : VALUE_NET_TYPE_MOBILE;
            mIsNetWorkAvailable = true;
            netWorkAvailableOnResume(mCurNetType);
        }
    }

    /**
     * 作者：CnPeng
     * 时间：2018/4/23
     * 功用：初始化广场接收器
     * 说明： 广播放在oncreate()方法中防止重复执行
     */
    private void initNetWorkChangeReceiver() {
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkChangeReceiver, intentfilter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public Boolean judgeNetwork() {
        // 判断是否有网络
        ConnectivityManager connectionmanager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Boolean flog = true;
        try {
            NetworkInfo networkInfo = connectionmanager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                System.out.println("网络连接正常");
            } else {
                // 网络未连接
                Toast.makeText(this, "网络断开啦。。。", Toast.LENGTH_LONG).show();
                flog = false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return flog;
    }

    /**
     * 作者：CnPeng
     * 时间：2018/4/23
     * 功用：网络重新连接之后的事件处理
     * 说明：在titleActivity基类中空实现，主要是为了给子类复写.
     *
     * @param preNetType 网络类型--wifi网络 {@link VALUE_NET_TYPE_WIFI};
     *                   移动网络（非WiFi网络，包含2G /3G /4G 等）{@link VALUE_NET_TYPE_MOBILE}
     */
    public void onNetWorkReConnect(String preNetType, String newNetType) {

    }

    /**
     * 作者：CnPeng
     * 时间：2018/4/23
     * 功用：网络断开之后的事件处理。
     * 说明：在TitleActivity中空实现，主要是为了给子类复写
     *
     * @param preNetType 先前的网络类型
     */
    public void onNetWorkConnectBreak(String preNetType) {

    }

    /**
     * 作者：CnPeng
     * 时间：2018/4/23 下午5:37
     * 功用：在onCreate创建页面时网络不可用的事件处理
     * 说明：
     */
    public void netWrokUnAvailableOnResume() {

    }

    /**
     * 作者：CnPeng
     * 时间：2018/4/23 下午5:45
     * 功用：在onCreate创建页面时网络可用的事件处理
     * 说明：
     *
     * @param netType 网络类型
     */
    public void netWorkAvailableOnResume(String netType) {

    }

    /**
     * 作者：CnPeng
     * 时间：2018/7/31 下午5:02
     * 功用：关闭当前页面
     * 说明：部分回调中可能也包含自身的finish,所以需要封装这么一个单独的关闭activity的方法
     */
    public void closeThisAct() {
        finish();
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!NetworkUtil.isNetworkAvailable(TitleActivity.this)) {
                mIsNetWorkAvailable = false;
                onNetWorkConnectBreak(mCurNetType);
            } else {
                mIsNetWorkAvailable = true;
                String newNetType = NetworkUtil.getConnectedType(TitleActivity.this) == 1 ? VALUE_NET_TYPE_WIFI : VALUE_NET_TYPE_MOBILE;
                onNetWorkReConnect(mCurNetType, newNetType);
                mCurNetType = newNetType;
            }
        }
    }
}
