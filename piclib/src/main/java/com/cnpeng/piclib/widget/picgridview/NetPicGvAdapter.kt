package com.cnpeng.piclib.widget.picgridview

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.cnpeng.piclib.ImagePagerActivity2
import com.cnpeng.piclib.R
import com.cnpeng.piclib.databinding.PiclibItemNetPicGvBinding
import com.cnpeng.piclib.entity.ImgPreviewBean
import com.cnpeng.piclib.model.ImagePreviewHolder2
import com.cnpeng.piclib.tools.GlideUtil
import org.jetbrains.anko.startActivity

/**
 * 作者：CnPeng
 * 时间：2019-05-09
 * 功用：专用于展示照片列表的 GridView 适配器
 * 其他：
 */
class NetPicGvAdapter(private val mMaxPicCount: Int) : BaseAdapter() {

    var mMediaList: MutableList<ImgPreviewBean> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int {
        return if (mMediaList.isEmpty()) {
            0
        } else {
            mMediaList.size
        }
    }

    override fun getItem(position: Int): Any {
        return mMediaList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val itemHolder: ItemHolder
        val itemBinding: PiclibItemNetPicGvBinding

        if (null == itemView) {
            val inflater = LayoutInflater.from(parent.context)
            itemBinding = DataBindingUtil.inflate(inflater, R.layout.piclib_item_net_pic_gv, parent, false)
            itemHolder = ItemHolder(itemBinding)
            itemView = itemBinding.root
            itemView.tag = itemHolder
        } else {
            itemHolder = itemView.tag as ItemHolder
            itemBinding = itemHolder.itemBinding
        }

        itemBinding.ivDelPic.visibility = View.GONE
        //CnPeng 2019-07-16 16:34 没想明白为什么使用 BindingAdapter 时一直不能正常编译，只能用这种方式了
        GlideUtil.loadImage(itemBinding.ivPic, mMediaList[position].url)

        initItemClickListener(itemBinding, position)
        itemBinding.executePendingBindings()
        return itemView
    }

    private fun initItemClickListener(itemBinding: PiclibItemNetPicGvBinding, position: Int) {

        //CnPeng 2019-07-03 10:47 条目点击进入预览状态或者继续添加图片
        itemBinding.root.setOnClickListener {

            // CnPeng 2019-05-10 10:13 跳转到大图查看界面
            val previewHolder = ImagePreviewHolder2.getInstance()
            previewHolder.curSelectedIndex = position
            previewHolder.imgList = mMediaList
            previewHolder.isFromNet = true

            val context = itemBinding.root.context
            val intent = Intent(context, ImagePagerActivity2::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity<ImagePagerActivity2>()
        }
    }

    internal inner class ItemHolder(val itemBinding: PiclibItemNetPicGvBinding)
}