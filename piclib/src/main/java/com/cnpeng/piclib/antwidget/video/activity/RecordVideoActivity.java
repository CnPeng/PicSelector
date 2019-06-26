package com.cnpeng.piclib.antwidget.video.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.cnpeng.piclib.R;
import com.cnpeng.piclib.antutils.AntFileUtil;
import com.cnpeng.piclib.antutils.PermissionUtils;
import com.cnpeng.piclib.antwidget.CaptureButton;
import com.cnpeng.piclib.antwidget.JCameraView;
import com.cnpeng.piclib.antwidget.TitleActivity;

/**
 * Created by yxj on 17/4/17.
 * <p>
 * 录制视频的界面
 */

public class RecordVideoActivity extends TitleActivity {

    /**
     * //拍照按钮的模式：拍照、拍照+录像
     */
    public static final String      MODE_CAPTUREBUTTON = "captureButtonMode";
    private             JCameraView mJCameraView;
    /**
     * //从首页加号里点击的，拍摄完毕后，跳转到创建广场界面，并且返回值
     */
    private             String      mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //没有标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                .FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.record_video);

        mFrom = getIntent().getStringExtra("FROM");
        initCameraView();
    }

    /**
     * 从上个页面传递到当前页面的intent中取出传递的 captureButtonMode.
     * 用来控制 JCameraView 中调用的 CaptureButton 的工作模式。
     * 如果模式为 MODE_TAKE_PHOTO ，则只能拍照，不能录视频；
     * 如果模式为 MODE_TAKE_PHOTO_AND_AUDIO，则既能拍照也能视频
     */
    private String getCaptureButtonModeFromIntent() {
        String captureButtonMode = getIntent().getStringExtra(MODE_CAPTUREBUTTON);
        if (TextUtils.isEmpty(captureButtonMode)) {
            captureButtonMode = CaptureButton.MODE_TAKE_PHOTO_AND_AUDIO;
        }
        return captureButtonMode;
    }

    private void initCameraView() {
        mJCameraView = (JCameraView) findViewById(R.id.cameraView);
        mJCameraView.setActivity(this);
        mJCameraView.setCaptureButtonMode(getCaptureButtonModeFromIntent());
        //设置视频保存路径（如果不设置默认为Environment.getExternalStorageDirectory().getPath()）
        //mJCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath());
        mJCameraView.setAutoFocus(false);
        mJCameraView.setCameraViewListener(new JCameraView.CameraViewListener() {
            @Override
            public void quit() {
                RecordVideoActivity.this.finish();
            }

            @Override
            public void captureSuccess(Bitmap bitmap) {//拍照成功
                String url = AntFileUtil.saveBitmapToSDCard(bitmap, RecordVideoActivity.this);//拍照保存路径
                Intent intent = new Intent();
                //把返回数据存入Intent 0是照片,1是视频
                intent.putExtra("url", url);
                intent.putExtra("type", 0);
                //                if (!TextUtils.isEmpty(mFrom) && mFrom.equals(PopupMenuUtil.class.getName())) {
                //                    intent.setClass(RecordVideoActivity.this, CreateNewTopicActivity.class);
                //                    intent.putExtra("FROM", mFrom);
                //                    startActivity(intent);
                //                } else {
                //设置返回数据
                setResult(RESULT_OK, intent);
                //                }
                finish();
            }

            @Override
            public void recordSuccess(String url) {//录视频成功
                Intent intent = new Intent();
                //把返回数据存入Intent 0是照片,1是视频
                intent.putExtra("url", url);
                intent.putExtra("type", 1);
                //                if (!TextUtils.isEmpty(mFrom) && mFrom.equals(PopupMenuUtil.class.getName())) {
                //                    intent.setClass(RecordVideoActivity.this, CreateNewTopicActivity.class);
                //                    intent.putExtra("FROM", mFrom);
                //                    startActivity(intent);
                //                } else {
                //设置返回数据
                setResult(RESULT_OK, intent);
                //                }
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT < 23) {
            if (!PermissionUtils.permissiontoolbefore23()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示").setMessage("没有照相机权限,请赋予本权限再开始拍照吧~").setPositiveButton("确定", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        }).show();
                return;
            }
        }
        mJCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mJCameraView.onPause();
    }

    public PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA:
                    PermissionUtils.requestPermission(RecordVideoActivity.this, PermissionUtils.CODE_RECORD_AUDIO,
                            mPermissionGrant, false);
                    break;
                case PermissionUtils.CODE_RECORD_AUDIO:
                    // TODO: CnPeng 2019-06-24 18:28 之前此处处理了埋点
                    break;
                default:
                    break;
            }
        }
    };
}
