package com.cnpeng.piclib.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.cnpeng.piclib.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.cnpeng.piclib.PictureVideoPlayActivity;
import com.cnpeng.piclib.config.PictureConfig;
import com.cnpeng.piclib.config.PictureMimeType;
import com.cnpeng.piclib.entity.LocalMedia;
import com.cnpeng.piclib.photoview.OnViewTapListener;
import com.cnpeng.piclib.photoview.PhotoView;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @author：luck
 * @data：2018/1/27 下午7:50
 * @描述:图片预览
 */

public class SimpleFragmentAdapter extends PagerAdapter {
    private final String TAG = getClass().getSimpleName();
    private List<LocalMedia>   images;
    private Context            mContext;
    private OnCallBackActivity onBackPressed;

    public SimpleFragmentAdapter(List<LocalMedia> images, Context context,
                                 OnCallBackActivity onBackPressed) {
        super();
        this.images = images;
        this.mContext = context;
        this.onBackPressed = onBackPressed;
    }

    @Override
    public int getCount() {
        if (images != null) {
            return images.size();
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View contentView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.picture_image_preview, container, false);
        // 常规图控件
        final PhotoView imageView = (PhotoView) contentView.findViewById(R.id.preview_image);
        // 长图控件
        final SubsamplingScaleImageView longImg = (SubsamplingScaleImageView) contentView.findViewById(R.id.longImg);

        //加载进度
        final LinearLayout loadingProgress = contentView.findViewById(R.id.ll_loadingHintParent);
        onImgLoadStart(loadingProgress);

        ImageView iv_play = (ImageView) contentView.findViewById(R.id.iv_play);
        LocalMedia media = images.get(position);
        if (media != null) {
            final String pictureType = media.getPictureType();
            boolean eqVideo = pictureType.startsWith(PictureConfig.VIDEO);
            iv_play.setVisibility(eqVideo ? View.VISIBLE : View.GONE);
            final String path;
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                path = media.getOriginalPath();
            }

            boolean isGif = PictureMimeType.isGif(pictureType);
            final boolean eqLongImg = PictureMimeType.isLongImg(media);

            imageView.setVisibility(isGif ? View.VISIBLE : View.GONE);
            longImg.setVisibility(isGif ? View.GONE : View.VISIBLE);

            // 压缩过的gif就不是gif了
            if (isGif && !media.isCompressed()) {
                RequestOptions gifOptions = new RequestOptions()
                        .override(480, 800)
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.NONE);
                Glide.with(contentView.getContext())
                        .asGif()
                        .load(path)
                        .listener(getGifRequestListener(loadingProgress))
                        .apply(gifOptions)
                        .into(imageView);
            } else {
                //  2018/6/6 下午3:59  本地非动图直接加载到长图控件中
                float scale = 0;
                scale = PictureMimeType.getScaleRate(media.getWidth(), media.getHeight(),mContext);

                longImg.setDoubleTapZoomDuration(100);
                longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                longImg.setMinScale(scale);
                longImg.setImage(ImageSource.uri(path), new ImageViewState(scale, new PointF(0, 0), 0));
            }

            initLongImgLoadingListener(longImg, loadingProgress);
            imageView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (onBackPressed != null) {
                        onBackPressed.onActivityBackPressed();
                    }
                }
            });
            longImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBackPressed != null) {
                        onBackPressed.onActivityBackPressed();
                    }
                }
            });
            iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("video_path", path);
                    intent.putExtras(bundle);
                    intent.setClass(mContext, PictureVideoPlayActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
        (container).addView(contentView, 0);
        return contentView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void initLongImgLoadingListener(SubsamplingScaleImageView longImg, final LinearLayout loadingProgress) {
        longImg.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                onImgLoadOver(loadingProgress);
                Log.i(TAG, "onReady");
            }

            @Override
            public void onImageLoaded() {
                Log.i(TAG, "onImageLoaded");
                onImgLoadOver(loadingProgress);
            }

            @Override
            public void onPreviewLoadError(Exception e) {
                Log.i(TAG, "onPreviewLoadError");
                onImgLoadOver(loadingProgress);
            }

            @Override
            public void onImageLoadError(Exception e) {
                Log.i(TAG, "onImageLoadError");
                onImgLoadOver(loadingProgress);
            }

            @Override
            public void onTileLoadError(Exception e) {
                Log.i(TAG, "onTileLoadError");
                onImgLoadOver(loadingProgress);
            }

            @Override
            public void onPreviewReleased() {
                Log.i(TAG, "onPreviewReleased");
                onImgLoadOver(loadingProgress);
            }
        });
    }

    private void onImgLoadStart(LinearLayout loadingProgress) {
        loadingProgress.setVisibility(View.VISIBLE);
    }

    private void onImgLoadOver(LinearLayout loadingProgress) {
        loadingProgress.setVisibility(View.GONE);
    }

    private RequestListener<GifDrawable> getGifRequestListener(final LinearLayout loadingProgress) {
        return new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                onImgLoadOver(loadingProgress);

                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                onImgLoadOver(loadingProgress);
                return false;
            }
        };
    }

    public interface OnCallBackActivity {
        /**
         * 关闭预览Activity
         */
        void onActivityBackPressed();
    }
}
