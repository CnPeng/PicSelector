package com.cnpeng.piclib.antwidget;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;

import com.cnpeng.piclib.R;

/*
 *445263848@qq.com.对焦的图标
 */
public class FocusView extends View {
    private int   foucsView_size;
    private int   x;
    private int   y;
    private int   length;
    private Paint mPaint;

    public FocusView(Context context, int size) {
        super(context);
        foucsView_size = size;
        setBackgroundResource(R.drawable.focus);

        //        mPaint = new Paint();
        //        mPaint.setAntiAlias(true);
        //        mPaint.setDither(true);
        //        mPaint.setColor(0xFF00CC00);
        //        mPaint.setStrokeWidth(1);
        //        mPaint.setStyle(Paint.Style.STROKE);
        //        setLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private FocusView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        x = y = (int) (foucsView_size / 2.0);
        length = (int) (foucsView_size / 2.0) - 2;
        setMeasuredDimension(foucsView_size, foucsView_size);
    }
    //    @Override
    //    protected void onDraw(Canvas canvas) {
    //        super.onDraw(canvas);
    //        canvas.drawRect(x - length, y - length, x + length, y + length, mPaint);
    //        canvas.drawLine(2, getHeight() / 2, foucsView_size / 10, getHeight() / 2, mPaint);
    //        canvas.drawLine(getWidth() - 2, getHeight() / 2, getWidth() - foucsView_size / 10, getHeight() / 2, 
    // mPaint);
    //        canvas.drawLine(getWidth() / 2, 2, getWidth() / 2, foucsView_size / 10, mPaint);
    //        canvas.drawLine(getWidth() / 2, getHeight() - 2, getWidth() / 2, getHeight() - foucsView_size / 10, mPaint);
    //    }
}
