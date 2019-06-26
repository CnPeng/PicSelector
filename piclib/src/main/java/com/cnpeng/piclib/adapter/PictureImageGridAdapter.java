package com.cnpeng.piclib.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cnpeng.piclib.R;
import com.cnpeng.piclib.anim.OptAnimationLoader;
import com.cnpeng.piclib.config.PictureConfig;
import com.cnpeng.piclib.config.PictureMimeType;
import com.cnpeng.piclib.config.PictureSelectionConfig;
import com.cnpeng.piclib.entity.LocalMedia;
import com.cnpeng.piclib.tools.DateUtils;
import com.cnpeng.piclib.tools.ExternalStorageUtils;
import com.cnpeng.piclib.tools.StringUtils;
import com.cnpeng.piclib.tools.ToastManage;
import com.cnpeng.piclib.tools.VoiceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：2016/12/30
 */
public class PictureImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int              DURATION           = 450;
    private final        String           TAG                = "PictureImageGridAdapter";
    private              boolean          showCamera         = true;
    private              List<LocalMedia> images             = new ArrayList<LocalMedia>();
    private              List<LocalMedia> selectImages       = new ArrayList<LocalMedia>();
    private              int              selectMode         = PictureConfig.MULTIPLE;
    private              boolean          enablePreviewVideo = false;
    private              boolean          enablePreviewAudio = false;

    private Context                      context;
    private int                          maxSelectNum;
    private OnPhotoSelectChangedListener imageSelectChangedListener;
    private boolean                      enablePreview;
    private boolean                      is_checked_num;
    private boolean                      enableVoice;
    private float                        sizeMultiplier;
    private Animation                    animation;
    private PictureSelectionConfig       config;
    private int                          mimeType;
    private boolean                      zoomAnim;
    private long                         mMaxSize;
    private int                          overrideWidth, overrideHeight;
    /**
     * 单选图片
     */
    private boolean isGo;

    public PictureImageGridAdapter(Context context, PictureSelectionConfig config) {
        this.context = context;
        this.config = config;
        this.selectMode = config.selectionMode;
        this.showCamera = config.isCamera;
        this.maxSelectNum = config.maxSelectNum;
        this.enablePreview = config.enablePreview;
        this.enablePreviewVideo = config.enPreviewVideo;
        this.enablePreviewAudio = config.enablePreviewAudio;
        this.is_checked_num = config.checkNumMode;
        this.overrideWidth = config.overrideWidth;
        this.overrideHeight = config.overrideHeight;
        this.enableVoice = config.openClickSound;
        this.sizeMultiplier = config.sizeMultiplier;
        this.mimeType = config.mimeType;
        this.zoomAnim = config.zoomAnim;
        mMaxSize = config.maxSize;
        animation = OptAnimationLoader.loadAnimation(context, R.anim.modal_in);
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void bindImagesData(List<LocalMedia> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void bindSelectImages(List<LocalMedia> images) {
        // 这里重新构构造一个新集合，不然会产生已选集合一变，结果集合也会添加的问题
        List<LocalMedia> selection = new ArrayList<>();
        for (LocalMedia media : images) {
            selection.add(media);
        }
        this.selectImages = selection;
        subSelectPosition();
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    public List<LocalMedia> getSelectedImages() {
        if (selectImages == null) {
            selectImages = new ArrayList<>();
        }
        return selectImages;
    }

    public List<LocalMedia> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PictureConfig.TYPE_ITEM_CAMERA) {
            View view = LayoutInflater.from(context).inflate(R.layout.picture_item_camera, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.picture_image_grid_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == PictureConfig.TYPE_ITEM_CAMERA) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageSelectChangedListener != null) {
                        imageSelectChangedListener.onTakePhoto();
                    }
                }
            });
        } else {
            final ViewHolder contentHolder = (ViewHolder) holder;
            final LocalMedia image = images.get(showCamera ? position - 1 : position);
            image.position = contentHolder.getAdapterPosition();
            final String path = image.getOriginalPath();

            final String pictureType = image.getPictureType();
            if (is_checked_num) {
                notifyCheckChanged(contentHolder, image);
            }
            selectImage(contentHolder, isSelected(image), false);

            final int mediaMimeType = PictureMimeType.isPictureType(pictureType);
            boolean gif = PictureMimeType.isGif(pictureType);

            contentHolder.tv_isGif.setVisibility(gif ? View.VISIBLE : View.GONE);
            if (mimeType == PictureMimeType.ofAudio()) {
                contentHolder.tv_duration.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.picture_audio);
                StringUtils.modifyTextViewDrawable(contentHolder.tv_duration, drawable, 0);
            } else {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.video_icon);
                StringUtils.modifyTextViewDrawable(contentHolder.tv_duration, drawable, 0);
                contentHolder.tv_duration.setVisibility(mediaMimeType == PictureConfig.TYPE_MEDIA_VIDEO
                        ? View.VISIBLE : View.GONE);
            }
            boolean eqLongImg = PictureMimeType.isLongImg(image);
            contentHolder.tv_long_chart.setVisibility(eqLongImg ? View.VISIBLE : View.GONE);
            long duration = image.getDuration();
            contentHolder.tv_duration.setText(DateUtils.timeParse(duration));
            if (mimeType == PictureMimeType.ofAudio()) {
                contentHolder.iv_picture.setImageResource(R.drawable.audio_placeholder);
            } else {
                RequestOptions options = new RequestOptions();
                if (overrideWidth <= 0 && overrideHeight <= 0) {
                    options.sizeMultiplier(sizeMultiplier);
                } else {
                    options.override(overrideWidth, overrideHeight);
                }
                options.diskCacheStrategy(DiskCacheStrategy.ALL);
                options.centerCrop();
                options.placeholder(R.drawable.image_placeholder);
                Glide.with(context)
                        .asBitmap()
                        .load(path)
                        .apply(options)
                        .into(contentHolder.iv_picture);
            }
            if (enablePreview || enablePreviewVideo || enablePreviewAudio) {
                contentHolder.ll_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 如原图路径不存在或者路径存在但文件不存在
                        if (!new File(path).exists()) {
                            ToastManage.s(context, PictureMimeType.s(context, mediaMimeType));
                            return;
                        }
                        changeCheckboxState(contentHolder, image);
                    }
                });
            }
            contentHolder.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 如原图路径不存在或者路径存在但文件不存在
                    if (!new File(path).exists()) {
                        ToastManage.s(context, PictureMimeType.s(context, mediaMimeType));
                        return;
                    }
                    int index = showCamera ? position - 1 : position;
                    boolean eqResult =
                            mediaMimeType == PictureConfig.TYPE_MEDIA_IMAGE && enablePreview
                                    || mediaMimeType == PictureConfig.TYPE_MEDIA_VIDEO && (enablePreviewVideo
                                    || selectMode == PictureConfig.SINGLE)
                                    || mediaMimeType == PictureConfig.TYPE_MEDIA_AUDIO && (enablePreviewAudio
                                    || selectMode == PictureConfig.SINGLE);
                    if (eqResult) {
                        imageSelectChangedListener.onPictureClick(image, index);
                    } else {
                        changeCheckboxState(contentHolder, image);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return PictureConfig.TYPE_ITEM_CAMERA;
        } else {
            return PictureConfig.TYPE_ITEM_PICTURE;
        }
    }

    @Override
    public int getItemCount() {
        return showCamera ? images.size() + 1 : images.size();
    }

    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getOriginalPath().equals(image.getOriginalPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(ViewHolder viewHolder, LocalMedia imageBean) {
        viewHolder.check.setText("");
        for (LocalMedia media : selectImages) {
            if (media.getOriginalPath().equals(imageBean.getOriginalPath())) {
                imageBean.setNum(media.getNum());
                media.setPosition(imageBean.getPosition());
                viewHolder.check.setText(String.valueOf(imageBean.getNum()));
            }
        }
    }

    /**
     * 改变图片选中状态
     */
    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isPreChecked = contentHolder.check.isSelected();
        String pictureType = selectImages.size() > 0 ? selectImages.get(0).getPictureType() : "";
        if (!TextUtils.isEmpty(pictureType)) {
            boolean toEqual = PictureMimeType.mimeToEqual(pictureType, image.getPictureType());
            if (!toEqual) {
                ToastManage.s(context, context.getString(R.string.picture_rule));
                return;
            }
        }

        boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);

        //判断是否超出个数
        if (selectImages.size() >= maxSelectNum && !isPreChecked) {
            String str = eqImg ? context.getString(R.string.picture_message_max_num, maxSelectNum)
                    : context.getString(R.string.picture_message_video_max_num, maxSelectNum);
            ToastManage.s(context, str);
            return;
        }

        // 2018/5/23 CnPeng 下午9:57 判断被选择的文件是否超出大小限制
        if (config.maxSize < image.getFileLength()) {

            // TODO: 2018/5/24 下午12:11 类型判断为什么不对？预览界面还要加大小控制
            // String str = eqImg ? "单图大小请勿超过" + ExternalStorageUtils.converSize((mMaxSize))
            //         : "单视频大小请勿超过" + ExternalStorageUtils.converSize((mMaxSize));

            String str = "单文件大小请勿超过" + ExternalStorageUtils.converSize((mMaxSize));
            ToastManage.s(context, str);

            return;
        }


        if (isPreChecked) {
            // isPreChecked 是被点击之前的状态
            for (LocalMedia media : selectImages) {
                if (media.getOriginalPath().equals(image.getOriginalPath())) {
                    selectImages.remove(media);
                    subSelectPosition();
                    disZoom(contentHolder.iv_picture);
                    break;
                }
            }
        } else {
            // 如果是单选，则清空已选中的并刷新列表(作单一选择)
            if (selectMode == PictureConfig.SINGLE) {
                singleRadioMediaImage();
            }
            selectImages.add(image);
            image.setNum(selectImages.size());
            VoiceUtils.playVoice(context, enableVoice);
            zoom(contentHolder.iv_picture);
        }
        //通知点击项发生了改变
        notifyItemChanged(contentHolder.getAdapterPosition());
        selectImage(contentHolder, !isPreChecked, true);
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    /**
     * 单选模式
     */
    private void singleRadioMediaImage() {
        if (selectImages != null
                && selectImages.size() > 0) {
            isGo = true;
            LocalMedia media = selectImages.get(0);
            notifyItemChanged(config.isCamera ? media.position :
                    isGo ? media.position : media.position > 0 ? media.position - 1 : 0);
            selectImages.clear();
        }
    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        if (is_checked_num) {
            int size = selectImages.size();
            for (int index = 0, length = size; index < length; index++) {
                LocalMedia media = selectImages.get(index);
                media.setNum(index + 1);
                notifyItemChanged(media.position);
            }
        }
    }

    /**
     * 选中的图片并执行动画
     */
    public void selectImage(ViewHolder holder, boolean isChecked, boolean isAnim) {
        holder.check.setSelected(isChecked);
        if (isChecked) {
            if (isAnim) {
                if (animation != null) {
                    holder.check.startAnimation(animation);
                }
            }
            holder.iv_picture.setColorFilter(ContextCompat.getColor
                    (context, R.color.image_overlay_true), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.iv_picture.setColorFilter(ContextCompat.getColor
                    (context, R.color.image_overlay_false), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setOnPhotoSelectChangedListener(OnPhotoSelectChangedListener
                                                        imageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener;
    }

    private void zoom(ImageView iv_img) {
        if (zoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(iv_img, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(iv_img, "scaleY", 1f, 1.12f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    private void disZoom(ImageView iv_img) {
        if (zoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(iv_img, "scaleX", 1.12f, 1f),
                    ObjectAnimator.ofFloat(iv_img, "scaleY", 1.12f, 1f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        View     headerView;
        TextView tv_title_camera;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = itemView;
            tv_title_camera = (TextView) itemView.findViewById(R.id.tv_title_camera);
            String title = mimeType == PictureMimeType.ofAudio() ?
                    context.getString(R.string.picture_tape)
                    : context.getString(R.string.picture_take_picture);
            tv_title_camera.setText(title);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_picture;
        TextView  check;
        TextView  tv_duration, tv_isGif, tv_long_chart;
        View         contentView;
        LinearLayout ll_check;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            iv_picture = (ImageView) itemView.findViewById(R.id.iv_picture);
            check = (TextView) itemView.findViewById(R.id.check);
            ll_check = (LinearLayout) itemView.findViewById(R.id.ll_check);
            tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);
            tv_isGif = (TextView) itemView.findViewById(R.id.tv_isGif);
            tv_long_chart = (TextView) itemView.findViewById(R.id.tv_long_chart);
        }
    }

    public interface OnPhotoSelectChangedListener {
        /**
         * 拍照回调
         */
        void onTakePhoto();

        /**
         * 已选Media回调
         */
        void onChange(List<LocalMedia> selectImages);

        /**
         * 图片预览回调
         */
        void onPictureClick(LocalMedia media, int position);
    }
}
