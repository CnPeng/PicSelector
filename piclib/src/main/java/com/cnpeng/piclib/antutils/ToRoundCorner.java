package com.cnpeng.piclib.antutils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ToRoundCorner {
	/** 
	 * 获取圆角位图的方法 
	 * @param bitmap 需要转化成圆角的位图 
	 * @param pixels 圆角的度数，数值越大，圆角越大 
	 * @return 处理后的圆角位图 
	 */  
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if(bitmap == null){
			return null;
		}
		
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),  
	            bitmap.getHeight(), Config.ARGB_8888);  
	    Canvas canvas = new Canvas(output);  
	    final int color = 0xff424242;  
	    final Paint paint = new Paint();  
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
	    final RectF rectF = new RectF(rect);
	    final float roundPx = pixels;
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);  
	    paint.setColor(color);  
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
	    canvas.drawBitmap(bitmap, rect, rect, paint);  
	    return output;  
	}  
}
