package com.cnpeng.piclib;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.cnpeng.piclib.adapter.PictureAlbumDirectoryAdapter;
import com.cnpeng.piclib.adapter.PictureImageGridAdapter;
import com.cnpeng.piclib.antwidget.CaptureButton;
import com.cnpeng.piclib.antwidget.video.activity.RecordVideoActivity;
import com.cnpeng.piclib.config.PictureConfig;
import com.cnpeng.piclib.config.PictureMimeType;
import com.cnpeng.piclib.crop.CutInfo;
import com.cnpeng.piclib.crop.UCropMulti;
import com.cnpeng.piclib.decoration.GridSpacingItemDecoration;
import com.cnpeng.piclib.dialog.CustomDialog;
import com.cnpeng.piclib.entity.EventEntity;
import com.cnpeng.piclib.entity.LocalMedia;
import com.cnpeng.piclib.entity.LocalMediaFolder;
import com.cnpeng.piclib.model.LocalMediaLoader;
import com.cnpeng.piclib.observable.ImagesObservable;
import com.cnpeng.piclib.permissions.RxPermissions;
import com.cnpeng.piclib.rxbus2.RxBus;
import com.cnpeng.piclib.rxbus2.Subscribe;
import com.cnpeng.piclib.rxbus2.ThreadMode;
import com.cnpeng.piclib.tools.DateUtils;
import com.cnpeng.piclib.tools.DoubleUtils;
import com.cnpeng.piclib.tools.PictureFileUtils;
import com.cnpeng.piclib.tools.ScreenUtils;
import com.cnpeng.piclib.tools.StringUtils;
import com.cnpeng.piclib.tools.ToastManage;
import com.cnpeng.piclib.widget.FolderPopWindow;
import com.cnpeng.piclib.widget.PhotoPopupWindow;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 *
 */
public class PictureSelectorActivity extends PictureBaseActivity implements View.OnClickListener,
        PictureAlbumDirectoryAdapter.OnItemClickListener, PhotoPopupWindow.OnItemClickListener,
        PictureImageGridAdapter.OnPhotoSelectChangedListener {

    private final static String TAG                  = PictureSelectorActivity.class.getSimpleName();
    private static final int    SHOW_DIALOG          = 0;
    private static final int    DISMISS_DIALOG       = 1;
    private static final int    REQUEST_RECORD_AUDIO = 3;

    //  通过 Handler 更新 UI 上的组件状态
    public  Handler                handler     = new Handler();
    private List<LocalMedia>       images      = new ArrayList<>();
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private boolean                anim        = false;
    private Animation              animation   = null;
    private boolean                isPlayAudio = false;

    private TextView                picture_title;
    private TextView                picture_tv_ok;
    private TextView                tv_empty;
    private TextView                picture_tv_img_num;
    private TextView                mPicPreviewTv;
    private TextView                tv_PlayPause;
    private TextView                tv_musicStatus;
    private TextView                tv_musicTotal;
    private TextView                tv_musicTime;
    private RelativeLayout          rl_picture_title;
    private LinearLayout            id_ll_ok;
    private PictureImageGridAdapter mAdapter;
    private FolderPopWindow         mFolderPopWindow;
    private RxPermissions           rxPermissions;
    private PhotoPopupWindow        popupWindow;
    private LocalMediaLoader        mediaLoader;
    private MediaPlayer             mediaPlayer;
    private SeekBar                 musicSeekBar;
    public  Runnable                runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null) {
                    tv_musicTime.setText(DateUtils.timeParse(mediaPlayer.getCurrentPosition()));
                    musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    musicSeekBar.setMax(mediaPlayer.getDuration());
                    tv_musicTotal.setText(DateUtils.timeParse(mediaPlayer.getDuration()));
                    handler.postDelayed(runnable, 200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private CustomDialog            audioDialog;
    private int                     audioH;
    private PictureSelectorActivity mActivity;
    private TextView                mShowAllPicTv;
    private Handler                 mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DIALOG:
                    showPleaseDialog();
                    break;
                case DISMISS_DIALOG:
                    dismissDialog();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * EventBus 3.0 回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case PictureConfig.UPDATE_FLAG:
                // 预览时勾选图片更新回调
                List<LocalMedia> selectImages = obj.medias;
                anim = selectImages.size() > 0;
                int position = obj.position;
                Log.i("刷新下标:", String.valueOf(position));
                mAdapter.bindSelectImages(selectImages);
                mAdapter.notifyItemChanged(position);

                break;
            case PictureConfig.PREVIEW_DATA_FLAG:
                List<LocalMedia> medias = obj.medias;
                if (medias.size() > 0) {
                    // 取出第1个判断是否是图片，视频和图片只能二选一，不必考虑图片和视频混合
                    String pictureType = medias.get(0).getPictureType();
                    if (config.isCompress && pictureType.startsWith(PictureConfig.IMAGE)) {
                        compressImage(medias);
                    } else {
                        onResult(medias);
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        rxPermissions = new RxPermissions(this);
        if (config.camera) {
            if (savedInstanceState == null) {
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    onTakePhoto();
                                } else {
                                    ToastManage.s(mContext, getString(R.string.picture_camera));
                                    closeActivity();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    , WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.picture_empty);

        } else {
            setContentView(R.layout.picture_selector);
            initView(savedInstanceState);
        }

        mActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        ImagesObservable.getInstance().clearLocalMedia();
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (mediaPlayer != null && handler != null) {
            handler.removeCallbacks(runnable);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            List<LocalMedia> selectedImages = mAdapter.getSelectedImages();
            PictureSelector.saveSelectorList(outState, selectedImages);
        }
    }

    /**
     * init views
     */
    private void initView(Bundle savedInstanceState) {

        rl_picture_title = (RelativeLayout) findViewById(R.id.rl_picture_title);
        RecyclerView picture_recycler = (RecyclerView) findViewById(R.id.picture_recycler);
        ImageView picture_left_back = (ImageView) findViewById(R.id.picture_left_back);

        picture_title = (TextView) findViewById(R.id.picture_title);
        picture_tv_ok = (TextView) findViewById(R.id.picture_tv_ok);
        mPicPreviewTv = (TextView) findViewById(R.id.picture_id_preview);
        picture_tv_img_num = (TextView) findViewById(R.id.picture_tv_img_num);
        id_ll_ok = (LinearLayout) findViewById(R.id.id_ll_ok);
        tv_empty = (TextView) findViewById(R.id.tv_empty);

        mShowAllPicTv = (TextView) findViewById(R.id.tv_showAllPic);
        mShowAllPicTv.setOnClickListener(this);

        isNumComplete(numComplete);
        if (config.mimeType == PictureMimeType.ofAll()) {
            popupWindow = new PhotoPopupWindow(this);
            popupWindow.setOnItemClickListener(this);
        }

        mPicPreviewTv.setOnClickListener(this);
        if (config.mimeType == PictureMimeType.ofAudio()) {
            mPicPreviewTv.setVisibility(View.GONE);
            audioH = ScreenUtils.getScreenHeight(mContext) + ScreenUtils.getStatusBarHeight(mContext);
        } else {
            mPicPreviewTv.setVisibility(config.mimeType == PictureConfig.TYPE_MEDIA_VIDEO
                    ? View.GONE : View.VISIBLE);
        }

        picture_left_back.setOnClickListener(this);
        id_ll_ok.setOnClickListener(this);
        picture_title.setOnClickListener(this);
        String title = config.mimeType == PictureMimeType.ofAudio() ?
                getString(R.string.picture_all_audio)
                : getString(R.string.picture_camera_roll);
        picture_title.setText(title);


        final LinearLayout rl_bottom = findViewById(R.id.rl_bottom);
        rl_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                rl_bottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // CnPeng 2018/9/5 下午1:57 让pop向上便宜一个底部栏的高度
                int offsetY = rl_bottom.getMeasuredHeight();
                int screenHeight = ScreenUtils.getScreenHeight(mActivity);
                ViewGroup.LayoutParams la = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenHeight - offsetY);
                mFolderPopWindow = new FolderPopWindow(mActivity, config.mimeType, la);
                mFolderPopWindow.setTvToNotify(mShowAllPicTv);
                mFolderPopWindow.setOnItemClickListener(mActivity);

                //由于 ReadLocalMedia 完成之后会将文件夹数据绑定到mFolderPopWindow，所以，必须保证创建了pop后再去取本地数据
                mediaLoader = new LocalMediaLoader(mActivity, config.mimeType, config.isGif, config.videoMaxSecond, config.videoMinSecond);
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    mHandler.sendEmptyMessage(SHOW_DIALOG);
                                    readLocalMedia();
                                } else {
                                    ToastManage.s(mContext, getString(R.string.picture_jurisdiction));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
        });


        picture_recycler.setHasFixedSize(true);
        picture_recycler.addItemDecoration(new GridSpacingItemDecoration(config.imageSpanCount,
                ScreenUtils.dip2px(this, 2), false));
        picture_recycler.setLayoutManager(new GridLayoutManager(this, config.imageSpanCount));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) picture_recycler.getItemAnimator()).setSupportsChangeAnimations(false);

        tv_empty.setText(config.mimeType == PictureMimeType.ofAudio() ?
                getString(R.string.picture_audio_empty)
                : getString(R.string.picture_empty));
        StringUtils.tempTextFont(tv_empty, config.mimeType);
        if (savedInstanceState != null) {
            // 防止拍照内存不足时activity被回收，导致拍照后的图片未选中
            selectionMedias = PictureSelector.obtainSelectorList(savedInstanceState);
        }
        mAdapter = new PictureImageGridAdapter(mContext, config);
        mAdapter.setOnPhotoSelectChangedListener(PictureSelectorActivity.this);
        mAdapter.bindSelectImages(selectionMedias);
        picture_recycler.setAdapter(mAdapter);
        String titleText = picture_title.getText().toString().trim();
        if (config.isCamera) {
            config.isCamera = StringUtils.isCamera(titleText);
        }
    }

    /**
     * none number style
     */
    private void isNumComplete(boolean numComplete) {
        picture_tv_ok.setText(numComplete ? getString(R.string.picture_done_front_num,
                0, config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum)
                : getString(R.string.picture_done));
        if (!numComplete) {
            animation = AnimationUtils.loadAnimation(this, R.anim.modal_in);
        }
        animation = numComplete ? null : AnimationUtils.loadAnimation(this, R.anim.modal_in);
    }

    /**
     * 获取本地多媒体文件，并展示到界面中
     */
    protected void readLocalMedia() {
        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    foldersList = folders;
                    LocalMediaFolder folder = folders.get(0);
                    folder.setChecked(true);
                    List<LocalMedia> localImg = folder.getImages();
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                    if (localImg.size() >= images.size()) {
                        images = localImg;
                        mFolderPopWindow.bindFolder(folders);
                    }
                }
                if (mAdapter != null) {
                    if (images == null) {
                        images = new ArrayList<>();
                    }
                    mAdapter.bindImagesData(images);
                    tv_empty.setVisibility(images.size() > 0
                            ? View.INVISIBLE : View.VISIBLE);
                }
                mHandler.sendEmptyMessage(DISMISS_DIALOG);
            }
        });
    }

    /**
     * open camera
     */
    public void startCamera() {
        // 防止快速点击，但是单独拍照不管
        if (!DoubleUtils.isFastDoubleClick() || config.camera) {
            switch (config.mimeType) {
                case PictureConfig.TYPE_MEDIA_ALL:
                    // 如果是全部类型下，单独拍照就默认图片 (因为单独拍照不会new此PopupWindow对象)
                    if (popupWindow != null) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        popupWindow.showAsDropDown(rl_picture_title);
                    } else {
                        startOpenCamera();
                    }
                    break;
                case PictureConfig.TYPE_MEDIA_IMAGE:
                    // 拍照
                    startOpenCamera();
                    break;
                case PictureConfig.TYPE_MEDIA_VIDEO:
                    // 录视频
                    startOpenCameraVideo();
                    break;
                case PictureConfig.TYPE_MEDIA_AUDIO:
                    // 录音
                    startOpenCameraAudio();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * start to camera、preview、crop
     */
    public void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            int type = config.mimeType == PictureConfig.TYPE_MEDIA_ALL ? PictureConfig.TYPE_MEDIA_IMAGE : config.mimeType;
            File cameraFile = PictureFileUtils.createCameraFile(this,
                    type,
                    outputCameraPath, config.suffixType);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera、video
     */
    public void startOpenCameraVideo() {
        //原始代码
        //  Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //  if (cameraIntent.resolveActivity(getPackageManager()) != null) {
        //      File cameraFile = PictureFileUtils.createCameraFile(this, config.mimeType ==
        //                      PictureConfig.TYPE_ALL ? PictureConfig.TYPE_VIDEO : config.mimeType,
        //              outputCameraPath, config.suffixType);
        //      cameraPath = cameraFile.getAbsolutePath();
        //      Uri imageUri = parseUri(cameraFile);
        //      cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //      cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.recordVideoSecond);
        //      cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, config.videoQuality);
        //      startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        //  }

        //一次更改
        // if (null != config.recordVideoListene) {
        //     config.recordVideoListene.openRecordVideo(this, PictureConfig.REQUEST_CAMERA);
        // }


        // TODO: 2018/5/31 下午10:58  跳转到自己封装的拍摄组件中
        Intent intent = new Intent(mActivity, RecordVideoActivity.class);
        intent.putExtra(RecordVideoActivity.MODE_CAPTUREBUTTON, CaptureButton.MODE_RECORD_AUDIO);
        startActivityForResult(intent, PictureConfig.REQUEST_CAMERA);
    }

    /**
     * start to camera audio
     */
    public void startOpenCameraAudio() {
        rxPermissions.request(Manifest.permission.RECORD_AUDIO).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                    }
                } else {
                    ToastManage.s(mContext, getString(R.string.picture_audio));
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * 生成uri
     */
    private Uri parUri(File cameraFile) {
        Uri imageUri;
        String authority = getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(mContext, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_left_back) {
            if (mFolderPopWindow.isShowing()) {
                mFolderPopWindow.dismiss();
            } else {
                closeActivity();
            }
        } else if (id == R.id.picture_title) {//  if (mFolderPopWindow.isShowing()) {
            //      mFolderPopWindow.dismiss();
            //  } else {
            //      if (images != null && images.size() > 0) {
            //          mFolderPopWindow.showAsDropDown(rl_picture_title);
            //          List<LocalMedia> selectedImages = adapter.getSelectedImages();
            //          mFolderPopWindow.notifyDataCheckedStatus(selectedImages);
            //      }
            //  }
        } else if (id == R.id.tv_showAllPic) {
            if (mFolderPopWindow.isShowing()) {
                mFolderPopWindow.dismiss();
            } else {
                if (images != null && images.size() > 0) {
                    int yOffset = findViewById(R.id.rl_bottom).getHeight();
                    mFolderPopWindow.showAtLocation(findViewById(R.id.rl_root), Gravity.BOTTOM, 0, yOffset);
                    List<LocalMedia> selectedImages = mAdapter.getSelectedImages();
                    mFolderPopWindow.notifyDataCheckedStatus(selectedImages);
                }
            }
        } else if (id == R.id.picture_id_preview) {
            List<LocalMedia> selectedImages = mAdapter.getSelectedImages();

            List<LocalMedia> medias = new ArrayList<>();
            for (LocalMedia media : selectedImages) {
                medias.add(media);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) medias);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
            bundle.putBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, true);
            startActivity(PicturePreviewActivity.class, bundle,
                    config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCropMulti.REQUEST_MULTI_CROP);
            overridePendingTransition(R.anim.a5, 0);
        } else if (id == R.id.id_ll_ok) {
            List<LocalMedia> images = mAdapter.getSelectedImages();
            LocalMedia image = images.size() > 0 ? images.get(0) : null;
            String pictureType = image != null ? image.getPictureType() : "";
            // 如果设置了图片最小选择数量，则判断是否满足条件
            int size = images.size();
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            if (config.minSelectNum > 0 && config.selectionMode == PictureConfig.MULTIPLE) {
                if (size < config.minSelectNum) {
                    String str = eqImg ? getString(R.string.picture_min_img_num, config.minSelectNum)
                            : getString(R.string.picture_min_video_num, config.minSelectNum);
                    ToastManage.s(mContext, str);
                    return;
                }
            }
            if (config.enableCrop && eqImg) {
                if (config.selectionMode == PictureConfig.SINGLE) {
                    originalPath = image.getOriginalPath();
                    startCrop(originalPath);
                } else {
                    // 是图片和选择压缩并且是多张，调用批量压缩
                    ArrayList<String> medias1 = new ArrayList<>();
                    for (LocalMedia media : images) {
                        medias1.add(media.getOriginalPath());
                    }
                    startCrop(medias1);
                }
            } else if (config.isCompress && eqImg) {
                // 图片才压缩，视频不管
                compressImage(images);
            } else {
                onResult(images);
            }
        }
    }

    /**
     * 播放音频
     */
    private void audioDialog(final String path) {
        audioDialog = new CustomDialog(mContext,
                LinearLayout.LayoutParams.MATCH_PARENT, audioH,
                R.layout.picture_audio_dialog, R.style.Theme_dialog);
        audioDialog.getWindow().setWindowAnimations(R.style.Dialog_Audio_StyleAnim);
        tv_musicStatus = (TextView) audioDialog.findViewById(R.id.tv_musicStatus);
        tv_musicTime = (TextView) audioDialog.findViewById(R.id.tv_musicTime);
        musicSeekBar = (SeekBar) audioDialog.findViewById(R.id.musicSeekBar);
        tv_musicTotal = (TextView) audioDialog.findViewById(R.id.tv_musicTotal);
        tv_PlayPause = (TextView) audioDialog.findViewById(R.id.tv_PlayPause);
        TextView tv_Stop = (TextView) audioDialog.findViewById(R.id.tv_Stop);
        TextView tv_Quit = (TextView) audioDialog.findViewById(R.id.tv_Quit);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initPlayer(path);
            }
        }, 30);
        tv_PlayPause.setOnClickListener(new audioOnClick(path));
        tv_Stop.setOnClickListener(new audioOnClick(path));
        tv_Quit.setOnClickListener(new audioOnClick(path));
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        audioDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stop(path);
                    }
                }, 30);
                try {
                    if (audioDialog != null
                            && audioDialog.isShowing()) {
                        audioDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        handler.post(runnable);
        audioDialog.show();
    }

    /**
     * 初始化音频播放组件
     */
    private void initPlayer(String path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            playAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     */
    private void playAudio() {
        if (mediaPlayer != null) {
            musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            musicSeekBar.setMax(mediaPlayer.getDuration());
        }
        String ppStr = tv_PlayPause.getText().toString();
        if (ppStr.equals(getString(R.string.picture_play_audio))) {
            tv_PlayPause.setText(getString(R.string.picture_pause_audio));
            tv_musicStatus.setText(getString(R.string.picture_play_audio));
            playOrPause();
        } else {
            tv_PlayPause.setText(getString(R.string.picture_play_audio));
            tv_musicStatus.setText(getString(R.string.picture_pause_audio));
            playOrPause();
        }
        if (isPlayAudio == false) {
            handler.post(runnable);
            isPlayAudio = true;
        }
    }

    /**
     * 停止播放
     */
    public void stop(String path) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停播放
     */
    public void playOrPause() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(String folderName, List<LocalMedia> images) {
        boolean camera = StringUtils.isCamera(folderName);
        camera = config.isCamera ? camera : false;
        mAdapter.setShowCamera(camera);
        picture_title.setText(folderName);
        mAdapter.bindImagesData(images);
        mFolderPopWindow.dismiss();
    }

    @Override
    public void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

                Log.e("请求权限", aBoolean + "");

                if (aBoolean) {
                    startCamera();
                } else {
                    ToastManage.s(mContext, getString(R.string.picture_camera));
                    if (config.camera) {
                        closeActivity();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("请求权限", e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e("请求权限", "onComplete");

            }
        });
    }

    @Override
    public void onChange(List<LocalMedia> selectImages) {
        changeImageNumber(selectImages);
    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
        List<LocalMedia> images = mAdapter.getImages();
        startPreview(images, position);
    }

    /**
     * preview image and video
     */
    public void startPreview(List<LocalMedia> previewImages, int position) {
        LocalMedia media = previewImages.get(position);
        String pictureType = media.getPictureType();
        Bundle bundle = new Bundle();
        List<LocalMedia> result = new ArrayList<>();
        int mediaType = PictureMimeType.isPictureType(pictureType);
        switch (mediaType) {
            case PictureConfig.TYPE_MEDIA_IMAGE:
                // image
                List<LocalMedia> selectedImages = mAdapter.getSelectedImages();
                ImagesObservable.getInstance().saveLocalMedia(previewImages);
                bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
                bundle.putInt(PictureConfig.EXTRA_POSITION, position);
                startActivity(PicturePreviewActivity.class, bundle,
                        config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCropMulti.REQUEST_MULTI_CROP);
                overridePendingTransition(R.anim.a5, 0);
                break;
            case PictureConfig.TYPE_MEDIA_VIDEO:
                // video
                if (config.selectionMode == PictureConfig.SINGLE) {
                    result.add(media);
                    onResult(result);
                } else {
                    bundle.putString("video_path", media.getOriginalPath());
                    startActivity(PictureVideoPlayActivity.class, bundle);
                }
                break;
            case PictureConfig.TYPE_MEDIA_AUDIO:
                // audio
                if (config.selectionMode == PictureConfig.SINGLE) {
                    result.add(media);
                    onResult(result);
                } else {
                    audioDialog(media.getOriginalPath());
                }
                break;
        }
    }

    /**
     * change image selector state
     */
    public void changeImageNumber(List<LocalMedia> selectImages) {
        // 如果选择的视频没有预览功能
        String pictureType = selectImages.size() > 0
                ? selectImages.get(0).getPictureType() : "";
        if (config.mimeType == PictureMimeType.ofAudio()) {
            mPicPreviewTv.setVisibility(View.GONE);
        } else {
            boolean isVideo = PictureMimeType.isVideo(pictureType);
            boolean eqVideo = config.mimeType == PictureConfig.TYPE_MEDIA_VIDEO;
            mPicPreviewTv.setVisibility(isVideo || eqVideo ? View.GONE : View.VISIBLE);
        }
        boolean enable = selectImages.size() != 0;
        if (enable) {
            id_ll_ok.setEnabled(true);
            mPicPreviewTv.setEnabled(true);
            mPicPreviewTv.setSelected(true);
            picture_tv_ok.setSelected(true);
            if (numComplete) {
                picture_tv_ok.setText(getString
                        (R.string.picture_done_front_num, selectImages.size(),
                                config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                if (!anim) {
                    picture_tv_img_num.startAnimation(animation);
                }
                picture_tv_img_num.setVisibility(View.VISIBLE);
                picture_tv_img_num.setText(String.valueOf(selectImages.size()));
                //                picture_tv_ok.setText(getString(R.string.picture_completed));
                anim = false;
            }
        } else {
            id_ll_ok.setEnabled(false);
            mPicPreviewTv.setEnabled(false);
            mPicPreviewTv.setSelected(false);
            picture_tv_ok.setSelected(false);
            if (numComplete) {
                picture_tv_ok.setText(getString(R.string.picture_done_front_num, 0,
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                picture_tv_img_num.setVisibility(View.INVISIBLE);
                picture_tv_ok.setText(getString(R.string.picture_done));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<LocalMedia> medias = new ArrayList<>();
            LocalMedia media;
            String imageType;
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    Uri resultUri = UCrop.getOutput(data);
                    String cutPath = resultUri.getPath();
                    if (mAdapter != null) {
                        // 取单张裁剪已选中图片的path作为原图
                        List<LocalMedia> mediaList = mAdapter.getSelectedImages();
                        media = mediaList != null && mediaList.size() > 0 ? mediaList.get(0) : null;
                        if (media != null) {
                            originalPath = media.getOriginalPath();
                            media = new LocalMedia(originalPath, media.getDuration(), false,
                                    media.getPosition(), media.getNum(), config.mimeType);
                            media.setCutPath(cutPath);
                            media.setCut(true);
                            imageType = PictureMimeType.createImageType(cutPath);
                            media.setPictureType(imageType);
                            medias.add(media);
                            handlerResult(medias);
                        }
                    } else if (config.camera) {
                        // 单独拍照
                        media = new LocalMedia(cameraPath, 0, false,
                                config.isCamera ? 1 : 0, 0, config.mimeType);
                        media.setCut(true);
                        media.setCutPath(cutPath);
                        imageType = PictureMimeType.createImageType(cutPath);
                        media.setPictureType(imageType);
                        medias.add(media);
                        handlerResult(medias);
                    }
                    break;
                case UCropMulti.REQUEST_MULTI_CROP:
                    List<CutInfo> mCuts = UCropMulti.getOutput(data);
                    for (CutInfo c : mCuts) {
                        media = new LocalMedia();
                        imageType = PictureMimeType.createImageType(c.getPath());
                        media.setCut(true);
                        media.setOriginalPath(c.getPath());
                        media.setCutPath(c.getCutPath());
                        media.setPictureType(imageType);
                        media.setMimeType(config.mimeType);
                        medias.add(media);
                    }
                    handlerResult(medias);
                    break;
                case PictureConfig.REQUEST_CAMERA:

                    int type;
                    String path;

                    //180516 CnPeng 增加 data.hasExtra 的判断，因为，在 Nexus手机上拍照时返回的 data不是null，而是{ }
                    if (null != data && data.hasExtra("url")) {
                        type = data.getIntExtra("type", 0);
                        cameraPath = data.getStringExtra("url");
                        Log.e(TAG, "获取到视频地址：" + cameraPath);
                    }
                    if (config.mimeType == PictureMimeType.ofAudio()) {
                        cameraPath = getAudioPath(data);
                    }
                    // on take photo success
                    final File file = new File(cameraPath);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    String toType = PictureMimeType.fileToType(file);
                    if (config.mimeType != PictureMimeType.ofAudio()) {
                        PictureFileUtils.checkAndRepairDegree(file);
                    }
                    // 生成新拍照片或视频对象
                    media = new LocalMedia();
                    media.setOriginalPath(cameraPath);

                    //CnPeng 2018/11/17 12:09 AM 此处必须有大小，否则，拍照之后点击列表中新增的对应条目直接去看大图时啥也不显示！！！
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(cameraPath, options);
                    media.setWidth(options.outWidth);
                    media.setHeight(options.outHeight);

                    boolean eqVideo = toType.startsWith(PictureConfig.VIDEO);
                    int duration = eqVideo ? PictureMimeType.getLocalVideoDuration(cameraPath) : 0;
                    String pictureType = "";
                    if (config.mimeType == PictureMimeType.ofAudio()) {
                        pictureType = "audio/mpeg";
                        duration = PictureMimeType.getLocalVideoDuration(cameraPath);
                    } else {
                        pictureType = eqVideo ? PictureMimeType.createVideoType(cameraPath)
                                : PictureMimeType.createImageType(cameraPath);
                    }
                    media.setPictureType(pictureType);
                    media.setDuration(duration);
                    media.setMimeType(config.mimeType);

                    // 因为加入了单独拍照功能，所有如果是单独拍照的话也默认为单选状态
                    if (config.camera) {
                        // 如果是单选 拍照后直接返回
                        boolean eqImg = toType.startsWith(PictureConfig.IMAGE);
                        if (config.enableCrop && eqImg) {
                            // 去裁剪
                            originalPath = cameraPath;
                            startCrop(cameraPath);
                        } else if (config.isCompress && eqImg) {
                            // 去压缩
                            medias.add(media);
                            compressImage(medias);
                            if (mAdapter != null) {
                                images.add(0, media);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            // 不裁剪 不压缩 直接返回结果
                            medias.add(media);
                            onResult(medias);
                        }
                    } else {
                        // 多选 返回列表并选中当前拍照的
                        images.add(0, media);
                        if (mAdapter != null) {
                            List<LocalMedia> selectedImages = mAdapter.getSelectedImages();
                            // 没有到最大选择量 才做默认选中刚拍好的
                            if (selectedImages.size() < config.maxSelectNum) {
                                pictureType = selectedImages.size() > 0 ? selectedImages.get(0).getPictureType() : "";
                                boolean toEqual = PictureMimeType.mimeToEqual(pictureType, media.getPictureType());
                                // 类型相同或还没有选中才加进选中集合中
                                if (toEqual || selectedImages.size() == 0) {
                                    if (selectedImages.size() < config.maxSelectNum) {
                                        // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                                        if (config.selectionMode == PictureConfig.SINGLE) {
                                            singleRadioMediaImage();
                                        }
                                        selectedImages.add(media);
                                        mAdapter.bindSelectImages(selectedImages);
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    if (mAdapter != null) {
                        // 解决部分手机拍照完Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                        // 不及时刷新问题手动添加
                        manualSaveFolder(media);
                        tv_empty.setVisibility(images.size() > 0
                                ? View.INVISIBLE : View.VISIBLE);
                    }

                    if (config.mimeType != PictureMimeType.ofAudio()) {
                        int lastImageId = getLastImageId(eqVideo);
                        if (lastImageId != -1) {
                            removeImage(lastImageId, eqVideo);
                        }
                    }
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (config.camera) {
                closeActivity();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            ToastManage.s(mContext, throwable.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    /**
     * 单选图片
     */
    private void singleRadioMediaImage() {
        if (mAdapter != null) {
            List<LocalMedia> selectImages = mAdapter.getSelectedImages();
            if (selectImages != null
                    && selectImages.size() > 0) {
                selectImages.clear();
            }
        }
    }

    /**
     * 手动添加拍照后的相片到图片列表，并设为选中
     */
    private void manualSaveFolder(LocalMedia media) {
        try {
            createNewFolder(foldersList);
            LocalMediaFolder folder = getImageFolder(media.getOriginalPath(), foldersList);
            LocalMediaFolder cameraFolder = foldersList.size() > 0 ? foldersList.get(0) : null;
            if (cameraFolder != null && folder != null) {
                // 相机胶卷
                cameraFolder.setFirstImagePath(media.getOriginalPath());
                cameraFolder.setImages(images);
                cameraFolder.setImageNum(cameraFolder.getImageNum() + 1);
                // 拍照相册
                int num = folder.getImageNum() + 1;
                folder.setImageNum(num);
                folder.getImages().add(0, media);
                folder.setFirstImagePath(cameraPath);
                mFolderPopWindow.bindFolder(foldersList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                // 拍照
                startOpenCamera();
                break;
            case 1:
                // 录视频
                startOpenCameraVideo();
                break;
        }
    }

    /**
     * 播放音频点击事件
     */
    public class audioOnClick implements View.OnClickListener {
        private String path;

        public audioOnClick(String path) {
            super();
            this.path = path;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_PlayPause) {
                playAudio();
            }
            if (id == R.id.tv_Stop) {
                tv_musicStatus.setText(getString(R.string.picture_stop_audio));
                tv_PlayPause.setText(getString(R.string.picture_play_audio));
                stop(path);
            }
            if (id == R.id.tv_Quit) {
                handler.removeCallbacks(runnable);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stop(path);
                    }
                }, 30);
                try {
                    if (audioDialog != null
                            && audioDialog.isShowing()) {
                        audioDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
