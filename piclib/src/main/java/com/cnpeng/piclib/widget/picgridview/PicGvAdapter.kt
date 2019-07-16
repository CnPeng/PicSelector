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
import com.cnpeng.piclib.databinding.PiclibItemPicGvBinding
import com.cnpeng.piclib.entity.LocalMedia
import com.cnpeng.piclib.model.ImagePreviewHolder2
import com.cnpeng.piclib.tools.GlideUtil
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity

/**
 * 作者：CnPeng
 * 时间：2019-05-09
 * 功用：专用于展示照片列表的 GridView 适配器
 * 其他：
 */
class PicGvAdapter(private val mMaxPicCount: Int) : BaseAdapter() {

    var mMediaList: MutableList<LocalMedia> = mutableListOf()
        set(value) {
            // mMediaList.clear()
            // mMediaList.addAll(value)
            field = value
            notifyDataSetChanged()
        }

    var mIsBeingDel = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int {
        return if (mMediaList.size < mMaxPicCount) mMediaList.size + 1 else mMaxPicCount
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
        val itemBinding: PiclibItemPicGvBinding

        if (null == itemView) {
            val inflater = LayoutInflater.from(parent.context)
            itemBinding = DataBindingUtil.inflate(inflater, R.layout.piclib_item_pic_gv, parent, false)
            itemHolder = ItemHolder(itemBinding)
            itemView = itemBinding.root
            itemView.tag = itemHolder
        } else {
            itemHolder = itemView.tag as ItemHolder
            itemBinding = itemHolder.itemBinding
        }

        val layoutParams = itemBinding.ivPic.layoutParams
        var picUrl = ""
        if (0 == position && mMediaList.size < mMaxPicCount) {
            itemBinding.ivDelPic.visibility = View.GONE

            layoutParams.width = itemBinding.root.context.dip(50)
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            picUrl = mMediaList[if (mMediaList.size < mMaxPicCount) position - 1 else position].path
            itemBinding.ivDelPic.visibility = if (mIsBeingDel) View.VISIBLE else View.GONE

            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        itemBinding.ivPic.layoutParams = layoutParams

        //CnPeng 2019-07-16 16:34 没想明白为什么使用 BindingAdapter 时一直不能正常编译，只能用这种方式了
        GlideUtil.loadImage(itemBinding.ivPic, picUrl)

        initItemClickListener(itemBinding, position)

        itemBinding.executePendingBindings()
        return itemView
    }

    private fun initItemClickListener(itemBinding: PiclibItemPicGvBinding, position: Int) {
        //CnPeng 2019-07-03 10:43 删除按钮的事件
        itemBinding.ivDelPic.setOnClickListener {
            val removedPos = if (mMediaList.size < mMaxPicCount) position - 1 else position

            mMediaList.removeAt(removedPos)

            //CnPeng 2019-07-03 12:00 如果数据空了，清除删除状态
            if (mMediaList.isEmpty() && mIsBeingDel) {
                mIsBeingDel = false
            }
            notifyDataSetChanged()
        }

        //CnPeng 2019-07-03 10:43 长按进入删除状态
        itemBinding.root.setOnLongClickListener {
            if (mMediaList.size < mMaxPicCount && position == 0) {
                //CnPeng 2019-07-03 10:55 添加图片的按钮长按之后不做处理
            } else {
                if (!mIsBeingDel) {
                    mIsBeingDel = true
                }
            }
            true
        }

        //CnPeng 2019-07-03 10:47 条目点击进入预览状态或者继续添加图片
        itemBinding.root.setOnClickListener {
            if (mMediaList.size < mMaxPicCount && 0 == position) {
                mChooseNewPicListener?.onChooseNewPic()

                if (mIsBeingDel) {
                    mIsBeingDel = false
                }

            } else {
                val curSelectedIndex = if (mMediaList.size < mMaxPicCount) {
                    position - 1
                } else {
                    position
                }

                // CnPeng 2019-05-10 10:13 跳转到大图查看界面
                val previewHolder = ImagePreviewHolder2.getInstance()
                previewHolder.curSelectedIndex = curSelectedIndex
                previewHolder.imgList2 = mMediaList
                previewHolder.isFromNet = false

                val context = itemBinding.root.context
                val intent = Intent(context, ImagePagerActivity2::class.java)
                intent.flags = FLAG_ACTIVITY_NEW_TASK
                context.startActivity<ImagePagerActivity2>()
            }

            //CnPeng 2019-07-08 11:22 点击事件对外暴露
            mPicItemClickListener?.onPicItemClick(position)
        }
    }

    internal inner class ItemHolder(val itemBinding: PiclibItemPicGvBinding)

    interface ChooseNewPicListener {
        fun onChooseNewPic()
    }

    var mChooseNewPicListener: ChooseNewPicListener? = null


    interface PicItemClickListener {
        /**
         * CnPeng:2019-07-08 11:20 Pic Gv 中的条目被点击了，position 为view中的position
         * 注意：外部不需要处理跳转到大图预览界面的事件，内部已经处理过了。该方法中只需要处理一些附件操作即可
         */
        fun onPicItemClick(position: Int)
    }

    var mPicItemClickListener: PicItemClickListener? = null
}