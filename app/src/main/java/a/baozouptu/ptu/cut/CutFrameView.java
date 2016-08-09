package a.baozouptu.ptu.cut;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.logging.MemoryHandler;

import a.baozouptu.R;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;

/**
 * Created by liuguicen on 2016/8/5.
 *
 * @description 裁剪的框，支持缩放和拖动，在中间格子显示宽和高
 */
public class CutFrameView {
    /**
     * 位置和宽高
     */
    float mLeft, mTop, mWidth, mHeight;

    /**
     * 临界宽度，小于此宽度时，如果图片能放大，就会自动放大
     */
    float criticalWidth;

    /**
     * 最小的宽高，宽度不能小于此宽度
     */
    float minLenth;

    /**
     * 裁剪框上各个小块的长宽
     */
    private float lumpWidth, cornerLumpLength, edgeLumpLenth;

    /**
     * 格子线的宽度
     */
    private float gridLineWidth;


    private boolean inFixedProportion;
    private boolean inFixedSize;
    private int proportionWidth, proportionHeight;
    private final Paint mLinePaint;
    private final Paint textPaint;
    private RectF rect;
    private final Paint mLumpPaint;

    /**
     * @param picBound 图片在屏幕，PtuFrame中的矩形，初始化时根据图片范围定位置大小
     */
    CutFrameView(Rect picBound) {
        mLeft = picBound.left;
        mTop = picBound.top;
        mWidth = picBound.width();
        mHeight = picBound.height();

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(2);
        mLumpPaint = new Paint();
        mLumpPaint.setStrokeWidth(1);
        mLumpPaint.setStyle(Paint.Style.FILL);
        textPaint = new Paint();
        textPaint.setTextSize(20);
        textPaint.setColor(0xffffff);
        mLinePaint.setColor(0xffffff);
        mLumpPaint.setColor(0xffffff);

//尺寸
        Resources resources = Util.MyApplication.getAppContext().getResources();
        lumpWidth = resources.getDimension(R.dimen.cut_frame_lump_width);
        gridLineWidth = lumpWidth / 8;
        cornerLumpLength = lumpWidth * 4;
        edgeLumpLenth = lumpWidth * 4;
        minLenth = edgeLumpLenth * 3;


        inFixedProportion = false;
        inFixedSize = false;
    }

    /********************************************************************************/

    public void setInFixedProportion(int width, int height, Rect picBound) {
        this.inFixedProportion = true;
        proportionWidth = width;
        proportionHeight = height;

        //把裁剪框放到图片中，让裁剪框最大
//        让宽满足
        if (picBound.height() * 1f / picBound.width() > height * 1f / width) {//高除以宽比例更大，高有多余的
            mLeft = 0;
            mWidth = picBound.width();
            mHeight = (int) (mWidth * height * 1.0 / width);
            mTop = (picBound.height() - mHeight) / 2;
        } else {
            mTop = 0;
            mHeight = picBound.height();
            mWidth = (int) (mHeight * width * 1.0 / height);
            mTop = (picBound.height() - mHeight) / 2;
        }
    }

    public void cancelInFixedProportion() {
        this.inFixedProportion = false;
    }

    /**
     * 宽高必须在图片范围内
     */
    public void setInFixedSize(int width, int height, Rect picBound) {
        inFixedSize = true;
        mWidth = width;
        mHeight = height;
        mLeft = picBound.width() / 2 - mWidth / 2;
        mHeight = (picBound.height() - mHeight) / 2;
    }

    public void cancelInFixedSzie() {
        inFixedSize = false;
    }

    /********************************************************************************/
    public void moveLeft(float dis, Rect picBound) {
        if (!inFixedSize) return;
        if (mLeft + dis < picBound.left) {
            mWidth = mLeft + mWidth - picBound.left;
            mLeft = picBound.left;
        } else if (dis > mWidth - minLenth) {
            mLeft = mLeft + mWidth - minLenth;
            mWidth = minLenth;
        } else {
            mLeft += dis;
            mWidth -= dis;
        }

        if (inFixedProportion) {
            moveBottom(dis * proportionHeight / proportionWidth, picBound);
        }
    }

    public void moveRight(float dis, Rect picBound) {
        if (!inFixedSize) return;
        if (mLeft + mWidth + dis > picBound.right) mWidth = picBound.right - mLeft;
        else if (mWidth + dis < minLenth) mWidth = minLenth;
        else mWidth += dis;

        if (inFixedProportion) {
            moveTop(dis * proportionHeight / proportionWidth, picBound);
        }
    }

    public void moveTop(float dis, Rect picBound) {
        if (!inFixedSize) return;
        if (mTop + dis < picBound.top) {
            mHeight = mTop + mHeight - picBound.top;
            mTop = picBound.top;
        } else if (dis > mHeight - minLenth) {
            mTop = mTop + mHeight - minLenth;
            mHeight = minLenth;
        } else {
            mTop += dis;
            mHeight -= dis;
        }

        if (inFixedProportion) {
            moveLeft(dis * proportionWidth / proportionHeight, picBound);
        }
    }

    public void moveBottom(float dis, Rect picBound) {
        if (!inFixedSize) return;
        if (mTop + mHeight + dis > picBound.bottom) mHeight = picBound.bottom - mTop;
        else if (mHeight + dis < minLenth) mHeight = minLenth;
        else mHeight += dis;

        if (inFixedProportion) {
            moveRight(dis * proportionWidth / proportionHeight, picBound);
        }
    }

    @Deprecated
    public void scale(float centerX, float centerY, float ratio, Rect picBound) {
        float minRatio = Math.max(minLenth / mWidth, minLenth / mHeight);//最小的缩放比例
        ratio = Math.max(minRatio, ratio);

        float maxRatio = Math.min(picBound.width() / mWidth, picBound.height() / mHeight);//最大的
        ratio = Math.min(maxRatio, ratio);

        mWidth *= ratio;
        mHeight *= ratio;
        mLeft = GeoUtil.getScaledX(mLeft, mTop, centerX, centerY, ratio);
        mTop = GeoUtil.getScaledY(mLeft, mTop, centerX, centerY, ratio);

        if (mLeft < picBound.left) mLeft = picBound.left;//放大时超出外边界，缩小时超出内边界，上面处理
        if (mTop < picBound.top) mTop = picBound.top;
    }

    public void move(float dx, float dy, Rect picBound) {
        mLeft += dx;
        mTop += dy;
        if (mLeft < picBound.left) mLeft = picBound.left;
        if (mLeft + mWidth > picBound.right) mLeft = picBound.right - mWidth;
        if (mTop < picBound.top) mTop = picBound.top;
        if (mTop + mHeight > picBound.bottom) mTop = picBound.bottom - mHeight;
    }

    /******************************************************************************/
    /**
     * 直接传入总的canvas，里面进行移动再绘图
     */
    public void draw(Canvas canvas) {
        canvas.translate(mLeft, mTop);
        int color = Util.getColor(R.color.cut_frame);
        //格子线
        for (int i = 0; i < 4; i++) {
            canvas.drawLine(0, mHeight * i / 4, mWidth, mHeight * i / 4, mLinePaint);
            canvas.drawLine(mWidth * i / 4, 0, mWidth * i / 4, mHeight, mLinePaint);
        }

        //画四个角
        rect.set(0 - lumpWidth / 2, 0 - lumpWidth / 2,
                0 + edgeLumpLenth / 2, 0 + lumpWidth / 2);
        canvas.drawRect(rect, mLumpPaint);
        rect.set(0 - lumpWidth / 2, 0 - lumpWidth / 2,
                0 + lumpWidth / 2, 0 + edgeLumpLenth / 2);
        canvas.drawRect(rect, mLumpPaint);

        rect.set(mWidth - edgeLumpLenth / 2, 0 - lumpWidth / 2,
                mWidth + lumpWidth / 2, 0 + lumpWidth / 2);
        canvas.drawRect(rect, mLumpPaint);
        rect.set(mWidth - lumpWidth / 2, 0 - lumpWidth / 2,
                mWidth + lumpWidth / 2, 0 + edgeLumpLenth / 2);
        canvas.drawRect(rect, mLumpPaint);

        rect.set(0 - lumpWidth / 2, mHeight - lumpWidth / 2,
                0 + edgeLumpLenth / 2, mHeight + lumpWidth / 2);
        canvas.drawRect(rect, mLumpPaint);
        rect.set(0 - lumpWidth / 2, mHeight - edgeLumpLenth / 2,
                0 + lumpWidth / 2, mHeight + lumpWidth / 2);
        canvas.drawRect(rect, mLumpPaint);

        rect.set(mWidth - edgeLumpLenth / 2, mHeight - lumpWidth / 2,
                mWidth + lumpWidth / 2, mHeight + lumpWidth / 2);
        canvas.drawRect(rect, mLumpPaint);
        rect.set(mWidth - lumpWidth / 2, mHeight - edgeLumpLenth / 2,
                mWidth + lumpWidth / 2, mHeight + lumpWidth / 2);
        canvas.drawRect(rect, mLumpPaint);

        //四条边上
        mLinePaint.setStrokeWidth(lumpWidth);
        canvas.drawLine(mWidth / 2 - edgeLumpLenth / 2, 0, mWidth / 2 + edgeLumpLenth / 2, 0, mLumpPaint);
        canvas.drawLine(mWidth / 2 - edgeLumpLenth / 2, mHeight, mWidth / 2 + edgeLumpLenth / 2, mHeight, mLumpPaint);
        canvas.drawLine(0, mHeight / 2 - edgeLumpLenth / 2, 0, mHeight / 2 + edgeLumpLenth / 2, mLumpPaint);
        canvas.drawLine(mWidth, mHeight / 2 - edgeLumpLenth / 2, mWidth, mHeight / 2 + edgeLumpLenth / 2, mLumpPaint);

        //四个交叉点
        mLinePaint.setStrokeWidth(lumpWidth * 2);
        canvas.drawLine(mWidth / 3 - lumpWidth / 2, mHeight / 3, mWidth / 3 + lumpWidth / 2, mHeight / 3, mLumpPaint);
        canvas.drawLine(mWidth * 2 / 3 - lumpWidth / 2, mHeight / 3, mWidth * 2 / 3 + lumpWidth / 2, mHeight / 3, mLumpPaint);
        canvas.drawLine(mWidth / 3 - lumpWidth / 2, mHeight * 2 / 3, mWidth / 3 + lumpWidth / 2, mHeight * 2 / 3, mLumpPaint);
        canvas.drawLine(mWidth * 2 / 3 - lumpWidth / 2, mHeight * 2 / 3, mWidth * 2 / 3 + lumpWidth / 2, mHeight * 2 / 3, mLumpPaint);

        //中间的数字
        String sWidth = String.valueOf(mWidth), sHeight = String.valueOf(mHeight);
        String show = sWidth + " × " + sHeight;
        float textWidth=textPaint.measureText(show);
        Paint.FontMetrics fm=textPaint.getFontMetrics();
        float textY = mHeight / 2 - fm.descent + (fm.bottom - fm.top) / 2;
        canvas.drawText(show,(mWidth-textWidth)/2,textY,textPaint);

    }


    /********************************************************************************/
    //判断触摸位置部分
    public boolean isInCenterLump(float x, float y) {
        rect = new RectF(mWidth / 3 - lumpWidth, mHeight / 3 - lumpWidth,
                mWidth / 3 + lumpWidth, mHeight / 3 + lumpWidth);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;
        rect.set(mWidth * 2 / 3 - lumpWidth, mHeight * 2 / 3 - lumpWidth,
                mWidth * 2 / 3 + lumpWidth, mHeight * 2 / 3 + lumpWidth);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;

        rect.set(mWidth * 2 / 3 - lumpWidth, mHeight / 3 - lumpWidth,
                mWidth * 2 / 3 + lumpWidth, mHeight / 3 + lumpWidth);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;
        rect.set(mWidth / 3 - lumpWidth, mHeight * 2 / 3 - lumpWidth,
                mWidth / 3 + lumpWidth, mHeight * 2 / 3 + lumpWidth);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;
        return false;
    }

    public boolean isInCornerLump(float x, float y) {
        rect.set(0 - lumpWidth / 2, 0 - lumpWidth / 2,
                0 + edgeLumpLenth / 2, 0 + edgeLumpLenth / 2);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;

        rect.set(mWidth - edgeLumpLenth / 2, 0 - lumpWidth / 2,
                mWidth + lumpWidth / 2, 0 + edgeLumpLenth / 2);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;

        //third
        rect.set(0 - lumpWidth / 2, mHeight - edgeLumpLenth / 2,
                0 + edgeLumpLenth / 2, mHeight + lumpWidth / 2);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;

        rect.set(mWidth - edgeLumpLenth / 2, mHeight - edgeLumpLenth / 2,
                mWidth + lumpWidth / 2, mHeight + lumpWidth / 2);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;
        return false;
    }

    public boolean isInEdgeLine(float x, float y) {

        rect.set(0 + edgeLumpLenth / 2, 0 - lumpWidth / 2,
                mWidth - edgeLumpLenth / 2, 0 + lumpWidth / 2);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;

        rect.set(mWidth - lumpWidth / 2, 0 + edgeLumpLenth / 2,
                mWidth + lumpWidth / 2, mHeight - edgeLumpLenth / 2);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;

        //third
        rect.set(0 + edgeLumpLenth / 2, mHeight - lumpWidth / 2,
                mWidth - edgeLumpLenth / 2, mHeight + edgeLumpLenth / 2);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;

        rect.set(0 - lumpWidth / 2, 0 + edgeLumpLenth / 2,
                0 + lumpWidth / 2, mHeight - edgeLumpLenth / 2);
        rect.offset(mLeft, mTop);
        if (rect.contains(x, y))
            return true;
        return false;
    }
}
