package com.cnpeng.piclib.tools

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cnpeng.piclib.R

/**
 * CnPeng 2019-07-16
 * 功用：Glide 加载工具
 * 其他：
 */
object GlideUtil {

    /**
     * CnPeng 2019-05-10 08:34
     * 功用：加载图片到指定 ImageView 中
     * 说明：
     * 1、兼容 Glide 的 load 方法所支持的： Url, drawableID ,bitmap ,drawable ,URI ,File ,URL ,ByteArray
     * 2、由于 @BindingAdapter 中 requireAll=false 表示在布局文件中为 ImageView 引用这些属性时可以只传递部分内容
     */
    fun loadImage(iv: ImageView,
                  imageUrl: String = "",
                  uri: Uri? = null,
                  @DrawableRes drawableId: Int = 0,
                  @DrawableRes defaultDrawableID: Int = R.drawable.ic_placeholder) {

        val options = RequestOptions()
                .error(defaultDrawableID)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        //                .placeholder(lDefaultDrawableId)

        Glide.with(iv)
                .load(
                        if (imageUrl.isEmpty()) {
                            uri ?: if (0 == drawableId) {
                                defaultDrawableID
                            } else {
                                drawableId
                            }
                        } else {
                            imageUrl
                        }
                )
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv)
    }
}