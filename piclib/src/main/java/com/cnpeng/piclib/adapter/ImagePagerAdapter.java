package com.cnpeng.piclib.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.cnpeng.piclib.R;
import com.cnpeng.piclib.antutils.DisplayUtil;
import com.cnpeng.piclib.antutils.NetworkUtil;
import com.cnpeng.piclib.config.PictureMimeType;
import com.cnpeng.piclib.entity.ImgPreviewBean;
import com.cnpeng.piclib.entity.LocalMedia;
import com.cnpeng.piclib.photoview.OnViewTapListener;
import com.cnpeng.piclib.photoview.PhotoView;
import com.cnpeng.piclib.tools.PictureFileUtils;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.util.List;

/**
 * 作者：CnPeng
 * 时间：2018/6/1
 * 功用：大图查看框架。
 * 其他：支持本地图片预览、支持网络图片预览。网络图片预览和本地图片预览使用不同的构造方法。
 */
public class ImagePagerAdapter extends PagerAdapter {

    private final String               TAG = getClass().getSimpleName();
    private       List<LocalMedia>     mLocalImages;
    private       List<ImgPreviewBean> mNetImages;
    private       OnImgClickListener   mImgClickListener;
    private       boolean              mIsNetImg;
    private       AlertDialog          mLongClickDialog;
    private       String               mFolderName;


    /**
     * 作者：CnPeng
     * 时间：2018/6/4 上午8:25
     * 功用：如果是网络图片，调用该构造
     * 说明：
     *
     * @param images           图片信息集合
     * @param imgClickListener 图片点击事件
     */
    public ImagePagerAdapter(OnImgClickListener imgClickListener, List<ImgPreviewBean> images) {
        super();
        this.mNetImages = images;
        this.mImgClickListener = imgClickListener;
        mIsNetImg = true;
    }


    /**
     * 作者：CnPeng
     * 时间：2018/6/4 上午8:25
     * 功用：如果是本地图片，调用该构造
     * 说明：
     */
    public ImagePagerAdapter(List<LocalMedia> images, OnImgClickListener imgClickListener) {
        super();
        this.mLocalImages = images;
        this.mImgClickListener = imgClickListener;
        mIsNetImg = false;
    }

    public void setFolderName(String folderName) {
        mFolderName = folderName;
    }

    @Override
    public int getCount() {
        if (mNetImages != null) {
            return mNetImages.size();
        } else if (null != mLocalImages) {
            return mLocalImages.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final View contentView = LayoutInflater.from(container.getContext()).inflate(R.layout.picture_image_preview, container, false);
        Context context = container.getContext();

        // 常规图控件
        final PhotoView imageView = (PhotoView) contentView.findViewById(R.id.preview_image);
        // 长图控件
        final SubsamplingScaleImageView longImg = (SubsamplingScaleImageView) contentView.findViewById(R.id.longImg);
        //        longImg.setQuickScaleEnabled(true);
        //        longImg.setZoomEnabled(true);
        //        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        //这一行和setMinScale() 配合使用，可以解决双击缩小视图时直接缩到最小的问题
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);

        //        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);

        //加载进度
        final LinearLayout loadingProgress = contentView.findViewById(R.id.ll_loadingHintParent);
        onImgLoadStart(loadingProgress);

        String imgUrl = "";

        if (mIsNetImg) {
            if (!NetworkUtil.isNetworkAvailable(context)) {
                Toast.makeText(context, R.string.hint_no_net, Toast.LENGTH_SHORT).show();
                onImgLoadOver(loadingProgress);
                imageView.setImageResource(R.drawable.ic_placeholder);
            } else {
                ImgPreviewBean img = mNetImages.get(position);
                if (img != null) {
                    Log.d(TAG, "图片地址:" + imgUrl);

                    //2018/6/7 上午9:18  完美：由于 RequestBuilder 的 downloadOnly 已被废弃，所以，根据建议使用 RequestManager中的
                    Glide.with(context)
                            .downloadOnly()
                            .load(imgUrl)
                            .listener(getFileRequestListener(loadingProgress, longImg))
                            .into(new SimpleTarget<File>() {
                                @Override
                                public void onResourceReady(@NonNull File file, @Nullable Transition<? super File> transition) {

                                    String filePath = file.getPath();
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = true;
                                    BitmapFactory.decodeFile(filePath, options);
                                    int bmpWidth = options.outWidth;
                                    int bmpHeight = options.outHeight;

                                    //outMimeType是以-- ”image/png”、”image/jpeg”、”image/gif”…….这样的方式返回的
                                    String mimeType = options.outMimeType;
                                    Log.d(TAG, "图片类型1：" + mimeType);

                                    boolean isGif = PictureMimeType.isGif(mimeType);

                                    //CnPeng 2018/8/7 下午5:45 之所以放在里面判断，是因为，我们不能单纯的通过后缀判断是否gif——可能会出现后缀为.jpg 的 gif
                                    if (isGif) {
                                        //  2018/6/5 下午4:18  如果是动图，隐藏长图控件，否则，点击和长按事件不生效
                                        longImg.setVisibility(View.GONE);
                                        imageView.setVisibility(View.VISIBLE);

                                        RequestOptions gifOptions = new RequestOptions()
                                                .override(480, 800)
                                                .priority(Priority.HIGH)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE);
                                        Glide.with(context)
                                                .asGif()
                                                .load(file)
                                                .listener(getGifRequestListener(loadingProgress))
                                                .apply(gifOptions)
                                                .into(imageView);
                                    } else {
                                        longImg.setVisibility(View.VISIBLE);
                                        imageView.setVisibility(View.GONE);

                                        float scale = PictureMimeType.getScaleRate(bmpWidth, bmpHeight, context);
                                        longImg.setMinScale(scale);

                                        float maxScale = PictureMimeType.getMaxScaleRate(bmpWidth, bmpHeight, context);
                                        longImg.setMaxScale(maxScale);

                                        float tapScale = PictureMimeType.getDoubleTapZoomScale(bmpWidth, bmpHeight, context);
                                        longImg.setDoubleTapZoomScale(tapScale);

                                        Log.i(TAG, "init" + scale + "/max" + maxScale + "/tap" + tapScale);

                                        longImg.setImage(ImageSource.uri(Uri.fromFile(file)), new ImageViewState(scale, new PointF(0, 0), 0));
                                    }
                                }
                            });
                }
            }
        } else {
            LocalMedia media = mLocalImages.get(position);
            if (media != null) {
                imgUrl = media.getPath();
                Log.d(TAG, "图片类型2：" + media.getPictureType());

                boolean isGif = PictureMimeType.isGif(media.getPictureType());

                imageView.setVisibility(isGif ? View.VISIBLE : View.GONE);
                longImg.setVisibility(isGif ? View.GONE : View.VISIBLE);

                // 压缩过的gif就不是gif了
                if (isGif) {
                    RequestOptions gifOptions = new RequestOptions()
                            .override(480, 800)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.NONE);
                    Glide.with(context)
                            .asGif()
                            .load(imgUrl)
                            .listener(getGifRequestListener(loadingProgress))
                            .apply(gifOptions)
                            .into(imageView);
                } else {
                    //  2018/6/6 下午3:59  本地非动图直接加载到长图控件中
                    float scale = 0;
                    scale = PictureMimeType.getScaleRate(media.getWidth(), media.getHeight(), context);
                    longImg.setMinScale(scale);

                    float maxScale = PictureMimeType.getMaxScaleRate(media.getWidth(), media.getHeight(), context);
                    longImg.setMaxScale(maxScale);

                    //CnPeng 2018/6/8 上午9:48 双击时的缩放比率步进.取最大和最小的中间值
                    float tapScale = PictureMimeType.getDoubleTapZoomScale(media.getWidth(), media.getHeight(), context);
                    longImg.setDoubleTapZoomScale(tapScale);

                    Log.i(TAG, "init" + scale + "/max" + maxScale + "/tap" + tapScale);

                    longImg.setImage(ImageSource.uri(imgUrl), new ImageViewState(scale, new PointF(0, 0), 0));
                }
            }
        }

        initImgClickEvent(imageView, imgUrl);
        initLongImgClickEvent(longImg, imgUrl);
        initLongImgLoadingListener(longImg, loadingProgress, imageView);

        (container).addView(contentView, 0);
        return contentView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void initLongImgLoadingListener(final SubsamplingScaleImageView longImg, final LinearLayout loadingProgress, final PhotoView imageView) {
        longImg.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                onImgLoadOver(loadingProgress);
                Log.i(TAG, "onReady");
            }

            @Override
            public void onImageLoaded() {
                onImgLoadOver(loadingProgress);
                Log.i(TAG, "onImageLoaded , scale" + longImg.getScale());
            }

            @Override
            public void onPreviewLoadError(Exception e) {
                onImgLoadOver(loadingProgress);
                longImg.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_placeholder);

                Log.i(TAG, "onPreviewLoadError");
            }

            @Override
            public void onImageLoadError(Exception e) {
                onImgLoadOver(loadingProgress);
                longImg.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_placeholder);

                Log.i(TAG, "onImageLoadError");

            }

            @Override
            public void onTileLoadError(Exception e) {
                onImgLoadOver(loadingProgress);
                longImg.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.ic_placeholder);
                Log.i(TAG, "onTileLoadError");
            }

            @Override
            public void onPreviewReleased() {
                onImgLoadOver(loadingProgress);
                Log.i(TAG, "onPreviewReleased");
            }
        });
    }

    private void initLongImgClickEvent(SubsamplingScaleImageView longImg, String imgUrl) {
        longImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "图片预览框架——图片被点击了");
                if (mImgClickListener != null) {
                    mImgClickListener.onImgClicked();
                }
            }
        });

        if (!TextUtils.isEmpty(imgUrl)) {
            longImg.setOnLongClickListener(getLongClickListener(imgUrl));
        }
    }

    private void initImgClickEvent(PhotoView imageView, String imgUrl) {
        imageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mImgClickListener != null) {
                    mImgClickListener.onImgClicked();
                }
            }
        });

        if (!TextUtils.isEmpty(imgUrl)) {
            imageView.setOnLongClickListener(getLongClickListener(imgUrl));
        }
    }

    private RequestListener<File> getFileRequestListener(final LinearLayout loadingProgress, final SubsamplingScaleImageView longImg) {
        return new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                onImgLoadOver(loadingProgress);
                longImg.setImage(ImageSource.resource(R.drawable.ic_placeholder));
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                //                onImgLoadOver(loadingProgress);
                return false;
            }
        };
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

    public View.OnLongClickListener getLongClickListener(final String imgUrl) {

        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e(TAG, "图片预览框架——图片被长按 了");

                final Context context = v.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                        .MATCH_PARENT));
                layout.setGravity(Gravity.CENTER_VERTICAL);
                TextView tv = new TextView(context);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(DisplayUtil.dip2px(context, 15f), DisplayUtil.dip2px(context, 15f), 0, DisplayUtil
                        .dip2px(context, 15f));
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                tv.setText(R.string.picture_prompt_content);
                layout.addView(tv);
                builder.setView(layout);
                mLongClickDialog = builder.show();
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String targetFolderName = TextUtils.isEmpty(mFolderName) ? "PicSelector" : mFolderName;
                        PictureFileUtils.saveBitmap(context, imgUrl, targetFolderName);
                        mLongClickDialog.dismiss();
                    }
                });
                return false;
            }
        };
    }

    private void onImgLoadStart(LinearLayout loadingProgress) {
        loadingProgress.setVisibility(View.VISIBLE);
    }

    private void onImgLoadOver(LinearLayout loadingProgress) {
        loadingProgress.setVisibility(View.GONE);
    }

    public interface OnImgClickListener {
        /**
         * 关闭预览Activity
         */
        void onImgClicked();
    }
}
