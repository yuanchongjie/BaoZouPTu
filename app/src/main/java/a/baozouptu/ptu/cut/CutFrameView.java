package a.baozouptu.ptu.cut;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;

import java.text.Format;
import java.util.Formatter;
import java.util.Locale;

import a.baozouptu.R;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;

/**
 * Created by liuguicen on 2016/8/5.
 * <p>
 * 裁剪的框，支持缩放和拖动，在中间格子显示宽和高
 */
public class CutFrameView extends View {
    private final String TAG = "CutFrame";
    private final Rect picBound;
    private static int MIN_FRAME_WIDTH=20;
    private static int MIN_FRAME_HEIGHT=20;
    /**
     * 位置和宽高
     */
    float mLeft, mTop, mWidth, mHeight;
    /**
     * frame的位置和宽高
     */
    float frameLeft, frameTop, frameWidth, frameHeight;
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
    private float cornerWidth, cornerLumpLength, edgeWidth;

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
    private final Paint noChosedPaint;
    private int color;
    private float lastX = -1, lastY = -1;
    private MOVESTATA CUR_STATA;

    enum MOVESTATA {
        LEFT_TOP,
        RIGNT_TOP,
        LEFT_BOTTOM,
        RIGHT_BOTTOM,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER,
        NONE
    }

    /**
     * @param picBound 图片在屏幕，PtuFrame中的矩形，初始化时根据图片范围定位置大小
     */
    public CutFrameView(Context context, Rect picBound) {
        super(context);
        CUR_STATA=MOVESTATA.NONE;
        color = Util.getColor(R.color.cut_frame);
        color = Color.RED;
        this.picBound = picBound;
        frameLeft = mLeft = picBound.left;
        frameTop = mTop = picBound.top;
        frameWidth = mWidth = picBound.width() / 2;
        frameHeight = mHeight = picBound.height();

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setColor(color);

        mLumpPaint = new Paint();
        mLumpPaint.setStrokeWidth(1);
        mLumpPaint.setStyle(Paint.Style.FILL);
        mLumpPaint.setAntiAlias(true);
        mLumpPaint.setDither(true);
        mLumpPaint.setColor(color);

        noChosedPaint = new Paint();
        noChosedPaint.setColor(Util.getColor(R.color.half_transparent_black));

        textPaint = new Paint();//显示尺寸的文字
        textPaint.setTextSize(20);
        textPaint.setAntiAlias(true);
        textPaint.setColor(color);
        //尺寸
        Resources resources = Util.MyApplication.getAppContext().getResources();
        cornerWidth = resources.getDimension(R.dimen.cut_frame_lump_width);
        gridLineWidth = cornerWidth / 8;
        cornerLumpLength = cornerWidth * 4;
        edgeWidth = cornerWidth * 0.8f;
        minLenth = edgeWidth * 3;


        inFixedProportion = false;
        inFixedSize = false;
        rect = new RectF();
    }

    /**
     * 只会处理移动的情况，其它情况它不处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float frameX = event.getX()-frameLeft, frameY = event.getY()-frameTop;
        float x=event.getX(),y=event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                //四个角
                // 左上角
                rect.set(-cornerWidth, -cornerWidth,
                        cornerWidth, 0 + cornerWidth);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.LEFT_TOP;
                    return true;
                }
//右上
                rect.set(frameWidth - cornerWidth, -cornerWidth,
                        frameWidth + cornerWidth, 0 + cornerWidth);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.RIGNT_TOP;
                    return true;
                }

//左下
                rect.set(-cornerWidth, frameHeight - cornerWidth,
                        cornerWidth, frameHeight + cornerWidth);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.LEFT_BOTTOM;
                    return true;
                }
//右下
                rect.set(frameWidth - cornerWidth, frameHeight - cornerWidth,
                        frameWidth + cornerWidth, frameHeight + cornerWidth);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.RIGHT_BOTTOM;
                    return true;
                }
                //四边
                //四条边上
                rect.left = -edgeWidth;
                rect.right = edgeWidth;
                rect.top = -edgeWidth * 3 / 4;
                rect.bottom = edgeWidth * 3 / 4;
                //上
                rect.offset(frameWidth / 2, 0);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.TOP;
                    return true;
                }
//        下
                rect.offset(0, frameHeight);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.BOTTOM;
                    return true;
                }

                rect.left = -edgeWidth * 3 / 4;
                rect.right = edgeWidth * 3 / 4;
                rect.top = -edgeWidth;
                rect.bottom = edgeWidth + 0;
                //右边
                rect.offset(frameWidth, frameHeight / 2);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.RIGHT;
                    return true;
                }
                //左边
                rect.offset(-frameWidth, 0);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.LEFT;
                    return true;
                }

                //中间位置
                rect.set(frameWidth / 3, frameHeight / 3, frameWidth * 2 / 3, frameHeight * 2 / 3);
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATA.CENTER;
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if(CUR_STATA==MOVESTATA.NONE)
                    return false;
                float dx = x - lastX, dy = y - lastY;
                if (CUR_STATA == MOVESTATA.CENTER) {//移动整个切图框
                    //判断边界
                    if (frameLeft + dx < picBound.left) frameLeft = picBound.left;
                    else if (frameLeft + dx + frameWidth > picBound.right)
                        frameLeft = picBound.right - frameWidth;
                    else frameLeft += dx;

                    if (frameTop + dy < picBound.top) frameTop = picBound.top;
                    else if (frameTop + dy + frameHeight > picBound.bottom)
                        frameTop = picBound.bottom - frameHeight;
                    else
                        frameTop += dy;
                } else {
                    switch (CUR_STATA) {
                        case LEFT_TOP:
                            frameLeft += dx;
                            frameWidth -= dx;//左边加多少，宽度减多少
                            frameTop += dy;
                            frameHeight -= dy;//上边加多少，高度减多少
                            break;
                        case RIGNT_TOP:
                            frameWidth += dx;//右边增加，及宽度增加
                            frameTop += dy;
                            frameHeight -= dy;//上边加多少，高度减多少
                            break;
                        case LEFT_BOTTOM:
                            frameLeft += dx;
                            frameWidth -= dx;//左边加多少，宽度减多少
                            frameHeight += dy;
                            break;
                        case RIGHT_BOTTOM:
                            frameWidth += dx;//右边增加，及宽度增加
                            frameHeight += dy;
                            break;
                        case LEFT:
                            frameLeft += dx;
                            frameWidth -= dx;//左边加多少，宽度减多少
                            break;
                        case RIGHT:
                            frameWidth += dx;//右边增加，及宽度增加
                            break;
                        case TOP:
                            frameTop += dy;
                            frameHeight -= dy;//上边加多少，高度减多少
                            break;
                        case BOTTOM:
                            frameHeight += dy;
                            break;

                    }
                    adjustEdge();
                }
                lastX=x;
                lastY=y;
                break;
            case MotionEvent.ACTION_UP:
                CUR_STATA=MOVESTATA.NONE;
                break;

        }
        invalidate();
        return false;
    }

    private void adjustEdge() {
        RectF inRect = new RectF(frameLeft, frameTop, frameLeft + frameWidth, frameTop + frameHeight);
        adjustRectInRect(inRect, picBound);
        frameLeft = inRect.left;
        frameTop = inRect.top;
        frameWidth = inRect.width();
        frameHeight = inRect.height();
    }

    public void adjustRectInRect(RectF inRect, Rect edgeRect) {
        if (inRect.left < edgeRect.left)
            inRect.left = edgeRect.left;
        if (inRect.left >= inRect.right-MIN_FRAME_WIDTH)
            inRect.left = inRect.right-MIN_FRAME_WIDTH;

        if (inRect.top < edgeRect.top)
            inRect.top = edgeRect.top;
        if (inRect.top >= inRect.bottom-MIN_FRAME_HEIGHT)
            inRect.top = inRect.bottom-MIN_FRAME_HEIGHT;

        if (inRect.right > edgeRect.right)
            inRect.right = edgeRect.right;
        if (inRect.right <= inRect.left+MIN_FRAME_WIDTH)
            inRect.right = inRect.left+MIN_FRAME_WIDTH;

        if (inRect.bottom > edgeRect.bottom)
            inRect.bottom = edgeRect.bottom;
        if (inRect.bottom <= inRect.top+MIN_FRAME_HEIGHT)
            inRect.bottom = inRect.top+MIN_FRAME_HEIGHT;
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

    /******************************************************************************/
    /**
     * 直接传入总的canvas，里面进行移动再绘图
     */
    public void onDraw(Canvas canvas) {
        Log.e(TAG, "draw开始 ");

        //暗色未选中背景
        canvas.drawRect(0, 0, getRight(), frameTop, noChosedPaint);//上下
        canvas.drawRect(0, frameTop + frameHeight,  getRight(), getBottom(), noChosedPaint);
        canvas.drawRect(0, frameTop, frameLeft, frameTop + frameHeight, noChosedPaint);//左右
        canvas.drawRect(frameLeft + frameWidth, frameTop,  getRight(), frameTop + frameHeight, noChosedPaint);

        canvas.translate(frameLeft, frameTop);
        drawFrame(canvas);
    }

    private void drawFrame(Canvas canvas) {
        for (int i = 0; i <= 3; i++) {//格子线
            canvas.drawLine(0, frameHeight * i / 3, frameWidth, frameHeight * i / 3, mLinePaint);
            canvas.drawLine(frameWidth * i / 3, 0, frameWidth * i / 3, frameHeight, mLinePaint);
        }

        //画四个角
        //左上
        rect.set(-cornerWidth, -cornerWidth,
                cornerWidth, 0 + cornerWidth);
        canvas.drawArc(rect, 0f, 90f, true, mLumpPaint);
//右上
        rect.set(frameWidth - cornerWidth, -cornerWidth,
                frameWidth + cornerWidth, 0 + cornerWidth);
        canvas.drawArc(rect, 90f, 90f, true, mLumpPaint);
//左下
        rect.set(-cornerWidth, frameHeight - cornerWidth,
                cornerWidth, frameHeight + cornerWidth);
        canvas.drawArc(rect, 0f, -90f, true, mLumpPaint);
//      右下
        rect.set(frameWidth - cornerWidth, frameHeight - cornerWidth,
                frameWidth + cornerWidth, frameHeight + cornerWidth);
        canvas.drawArc(rect, -90f, -90f, true, mLumpPaint);


        //四条边上
        rect.left = -edgeWidth;
        rect.right = edgeWidth;
        rect.top = -edgeWidth * 3 / 4;
        rect.bottom = edgeWidth * 3 / 4;
        //上
        rect.offset(frameWidth / 2, 0);
        canvas.drawArc(rect, 0f, 180f, true, mLumpPaint);
//        下
        rect.offset(0, frameHeight);
        canvas.drawArc(rect, 180f, 180f, true, mLumpPaint);

        rect.left = -edgeWidth * 3 / 4;
        rect.right = edgeWidth * 3 / 4;
        rect.top = -edgeWidth;
        rect.bottom = edgeWidth + 0;
        //右边
        rect.offset(frameWidth, frameHeight / 2);
        canvas.drawArc(rect, 90f, 180f, true, mLumpPaint);
        //左边
        rect.offset(-frameWidth, 0);
        canvas.drawArc(rect, 270f, 180f, true, mLumpPaint);

     /*   //四个交叉点
        mLinePaint.setStrokeWidth(cornerWidth * 2);
        canvas.drawRect(frameWidth / 3, frameHeight / 3, frameWidth / 3, frameHeight / 3, mLumpPaint);
        canvas.drawRect(frameWidth * 2 / 3 - cornerWidth / 2, frameHeight / 3, frameWidth * 2 / 3 + cornerWidth / 2, frameHeight / 3, mLumpPaint);
        canvas.drawRect(frameWidth / 3 - cornerWidth / 2, frameHeight * 2 / 3, frameWidth / 3 + cornerWidth / 2, frameHeight * 2 / 3, mLumpPaint);
        canvas.drawRect(frameWidth * 2 / 3 - cornerWidth / 2, frameHeight * 2 / 3, frameWidth * 2 / 3 + cornerWidth / 2, frameHeight * 2 / 3, mLumpPaint);
*/
        //中间的数字
        String show = String.format(Locale.getDefault(), "%d × %d", (int) frameWidth, (int) frameHeight);
        float textWidth = textPaint.measureText(show);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = frameHeight / 2 - fm.descent + (fm.bottom - fm.top) / 2;
        canvas.drawText(show, (frameWidth - textWidth) / 2, textY, textPaint);

        Util.P.le(TAG, "drawFrame完成");
    }
}
