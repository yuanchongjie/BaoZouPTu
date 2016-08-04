package a.baozouptu.ptu.mat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.ViewConfiguration;

import a.baozouptu.R;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.MathUtil;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.view.IconBitmapCreator;

/**
 * Created by liuguicen on 2016/8/3.
 *
 * @description
 */
public class Pen {

    private final Context mContext;
    private final Rect totalBound;
    private Bitmap penBmMove, penBmLine;
    private boolean isDrawLine;
    /**
     * 可旋转，旋转角度，默认30度
     */
    private float angle = -30;
    /**
     * <p> penLeft和penTop是旋转中心，也是
     * <P>钢笔的笔记所在位置，不会因为旋转改变
     */
    private float pointLeft, pointTop, penWidth, penHeight;

    private final GeoUtil.UnLevelRect mBound;
    /**
     * 保存的是在原图中的位置
     */
    private Path mLinePath;
    private Paint mPenPaint, mlinePaint;
    private Bitmap penBm;
    public float lastX, lastY;
    private float lastLineTop, lastLineLeft;

    Pen(Context context, Rect bound) {
        mContext = context;
        mLinePath = new Path();

        mPenPaint = new Paint();
        mPenPaint.setAntiAlias(true);
        mPenPaint.setDither(true);

        isDrawLine = false;
        mlinePaint = new Paint();
        mlinePaint.setAntiAlias(true);
        mlinePaint.setDither(true);
        mlinePaint.setStrokeWidth(5);
        mlinePaint.setStyle(Paint.Style.STROKE);
        mlinePaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        mlinePaint.setColor(Util.getColor(R.color.base_color1));

        penWidth = 60;
        int c1 = Util.getColor(R.color.mat_pen_line1);
        int c_1 = Color.argb(Color.alpha(c1) / 3, Color.red(c1), Color.green(c1), Color.blue(c1));
        int c2 = Util.getColor(R.color.mat_pen_line2);
        int c_2 = Color.argb(Color.alpha(c2) / 3, Color.red(c2), Color.green(c2), Color.blue(c2));
        penBmLine = IconBitmapCreator.createPen(mContext, (int) penWidth, c1, c2);
        penBm = penBmMove = IconBitmapCreator.createPen(mContext, (int) penWidth, c_1, c_2);

        penHeight = penBm.getHeight();

        pointLeft = bound.width() / 2;
        pointTop = bound.height() / 2;
        totalBound = bound;

        this.mBound = new GeoUtil.UnLevelRect();
        resetBound();
    }

    //动作以及包含区域范围部分

    /*********************************************************************************************/

    /**
     * 移动，会先检测边界(图片边界和总的边界）
     *
     * @param srcRect  在图片中的范围
     * @param picBound 图片的范围
     */
    public void move(float nx, float ny, Rect srcRect, Rect picBound) {
        if (lastX < 0) {//从外面突然移进了Pen时
            lastX = nx;
            lastY = ny;
        }
        float dx = nx - lastX, dy = ny - lastY;
        //图片边界
        if (pointLeft + dx < picBound.left) {
            dx = picBound.left - pointLeft;
        }
        if (pointLeft + dx > picBound.right) {
            dx = picBound.right - pointLeft;
        }
        if (pointTop + dy < picBound.top) {
            dy = picBound.top - pointTop;
        }
        if (pointTop + dy > picBound.bottom) {
            dy = picBound.bottom - pointTop;
        }
        //总的边界
        GeoUtil.UnLevelRect tempRect = new GeoUtil.UnLevelRect(mBound);
        tempRect.translate(dx, dy);
        if (tempRect.getLeft() + penWidth / 2 < totalBound.left) {//左边超出
            if (pointTop < totalBound.height() - penHeight)//在上部
                rotateTo(-30);
            else//在下部
                rotateTo(-150);
        }
        if (tempRect.getRight() - penWidth / 2 > totalBound.right) {//右边超出
            if (pointTop < totalBound.height() - penHeight)//在上部
                rotateTo(30);
            else
                rotateTo(150);
        }
        if (tempRect.getTop() + penHeight / 3 < totalBound.top) {//上边超出
            if (pointLeft < totalBound.width() - penWidth)//在左部
                rotateTo(-30);
            else//在右部
                rotateTo(-150);
        }
        if (tempRect.getButtom() - penHeight / 3 > totalBound.bottom) {//下边超出
            if (pointLeft < totalBound.width() - penWidth)
                rotateTo(-30);
            else
                rotateTo(-150);
        }

        lastX = nx;
        lastY = ny;
        Util.P.le("lastX = " + lastX, "lastY= " + lastY);

        pointLeft += dx;
        pointTop += dy;

        float[] xy = getLocationAtPicture(pointLeft, pointTop, srcRect, picBound);
        float lineLeft = xy[0], lineTop = xy[1];
        mLinePath.rQuadTo(lastLineLeft, lastLineTop,
                (lastLineLeft + lineLeft) / 2,
                (lastLineTop + lineTop) / 2);
        lastLineLeft = lineLeft;
        lastLineTop = lineTop;
        resetBound();
    }

    private float[] getLocationAtPicture(float px, float py, Rect srcRect, Rect dstRect) {
        float x, y;

        //采用精确计算
        String srcWidth = MathUtil.subtract(Float.toString(srcRect.right), Float.toString(srcRect.left));
        String dstWidth = MathUtil.subtract(Float.toString(dstRect.right), Float.toString(dstRect.left));
        String ratio = MathUtil.divide(dstWidth, srcWidth);
        String px1 = MathUtil.multiply(Float.toString(px), ratio);
        String py1 = MathUtil.multiply(Float.toString(py), ratio);
        x = Float.valueOf(MathUtil.add(px1, Float.toString(srcRect.left)));
        y = Float.valueOf(MathUtil.add(py1, Float.toString(srcRect.top)));

        return new float[]{x, y};
    }

    public void rotateTo(float angle) {
        this.angle = angle;
    }


    private void resetBound() {
        PointF p0 = new PointF(pointLeft, pointTop);
        PointF p1 = new PointF(p0.x - penWidth / 2, p0.y), p2 = new PointF(p0.x + penWidth / 2, p0.y),
                p3 = new PointF(p0.x + penWidth / 2, p0.y + penHeight), p4 = new PointF(p0.x - penWidth / 2, p0.y + penHeight);

        p1 = GeoUtil.getCooderAfterRotate(p0, p1, angle);
        p2 = GeoUtil.getCooderAfterRotate(p0, p2, angle);
        p3 = GeoUtil.getCooderAfterRotate(p0, p3, angle);
        p4 = GeoUtil.getCooderAfterRotate(p0, p4, angle);
        mBound.set(p1, p2, p3, p4);
    }

    public boolean contain(float x, float y) {
        return mBound.contain(x, y);
    }

    public void startAt(float x, float y) {
        lastX = x;
        lastY = y;
        if (isDrawLine)
            mLinePath.moveTo(pointLeft, pointTop);
    }
/******************************************************************************************/


    //绘制部分

    /*****************************************************************************/
    void drawPen(Canvas canvas) {
        Util.P.le("pen:letf= " + pointLeft, "pen:top= " + pointTop);
        canvas.save();
        canvas.rotate(angle, pointLeft, pointTop);
        canvas.drawBitmap(penBm, pointLeft - penWidth / 2, pointTop, mPenPaint);
        canvas.restore();
    }


    public void reStart() {
        lastX = lastY = -1;
    }

    public void reSet(Rect bound) {
        lastX = lastY = -1;
        pointLeft = bound.width() / 2;
        pointTop = bound.height() / 2;
        mLinePath.rewind();
        mLinePath.moveTo(pointLeft, pointTop);
    }

    public void drawLine(Canvas sourceCanvas) {
        sourceCanvas.drawPath(mLinePath, mlinePaint);
    }

    /*******************************************************************************/
    public boolean isDrawLine() {
        return isDrawLine;
    }

    public long lastTime = -1;

    public void onClick() {
        Util.P.le("检测pen的双击");
        long curTime = System.currentTimeMillis();
        //貌似系统定义的双击正是300毫秒 ViewConfiguration.getDoubleTapTimeout()
        if (curTime - lastTime < ViewConfiguration.getDoubleTapTimeout()) {
            lastTime = curTime;
            isDrawLine = !isDrawLine;
            changeApearence();
            Util.P.le("pen双击成功");
        } else {
            lastTime = curTime;
        }
    }

    public void cancel() {
        lastTime = -1;
    }

    private void changeApearence() {
        if (isDrawLine) penBm = penBmLine;
        else penBm = penBmMove;
    }

    public void releaseResource() {
        penBmLine.recycle();
        penBmMove.recycle();
    }

}
