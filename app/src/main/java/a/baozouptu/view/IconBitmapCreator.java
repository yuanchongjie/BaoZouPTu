package a.baozouptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import a.baozouptu.tools.TempUtil;

/**
 * Created by Administrator on 2016/6/1.
 */
public class IconBitmapCreator {


    /**
     * 获取编辑按钮的bitmap对象
     *
     * @param color     前景
     * @param backColor 背景色默认透明
     * @return
     */
    public static Bitmap getEditBitmap(Context context, int width, int color, int backColor) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(color);
        canvas.drawOval(new RectF(0, 0, (float) width, (float) width), paint);
        return bitmap;
    }

    /**
     * 获取编辑按钮的bitmap对象，背景色默认透明
     *
     * @param color 前景
     * @return
     */
    public static Bitmap getEditBitmap(Context context, int width, int color) {
        return getEditBitmap(context, width, color, 0x00000000);
    }

    /**
     * 获取返回底部中间位置按钮的bitmap对象
     *
     * @param color     前景
     * @param backColor 背景色默认透明
     */
    public static Bitmap getToBottomCenterBitmap(Context context, int width, int color, int backColor) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(3.5f);
        canvas.drawLine(0, width / 4, width, width / 4, paint);
        canvas.drawLine(0, width / 2 - 3, width, width / 2 - 3, paint);
        canvas.drawLine(0, width / 2 - 3, width / 2 + 1.5f, width - 15, paint);
        canvas.drawLine(width, width / 2 - 3, width / 2 - 1.5f, width - 15, paint);
        return bitmap;
    }

    /**
     * 获取返回底部中间位置按钮的bitmap对象，背景色默认透明
     *
     * @param color 前景
     */
    public static Bitmap getToBottomCenterBitmap(Context context, int width, int color) {
        return getToBottomCenterBitmap(context, width, color, 0x00000000);
    }

    /**
     * 获取缩放按钮的bitmap对象
     *
     * @param color     前景
     * @param backColor 背景色
     */
    public static Bitmap getRotateBitmap(Context context, int width, int color, int backColor) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int gap = width / 8, arcWidth = 5;
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(arcWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(new RectF(gap, gap, width - gap, width - gap), 70, 300, false, paint);
        Path path = new Path();
        path.moveTo(width - arcWidth - gap - gap, width / 2 + arcWidth);
        path.lineTo(width, width / 2 + arcWidth);
        path.lineTo(width - gap - arcWidth / 2, width / 2 + gap + arcWidth / 2 + arcWidth);
        path.close();
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        TempUtil.showBitmapInDialog(context, bitmap);
        return bitmap;
    }

    /**
     * 获取缩放按钮的bitmap对象，背景色默认透明
     *
     * @param color 前景
     */
    public static Bitmap getRotateBitmap(Context context, int width, int color) {
        return getRotateBitmap(context, width, color, 0x00000000);
    }

    /**
     * 一个叉的形状，取消的icon
     *
     * @param context
     * @param width
     * @param backgroundColor
     * @param foregroundColor
     * @return
     */
    public static Bitmap getCancelBitmap(Context context, int width, int foregroundColor, int backgroundColor) {
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(7.5f);
        canvas.drawOval(new RectF(0,0,width,width),paint);

        paint.setColor(foregroundColor);
        double dx = (2 - Math.sqrt(2)) / 4.0 * width;
        int x = (int) (dx * 1.5);
        canvas.drawLine(x, x, width - x, width - x, paint);
        canvas.drawLine(width - x, x, x, width - x, paint);
        return bitmap;
    }

    public static Bitmap getSureBitmap(Context context, int width, int foregroundColor, int backgroundColor) {
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(7.5f);
        canvas.drawOval(new RectF(0,0,width,width),paint);

        paint.setColor(foregroundColor);
        float w = (float) width;
        canvas.drawLine(w * (35.0f / 200), w * (83.0f / 200), w * (72.0f / 200), w * (143.0f / 200), paint);
        canvas.drawLine(w * (72.0f / 200), w * (143.0f / 200), w * (168.0f / 200), w * (69.0f / 200), paint);
        return bitmap;
    }

    public static Bitmap getRedoBitmap(Context context, int width, int foregroundColor, int backgroundColor) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);

        paint.setDither(true);
        paint.setColor(foregroundColor);
        paint.setStrokeWidth(7);
        paint.setStyle(Paint.Style.STROKE);
        return bitmap;
    }

    public static Bitmap getRedpealBitmap(Context context, int width, int foregroundColor, int backgroundColor) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);

        paint.setDither(true);
        paint.setColor(foregroundColor);
        paint.setStrokeWidth(7);
        paint.setStyle(Paint.Style.STROKE);
        return bitmap;
    }
    private void getCircleCenter(PointF a, PointF b,float r,Point o){

    }

}
