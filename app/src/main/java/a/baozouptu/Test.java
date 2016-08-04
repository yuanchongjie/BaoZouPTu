package a.baozouptu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public Test(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext=context;
    }

    public Test(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int c1 = Util.getColor(R.color.mat_pen_line1);
        int c_1 = Color.argb(Color.alpha(c1) / 3, Color.red(c1), Color.green(c1), Color.blue(c1));
        int c2 = Util.getColor(R.color.mat_pen_line2);
        int c_2 = Color.argb(Color.alpha(c2) / 3, Color.red(c2), Color.green(c2), Color.blue(c2));
        Object penWidth=50;
        Bitmap penBmLine = IconBitmapCreator.createPen(mContext, (int) penWidth, c1, c2);
        canvas.drawBitmap(penBmLine,100,100,new Paint());
        super.onDraw(canvas);
    }
}
