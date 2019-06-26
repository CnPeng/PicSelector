package com.cnpeng.piclib.antutils;

import android.view.View;

/**
 * 作者：CnPeng
 * 时间：2018/10/15
 * 功用：数据绑定的事件处理handler基类
 * 其他：封装基类方便将Handler中的点击事件再反馈给对应页面
 */
public class BaseEventHandler {
    private HandleClickEventCallBack mEventCallBack;

    /**
     * CnPeng 2018/9/28 下午9:51
     * 功用：Activity/Fragment中调用
     * 说明：外部接收到该回调之后可以处理需要的事件，比如关闭 drawerLayout
     */
    public void setEventCallBack(HandleClickEventCallBack eventCallBack) {
        mEventCallBack = eventCallBack;
    }

    public interface HandleClickEventCallBack {
        /**
         * CnPeng 2018/10/15 下午8:16
         * 功用：Handler中的点击事件被调用了
         * 说明：当Handler中的点击事件被调用之后，可能需要与Activity/Fragment中的代码产生交互，为了避免高耦合，
         * 以回调的形式再将事件反馈给对应的界面。
         *
         * @param view 被点击的view
         */
        void onClickHappenedInHandler(View view);
    }

    /**
     * CnPeng 2018/10/15 下午8:16
     * 功用：当View被点击时，handler子类内部调用
     * 说明：通过该方法将回调暴露给外部
     */
    protected void setClickCallBack(View view) {
        if (null != mEventCallBack) {
            mEventCallBack.onClickHappenedInHandler(view);
        }
    }
}
