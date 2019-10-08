package com.cnpeng.piclib.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnpeng.piclib.R;
import com.cnpeng.piclib.adapter.PictureAlbumDirectoryAdapter;
import com.cnpeng.piclib.decoration.RecycleViewDivider;
import com.cnpeng.piclib.entity.LocalMedia;
import com.cnpeng.piclib.entity.LocalMediaFolder;
import com.cnpeng.piclib.tools.AttrsUtils;
import com.cnpeng.piclib.tools.ScreenUtils;
import com.cnpeng.piclib.tools.StringUtils;

import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：2017/5/25
 */

public class FolderPopWindow extends PopupWindow implements View.OnClickListener {
    private Context                      context;
    private View                         window;
    private RecyclerView                 recyclerView;
    private PictureAlbumDirectoryAdapter adapter;
    private boolean                      isDismiss = false;
    private LinearLayout                 id_ll_root;
    private TextView                     mTvToNotify;
    private int                          mimeType;
    private Drawable                     drawableUp, drawableDown;
    private Animation animationIn, animationOut;

    public FolderPopWindow(Context context, int mimeType, ViewGroup.LayoutParams layoutParams) {
        this.context = context;
        this.mimeType = mimeType;
        window = LayoutInflater.from(context).inflate(R.layout.picture_window_folder, null);
        this.setContentView(window);

        if (null == layoutParams) {
            this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            this.setWidth(layoutParams.width);
            this.setHeight(layoutParams.height);
        }

        this.setAnimationStyle(R.style.WindowStyle);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.argb(123, 0, 0, 0)));
        drawableUp = AttrsUtils.getTypeValuePopWindowImg(context, R.attr.picture_arrow_up_icon);
        drawableDown = AttrsUtils.getTypeValuePopWindowImg(context, R.attr.picture_arrow_down_icon);
        animationIn = AnimationUtils.loadAnimation(context, R.anim.photo_album_show);
        animationOut = AnimationUtils.loadAnimation(context, R.anim.photo_album_dismiss);
        initView();
    }

    public void initView() {
        id_ll_root = (LinearLayout) window.findViewById(R.id.id_ll_root);
        adapter = new PictureAlbumDirectoryAdapter(context);
        recyclerView = (RecyclerView) window.findViewById(R.id.folder_list);
        recyclerView.getLayoutParams().height = (int) (ScreenUtils.getScreenHeight(context) * 0.6);
        recyclerView.addItemDecoration(new RecycleViewDivider(
                context, LinearLayoutManager.HORIZONTAL, ScreenUtils.dip2px(context, 0), ContextCompat.getColor(context, R.color.transparent)));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        id_ll_root.setOnClickListener(this);

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void bindFolder(List<LocalMediaFolder> folders) {
        adapter.setMimeType(mimeType);
        adapter.bindFolderData(folders);
    }

    /**
     * 绑定pop展开收起时动态更改的TextView
     */
    public void setTvToNotify(TextView textView) {
        this.mTvToNotify = textView;
    }

    @Override
    public void showAsDropDown(View anchor) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                Rect rect = new Rect();
                anchor.getGlobalVisibleRect(rect);
                int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
                setHeight(h);
            }
            //CnPeng 2019-07-23 08:56 防止软键盘遮挡内容
            // setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            super.showAsDropDown(anchor);
            isDismiss = false;
            recyclerView.startAnimation(animationIn);
            StringUtils.modifyTextViewDrawable(mTvToNotify, drawableUp, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        //CnPeng 2019-07-23 08:55  //防止软键盘遮挡内容

        super.showAtLocation(parent, gravity, x, y);

        StringUtils.modifyTextViewDrawable(mTvToNotify, drawableUp, 2);
        recyclerView.startAnimation(animationIn);
    }

    public void setOnItemClickListener(PictureAlbumDirectoryAdapter.OnItemClickListener onItemClickListener) {
        adapter.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        StringUtils.modifyTextViewDrawable(mTvToNotify, drawableDown, 2);

        isDismiss = true;
        recyclerView.startAnimation(animationOut);
        dismiss();
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                    dismiss4Pop();
                } else {
                    FolderPopWindow.super.dismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 在android4.1.1和4.1.2版本关闭PopWindow
     */
    private void dismiss4Pop() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FolderPopWindow.super.dismiss();
            }
        });
    }


    /**
     * 设置选中状态
     */
    public void notifyDataCheckedStatus(List<LocalMedia> medias) {
        try {
            // 获取选中图片
            List<LocalMediaFolder> folders = adapter.getFolderData();
            for (LocalMediaFolder folder : folders) {
                folder.setCheckedNum(0);
            }
            if (medias.size() > 0) {
                for (LocalMediaFolder folder : folders) {
                    int num = 0;// 记录当前相册下有多少张是选中的
                    List<LocalMedia> images = folder.getImages();
                    for (LocalMedia media : images) {
                        String path = media.getOriginalPath();
                        for (LocalMedia m : medias) {
                            if (path.equals(m.getOriginalPath())) {
                                num++;
                                folder.setCheckedNum(num);
                            }
                        }
                    }
                }
            }
            adapter.bindFolderData(folders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_ll_root) {
            dismiss();
        }
    }

}
