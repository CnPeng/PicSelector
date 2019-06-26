package com.cnpeng.piclib.antutils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

/**
 * Created by yxj on 17/4/17.
 * 获取gif第一帧图片
 */

public class VideoHelper {
    public static Bitmap getFirstImage(String url) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(url);
        Bitmap bitmap = media.getFrameAtTime();
        media.release();
        return bitmap;
    }
}
