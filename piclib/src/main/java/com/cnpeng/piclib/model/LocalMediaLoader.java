package com.cnpeng.piclib.model;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.cnpeng.piclib.R;
import com.cnpeng.piclib.config.PictureConfig;
import com.cnpeng.piclib.config.PictureMimeType;
import com.cnpeng.piclib.config.PictureSelectionConfig;
import com.cnpeng.piclib.entity.LocalMedia;
import com.cnpeng.piclib.entity.LocalMediaFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * author：luck
 * project：LocalMediaLoader
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */

public class LocalMediaLoader {
    private static final Uri    QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY  = MediaStore.Files.FileColumns._ID + " DESC";

    /**
     * 之所以将时长单独定义，是因为 VideoColumns 和 AudioColums 中各有一个
     */
    private static final String DURATION = "duration";
    private static final String NOT_GIF  = "!='image/gif'";

    /**
     * // 过滤掉小于500毫秒的录音
     */
    private static final int AUDIO_DURATION = 500;

    /**
     * 媒体文件数据库字段
     * 如果需要新增字段，注意 setMediaToList 中索引的调用
     */
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.SIZE,
            DURATION};

    // 获取图片or视频
    private static final String[]         SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };
    private static final String           TAG                = "LocalMediaLoader";
    private              int              mType              = PictureConfig.TYPE_MEDIA_IMAGE;
    private              FragmentActivity mActivity;
    private              boolean          mIsGif;
    private              long             mVideoMaxSecond    = 0;
    private              long             mVideoMinSecond    = 0;
    private              int              mMinPicHeight;
    private              int              mMinPicWidth;

    public LocalMediaLoader(FragmentActivity activity, PictureSelectionConfig config) {
        mActivity = activity;
        mType = config.mimeType;
        mIsGif = config.isGif;
        mVideoMaxSecond = config.videoMaxSecond;
        mVideoMinSecond = config.videoMinSecond;
        mMinPicHeight = config.minPicHeight;
        mMinPicWidth = config.minPicWidth;
    }

    // 查询条件(音视频)
    private static String getSelectionArgsForSingleMediaCondition(String time_condition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + time_condition;
    }

    // 全部模式下条件
    private static String getSelectionArgsForAllMediaCondition(String time_condition, boolean isGif) {
        String condition = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                + " OR "
                + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + time_condition) + ")"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        return condition;
    }

    /**
     * CnPeng:2019-10-08 15:35 图片的过滤条件
     */
    private String getPicSelectCondition(boolean isGif) {

        String preCondition = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        if (!isGif) {
            preCondition += " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF;
        }

        preCondition += " AND " + MediaStore.MediaColumns.WIDTH + ">" + mMinPicWidth
                + " AND " + MediaStore.MediaColumns.HEIGHT + ">" + mMinPicHeight;
        return preCondition;
    }

    /**
     * 获取指定类型的文件
     */
    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    /**
     * 功用：加载本地多媒体文件
     * 说明：
     */
    public void loadAllMedia(final LocalMediaLoadListener imageLoadListener) {
        mActivity.getSupportLoaderManager().initLoader(mType, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        CursorLoader cursorLoader = null;
                        switch (id) {
                            case PictureConfig.TYPE_MEDIA_ALL:
                                String all_condition = getSelectionArgsForAllMediaCondition(getDurationCondition(0, 0), mIsGif);
                                cursorLoader = new CursorLoader(
                                        mActivity, QUERY_URI,
                                        PROJECTION, all_condition,
                                        SELECTION_ALL_ARGS, ORDER_BY);
                                break;
                            case PictureConfig.TYPE_MEDIA_IMAGE:
                                // 只获取图片
                                String[] MEDIA_TYPE_IMAGE = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                                String picSelectCondition = getPicSelectCondition(mIsGif);
                                cursorLoader = new CursorLoader(
                                        mActivity, QUERY_URI,
                                        PROJECTION, picSelectCondition, MEDIA_TYPE_IMAGE
                                        , ORDER_BY);
                                break;
                            case PictureConfig.TYPE_MEDIA_VIDEO:
                                // 只获取视频
                                String video_condition = getSelectionArgsForSingleMediaCondition(getDurationCondition(0, 0));
                                String[] MEDIA_TYPE_VIDEO = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                                cursorLoader = new CursorLoader(
                                        mActivity, QUERY_URI, PROJECTION, video_condition, MEDIA_TYPE_VIDEO
                                        , ORDER_BY);
                                break;
                            case PictureConfig.TYPE_MEDIA_AUDIO:
                                String audio_condition = getSelectionArgsForSingleMediaCondition(getDurationCondition(0, AUDIO_DURATION));
                                String[] MEDIA_TYPE_AUDIO = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
                                cursorLoader = new CursorLoader(
                                        mActivity, QUERY_URI, PROJECTION, audio_condition, MEDIA_TYPE_AUDIO
                                        , ORDER_BY);
                                break;
                            default:
                                break;
                        }
                        return cursorLoader;
                    }

                    @Override
                    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                        try {
                            List<LocalMediaFolder> imageFolders = new ArrayList<>();
                            LocalMediaFolder allImageFolder = new LocalMediaFolder();
                            List<LocalMedia> latelyImages = new ArrayList<>();
                            if (data != null) {
                                // ATTENTION: 这里包含全部的视频类/图片类文件
                                int count = data.getCount();
                                if (count > 0) {
                                    data.moveToFirst();
                                    do {
                                        if (PictureConfig.TYPE_MEDIA_VIDEO == mType) {
                                            String pictureType = data.getString(data.getColumnIndexOrThrow(PROJECTION[2]));

                                            if ("video/mp4".equals(pictureType)) {
                                                setMediaToList(data, imageFolders, allImageFolder, latelyImages);
                                            }
                                        } else {
                                            setMediaToList(data, imageFolders, allImageFolder, latelyImages);
                                        }
                                    } while (data.moveToNext());

                                    if (latelyImages.size() > 0) {
                                        sortFolder(imageFolders);
                                        imageFolders.add(0, allImageFolder);
                                        allImageFolder.setFirstImagePath(latelyImages.get(0).getOriginalPath());
                                        String title = mType == PictureMimeType.ofAudio() ? mActivity.getString(R.string.picture_all_audio) : mActivity.getString(R.string.picture_camera_roll);
                                        allImageFolder.setName(title);
                                        allImageFolder.setImages(latelyImages);
                                    }
                                    imageLoadListener.loadComplete(imageFolders);
                                } else {
                                    // 如果没有相册
                                    imageLoadListener.loadComplete(imageFolders);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                    }
                });
    }

    /**
     * 作者：CnPeng
     * 时间：2018/5/23 下午9:27
     * 功用：将获取到的多媒体数据存储到不同的集合中
     *
     * CnPeng 2019-07-01 14:24 将文件是否存在判断提前，文件不存在也不用再获取其他媒体信息了
     */
    private void setMediaToList(Cursor
                                        data, List<LocalMediaFolder> imageFolders, LocalMediaFolder
                                        allImageFolder, List<LocalMedia> latelyImages) {
        String path = data.getString(data.getColumnIndexOrThrow(PROJECTION[1]));

        if (isFileExist(path)) {
            // CnPeng 2018/8/7 下午6:18 如果用户手动修改gif的后缀为.jpg, 原生的data.getColumnIndexOrThrow 获取的是 image/jpeg。而options中获取的则是准确的
            String pictureType = "";
            try {
                pictureType = data.getString(data.getColumnIndexOrThrow(PROJECTION[2]));

                if (PictureConfig.TYPE_MEDIA_IMAGE == PictureMimeType.isPictureType(pictureType)) {
                    //CnPeng 2018/8/15 下午5:47 如果是图片类型的才去获取真实的格式；如果是视频，不走这里，否则会返回null
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    pictureType = options.outMimeType;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            int w = data.getInt(data.getColumnIndexOrThrow(PROJECTION[3]));
            int h = data.getInt(data.getColumnIndexOrThrow(PROJECTION[4]));
            long fileLength = data.getLong(data.getColumnIndexOrThrow(PROJECTION[5]));
            int duration = data.getInt(data.getColumnIndexOrThrow(PROJECTION[6]));

            LocalMedia image = new LocalMedia(path, duration, mType, pictureType, w, h, fileLength);

            LocalMediaFolder folder = getImageFolder(path, imageFolders);
            List<LocalMedia> images = folder.getImages();
            images.add(image);

            folder.setImageNum(folder.getImageNum() + 1);
            latelyImages.add(image);

            int imageNum = allImageFolder.getImageNum();
            allImageFolder.setImageNum(imageNum + 1);
        }
    }

    /**
     * CnPeng:2019-06-24 15:38 判断文件是否存在，不存在不要加入列表了——避免出现文件不存在但依旧在列表显示的情况
     */
    private boolean isFileExist(String path) {
        if (!TextUtils.isEmpty(path)) {
            return new File(path).exists();
        }
        return false;
    }

    /**
     * 文件夹数量进行排序
     */
    private void sortFolder(List<LocalMediaFolder> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<LocalMediaFolder>() {
            @Override
            public int compare(LocalMediaFolder lhs, LocalMediaFolder rhs) {
                if (lhs.getImages() == null || rhs.getImages() == null) {
                    return 0;
                }
                int lsize = lhs.getImageNum();
                int rsize = rhs.getImageNum();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }

    /**
     * 创建相应文件夹
     */
    private LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();
        for (LocalMediaFolder folder : imageFolders) {
            // 同一个文件夹下，返回自己，否则创建新文件夹
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * 获取视频(最长或最小时间)
     */
    private String getDurationCondition(long exMaxLimit, long exMinLimit) {
        long maxS = mVideoMaxSecond == 0 ? Long.MAX_VALUE : mVideoMaxSecond;
        if (exMaxLimit != 0) {
            maxS = Math.min(maxS, exMaxLimit);
        }

        return String.format(Locale.CHINA, "%d <%s duration and duration <= %d",
                Math.max(exMinLimit, mVideoMinSecond),
                Math.max(exMinLimit, mVideoMinSecond) == 0 ? "" : "=",
                maxS);
    }


    public interface LocalMediaLoadListener {
        void loadComplete(List<LocalMediaFolder> folders);
    }
}
