## 前言

该库是基于 [LuckSiege/PictureSelector 2.2.3 版本](https://github.com/LuckSiege/PictureSelector/releases) 修改的图片选择框架，其中的内容根据我厂的实际需求做了定制化，目前已经应用到生产环境。

在定制时也引入或者拷贝了其他开源项目中的内容，部分来源已经不可考，所以不再一一列举。 


## 待处理的内容

* 使用 implemention 的方式引用 RxPermissions
* 使用 implemention 的方式引用 LuBan 压缩
* [690 issue ](https://github.com/LuckSiege/PictureSelector/issues/690)
* 调用自定义的拍照
* 添加获取视频第一帧图片的通用方法
* 获取图片真实 MimeType 的类型——目前可能会存在图片原始类型为.gif , 但用户手动修改为 .jpg 后被识别为 .jpg 的情况。
    - PictureMimeType.createImageType() 中只是根据后缀名取的 类型，不准确
* 获取图片/视频的后缀名
* 获取图片/视频的URI



## 更新记录


### 2019-06-27

* 迁移到 androidX 
* 引用 UCrop 的方式不再使用 module, 而是用 implemention
* 修改了支持的图片和视频格式
* 新增对大文件的过滤——maxSize()
* 替换录制视频的组件（降低画质和体积）
* 增加图片 originalPath 属性, 用该属性表示原始路径；而 path 属性表示最终路径
* 增加对不存在文件/已损坏文件的过滤
* 优化超过1小时视频的时长显示
* 升级 Glide 为 4.9.0
* 升级 gson 为 2.8.5 
* 替换大图预览框架，更好的兼容清明上河图等超大图,并支持网络图片( ImagePagerAdapter /ImagePagerActivity2)

### 新增API的使用示例

#### (1)、大文件过滤的示例代码：

```java
 PictureSelector.create(mActivity)
            .openGallery(PictureMimeType.ofImage())
            .maxSelectNum(MAX_PIC_COUNT)
            // CnPeng 大文件过滤，传递的是字节数，20*1024*1024 表示单文件最大20M
            .maxSize(20 * 1024 * 1024)
            .isGif(false)
            .imageSpanCount(3)
            .previewImage(true)
            .isCamera(true)
            .selectionMedia(bean.selectedPicList)
            .forResult(CommKey.REQUEST_SELECT_PIC)            
```

#### (2)、大图预览的使用示例

* 网络图片的跳转方式

```java
 // 2018/6/4 下午4:42  跳转到大图查看界面
 Intent intent = new Intent(context, ImagePagerActivity2.class);
 ImagePreviewHolder2 previewHolder = ImagePreviewHolder2.getInstance();
 // 标记是否是网络图片
 previewHolder.setFromNet(true);
 // mPosition 表示用户当前点击的图片索引。
 previewHolder.setCurSelectedIndex(mPosition);
 // netImageList 为 ImagePreviewBean 的集合, bean 中的 url 为完整网络路径
 previewHolder.setImgList(netImageList);

 startActivity(intent);
```

* 本地图片的跳转方式

```java
 Intent intent = new Intent(this, ImagePagerActivity2.class);
 ImagePreviewHolder2 previewHolder = ImagePreviewHolder2.getInstance();
 previewHolder.setFromNet(false);
 previewHolder.setImgList2(mSelectedMediaList);
 previewHolder.setCurSelectedIndex(position);
 startActivity(intent);
```

## 引用方式

Add it in your root build.gradle at the end of repositories:

```xml
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
	
Step 2. Add the dependency

```xml
	dependencies {
	        implementation 'com.github.CnPeng:PicSelector:V1.0.4'
	}
```

由于使用了 java 1.8 ,所以，需要在 module 的 build.gradle 的 android 节点下声明 java 版本

```xml
android{
    //CnPeng 2019-06-25 19:20 图片选择库需要使用
    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
}
```

---

## 以下为 PictureSelector 原项目的API 

### 目录

-[功能特点](#功能特点)<br>
-[集成方式](#集成方式)<br>
-[常见错误](#常见错误)<br>
-[功能配置](#功能配置)<br>
-[缓存清除](#缓存清除)<br>
-[主题配置](#主题配置)<br>
-[常用功能](#常用功能)<br>
-[结果回调](#结果回调)<br>
-[更新日志](#更新日志)<br>
-[混淆配置](#混淆配置)<br>
-[兼容性测试](#兼容性测试)<br>
-[演示效果](#演示效果)<br>
-[打赏](#打赏)<br>

## 功能特点

* 1.适配android6.0+系统
* 2.解决部分机型裁剪闪退问题
* 3.解决图片过大oom闪退问题
* 4.动态获取系统权限，避免闪退
* 5.支持相片or视频的单选和多选
* 6.支持裁剪比例设置，如常用的 1:1、3：4、3:2、16:9 默认为图片大小
* 7.支持视频预览
* 8.支持gif图片
* 9.支持.webp格式图片 
* 10.支持一些常用场景设置：如:是否裁剪、是否预览图片、是否显示相机等
* 11.新增自定义主题设置
* 12.新增图片勾选样式设置
* 13.新增图片裁剪宽高设置
* 14.新增图片压缩处理
* 15.新增录视频最大时间设置
* 16.新增视频清晰度设置
* 17.新增QQ选择风格，带数字效果 
* 18.新增自定义 文字颜色 背景色让风格和项目更搭配
* 19.新增多图裁剪功能
* 20.新增LuBan多图压缩
* 21.新增单独拍照功能
* 22.新增压缩大小设置
* 23.新增Luban压缩档次设置
* 24.新增圆形头像裁剪
* 25.新增音频功能查询


重要的事情说三遍记得添加权限

```xml
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.CAMERA" /> 
```


### 常见错误

**1、重要：**
 
调用 `PictureSelector.create()；` 时，在 activity 中就传 activity.this，在 fragment 中就传 fragment.this , 影响回调到哪个地方的 `onActivityResult()`。
 
**2、问题一：rxjava冲突**
 
 在app build.gradle下添加
 
 ```xml
 packagingOptions {
   		exclude 'META-INF/rxjava.properties'
 }  
 ```
 
**3、问题二：**
 
```xml 
 java.lang.NullPointerException: 
Attempt to invoke virtual method 'android.content.res.XmlResourceParser 
android.content.pm.ProviderInfo.loadXmlMetaData(android.content.pm.PackageManager, java.lang.String)'
on a null object reference
```
 
 * 注意 从v2.1.3版本中，将不需要配制以下内容
 
 application下添加如下节点:

```xml 
 <provider
      android:name="android.support.v4.content.FileProvider"
      android:authorities="${applicationId}.provider"
      android:exported="false"
      android:grantUriPermissions="true">
       <meta-data
         android:name="android.support.FILE_PROVIDER_PATHS"
         android:resource="@xml/file_paths" />
</provider>
```

注意：如已添加其他sdk或项目中已使用过provider节点，
[请参考博客](http://blog.csdn.net/luck_mw/article/details/54970105)的解决方案

**4、问题三：**

经测试在小米部分低端机中，Fragment 调用 PictureSelector 2.0 拍照有时内存不足会暂时回收 activity ,导致其 fragment 会重新创建 建议在 fragment 所依赖的 activity 加上如下代码:

```java
if (savedInstanceState == null) {
      // 添加显示第一个fragment
      	fragment = new PhotoFragment();
      		getSupportFragmentManager().beginTransaction().add(R.id.tab_content, fragment,
                    PictureConfig.FC_TAG).show(fragment)
                    .commit();
     } else { 
      	fragment = (PhotoFragment) getSupportFragmentManager()
          .findFragmentByTag(PictureConfig.FC_TAG);
}
```

这里就是如果是被回收时，则不重新创建 通过 tag 取出 fragment 的实例。

**5、问题四：glide冲突**

由于 PictureSelector 2.0 引入的是 glide 4.5.0 ,所以将项目中老版本的 glide 删除,并且将报错代码换成如下写法：

```java
RequestOptions options = new RequestOptions();
options.placeholder(R.drawable.image);
Glide.with(context).load(url).apply(options).into(imageView);
```

### 功能配置

```java
// 进入相册 以下是例子：用不到的api可以不写
 PictureSelector.create(MainActivity.this)
 	.openGallery()//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
 	.theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
 	.maxSelectNum()// 最大图片选择数量 int
 	.minSelectNum()// 最小选择数量 int
	.imageSpanCount(4)// 每行显示个数 int
 	.selectionMode()// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
 	.previewImage()// 是否可预览图片 true or false
 	.previewVideo()// 是否可预览视频 true or false
	.enablePreviewAudio() // 是否可播放音频 true or false
 	.isCamera()// 是否显示拍照按钮 true or false
	.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
	.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
	.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
	.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
 	.enableCrop()// 是否裁剪 true or false
 	.compress()// 是否压缩 true or false
 	.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
 	.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
 	.hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
 	.isGif()// 是否显示gif图片 true or false
	.compressSavePath(getPath())//压缩图片保存地址
 	.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
 	.circleDimmedLayer()// 是否圆形裁剪 true or false
 	.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
 	.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
 	.openClickSound()// 是否开启点击声音 true or false
 	.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
 	.previewEggs()// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
 	.cropCompressQuality()// 裁剪压缩质量 默认90 int
 	.minimumCompressSize(100)// 小于100kb的图片不压缩 
 	.synOrAsy(true)//同步true或异步false 压缩 默认同步
 	.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int 
 	.rotateEnabled() // 裁剪是否可旋转图片 true or false
 	.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
 	.videoQuality()// 视频录制质量 0 or 1 int
	.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int 
    .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int 
	.recordVideoSecond()//视频秒数录制 默认60s int
	.isDragFrame(false)// 是否可拖动裁剪框(固定)
 	.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code,可自定义     
```

### 缓存清除

```java
 //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限 
 PictureFileUtils.deleteCacheDirFile(MainActivity.this);
```

### 主题配置

```xml
<!--默认样式 注意* 样式只可修改，不能删除任何一项 否则报错-->
    <style name="picture.default.style" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <!--标题栏背景色-->
        <item name="colorPrimary">@color/bar_grey</item>
        <!--状态栏背景色-->
        <item name="colorPrimaryDark">@color/bar_grey</item>
        <!--是否改变图片列表界面状态栏字体颜色为黑色-->
        <item name="picture.statusFontColor">false</item>
        <!--返回键图标-->
        <item name="picture.leftBack.icon">@drawable/picture_back</item>
        <!--标题下拉箭头-->
        <item name="picture.arrow_down.icon">@drawable/arrow_down</item>
        <!--标题上拉箭头-->
        <item name="picture.arrow_up.icon">@drawable/arrow_up</item>
        <!--标题文字颜色-->
        <item name="picture.title.textColor">@color/white</item>
        <!--标题栏右边文字-->
        <item name="picture.right.textColor">@color/white</item>
        <!--图片列表勾选样式-->
        <item name="picture.checked.style">@drawable/checkbox_selector</item>
        <!--开启图片列表勾选数字模式-->
        <item name="picture.style.checkNumMode">false</item>
        <!--选择图片样式0/9-->
        <item name="picture.style.numComplete">false</item>
        <!--图片列表底部背景色-->
        <item name="picture.bottom.bg">@color/color_fa</item>
        <!--图片列表预览文字颜色-->
        <item name="picture.preview.textColor">@color/tab_color_true</item>
        <!--图片列表已完成文字颜色-->
        <item name="picture.complete.textColor">@color/tab_color_true</item>
        <!--图片已选数量圆点背景色-->
        <item name="picture.num.style">@drawable/num_oval</item>
        <!--预览界面标题文字颜色-->
        <item name="picture.ac_preview.title.textColor">@color/white</item>
        <!--预览界面已完成文字颜色-->
        <item name="picture.ac_preview.complete.textColor">@color/tab_color_true</item>
        <!--预览界面标题栏背景色-->
        <item name="picture.ac_preview.title.bg">@color/bar_grey</item>
        <!--预览界面底部背景色-->
        <item name="picture.ac_preview.bottom.bg">@color/bar_grey_90</item>
        <!--预览界面返回箭头-->
        <item name="picture.preview.leftBack.icon">@drawable/picture_back</item>
        <!--是否改变预览界面状态栏字体颜色为黑色-->
        <item name="picture.preview.statusFontColor">false</item>
        <!--裁剪页面标题背景色-->
        <item name="picture.crop.toolbar.bg">@color/bar_grey</item>
        <!--裁剪页面状态栏颜色-->
        <item name="picture.crop.status.color">@color/bar_grey</item>
        <!--裁剪页面标题文字颜色-->
        <item name="picture.crop.title.color">@color/white</item>
        <!--相册文件夹列表选中图标-->
        <item name="picture.folder_checked_dot">@drawable/orange_oval</item>
    </style>

```

### 常用功能

**启动相册并拍照**  
 
```java
 PictureSelector.create(MainActivity.this)
       .openGallery(PictureMimeType.ofImage())
       .forResult(PictureConfig.CHOOSE_REQUEST);
```

**单独启动拍照或视频 根据PictureMimeType自动识别**  

```java
  PictureSelector.create(MainActivity.this)
       .openCamera(PictureMimeType.ofImage())
       .forResult(PictureConfig.CHOOSE_REQUEST);
```

**预览图片**    

> CnPeng 下面是原仓库中的预览框架，修改后的预览框架暂时没有保存功能，也没有列举使用方法

```java
// 预览图片 可自定长按保存路径
// *注意 .themeStyle(themeId)；不可少，否则闪退...

PictureSelector.create(MainActivity.this)
		.themeStyle(themeId)
		.openExternalPreview(position, "/custom_file", selectList);
		
PictureSelector.create(MainActivity.this)
		.themeStyle(themeId)
		.openExternalPreview(position, selectList);
```

**预览视频**

```java
PictureSelector.create(MainActivity.this)
	.externalPictureVideo(video_path);
```

### 结果回调

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    
```