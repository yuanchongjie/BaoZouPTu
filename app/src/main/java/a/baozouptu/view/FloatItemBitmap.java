package a.baozouptu.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2016/6/1.
 */
public class FloatItemBitmap {
    Paint paint;

    public FloatItemBitmap() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public Bitmap getEditBitmap(Context context, int width, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(color);
        canvas.drawOval(new RectF(0, 0, (float) width, (float) width), paint);
        return bitmap;
    }

    /**
     * 获取返回底部中间位置按钮的bitmap对象
     *
     * @param context
     * @param width
     * @param color
     * @return
     */
    public Bitmap getToBottomCenterBitmap(Context context, int width, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(3.5f);
        canvas.drawLine(0, width / 4, width, width / 4, paint);
        canvas.drawLine(0, width / 2-3, width, width / 2-3, paint);
        canvas.drawLine(0, width / 2-3, width / 2+1.5f, width-15, paint);
        canvas.drawLine(width, width / 2-3, width / 2-1.5f, width-15, paint);
        return bitmap;
    }

}
