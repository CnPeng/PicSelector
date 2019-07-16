package com.cnpeng.piclib.widget.picgridview

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView
import com.cnpeng.piclib.entity.LocalMedia
import org.jetbrains.anko.dip

/**
 * CnPeng 2019-05-09 09:02
 * 功用：不让滚动的、专门用来展示 Pic 的 Gv
 * 说明：
 */
class PicGridView : GridView {

    private lateinit var mGvAdapter: PicGvAdapter

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

    /**
     * CnPeng:2019-07-03 08:45 初始化图片GV
     * 之所以没有放在构造中是考虑到方便后期将这些参数暴露，方便自定义。
     *
     * @param itemSpace 单位 dp
     */
    fun initPicGv(itemSpace: Int = 8, maxPicCount: Int = 9, picColumns: Int = 3) {

        val gvItemSpace = dip(itemSpace)

        numColumns = picColumns
        isVerticalScrollBarEnabled = false
        stretchMode = STRETCH_COLUMN_WIDTH
        verticalSpacing = gvItemSpace
        horizontalSpacing = gvItemSpace

        mGvAdapter = PicGvAdapter(maxPicCount)
        adapter = mGvAdapter

    }

    /**
     * CnPeng:2019-07-03 09:16 取消可删除状态
     *
     * 通常是点击返回键的时候取消删除状态
     */
    fun cancelDelEditEvent() {
        if (mGvAdapter.mIsBeingDel) {
            mGvAdapter.mIsBeingDel = false
        }
    }

    /**
     * CnPeng:2019-07-03 11:39 是否正在处理删除事件/处于可删除状态
     */
    fun isDoingDelEvent(): Boolean {
        return mGvAdapter.mIsBeingDel
    }

    /**
     * CnPeng:2019-07-03 11:28 选择新的照片的条目被点击时的事件处理
     *
     * 需要由外部控制是拍照、选择或者两者混合，所以，这个选择事件没有从 PicGvAdapter 中做封装
     */
    fun setChooseNewPicListener(chooseNewPicListener: PicGvAdapter.ChooseNewPicListener) {
        mGvAdapter.mChooseNewPicListener = chooseNewPicListener
    }

    /**
     * CnPeng:2019-07-08 11:24 条目点击的事件监听
     */
    fun setPicItemClickListener(picItemClickListener: PicGvAdapter.PicItemClickListener) {
        mGvAdapter.mPicItemClickListener = picItemClickListener
    }

    /**
     * CnPeng:2019-07-03 08:55 更新图片数据
     */
    fun setDataList(picList: MutableList<LocalMedia>?) {
        if (null != picList) {
            mGvAdapter.mMediaList = picList
        }
    }
}
