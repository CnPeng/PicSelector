package com.cnpeng.piclib.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * author：luck
 * project：PictureSelector
 * describe：for PictureSelector media entity.
 * email：893855882@qq.com
 * data：2017/5/24
 */

public class LocalMedia implements Parcelable {
    public static final Creator<LocalMedia> CREATOR = new Creator<LocalMedia>() {
        @Override
        public LocalMedia createFromParcel(Parcel source) {
            return new LocalMedia(source);
        }

        @Override
        public LocalMedia[] newArray(int size) {
            return new LocalMedia[size];
        }
    };
    public  int     position;
    private String  path;
    private String  compressPath;
    private String  cutPath;
    private long    duration;
    private boolean isChecked;
    private boolean isCut;
    private int     num;
    /**
     * 这并不是我们常说的 mimeType,而是多媒体选择界面中的内容类型，
     * 具体取值为 #{@link com.cnpeng.piclib.config.PictureConfig} 中的 TYPE_ALL、TYPE_IMAGE、TYPE_VIDEO、TYPE_AUDIO
     */
    private int     mimeType;
    /**
     * 这一个才是我们常说的 字符串类型的 Mimeype
     */
    private String  pictureType;
    private boolean compressed;
    private int     width;
    private int     height;
    /**
     * 文件大小，单位byte.
     */
    private long    mFileLength;
    /**
     * 原图地址
     */
    private String  mOriginalPath;

    public LocalMedia() {

    }

    public LocalMedia(String path, long duration, int mimeType, String pictureType) {
        mOriginalPath = path;
        this.duration = duration;
        this.mimeType = mimeType;
        this.pictureType = pictureType;
    }

    public LocalMedia(String path, long duration, int mimeType, String pictureType, int width, int height, long fileLength) {
        mOriginalPath = path;
        this.duration = duration;
        this.mimeType = mimeType;
        this.pictureType = pictureType;
        this.width = width;
        this.height = height;
        mFileLength = fileLength;
    }

    public LocalMedia(String path, long duration, boolean isChecked, int position, int num, int mimeType) {
        mOriginalPath = path;
        this.duration = duration;
        this.isChecked = isChecked;
        this.position = position;
        this.num = num;
        this.mimeType = mimeType;
    }

    protected LocalMedia(Parcel in) {
        this.path = in.readString();
        this.compressPath = in.readString();
        this.cutPath = in.readString();
        this.duration = in.readLong();
        this.isChecked = in.readByte() != 0;
        this.isCut = in.readByte() != 0;
        this.position = in.readInt();
        this.num = in.readInt();
        this.mimeType = in.readInt();
        this.pictureType = in.readString();
        this.compressed = in.readByte() != 0;
        this.width = in.readInt();
        this.height = in.readInt();
        mFileLength = in.readLong();
        mOriginalPath = in.readString();
    }

    public long getFileLength() {
        return mFileLength;
    }

    public void setFileLength(long fileLength) {
        mFileLength = fileLength;
    }

    /**
     * 这才是我们常说的 MimeType。
     */
    public String getPictureType() {
        if (TextUtils.isEmpty(pictureType)) {
            pictureType = "image/jpeg";
        }
        return pictureType;
    }

    public void setPictureType(String pictureType) {
        this.pictureType = pictureType;
    }


    /**
     * 作者：CnPeng
     * 时间：2018/5/31 上午11:32
     * 功用：获取图片地址
     * 说明：1、如果压缩了，返回压缩路径。（裁剪并压缩也是如此，因为是先裁剪再压缩）
     * 2、如果只是裁剪，返回裁剪路径
     * 3、如果未压缩，返回原图路径
     */
    public String getPath() {
        if (compressed) {
            return compressPath;
        } else if (isCut) {
            return cutPath;
        } else {
            return mOriginalPath;
        }
    }


    /**
     * 作者：CnPeng
     * 时间：2018/5/31 上午11:40
     * 功用：获取文件的原始路径
     * 说明：
     */
    public String getOriginalPath() {
        return mOriginalPath;
    }

    /**
     * 作者：CnPeng
     * 时间：2018/5/31 上午11:40
     * 功用：设置文件的原始路径
     * 说明：
     */
    public void setOriginalPath(String originalPath) {
        mOriginalPath = originalPath;
    }

    /**
     * // LocalMedia 里面返回三种path
     * // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
     * // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
     * // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
     */
    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    /**
     * // LocalMedia 里面返回三种path
     * // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
     * // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
     * // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
     */
    public String getCutPath() {
        return cutPath;
    }

    public void setCutPath(String cutPath) {
        this.cutPath = cutPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean cut) {
        isCut = cut;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


    /**
     * //180329 mimeType对应 PictureMimeType.ofVideo()及其他，PictureType 代表扩展名
     */
    public int getMimeType() {
        return mimeType;
    }

    public void setMimeType(int mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.compressPath);
        dest.writeString(this.cutPath);
        dest.writeLong(this.duration);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCut ? (byte) 1 : (byte) 0);
        dest.writeInt(this.position);
        dest.writeInt(this.num);
        dest.writeInt(this.mimeType);
        dest.writeString(this.pictureType);
        dest.writeByte(this.compressed ? (byte) 1 : (byte) 0);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(mFileLength);
        dest.writeString(mOriginalPath);
    }
}
