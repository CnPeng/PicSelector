package com.cnpeng.piclib;

import android.app.Activity;
import android.content.Intent;

import com.cnpeng.piclib.config.PictureSelectionConfig;
import com.cnpeng.piclib.entity.LocalMedia;
import com.cnpeng.piclib.tools.DoubleUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;

/**
 * author：luck
 * project：PictureSelector
 * describe：PictureSelector selection configuration.
 * email：893855882@qq.com
 * data：2017/5/24
 */

public class PictureSelectionModel {
    private PictureSelectionConfig               selectionConfig;
    private PictureSelector selector;

    public PictureSelectionModel(PictureSelector selector, int mimeType) {
        this.selector = selector;
        selectionConfig = PictureSelectionConfig.getCleanInstance();
        selectionConfig.mimeType = mimeType;
    }

    public PictureSelectionModel(PictureSelector selector, int mimeType, boolean camera) {
        this.selector = selector;
        selectionConfig = PictureSelectionConfig.getCleanInstance();
        selectionConfig.camera = camera;
        selectionConfig.mimeType = mimeType;
    }

    /**
     * @param themeStyleId PictureSelector Theme style
     */
    public PictureSelectionModel theme(@StyleRes int themeStyleId) {
        selectionConfig.themeStyleId = themeStyleId;
        return this;
    }

    /**
     * Start PictureSelector for Activity.
     *
     * @return PictureSelector instance.
     */
    public PictureSelectionModel addRecordVideoListener(RecordVideoListener recordVideoListener) {
        selectionConfig.recordVideoListene = recordVideoListener;
        return this;
    }

    /**
     * @param selectionMode PictureSelector Selection model and PictureConfig.MULTIPLE or PictureConfig.SINGLE
     */
    public PictureSelectionModel selectionMode(int selectionMode) {
        selectionConfig.selectionMode = selectionMode;
        return this;
    }

    /**
     * @param enableCrop Do you want to start cutting ?
     */
    public PictureSelectionModel enableCrop(boolean enableCrop) {
        selectionConfig.enableCrop = enableCrop;
        return this;
    }

    /**
     * @param enablePreviewAudio Do you want to play audio ?
     */
    public PictureSelectionModel enablePreviewAudio(boolean enablePreviewAudio) {
        selectionConfig.enablePreviewAudio = enablePreviewAudio;
        return this;
    }

    /**
     * @param freeStyleCropEnabled Crop frame is move ?
     */
    public PictureSelectionModel freeStyleCropEnabled(boolean freeStyleCropEnabled) {
        selectionConfig.freeStyleCropEnabled = freeStyleCropEnabled;
        return this;
    }

    /**
     * @param scaleEnabled Crop frame is zoom ?
     */
    public PictureSelectionModel scaleEnabled(boolean scaleEnabled) {
        selectionConfig.scaleEnabled = scaleEnabled;
        return this;
    }

    /**
     * @param rotateEnabled Crop frame is rotate ?
     */
    public PictureSelectionModel rotateEnabled(boolean rotateEnabled) {
        selectionConfig.rotateEnabled = rotateEnabled;
        return this;
    }

    /**
     * @param circleDimmedLayer Circular head cutting
     */
    public PictureSelectionModel circleDimmedLayer(boolean circleDimmedLayer) {
        selectionConfig.circleDimmedLayer = circleDimmedLayer;
        return this;
    }

    /**
     * @param showCropFrame Whether to show crop frame
     */
    public PictureSelectionModel showCropFrame(boolean showCropFrame) {
        selectionConfig.showCropFrame = showCropFrame;
        return this;
    }

    /**
     * @param showCropGrid Whether to show CropGrid
     */
    public PictureSelectionModel showCropGrid(boolean showCropGrid) {
        selectionConfig.showCropGrid = showCropGrid;
        return this;
    }

    /**
     * @param hideBottomControls Whether is Clipping function bar
     *                           单选有效
     */
    public PictureSelectionModel hideBottomControls(boolean hideBottomControls) {
        selectionConfig.hideBottomControls = hideBottomControls;
        return this;
    }

    /**
     * @param aspect_ratio_x Crop Proportion x
     * @param aspect_ratio_y Crop Proportion y
     */
    public PictureSelectionModel withAspectRatio(int aspect_ratio_x, int aspect_ratio_y) {
        selectionConfig.aspect_ratio_x = aspect_ratio_x;
        selectionConfig.aspect_ratio_y = aspect_ratio_y;
        return this;
    }

    /**
     * @param maxSelectNum PictureSelector max selection
     */
    public PictureSelectionModel maxSelectNum(int maxSelectNum) {
        selectionConfig.maxSelectNum = maxSelectNum;
        return this;
    }

    /**
     * 作者：CnPeng
     * 时间：2018/5/24 上午10:59
     * 功用：选择文件时，控制可选择文件的最大大小.单位 byte
     * 说明：
     *
     * @param maxSize 可选择的最大文件大小.单位 byte, 如：控制最大可选5M，转换后为 5*1024*1024
     */
    public PictureSelectionModel maxSize(long maxSize) {
        selectionConfig.maxSize = maxSize;
        return this;
    }


    /**
     * @param minSelectNum PictureSelector min selection
     */
    public PictureSelectionModel minSelectNum(int minSelectNum) {
        selectionConfig.minSelectNum = minSelectNum;
        return this;
    }

    /**
     * @param videoQuality video quality and 0 or 1
     */
    public PictureSelectionModel videoQuality(int videoQuality) {
        selectionConfig.videoQuality = videoQuality;
        return this;
    }

    /**
     * @param suffixType PictureSelector media format
     */
    public PictureSelectionModel imageFormat(String suffixType) {
        selectionConfig.suffixType = suffixType;
        return this;
    }


    /**
     * @param cropWidth  crop width
     * @param cropHeight crop height
     */
    public PictureSelectionModel cropWH(int cropWidth, int cropHeight) {
        selectionConfig.cropWidth = cropWidth;
        selectionConfig.cropHeight = cropHeight;
        return this;
    }

    /**
     * @param videoMaxSecond selection video max second
     */
    public PictureSelectionModel videoMaxSecond(int videoMaxSecond) {
        selectionConfig.videoMaxSecond = videoMaxSecond * 1000;
        return this;
    }

    /**
     * @param videoMinSecond selection video min second
     */
    public PictureSelectionModel videoMinSecond(int videoMinSecond) {
        selectionConfig.videoMinSecond = videoMinSecond * 1000;
        return this;
    }


    /**
     * @param recordVideoSecond video record second
     */
    public PictureSelectionModel recordVideoSecond(int recordVideoSecond) {
        selectionConfig.recordVideoSecond = recordVideoSecond;
        return this;
    }

    /**
     * @param width  glide width
     * @param height glide height
     */
    public PictureSelectionModel glideOverride(@IntRange(from = 100) int width,
                                               @IntRange(from = 100) int height) {
        selectionConfig.overrideWidth = width;
        selectionConfig.overrideHeight = height;
        return this;
    }

    /**
     * @param sizeMultiplier The multiplier to apply to the
     *                       {@link com.bumptech.glide.request.target.Target}'s dimensions when
     *                       loading the resource.
     */
    public PictureSelectionModel sizeMultiplier(@FloatRange(from = 0.1f) float sizeMultiplier) {
        selectionConfig.sizeMultiplier = sizeMultiplier;
        return this;
    }

    /**
     * @param imageSpanCount PictureSelector image span count
     */
    public PictureSelectionModel imageSpanCount(int imageSpanCount) {
        selectionConfig.imageSpanCount = imageSpanCount;
        return this;
    }

    /**
     * @param size than how many KB images are not compressed
     */
    public PictureSelectionModel minimumCompressSize(int size) {
        selectionConfig.minimumCompressSize = size;
        return this;
    }

    /**
     * @param compressQuality crop compress quality default 90
     */
    public PictureSelectionModel cropCompressQuality(int compressQuality) {
        selectionConfig.cropCompressQuality = compressQuality;
        return this;
    }

    /**
     * @param isCompress Whether to open compress
     */
    public PictureSelectionModel compress(boolean isCompress) {
        selectionConfig.isCompress = isCompress;
        return this;
    }

    /**
     * @param synOrAsy Synchronous or asynchronous compression
     */
    public PictureSelectionModel synOrAsy(boolean synOrAsy) {
        selectionConfig.synOrAsy = synOrAsy;
        return this;
    }

    /**
     * @param path save path
     */
    public PictureSelectionModel compressSavePath(String path) {
        selectionConfig.compressSavePath = path;
        return this;
    }

    /**
     * @param zoomAnim Picture list zoom anim
     */
    public PictureSelectionModel isZoomAnim(boolean zoomAnim) {
        selectionConfig.zoomAnim = zoomAnim;
        return this;
    }

    /**
     * @param previewEggs preview eggs  It doesn't make much sense
     */
    public PictureSelectionModel previewEggs(boolean previewEggs) {
        selectionConfig.previewEggs = previewEggs;
        return this;
    }

    /**
     * @param isCamera Whether to open camera button
     */
    public PictureSelectionModel isCamera(boolean isCamera) {
        selectionConfig.isCamera = isCamera;
        return this;
    }

    /**
     * @param outputCameraPath Camera save path
     */
    public PictureSelectionModel setOutputCameraPath(String outputCameraPath) {
        selectionConfig.outputCameraPath = outputCameraPath;
        return this;
    }

    /**
     * @param isGif Whether to open gif
     */
    public PictureSelectionModel isGif(boolean isGif) {
        selectionConfig.isGif = isGif;
        return this;
    }

    /**
     * @param enablePreview Do you want to preview the picture?
     */
    public PictureSelectionModel previewImage(boolean enablePreview) {
        selectionConfig.enablePreview = enablePreview;
        return this;
    }

    /**
     * @param enPreviewVideo Do you want to preview the video?
     */
    public PictureSelectionModel previewVideo(boolean enPreviewVideo) {
        selectionConfig.enPreviewVideo = enPreviewVideo;
        return this;
    }

    /**
     * @param openClickSound Whether to open click voice
     */
    public PictureSelectionModel openClickSound(boolean openClickSound) {
        selectionConfig.openClickSound = openClickSound;
        return this;
    }

    /**
     * 是否可拖动裁剪框(setFreeStyleCropEnabled 为true 有效)
     */
    public PictureSelectionModel isDragFrame(boolean isDragFrame) {
        selectionConfig.isDragFrame = isDragFrame;
        return this;
    }

    /**
     * @param selectionMedia Select the selected picture set
     */
    public PictureSelectionModel selectionMedia(List<LocalMedia> selectionMedia) {
        if (selectionMedia == null) {
            selectionMedia = new ArrayList<>();
        }
        selectionConfig.selectionMedias = selectionMedia;
        return this;
    }

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    public void forResult(int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                return;
            }
            Intent intent = new Intent(activity, PictureSelectorActivity.class);
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
            activity.overridePendingTransition(R.anim.a5, 0);
        }
    }

    /**
     * 提供外部预览图片方法
     */
    public void openExternalPreview(int position, List<LocalMedia> medias) {
        if (selector != null) {
            selector.externalPicturePreview(position, medias);
        } else {
            throw new NullPointerException("This PictureSelector is Null");
        }
    }

    /**
     * 提供外部预览图片方法-带自定义下载保存路径
     */
    public void openExternalPreview(int position, String directory_path, List<LocalMedia> medias) {
        if (selector != null) {
            selector.externalPicturePreview(position, directory_path, medias);
        } else {
            throw new NullPointerException("This PictureSelector is Null");
        }
    }

}
