package a.baozouptu.ptu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 它的宽高用xml设置
 * Created by Administrator on 2016/5/24.
 */
public class ColorLump extends View {
    private int mcolor;
    Paint mPaint=new Paint();

    public ColorLump(Context context) {
        super(context);
    }

    public ColorLump(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * 设置选中的颜色，同时重回绘图像
     * @param color
     */
    public void setColor(int color) {
        mcolor = color;
        invalidate();
    }
    public int getColor() {
        return mcolor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mcolor);
        canvas.drawRect(0,0,getWidth()-getPaddingRight(),getHeight(),mPaint);
        super.onDraw(canvas);
    }
}
