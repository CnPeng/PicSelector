package com.cnpeng.picselector

import android.app.Activity
import com.cnpeng.piclib.PictureSelector
import com.cnpeng.piclib.config.PictureMimeType
import com.cnpeng.piclib.permissions.RxPermissions

/**
 * CnPeng 2019-06-27
 * 功用：测试是否可以直接拍照或者录视频、测试是否可以在非 Activity/Fragment 中调用 RxPermission请求权限并响应回调
 * 其他：
 */
object TestHandler {
    fun openCamera(pActivity: Activity, pRxPermission: RxPermissions, pRequestCode: Int) {
        //        pRxPermission.request(Manifest.permission.READ_EXTERNAL_STORAGE,
        //                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        //                Manifest.permission.CAMERA)
        //                //CnPeng 2019-06-27 09:20 注意：subscribe 后面是小括号，不是大括号，如果用大括号不会触发重写的方法
        //                .subscribe(
        //                        object : Observer<Boolean> {
        //                            override fun onComplete() {
        //
        //                            }
        //
        //                            override fun onSubscribe(d: Disposable) {
        //                            }
        //
        //                            override fun onNext(t: Boolean) {
        //                                if (t) {
        PictureSelector.create(pActivity)
                .openGallery(PictureMimeType.ofImage())
                .isCamera(false)
                .maxSelectNum(1)
                //                .openCamera(PictureMimeType.ofImage())
                .enableCrop(true)
                .minPicHeight(1)
                .minPicWidth(1)
                .forResult(pRequestCode)
        //                                } else {
        //                                    Toast.makeText(pActivity, "获取权限失败", Toast.LENGTH_SHORT).show()
        //                                }
        //                            }
        //
        //                            override fun onError(e: Throwable) {
        //                            }
        //                        }
        //                )
    }
}