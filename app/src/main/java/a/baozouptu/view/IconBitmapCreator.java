package a.baozouptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.style.LineHeightSpan;
import android.text.style.TtsSpan;
import android.util.DisplayMetrics;

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
        paint.setStrokeWidth(2f);
        canvas.drawLine(0, width / 4 - width / 11, width, width / 4 - width / 11, paint);
        canvas.drawLine(0, width / 4 + width / 16, width, width / 4 + width / 16, paint);
        canvas.drawLine(0, width / 2 - 3, width, width / 2 - 3, paint);
        canvas.drawLine(0, width / 2 - 3, width / 2 + 1f, width - 15, paint);
        canvas.drawLine(width, width / 2 - 3, width / 2 - 1f, width - 15, paint);
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
     * @param foregroundColor
     * @return
     */
    public static Bitmap createCancelBitmap(Context context, int width, int foregroundColor) {
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //内圆上画图标
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(7.5f);
        paint.setColor(foregroundColor);
        double dx = (2 - Math.sqrt(2)) / 4.0 * width;
        int x = (int) (dx * 2);
        canvas.drawLine(x, x, width - x, width - x, paint);
        canvas.drawLine(width - x, x, x, width - x, paint);
        return bitmap;
    }

    public static Bitmap createSureBitmap(Context context, int width, int foregroundColor) {
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(7.5f);
        paint.setColor(foregroundColor);
        float w = (float) width;
        canvas.drawLine(w * (35.0f / 200), w * (83.0f / 200), w * (72.0f / 200)+2, w * (143.0f / 200)+2, paint);
        canvas.drawLine(w * (72.0f / 200)-2, w * (143.0f / 200)+2, w * (168.0f / 200), w * (69.0f / 200), paint);
        return bitmap;
    }


    public static Bitmap createRedpealBitmap(Context context, int width, int foregroundColor) {

        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        PointF c1 = new PointF(width * 100f / 200, width * 61f / 200),
                c2 = new PointF(width * 100f / 200, width * 100f / 200),
                c3 = new PointF(width * 28f / 200, width * 156f / 200);
        PointF rectP1=new PointF(c3.x,c1.y),rectP2=new PointF(c3.x-(c1.x-c3.x)/4,c2.x-(c1.x-c3.x)/10);
        float r1 =c1.x-c3.x;
        float r2 = width / 3;

        PointF a1 = new PointF(width * 100f / 200, width * 20f / 200),
                a2 = new PointF(width * 175f / 200, width * 86f / 200),
                a3 = new PointF(width * 100f / 200, width * 136f / 200);


        paint.setColor(foregroundColor);
        PointF o1 = getCircleCenter(c2, c3, r1), o2 = getCircleCenter(c1, c3, r2);
        canvas.drawOval(new RectF(o2.x - r2, o2.y - r2, o2.x + r2, o2.y + r2), paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawOval(new RectF(o1.x - r1, o1.y - r1, o1.x + r1, o1.y + r1), paint);
        canvas.drawRect(width * 100f / 200, 0, width, width, paint);
        canvas.drawRect(0, width * 156f / 200, width, width, paint);

        Path path = new Path();
        path.moveTo(a1.x, a1.y);
        path.lineTo(a2.x, a2.y);
        path.lineTo(a3.x, a3.y);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(foregroundColor);
        canvas.drawPath(path, paint);

        return bitmap;
    }

    /**
     * 根据圆上两点和它的半径获取圆心
     */
    private static PointF getCircleCenter(PointF a, PointF b, float r) {
        PointF base = new PointF(b.x - a.x, b.y - a.y);//已知两点的连线的向量表示
        PointF p = new PointF((a.x + b.x) / 2, (a.y + b.y) / 2);//垂线的底点
        PointF v = new PointF(base.y, -base.x);//垂线的方向向量
        double l = Math.sqrt(v.x * v.x + v.y * v.y);
        v.x /= l;
        v.y /= l;//垂线向量改为单位向量
        double d = Math.sqrt(base.x * base.x + base.y * base.y);//连线距离
        double dr = Math.sqrt(r * r - d * d / 4);
        return new PointF(p.x + (float) (v.x * dr), p.y + (float) (v.y * dr));
    }

    /**
     * 制作重做图标的bitmap
     *
     * @param context
     * @param width
     * @param foregroundColor
     * @return
     */
    public static Bitmap createRedoBitmap(Context context, int width, int foregroundColor) {
        Bitmap tempBitmap = createRedpealBitmap(context, width, foregroundColor);
        Matrix m = new Matrix();
        m.setScale(-1, 1);
        m.postTranslate(tempBitmap.getWidth(), 0); //镜像水平翻转
        return tempBitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), m
                , true);
    }

    public static Bitmap createSendBitmap(Context context, int width, int foregroundColor) {
        width=(int)(width*(0.75));
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(foregroundColor);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);

        Path path = new Path();
        path.moveTo(60 / 720.0f * width, 90 / 720.0f * width);
        path.lineTo(690 / 720.0f * width, 360 / 720.0f * width);
        path.lineTo(60 / 720.0f * width, 630 / 720.0f * width);
        path.close();
        canvas.drawPath(path, paint);

        Paint paint1=new Paint();
        paint1.setAntiAlias(true);
        paint1.setDither(true);
        path.reset();
        path.moveTo(60 / 720.0f * width, 300 / 720.0f * width);
        path.lineTo(508 / 720.0f * width, 360 / 720.0f * width);
        path.lineTo(60 / 720.0f * width, 420 / 720.0f * width);
        path.close();
        paint1.setStyle(Paint.Style.FILL);
        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(path, paint1);
        return bitmap;
    }
}
