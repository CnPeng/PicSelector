## 后续将增加的内容

* 使用 implemention 的方式引用 RxPermissions

## 版权说明

该框架是基于 [LuckSiege/PictureSelector 2.2.3 版本](https://github.com/LuckSiege/PictureSelector/releases) 修改的图片选择框架，

修改的内容大致如下：

* 引用 UCrop 的方式不再使用 module, 而是用 implemention
* 修改了支持的图片和视频格式
* 新增对大文件的过滤——maxSize()
* 替换录制视频的组件
* 替换大图预览框架，更好的兼容清明上河图等超大图
* 增加图片最终路径的属性和对应的set/get——path，具体可查阅 LocalMedia 中的 getPath()

大文件过滤的示例代码：

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