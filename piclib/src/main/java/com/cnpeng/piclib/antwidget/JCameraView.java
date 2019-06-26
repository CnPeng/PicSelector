package com.cnpeng.piclib.antwidget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.cnpeng.piclib.R;
import com.cnpeng.piclib.antutils.AntFileUtil;
import com.cnpeng.piclib.antutils.PathUtil;
import com.cnpeng.piclib.antwidget.video.activity.RecordVideoActivity;
import com.cnpeng.piclib.antwidget.video.helper.CameraFocusListener;
import com.cnpeng.piclib.antwidget.video.helper.CameraParamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by yxj on 17/4/17.
 * 录制视频的自定义控件
 */

public class JCameraView extends RelativeLayout implements SurfaceHolder.Callback, Camera.AutoFocusCallback,
        CameraFocusListener {

    public final String                TAG                   = "JCameraView";
    private      Context               mContext;
    private      VideoView             mVideoView;
    private      FocusView             mFocusView;     //对焦的按钮
    private      CaptureButton         mCaptureButton; //录制视频/拍照的按钮
    private      ImageView             mImageView;     //右上角摄像头切换的图标
    private      ImageView             picPreImageView;//拍照完成的预览的图片
    private      float                 screenProp;
    private      MediaRecorder         mediaRecorder;
    private      Camera                mCamera;
    private      Camera.Parameters     mParam;
    private      int                   previewWidth;
    private      int                   previewHeight;
    private      int                   pictureWidth;
    private      int                   pictureHeight;
    private      boolean               autoFocus;
    private      String                fileName;
    private      Bitmap                pictureBitmap;
    private      CameraViewListener    cameraViewListener;
    private      RecordVideoActivity   activity;
    private      boolean               needSetVisible;
    private      PowerManager          powerManager          = null;
    private      PowerManager.WakeLock wakeLock              = null;
    private      int                   iconMargin            = 0;
    private      int                   iconSrc               = 0;
    private      SurfaceHolder         mHolder               = null;
    private      String                videoFileName         = "";
    private      boolean               isPlay                = false;
    private      boolean               isRecorder            = false;
    private      boolean               isPre                 = false;//是不是在拍照的预览
    private      int                   SELECTED_CAMERA       = 0;
    private      int                   CAMERA_POST_POSITION  = 0;  //后置摄像头
    private      int                   CAMERA_FRONT_POSITION = 1; //前置摄像头
    private      int                   count                 = 0;
    private      int                   mRotateDegree;

    public JCameraView(Context context) {
        this(context, null);
    }

    public JCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        powerManager = (PowerManager) mContext.getSystemService(mContext.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        findAvailableCameras();
        SELECTED_CAMERA = CAMERA_POST_POSITION;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCameraView, defStyleAttr, 0);
        iconMargin = a.getDimensionPixelSize(R.styleable.JCameraView_iconMargin, (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        iconSrc = a.getResourceId(R.styleable.JCameraView_iconSrc, R.drawable.capture);

        initView();
    }


    public void setCameraViewListener(CameraViewListener cameraViewListener) {
        this.cameraViewListener = cameraViewListener;
    }

    public void setActivity(RecordVideoActivity activity) {
        this.activity = activity;
        mCaptureButton.setActivity(activity);
    }

    /**
     * 初始化View界面，含控件的初始化以及控件监听器的初始化
     */
    private void initView() {
        setWillNotDraw(false);
        this.setBackgroundColor(Color.BLACK);

        initVideoView();            //视频播放控件
        initPicPreImageView();      //预览
        initCaptureButton();        //录像按钮
        initCameraChangeButton();   //切换摄像头
        initFocusView();            //对焦视图
        initSurfaceHolder();
    }

    private void initSurfaceHolder() {
        mHolder = mVideoView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);
    }

    private void initFocusView() {
        //        对焦的图标
        mFocusView = new FocusView(mContext, 120);
        mFocusView.setVisibility(INVISIBLE);

        this.addView(mFocusView);       //对焦
    }

    private void initCameraChangeButton() {
        //右上角切换摄像头的按钮
        mImageView = new ImageView(mContext);
        LayoutParams imageViewParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageViewParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        imageViewParam.setMargins(0, iconMargin, iconMargin, 0);
        mImageView.setLayoutParams(imageViewParam);
        mImageView.setImageResource(iconSrc);
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecorder && mCamera != null) {
                    releaseCamera();

                    //切换摄像头
                    SELECTED_CAMERA = SELECTED_CAMERA == CAMERA_POST_POSITION ? CAMERA_FRONT_POSITION :
                            CAMERA_POST_POSITION;

                    getCamera(SELECTED_CAMERA);
                    previewWidth = previewHeight = 0;
                    pictureWidth = pictureHeight = 0;
                    Log.e("setStartPreview", "切换摄像头");
                    setStartPreview(mCamera, mHolder);
                }
            }
        });

        this.addView(mImageView);       //右上角摄像头切换
    }

    private void initCaptureButton() {
        //底部居中的录像/拍照按钮
        LayoutParams btnParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        mCaptureButton = new CaptureButton(mContext);
        //        mCaptureButton.setCaptureButtonMode(captureButtonMode);
        mCaptureButton.setLayoutParams(btnParams);

        //初始化为自动对焦
        autoFocus = true;
        initCaptureButtonListener();

        this.addView(mCaptureButton);   //录视频
    }

    private void initPicPreImageView() {
        LayoutParams picPreImageViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        picPreImageViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        picPreImageView = new ImageView(mContext);
        picPreImageView.setLayoutParams(picPreImageViewParam);
        picPreImageView.setVisibility(INVISIBLE);

        this.addView(picPreImageView);  //预览
    }

    private void initVideoView() {
        /*VideoView 播放视频的界面*/
        mVideoView = new VideoView(mContext);
        LayoutParams videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mVideoView.setLayoutParams(videoViewParam);
        this.addView(mVideoView);       //viedoView

        mVideoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(JCameraView.this);
            }
        });
    }

    /**
     * 初始化拍照/录像按钮的监听器
     */
    private void initCaptureButtonListener() {
        mCaptureButton.setCaptureListener(new CaptureButton.CaptureListener() {
            @Override
            public void capture() {
                JCameraView.this.capture();
            }

            @Override
            public void cancel() {
                isRecorder = false;
                releaseCamera();
                getCamera(SELECTED_CAMERA);
                isPre = false;
                setStartPreview(mCamera, mHolder);
                picPreImageView.setVisibility(INVISIBLE);
            }

            @Override
            public void determine() {
                if (cameraViewListener != null) {
                    cameraViewListener.captureSuccess(pictureBitmap);
                }
                releaseCamera();
                isPre = false;
            }

            @Override
            public void quit() {
                if (cameraViewListener != null) {
                    cameraViewListener.quit();
                }
            }

            @Override
            public void record() {
                startRecord();
            }

            @Override
            public void recordEnd() {
                stopRecord();
            }

            @Override
            public void getRecordResult() {
                if (cameraViewListener != null) {
                    cameraViewListener.recordSuccess(fileName);
                }
                mVideoView.stopPlayback();
                releaseCamera();
                isPlay = false;
            }

            @Override
            public void deleteRecordResult() {
                File file = new File(fileName);
                if (file.exists()) {
                    file.delete();
                }
                fileName = null;
                releaseCamera();

                mVideoView.pause();
                mVideoView.stopPlayback();  //整个界面销毁，各种按钮不存在了，依附于按钮的监听器也就不存在了

                isPlay = false;
                getCamera(SELECTED_CAMERA);
                setStartPreview(mCamera, mHolder);
            }

            @Override
            public void scale(float scaleValue) {
                if (scaleValue >= 0) {
                    int scaleRate = (int) (scaleValue / 50);

                    if (scaleRate < 10 && scaleRate >= 0 && mParam != null && mCamera != null && mParam
                            .isSmoothZoomSupported()) {
                        mParam = mCamera.getParameters();
                        mParam.setZoom(scaleRate);
                        mCamera.setParameters(mParam);
                    }
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    //获取Camera
    private void getCamera(int position) {
        try {
            if (null == mCamera) {
                Log.d(JCameraView.class.getName(), "position:" + position);
                mCamera = Camera.open(position);
                Log.d("guo_zjin:", "create camera success");
            }
        } catch (RuntimeException e) {
            Log.d("guo_zjin:", "create camera Runtime Exception");
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("提示").setMessage("没有照相机权限,请赋予本权限再开始拍照吧~").setPositiveButton("确定", new DialogInterface
                    .OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    activity.finish();
                }
            }).show();
        } catch (Exception e) {
            Log.d("guo_zjin:", "create camera Exception");
            mCamera = null;
            e.printStackTrace();
        }
    }

    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        if (camera == null) {
            return;
        }
        if (mImageView.getVisibility() != View.VISIBLE && !isPre && !isPlay && !isRecorder) {
            mImageView.setVisibility(View.VISIBLE);
            this.removeView(mImageView);
            this.addView(mImageView);
        }
        this.invalidate();

        mParam = camera.getParameters();

        if (screenProp != 0) {
            Camera.Size previewSize = CameraParamUtil.getInstance().getPreviewSize(mParam.getSupportedPreviewSizes(),
                    1000, screenProp);
            Camera.Size pictureSize = CameraParamUtil.getInstance().getPictureSize(mParam.getSupportedPictureSizes(),
                    1200, screenProp);
            mParam.setPreviewSize(previewSize.width, previewSize.height);
            mParam.setPictureSize(pictureSize.width, pictureSize.height);
        }
        if (CameraParamUtil.getInstance().isSupportedFocusMode(mParam.getSupportedFocusModes(), Camera.Parameters
                .FOCUS_MODE_CONTINUOUS_VIDEO)) {
            mParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (CameraParamUtil.getInstance().isSupportedFocusMode(mParam.getSupportedFocusModes(), Camera
                .Parameters.FOCUS_MODE_AUTO)) {
            mParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        if (CameraParamUtil.getInstance().isSupportedPictureFormats(mParam.getSupportedPictureFormats(), ImageFormat
                .JPEG)) {
            mParam.setPictureFormat(ImageFormat.JPEG);
            mParam.setJpegQuality(100);
        }
        camera.setParameters(mParam);
        try {
            camera.setPreviewDisplay(holder);
            Log.d(TAG, "准备预览——setPreviewDisplay");

            mRotateDegree = CameraParamUtil.getInstance().setCameraDisplayOrientation(activity, SELECTED_CAMERA);
            camera.setDisplayOrientation(mRotateDegree);
            Log.d(TAG, "设置旋转角度——setDisplayOrientation");

            camera.startPreview();
            Log.d(TAG, "开启预览");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "在setStartPreview里发生文件不存在,被release");
            releasePre();
        } catch (IOException e) {
            Log.d(TAG, "在这里发生了一个该死的异常,但是我不得不在这里处理掉。。。。。00" + e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "在这里发生了一个该死的异常,但是我不得不在这里处理掉。。。。。01" + e.getMessage());
        }
    }

    private void releaseMediaRecorder() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mHolder = mVideoView.getHolder();
        }
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        Log.d("guo_zjin: ", "release Camera here.......");
        if (mCamera != null) {
            mCamera.stopPreview();
            try {
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void capture() {
        if (autoFocus) {
            mCamera.autoFocus(this);
        } else {
            isPre = true;
            if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        mCamera.stopPreview();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        Matrix matrix = new Matrix();
                        //matrix.setRotate(90);    //180516 不能固定90，有些手机这个角度不是90
                        matrix.setRotate(mRotateDegree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        pictureBitmap = bitmap;
                        mImageView.setVisibility(INVISIBLE);
                        mCaptureButton.captureSuccess();
                        picPreImageView.setVisibility(VISIBLE);
                        picPreImageView.setImageBitmap(pictureBitmap);
                    }
                });
            } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        mCamera.stopPreview();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(360 - mRotateDegree);  //180516 准备拍照时做了减法运算，拍完照了再减回去
                        //matrix.setScale(-1, 1);
                        matrix.postTranslate(bitmap.getWidth(), 0);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        pictureBitmap = bitmap;
                        mImageView.setVisibility(INVISIBLE);
                        mCaptureButton.captureSuccess();
                        picPreImageView.setVisibility(VISIBLE);
                        picPreImageView.setImageBitmap(pictureBitmap);
                    }
                });
            }
        }
    }

    //自动对焦
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (autoFocus) {
            isPre = true;
            if (SELECTED_CAMERA == CAMERA_POST_POSITION && success) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        mCamera.stopPreview();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        pictureBitmap = bitmap;
                        mImageView.setVisibility(INVISIBLE);
                        mCaptureButton.captureSuccess();
                        picPreImageView.setVisibility(VISIBLE);
                        picPreImageView.setImageBitmap(pictureBitmap);
                    }
                });
            } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        mCamera.stopPreview();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(270);
                        matrix.postScale(-1, 1);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        pictureBitmap = bitmap;
                        mImageView.setVisibility(INVISIBLE);
                        mCaptureButton.captureSuccess();
                        picPreImageView.setVisibility(VISIBLE);
                        picPreImageView.setImageBitmap(pictureBitmap);
                    }
                });
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        float heightSize = MeasureSpec.getSize(heightMeasureSpec);
        screenProp = heightSize / widthSize;
    }

    //    @Override
    //    public void surfaceCreated(SurfaceHolder holder) {
    //        Log.d("拍摄视频", "surfaceCreated");
    //        setStartPreview(mCamera, holder);
    //        mHolder = holder;
    //        Log.d("guo_zjin:surfaceCreated ", mHolder.getSurface().hashCode() + "");
    //    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mHolder = holder;
            }
            Log.d("guo_zjin:surfaceCreated ", mHolder.getSurface().hashCode() + "");
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("拍摄视频", "surfaceChanged");
        if (mHolder.getSurface() == null) {
            return;
        }
        mHolder = holder;
        // stop preview before making changes
        try {
            Log.d("guo_zjin:surfaceChanged before stopPreview ", mHolder.hashCode() + "");

            if (null == mCamera) {
                getCamera(SELECTED_CAMERA);
            }
            mCamera.stopPreview();
            Log.e("setStartPreview", "surfaceChanged——Surface改变");

            setStartPreview(mCamera, holder);
        } catch (Exception e) {
            Log.d("guo_zjin:", "surfaceChanged Exception " + e.getMessage());
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    public void onResume() {
        SystemClock.sleep(500);
        if (mCamera == null) {
            getCamera(SELECTED_CAMERA);
            if (needSetVisible && mVideoView != null) {
                mVideoView.setVisibility(View.VISIBLE);
            } else {
                needSetVisible = true;
            }
            if (mHolder != null && !isPre) {
                Log.e("setStartPreview", "获取焦点");

                setStartPreview(mCamera, mHolder);
            }
        }
        wakeLock.acquire();
    }

    public void onPause() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        releaseMediaRecorder();
        releaseCamera();
        if (needSetVisible && mVideoView != null) {
            mVideoView.setVisibility(View.INVISIBLE);
        }
    }

    private void startRecord() {
        Log.e("startRecord", "开始录像");

        if (isRecorder) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mCamera == null) {
            stopRecord();
            return;
        }
        mCamera.unlock();
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        if (android.os.Build.MODEL.contains("vivo X5Pro")) {
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        } else {
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        }
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //在1加手机上,如果设置15,直接挂掉,好像是能接受的最大值是30,所以屏蔽掉
        //        mediaRecorder.setVideoFrameRate(15);//帧率

        if (mParam == null) {
            mParam = mCamera.getParameters();
        }
        Camera.Size videoSize = CameraParamUtil.getInstance().getPictureSize(CameraParamUtil.getSupportedVideoSizes
                (mParam), 1000, screenProp);

        mediaRecorder.setVideoSize(videoSize.width, videoSize.height);
        int rotationRecord = CameraParamUtil.getInstance().setCameraDisplayOrientation(activity, SELECTED_CAMERA);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(SELECTED_CAMERA, info);
        int frontRotation;
        if (rotationRecord == 180) {
            //反向横屏的前置角度
            frontRotation = 180;
        } else {
            //竖屏和正向横屏的前置角度
            //录制下来的视屏选择角度，此处为前置
            frontRotation = (rotationRecord == 0) ? 270 - info.orientation : info.orientation;
        }
        mediaRecorder.setOrientationHint((SELECTED_CAMERA == 1) ? frontRotation : rotationRecord);
        mediaRecorder.setMaxDuration(10000);
        mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        mediaRecorder.setPreviewDisplay(mHolder.getSurface());

        videoFileName = "/myimage/video_" + System.currentTimeMillis() + ".mp4";
        File file = new File(PathUtil.getPathSDCard(), videoFileName);
        AntFileUtil.checkFilePath(file, false);
        mediaRecorder.setOutputFile(file.getPath());
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecorder = true;
            mImageView.setVisibility(INVISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("错误", e.getMessage());
        }
    }

    private void stopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Toast.makeText(mContext, "视频过短，请重新录制", Toast.LENGTH_SHORT);
                    if (mr != null) {
                        mr.reset();
                    }
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecorder = false;
                    fileName = PathUtil.getPathSDCard() + videoFileName;
                    //                    fileName = PathUtil.getPathFile() + videoFileName;
                    File file = new File(fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    releaseCamera();
                    getCamera(SELECTED_CAMERA);
                    isPlay = false;
                    setStartPreview(mCamera, mHolder);
                    mCaptureButton.initButton();
                }
            });

            mediaRecorder.setOnInfoListener(null);
            mediaRecorder.setPreviewDisplay(null);
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecorder = false;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            releaseCamera();

            mImageView.setVisibility(INVISIBLE);

            fileName = PathUtil.getPathSDCard() + videoFileName;
            File file = new File(fileName);
            if (file.exists() && file.length() > 0) {
                mVideoView.setVideoPath(fileName);
                mVideoView.start();

                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        isPlay = true;
                        mp.start();
                        mp.setLooping(true);
                    }
                });
                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (fileName != null) {
                            mVideoView.setVideoPath(fileName);
                            mVideoView.start();
                        }
                    }
                });

                mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.d("guo_zjin:", "mVideoView.setOnErrorListener,被release");
                        if (!isPre) {
                            releasePre();
                        }
                        return true;
                    }
                });
            } else {
                Toast.makeText(mContext, "视频不存在，赶紧重拍一个吧~", Toast.LENGTH_SHORT);
                Log.d("guo_zjin:", "stopRecorder,被release");
                releasePre();
            }
        }
    }

    //关闭预览
    private void releasePre() {
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
        if (mHolder != null) {
            mHolder.getSurface().release();
        }
        mCaptureButton.initButton();
        picPreImageView.setVisibility(INVISIBLE);
        // 区分一下是照片还是视频
        releaseCamera();
        isPre = false;
        isPlay = false;
        getCamera(SELECTED_CAMERA);
        setStartPreview(mCamera, mHolder);
    }

    /**
     * 获得可用的相机，并设置前后摄像机的ID
     */
    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int numCamera = Camera.getNumberOfCameras();
        for (int i = 0; i < numCamera; i++) {
            Camera.getCameraInfo(i, info);
            // 找到了前置摄像头
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                CAMERA_FRONT_POSITION = info.facing;
            }
            // 找到了后置摄像头
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                CAMERA_POST_POSITION = info.facing;
            }
        }
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    @Override
    public void onFocusBegin(float x, float y) {
        mFocusView.setVisibility(VISIBLE);
        mFocusView.setX(x - mFocusView.getWidth() / 2);
        mFocusView.setY(y - mFocusView.getHeight() / 2);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (!success) {
                    count++;
                    if (count > 3) {
                        mCamera.cancelAutoFocus();
                        onFocusEnd();
                        count = 0;
                    }
                }
                if (success) {
                    mCamera.cancelAutoFocus();
                    onFocusEnd();
                    count = 0;
                }
            }
        });
    }

    //手动对焦结束
    @Override
    public void onFocusEnd() {
        mFocusView.setVisibility(INVISIBLE);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!autoFocus && event.getAction() == MotionEvent.ACTION_DOWN && SELECTED_CAMERA == CAMERA_POST_POSITION &&
                !isPlay && !isPre) {
            if (mCamera != null) {
                mParam = mCamera.getParameters();
                mParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(mParam);
            }
            onFocusBegin(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置按钮的操作模式，拍照或者录像。设置为拍照模式之后，即便长按也不会执行录像功能
     *
     * @param mode 按妞的功能模式。取值： MODE_TAKE_PHOTO、MODE_TAKE_PHOTO_AND_AUDIO
     */
    public void setCaptureButtonMode(String mode) {

        mCaptureButton.setCaptureButtonMode(mode);
        //        int index = this.indexOfChild(mCaptureButton);
        //        if (index >= 0) {
        //            this.removeView(mCaptureButton);
        //            initCaptureButton();
        //        }
    }

    public interface CameraViewListener {
        void quit();//关闭界面

        void captureSuccess(Bitmap bitmap);

        void recordSuccess(String url);
    }
}
