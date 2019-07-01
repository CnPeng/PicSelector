package com.cnpeng.piclib.model;

import com.cnpeng.piclib.entity.ImgPreviewBean;
import com.cnpeng.piclib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：CnPeng
 * 时间：2018/5/28 下午2:54
 * 功用：传递图片预览时所需要的全部数据
 * 说明：之所以使用这个单例类传递数据，而不是用 intent ，是因为：intent的最大传递数据量在 500k-1M之间，
 * 虽然我们传递的实际是图片的uri或者 url,但是图片比较多的时候，url数组也是很大，可能会导致崩溃
 */
public class ImagePreviewHolder2 {
    private static final ImagePreviewHolder2  holder    = new ImagePreviewHolder2();
    /**
     * 需要查看大图的图片集合
     */
    private              List<ImgPreviewBean> mImgList  = new ArrayList();
    /**
     * 需要查看大图的图片集合
     */
    private              List<LocalMedia>     mImgList2 = new ArrayList();
    /**
     * 当前被点击的索引位置
     */
    private              int                  mCurSelectedIndex;
    /**
     * 是网络图片还是本地图片，默认false，表示本地图片；true 表示网络图片
     */
    private              boolean              mIsFromNet;

    private ImagePreviewHolder2() {
    }

    public static ImagePreviewHolder2 getInstance() {
        return holder;
    }

    public int getCurSelectedIndex() {
        return mCurSelectedIndex;
    }

    public void setCurSelectedIndex(int curSelectedIndex) {
        this.mCurSelectedIndex = curSelectedIndex;
    }


    /**
     * 获取需要查看大图的网络图片
     */
    public List<ImgPreviewBean> getImgList() {
        return mImgList;
    }

    /**
     * 设置需要查看大图的网络图片集合
     */
    public void setImgList(List<ImgPreviewBean> imgList) {
        mImgList = imgList;
    }

    /**
     * 获取需要查看大图的本地图片集合
     */
    public List<LocalMedia> getImgList2() {
        return mImgList2;
    }

    /**
     * 设置需要查看大图的本地图片集合
     */
    public void setImgList2(List<LocalMedia> imgList2) {
        mImgList2 = imgList2;
    }

    public boolean isFromNet() {
        return mIsFromNet;
    }

    public void setFromNet(boolean isFromNet) {
        mIsFromNet = isFromNet;
    }
}
