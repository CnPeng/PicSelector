package com.cnpeng.piclib.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：CnPeng
 * 时间：2018/5/28
 * 功用：查看图片大图时传递到预览界面的bean
 * 其他：
 */
public class ImgPreviewBean {
    /**
     * 图片的url（网络图片）地址/uri（本地图片）地址
     * 注意：这个注解不要动。因为长文详情页面中点击图片后，H5会传递json数据回来，解析时需要使用该注解
     */
    @SerializedName("src")
    private String url;
    /**
     * 图片的描述信息
     */
    @SerializedName("description")
    private String desc;

    /**
     * 多媒体的mimeType类型
     */
    private String mimeType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
