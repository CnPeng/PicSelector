package com.cnpeng.piclib.antwidget.video.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.databinding.DataBindingUtil;

import com.cnpeng.piclib.R;
import com.cnpeng.piclib.antutils.NetworkUtil;
import com.cnpeng.piclib.antwidget.TitleActivity;
import com.cnpeng.piclib.databinding.VideoPreviewBinding;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by yxj on 17/4/18.
 * 已经缓存了,直接播放,如果没有缓存需要判断是否是wifi
 * status -1是初始化,0是加载中,1是播放中,2是播放完成 -1只需要显示默认图片 0需要显示默认图片和加载状态, 1需要显示videoview, 2需要重播按钮
 */
public class VideoPreviewActivity extends TitleActivity implements CacheListener {
    private String              videoUrl;
    private VideoPreviewBinding binding;
    private VideoView           videoView;
    /**
     * //0和1 本地和网络
     */
    private String              from;

    private HttpProxyCacheServer mProxyCacheServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //没有标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        binding = DataBindingUtil.setContentView(this, R.layout.video_preview);
        videoView = (VideoView) findViewById(R.id.video_preview_view);

        //        AntLinkApplication.getProxy(context)
        mProxyCacheServer = new HttpProxyCacheServer(this);

        binding.videoPreviewReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCachedState(true);
            }
        });

        binding.videoPreviewCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        videoUrl = getIntent().getStringExtra("VideoUrl");

        //是否是网络视频，"0" 本地视频，"1" 网络视频
        from = getIntent().getStringExtra("From");
        if ("0".equals(from)) {//网络需要加载,本地不用
            setCachedState(true);
        } else {
            //初始是0
            binding.setStatus(-1);
            checkCachedState();
        }
    }

    private void getVideoCache() {
        //注册缓存video
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProxyCacheServer.registerCacheListener(VideoPreviewActivity.this, videoUrl);
                InputStream is = null;
                try {
                    URL url = new URL(mProxyCacheServer.getProxyUrl(videoUrl));
                    is = url.openStream();
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    while (is.read(buffer) != -1) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // Long hair, don't care.
                            Log.e(VideoPreviewActivity.class.getName(), e.getMessage());
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void checkCachedState() {
        boolean fullyCached = mProxyCacheServer.isCached(videoUrl);

        //没有加载完成并且不是wifi就提示是否使用流量加载
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, getString(R.string.hint_no_net), Toast.LENGTH_SHORT);
            finish();
        } else if (!fullyCached && !NetworkUtil.isWifiConnect(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPreviewActivity.this);
            builder.setTitle("加载视频")
                    .setMessage("正在尝试使用移动流量加载视频?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    //加载中
                    binding.setStatus(0);
                    getVideoCache();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return;
        } else if (!fullyCached) {
            //缓存了,只需要直接显示就行了_加载中
            binding.setStatus(0);
            getVideoCache();
        } else if (fullyCached) {
            //加载中
            binding.setStatus(0);
            setCachedState(fullyCached);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mProxyCacheServer) {
            mProxyCacheServer.unregisterCacheListener(this);
        }
    }

    @Override
    public void onCacheAvailable(File file, String url, int percentsAvailable) {
        setCachedState(percentsAvailable == 100);
    }

    /**
     * //这个方法是加载完成后用来播放视频的
     */
    private void setCachedState(boolean cached) {
        if (cached) {
            binding.setStatus(1);
            if ("0".equals(from)) {
                //本地直接播放
                videoView.setVideoPath(videoUrl);
            } else {
                videoView.setVideoPath(mProxyCacheServer.getProxyUrl(videoUrl));
            }
            videoView.start();
            //播放完成回调
            videoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
        }
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            binding.setStatus(2);
        }
    }
}