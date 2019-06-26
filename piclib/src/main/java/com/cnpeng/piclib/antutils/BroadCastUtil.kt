package com.cnpeng.piclib.antutils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * 作者：CnPeng
 * 时间：2019/3/21
 * 功用：广播工具类
 * 其他：
 */
class BroadCastUtil(val context: Context) {

    /**
     * CnPeng 2019/3/21 10:08 AM
     * 功用：发送本地广播
     * 说明：
     * @param pAction 为广播的Action。
     * @param pBundle 内部封装了携带的数据. 默认为 null
     */
    @JvmOverloads
    fun sendLocalBroadCast(pAction: String, pBundle: Bundle? = null) {
        val intent = Intent(pAction);
        intent.putExtra("broadCastAction", pAction)
        if (null != pBundle) {
            intent.putExtra("broadCastExtra", pBundle)
        }
        val localBroadcastManager = LocalBroadcastManager.getInstance(context)
        localBroadcastManager.sendBroadcast(intent)
    }

}