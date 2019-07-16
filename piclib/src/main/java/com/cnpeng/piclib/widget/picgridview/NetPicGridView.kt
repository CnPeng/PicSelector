package com.cnpeng.piclib.widget.picgridview

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView
import com.cnpeng.piclib.entity.ImgPreviewBean
import org.jetbrains.anko.dip

/**
 * CnPeng 2019-05-09 09:02
 * 功用：不让滚动的、专门用来展示网络 Pic 的 Gv
 * 说明：
 */
class NetPicGridView : GridView {

    private lateinit var mGvAdapter: NetPicGvAdapter

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

        mGvAdapter = NetPicGvAdapter(maxPicCount)
        adapter = mGvAdapter
    }

    /**
     * CnPeng:2019-07-03 08:55 更新图片数据
     */
    fun setDataList(picList: MutableList<ImgPreviewBean>) {
        mGvAdapter.mMediaList = picList
    }
}
