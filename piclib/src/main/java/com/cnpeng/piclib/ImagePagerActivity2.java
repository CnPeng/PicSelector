package com.cnpeng.piclib;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.cnpeng.piclib.adapter.ImagePagerAdapter;
import com.cnpeng.piclib.antwidget.TitleActivity;
import com.cnpeng.piclib.databinding.ActivityImageDetailPreviewBinding;
import com.cnpeng.piclib.entity.ImgPreviewBean;
import com.cnpeng.piclib.entity.LocalMedia;
import com.cnpeng.piclib.model.ImagePreviewHolder2;

import java.util.List;

/**
 * 作者：CnPeng
 * 时间：2018/5/28 下午8:52
 * 功用：查看图片大图的页面，包含缩放操作。
 * 说明：页面接收需要预览的图片对象集合，这样方便扩展被预览图片的属性。另外，对于长图的展示做了优化
 */
public class ImagePagerActivity2 extends TitleActivity implements ImagePagerAdapter.OnImgClickListener {
    private static final String                            STATE_POSITION = "STATE_POSITION";
    private              int                               mCurSelectedPosition;
    private              List<ImgPreviewBean>              mNetImages;
    private              List<LocalMedia>                  mLocalImages;
    private              ActivityImageDetailPreviewBinding mBinding;
    /**
     * true 查看网络大图预览，比如：长文详情页面中的大图；false 查看本地大图，默认值就是false
     */
    private              boolean                           mIsFromNet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_detail_preview);

        getDataFromHolder();

        initViewPager();
    }


    private void initViewPager() {
        ImagePagerAdapter mAdapter = null;
        if (mIsFromNet) {
            mAdapter = new ImagePagerAdapter(this, mNetImages);
        } else {
            mAdapter = new ImagePagerAdapter(mLocalImages, this);
        }

        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.viewPager.setCurrentItem(mCurSelectedPosition);

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (null != mNetImages && null != mNetImages.get(position)) {
                    mBinding.setImgDesc(mNetImages.get(position).getDesc());
                }
                mBinding.setCurPosition(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getDataFromHolder() {
        ImagePreviewHolder2 previewHolder = ImagePreviewHolder2.getInstance();
        mIsFromNet = previewHolder.isFromNet();
        mCurSelectedPosition = previewHolder.getCurSelectedIndex();

        if (mIsFromNet) {
            mNetImages = previewHolder.getImgList();
            mBinding.setTotalSize(mNetImages.size());
            if (null != mNetImages && null != mNetImages.get(mCurSelectedPosition)) {
                mBinding.setImgDesc(mNetImages.get(mCurSelectedPosition).getDesc());
            }
        } else {
            mLocalImages = previewHolder.getImgList2();
            mBinding.setTotalSize(mLocalImages.size());
        }
        //初始化界面时先填入数据，否则，进入页面时被选中的那张图不展示描述信息
        mBinding.setCurPosition(mCurSelectedPosition + 1);
    }

    @Override
    public void onImgClicked() {
        finish();
    }
}