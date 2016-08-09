package a.baozouptu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.view.IconBitmapCreator;

/**
 * Created by liuguicen on 2016/8/3.
 *
 * @description
 */
public class Test extends View {
    Context mContext;
    private Paint textPaint;

    public Test(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        textPaint=new Paint();
        textPaint.setTextSize(20);
    }

    public Test(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float mWidth=canvas.getWidth();
        float mHeight=canvas.getHeight();

        //中间的数字
        String sWidth = String.valueOf(mWidth), sHeight = String.valueOf(mHeight);
        String show = sWidth + " × " + sHeight;
        float textWidth=textPaint.measureText(show);
        Paint.FontMetrics fm=textPaint.getFontMetrics();
        float textY = mHeight / 2 - fm.descent + (fm.bottom - fm.top) / 2;
        canvas.drawText(show,(mWidth-textWidth)/2,textY,textPaint);
        textPaint.setStrokeWidth(5);
        canvas.drawLine(0,mHeight/2,mWidth,mHeight/2,textPaint);
    }
}
