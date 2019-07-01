package com.cnpeng.piclib.config;


import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.cnpeng.piclib.R;
import com.cnpeng.piclib.entity.LocalMedia;

import java.io.File;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：2017/5/24
 *
 * @author luck
 */

public final class PictureMimeType {
    public final static String JPEG = ".JPEG";
    public final static String PNG  = ".png";

    public static int ofAll() {
        return PictureConfig.TYPE_MEDIA_ALL;
    }

    public static int ofImage() {
        return PictureConfig.TYPE_MEDIA_IMAGE;
    }

    public static int ofVideo() {
        return PictureConfig.TYPE_MEDIA_VIDEO;
    }

    public static int ofAudio() {
        return PictureConfig.TYPE_MEDIA_AUDIO;
    }

    public static int isPictureType(String pictureType) {
        switch (pictureType) {
            case "image/png":
            case "image/PNG":
            case "image/jpeg":
            case "image/JPEG":
            case "image/webp":
            case "image/WEBP":
            case "image/gif":
            case "image/bmp":
            case "image/GIF":
            case "imagex-ms-bmp":
                return PictureConfig.TYPE_MEDIA_IMAGE;


            //   case "video/3gp":
            //   case "video/3gpp":
            //   case "video/3gpp2":
            //   case "video/avi":
            case "video/mp4": // ATTENTION: 180419 由于H5中只能播放mp4所以过滤掉其他视频格式
                //   case "video/quicktime":
                //   case "video/x-msvideo":
                //   case "video/x-matroska":
                //   case "video/mpeg":
                //   case "video/webm":
                //   case "video/mp2ts":
                return PictureConfig.TYPE_MEDIA_VIDEO;
            case "audio/mpeg":
            case "audio/x-ms-wma":
            case "audio/x-wav":
            case "audio/amr":
            case "audio/wav":
            case "audio/aac":
            case "audio/mp4":
            case "audio/quicktime":
            case "audio/lamr":
            case "audio/3gpp":
                return PictureConfig.TYPE_MEDIA_AUDIO;
        }
        return PictureConfig.TYPE_MEDIA_IMAGE;
    }

    /**
     * 是否是gif
     */
    public static boolean isGif(String pictureType) {
        switch (pictureType) {
            case "image/gif":
            case "image/GIF":
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否是gif。
     * 输入图片地址根据后缀名判断是否是GIF，暂时只考虑 .gif 和 .GIF
     */
    public static boolean isGif2(String path) {
        if (!TextUtils.isEmpty(path)) {
            // int lastIndex = path.lastIndexOf(".");
            // String pictureType = path.substring(lastIndex, path.length());
            // return pictureType.startsWith(".gif") || pictureType.startsWith(".GIF");

            return path.endsWith(".gif") || path.endsWith(".GIF");
        }
        return false;
    }

    /**
     * 是否是视频
     */
    public static boolean isVideo(String pictureType) {
        switch (pictureType) {
            //            case "video/3gp":
            //            case "video/3gpp":
            //            case "video/3gpp2":
            //            case "video/avi":
            case "video/mp4":            // ATTENTION: 180419 由于H5中只能播放mp4所以过滤掉其他视频格式

                //            case "video/quicktime":
                //            case "video/x-msvideo":
                //            case "video/x-matroska":
                //            case "video/mpeg":
                //            case "video/webm":
                //            case "video/mp2ts":
                return true;
        }
        return false;
    }

    /**
     * 作者：CnPeng
     * 时间：2018/6/4 下午3:35
     * 功用：根据传入的路径后缀名判断是否是视频
     * 说明：这个方法并不精确，但是，能涵盖常见的视频格式
     *
     * 华为 荣耀10 手机上，走图库分享时拿到的地址为 content://media/external/images/media/152 ，此时，suffixStartIndex 为-1
     */
    public static boolean isVideo2(String path) {
        int suffixStartIndex = path.lastIndexOf(".");
        if (-1 == suffixStartIndex) {
            return false;
        } else {
            String suffix = path.substring(suffixStartIndex);
            return (".3gp").equalsIgnoreCase(suffix) ||
                    (".3gpp").equalsIgnoreCase(suffix) ||
                    (".3gpp2").equalsIgnoreCase(suffix) ||
                    (".avi").equalsIgnoreCase(suffix) ||
                    (".mp4").equalsIgnoreCase(suffix) ||
                    (".quicktime").equalsIgnoreCase(suffix) ||
                    (".x-msvideo").equalsIgnoreCase(suffix) ||
                    (".x-matroska").equalsIgnoreCase(suffix) ||
                    (".mpeg").equalsIgnoreCase(suffix) ||
                    (".mpg").equalsIgnoreCase(suffix) ||
                    (".dat").equalsIgnoreCase(suffix) ||
                    (".webm").equalsIgnoreCase(suffix) ||
                    (".rmvb").equalsIgnoreCase(suffix) ||
                    (".ra").equalsIgnoreCase(suffix) ||
                    (".rm").equalsIgnoreCase(suffix) ||
                    (".mov").equalsIgnoreCase(suffix) ||
                    (".qt").equalsIgnoreCase(suffix) ||
                    (".asf").equalsIgnoreCase(suffix) ||
                    (".wmv").equalsIgnoreCase(suffix) ||
                    (".flv").equalsIgnoreCase(suffix) ||
                    (".mp2ts").equalsIgnoreCase(suffix);
        }
    }


    /**
     * 是否是网络图片
     */
    public static boolean isHttp(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")
                    || path.startsWith("https")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件类型是图片还是视频
     */
    public static String fileToType(File file) {
        if (file != null) {
            String name = file.getName();
            //            if (name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".3gpp") || name.endsWith(".3gp") || name.startsWith(".mov")) {
            if (name.endsWith(".mp4")) {
                return "video/mp4";
            } else if (name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                    || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")
                    || name.endsWith(".bmp")) {
                return "image/jpeg";
            } else if (name.endsWith(".mp3") || name.endsWith(".amr")
                    || name.endsWith(".aac") || name.endsWith(".war")
                    || name.endsWith(".flac") || name.endsWith(".lamr")) {
                return "audio/mpeg";
            }
        }
        return "image/jpeg";
    }

    /**
     * is type Equal
     */
    public static boolean mimeToEqual(String p1, String p2) {
        return isPictureType(p1) == isPictureType(p2);
    }

    public static String createImageType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last, fileName.length());
                return "image/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    public static String createVideoType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last, fileName.length());
                return "video/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "video/mp4";
        }
        return "video/mp4";
    }

    /**
     * Picture or video
     */
    public static int pictureToVideo(String pictureType) {
        if (!TextUtils.isEmpty(pictureType)) {
            if (pictureType.startsWith("video")) {
                return PictureConfig.TYPE_MEDIA_VIDEO;
            } else if (pictureType.startsWith("audio")) {
                return PictureConfig.TYPE_MEDIA_AUDIO;
            }
        }
        return PictureConfig.TYPE_MEDIA_IMAGE;
    }

    /**
     * get Local video duration
     */
    public static int getLocalVideoDuration(String videoPath) {
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }

    /**
     * 是否是长图
     *
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(LocalMedia media) {
        if (null != media) {
            int width = media.getWidth();
            int height = media.getHeight();
            int h = width * 3;
            return height > h;
        }
        return false;
    }

    //    /**
    //     * 作者：CnPeng
    //     * 时间：2018/5/28 下午9:15
    //     * 功用：判断是否是长图。
    //     * 说明：长图分为纵向长图和横向长图
    //     */
    //    public static boolean isLongImg(int imgWidth, int imgHeight) {
    //
    //        int screenWidth = CommonUtils.getScreenWidth(AntLinkApplication.getAppContext());
    //        int screenHeight = CommonUtils.getScreenHeight(AntLinkApplication.getAppContext());
    //
    //        if (imgHeight > imgWidth) {
    //            //高度大于3倍的宽度，并且高度超过了屏幕高度才算是长图--纵向长图
    //            int thirdWidth = imgWidth * 3;
    //            return imgHeight > thirdWidth && imgHeight > screenHeight;
    //        } else {
    //            //宽度大于3倍高度，并且宽度超过了屏幕宽度才算是长图——横向长图
    //            int thirdHeight = imgHeight * 3;
    //            return imgWidth > thirdHeight && imgWidth > screenWidth;
    //        }
    //        //        return false;
    //    }

    //    /**
    //     * 作者：CnPeng
    //     * 时间：2018/5/28 下午9:15
    //     * 功用：判断是否是长图。
    //     * 说明：简单粗暴，只要 高度>=2*屏幕高度，或 宽度>=2*屏幕宽度， 则认为这是一个大图/长图。
    //     */
    //    public static boolean isLongImg2(int imgWidth, int imgHeight) {
    //        boolean isLong = false;
    //
    //        int screenWidth = CommonUtils.getScreenWidth(AntLinkApplication.getAppContext());
    //        int screenHeight = CommonUtils.getScreenHeight(AntLinkApplication.getAppContext());
    //
    //        if (imgHeight >= 2 * screenHeight || imgWidth >= 2 * screenWidth) {
    //            //宽或高超出了屏幕的2倍
    //            isLong = true;
    //        }
    //
    //        return isLong;
    //    }

    /**
     * 作者：CnPeng
     * 时间：2018/5/28 下午9:15
     * 功用：判断是否是一个超宽图。
     * 说明：长图分为纵向长图和横向长图
     */
    public static boolean isLongWidthImg(int imgWidth, int imgHeight, Context context) {

        int screenWidth = getScreenWidth(context);
        int screenHeight = getScreenHeight(context);

        if (imgWidth > imgHeight) {
            //宽度大于3倍高度，并且宽度超过了屏幕宽度才算是长图——横向长图
            int thirdHeight = imgHeight * 3;
            return imgWidth > thirdHeight && imgWidth > screenWidth;
        }
        return false;
    }

    /**
     * 作者：CnPeng
     * 时间：2018/5/28 下午9:15
     * 功用：判断是否是一个超高图。
     * 说明：长图分为纵向长图和横向长图
     */
    public static boolean isLongHeightImg(int imgWidth, int imgHeight, Context context) {

        int screenWidth = getScreenWidth(context);
        int screenHeight = getScreenHeight(context);

        if (imgHeight > imgWidth) {
            //高度大于3倍的宽度，并且高度超过了屏幕高度才算是长图--纵向长图
            int thirdWidth = imgWidth * 3;
            return imgHeight > thirdWidth && imgHeight > screenHeight;
        }
        return false;
    }

    /**
     * 获取图片后缀
     */
    public static String getLastImgType(String path) {
        try {
            int index = path.lastIndexOf(".");
            if (index > 0) {
                String imageType = path.substring(index, path.length());
                switch (imageType) {
                    case ".png":
                    case ".PNG":
                    case ".jpg":
                    case ".jpeg":
                    case ".JPEG":
                    case ".WEBP":
                    case ".bmp":
                    case ".BMP":
                    case ".webp":
                        return imageType;
                    default:
                        return ".png";
                }
            } else {
                return ".png";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".png";
        }
    }

    /**
     * 根据不同的类型，返回不同的错误提示
     */
    public static String s(Context context, int mediaMimeType) {
        Context ctx = context.getApplicationContext();
        switch (mediaMimeType) {
            case PictureConfig.TYPE_MEDIA_IMAGE:
                return ctx.getString(R.string.picture_error);
            case PictureConfig.TYPE_MEDIA_VIDEO:
                return ctx.getString(R.string.picture_video_error);
            case PictureConfig.TYPE_MEDIA_AUDIO:
                return ctx.getString(R.string.picture_audio_error);
            default:
                return ctx.getString(R.string.picture_error);
        }
    }

    /**
     * 作者：CnPeng
     * 时间：2018/6/7 上午9:57
     * 功用：使用长图控件加载图片时，获取图片的初始缩放比率
     * 说明：
     */
    public static float getScaleRate(int bmpWidth, int bmpHeight, Context context) {
        float scale = 0;

        int screenWidth = getScreenWidth(context);
        int screenHeight = getScreenHeight(context);

        if (isLongWidthImg(bmpWidth, bmpHeight, context)) {
            //如果是超宽图——让图片高度占据屏幕的50%
            scale = 0.5f * screenHeight / bmpHeight;
        } else if (isLongHeightImg(bmpWidth, bmpHeight, context)) {
            //如果是超高图——让图片宽度满屏
            scale = 1.0f * screenWidth / bmpWidth;
        } else {
            // float minScale= Math.min((getWidth() - hPadding) / (float) sWidth(), (getHeight() - vPadding) / (float) sHeight());
            // scale = Math.max(minScale, scale);
            // scale = Math.min(maxScale, scale);
            scale = Math.min(screenWidth * 1.0f / bmpWidth, screenHeight * 1.0f / bmpHeight);
        }

        return scale;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        if (null != wm) {
            wm.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.widthPixels;

        //方式2
        // WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (null != wm) {
            wm.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.heightPixels;
    }

    /**
     * 作者：CnPeng
     * 时间：2018/6/7 上午9:57
     * 功用：使用长图控件加载图片时，获取图片的最大缩放比率
     * 说明：宽>=高，高>宽
     */
    public static float getMaxScaleRate(int bmpWidth, int bmpHeight, Context context) {
        //        float scale = 0f;
        //        int screenWidth = CommonUtils.getScreenWidth(AntLinkApplication.getAppContext());
        //        int screenHeight = CommonUtils.getScreenHeight(AntLinkApplication.getAppContext());
        //
        //        if (bmpWidth > bmpHeight) {
        //            //宽度大于高度，最大缩放为屏幕高度 1.1f
        //            scale = 1.3f * screenHeight / bmpHeight;
        //        } else if (bmpHeight >= bmpWidth) {
        //            //高度大于等于宽度，最大缩放为最大宽度1.1f
        //            scale = 1.3f * screenWidth / bmpWidth;
        //        }
        //        return scale;
        return getScaleRate(bmpWidth, bmpHeight, context) * 3;
    }

    /**
     * 作者：CnPeng
     * 时间：2018/6/7 上午9:57
     * 功用：使用长图控件加载图片时，获取双击时的步进缩放比率
     */
    public static float getDoubleTapZoomScale(int bmpWidth, int bmpHeight, Context context) {
        float maxScale = getMaxScaleRate(bmpWidth, bmpHeight, context);
        float initScale = getScaleRate(bmpWidth, bmpHeight, context);
        return (maxScale - initScale) * 0.75f;
    }


}
