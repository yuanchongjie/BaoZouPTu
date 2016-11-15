package a.baozouptu.ptu.cut;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Locale;

import a.baozouptu.R;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;

/**
 * Created by liuguicen on 2016/8/5.
 * <p>
 * 裁剪的框，支持缩放和拖动，在中间格子显示宽和高
 */
public class CutFrame {
    private final String TAG = "CutFrame";
    /**
     * 最小剪切框的宽度
     */
    static final float MIN_WIDTH = Util.dp2Px(75);
    private final static int EXTENSE_LENGTH = Util.dp2Px(4);

    private final Rect totalBound;
    /**
     * 位置和宽高
     */
    private float mLeft, mTop, mWidth, mHeight;
    /**
     * frame的位置和宽高
     */
    float frameLeft, frameTop, frameWidth, frameHeight;

    /**
     * 裁剪框上各个小块的长宽
     */
    private float cornerWidth, edgeWidth;

    private boolean onTouch;
    private final Paint mLinePaint;
    private final Paint textPaint;
    private RectF rect;
    private final Paint mLumpPaint;
    private final Paint noChosedPaint;
    private int color;
    private float lastX = -1, lastY = -1;
    private MOVESTATE CUR_STATA;
    private CutView cutView;
    private Path edgeShape;
    float lastCenterX, lastCenterY;
    private int fixedWidth, fixedHeight;
    private float fixedRatio;

    boolean isFixedSize() {
        return fixedWidth>0;
    }

    int getFixedWidth() {
        return fixedWidth;
    }


    private enum MOVESTATE {
        LEFT_TOP,
        RIGHT_TOP,
        LEFT_BOTTOM,
        RIGHT_BOTTOM,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER,
        NONE
    }

    CutFrame(CutView cutView, Rect totalBound) {
        this.cutView = cutView;
        this.totalBound = totalBound;
        CUR_STATA = MOVESTATE.NONE;
        color = Util.getColor(R.color.cut_frame);
        color = Color.WHITE;
        frameLeft = mLeft = cutView.getDstRect().left;
        frameTop = mTop = cutView.getDstRect().top;
        frameWidth = mWidth = cutView.getDstRect().width() / 2;
        frameHeight = mHeight = cutView.getDstRect().height() / 2;

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
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
        edgeWidth = cornerWidth * 0.8f;


        rect = new RectF();
        edgeShape = new Path();

        fixedWidth = fixedHeight = -1;
    }

    /**
     * 初始化CutFrame,开始或重置时调用
     */
    void reInit() {
        frameLeft = cutView.getDstRect().left;
        frameTop = cutView.getDstRect().top;
        frameWidth = cutView.getDstRect().width();
        frameHeight = cutView.getDstRect().height();
    }

    /**
     * 只会处理移动的情况，其它情况它不处理
     */
    public boolean onTouchEvent(MotionEvent event) {
        float frameX = event.getX() - frameLeft, frameY = event.getY() - frameTop;
        float x = event.getX(), y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;

                //中间位置
                rect.set(frameWidth / 3, frameHeight / 3, frameWidth * 2 / 3, frameHeight * 2 / 3);
                rect.left -= EXTENSE_LENGTH;
                rect.right += EXTENSE_LENGTH;
                rect.top -= EXTENSE_LENGTH;
                rect.bottom += EXTENSE_LENGTH;
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATE.CENTER;
                    onTouch = true;
                    break;
                }

                //四个角
                // 左上角
                rect.set(-cornerWidth, -cornerWidth,
                        cornerWidth, 0 + cornerWidth);
                rect.left -= EXTENSE_LENGTH;
                rect.right += EXTENSE_LENGTH;
                rect.top -= EXTENSE_LENGTH;
                rect.bottom += EXTENSE_LENGTH;
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATE.LEFT_TOP;
                    onTouch = true;
                    break;
                }
                //右上
                rect.set(frameWidth - cornerWidth, -cornerWidth,
                        frameWidth + cornerWidth, 0 + cornerWidth);
                rect.left -= EXTENSE_LENGTH;
                rect.right += EXTENSE_LENGTH;
                rect.top -= EXTENSE_LENGTH;
                rect.bottom += EXTENSE_LENGTH;
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATE.RIGHT_TOP;
                    onTouch = true;
                    break;
                }
                //左下
                rect.set(-cornerWidth, frameHeight - cornerWidth,
                        cornerWidth, frameHeight + cornerWidth);
                rect.left -= EXTENSE_LENGTH;
                rect.right += EXTENSE_LENGTH;
                rect.top -= EXTENSE_LENGTH;
                rect.bottom += EXTENSE_LENGTH;
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATE.LEFT_BOTTOM;
                    onTouch = true;
                    break;
                }
                //右下
                rect.set(frameWidth - cornerWidth, frameHeight - cornerWidth,
                        frameWidth + cornerWidth, frameHeight + cornerWidth);
                rect.left -= EXTENSE_LENGTH;
                rect.right += EXTENSE_LENGTH;
                rect.top -= EXTENSE_LENGTH;
                rect.bottom += EXTENSE_LENGTH;
                if (rect.contains(frameX, frameY)) {
                    CUR_STATA = MOVESTATE.RIGHT_BOTTOM;
                    onTouch = true;
                    break;
                }

                //四边
                //四条边上,
                if (fixedRatio > 0) {//固定尺寸和比例不用点击边框，详见setFixedRatio方法

                    rect.left = -edgeWidth;
                    rect.right = edgeWidth;
                    rect.top = -edgeWidth * 3 / 4;
                    rect.bottom = edgeWidth * 3 / 4;
                    //上
                    rect.offset(frameWidth / 2, 0);
                    rect.left -= EXTENSE_LENGTH;
                    rect.right += EXTENSE_LENGTH;
                    rect.top -= EXTENSE_LENGTH;
                    rect.bottom += EXTENSE_LENGTH;
                    if (rect.contains(frameX, frameY)) {
                        CUR_STATA = MOVESTATE.TOP;
                        onTouch = true;
                        break;
                    }
                    //下
                    rect.offset(0, frameHeight);
                    //不扩大，前面以扩大
                    if (rect.contains(frameX, frameY)) {
                        CUR_STATA = MOVESTATE.BOTTOM;
                        onTouch = true;
                        break;
                    }

                    rect.left = -edgeWidth * 3 / 4;
                    rect.right = edgeWidth * 3 / 4;
                    rect.top = -edgeWidth;
                    rect.bottom = edgeWidth + 0;
                    //右边
                    rect.offset(frameWidth, frameHeight / 2);
                    rect.left -= EXTENSE_LENGTH;
                    rect.right += EXTENSE_LENGTH;
                    rect.top -= EXTENSE_LENGTH;
                    rect.bottom += EXTENSE_LENGTH;
                    if (rect.contains(frameX, frameY)) {
                        CUR_STATA = MOVESTATE.RIGHT;
                        onTouch = true;
                        break;
                    }
                    //左边
                    rect.offset(-frameWidth, 0);
                    rect.left -= EXTENSE_LENGTH;
                    rect.right += EXTENSE_LENGTH;
                    rect.top -= EXTENSE_LENGTH;
                    rect.bottom += EXTENSE_LENGTH;
                    if (rect.contains(frameX, frameY)) {
                        CUR_STATA = MOVESTATE.LEFT;
                        onTouch = true;
                        break;
                    }
                }
                onTouch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!onTouch) return false;
                if (CUR_STATA == MOVESTATE.NONE)
                    onTouch = false;
                float dx = x - lastX, dy = y - lastY;
                if (CUR_STATA == MOVESTATE.CENTER) {//移动整个切图框
                    move(dx, dy);
                } else {
                    if (fixedRatio > 0) {//固定缩放比的情况，取移动的短边进行变化
                        if (dx < dy) {
                            dy = dx * fixedRatio;
                        } else {
                            dx = dy / fixedRatio;
                        }
                    }
                    switch (CUR_STATA) {
                        //四个角
                        case LEFT_TOP:
                            if (frameWidth - dx >= MIN_WIDTH) {
                                frameLeft += dx;
                                frameWidth -= dx;//左边加多少，宽度减多少
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            if (frameHeight - dy >= MIN_WIDTH) {
                                frameTop += dy;
                                frameHeight -= dy;//上边加多少，高度减多少
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            break;
                        case RIGHT_TOP:
                            if (frameWidth + dx >= MIN_WIDTH) {
                                frameWidth += dx;//右边增加，及宽度增加
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            if (frameHeight - dy >= MIN_WIDTH) {
                                frameTop += dy;
                                frameHeight -= dy;//上边加多少，高度减多少
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            break;
                        case LEFT_BOTTOM:
                            if (frameWidth - dx >= MIN_WIDTH) {
                                frameLeft += dx;
                                frameWidth -= dx;//左边加多少，宽度减多少
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            if (frameHeight + dy >= MIN_WIDTH) {
                                frameHeight += dy;
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            break;
                        case RIGHT_BOTTOM:
                            if (frameWidth + dx >= MIN_WIDTH) {
                                frameWidth += dx;//右边增加，及宽度增加
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            if (frameHeight + dy >= MIN_WIDTH) {
                                frameHeight += dy;
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            break;

                        //四条边
                        case LEFT:
                            if (frameWidth - dx >= MIN_WIDTH) {
                                frameLeft += dx;
                                frameWidth -= dx;//左边加多少，宽度减多少
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            break;
                        case RIGHT:
                            if (frameWidth + dx >= MIN_WIDTH) {
                                frameWidth += dx;//右边增加，及宽度增加
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            break;
                        case TOP:
                            if (frameHeight - dy >= MIN_WIDTH) {
                                frameTop += dy;
                                frameHeight -= dy;//上边加多少，高度减多少
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            break;
                        case BOTTOM:
                            if (frameHeight + dy >= MIN_WIDTH) {
                                frameHeight += dy;
                                adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
                                cutView.invalidate();
                            }
                            break;
                    }
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (!onTouch) return false;
                //为CutView适配做准备
                lastCenterX = frameX + frameWidth / 2;
                lastCenterY = frameY + frameHeight / 2;
                autoScalePic(CUR_STATA);
                CUR_STATA = MOVESTATE.NONE;
                onTouch = false;
                break;
        }
        return onTouch;
    }

    void move(float dx, float dy) {
        //判断边界
        if (frameLeft + dx < cutView.getDstRect().left) frameLeft = cutView.getDstRect().left;
        else if (frameLeft + dx + frameWidth > cutView.getDstRect().right)
            frameLeft = cutView.getDstRect().right - frameWidth;
        else frameLeft += dx;

        if (frameTop + dy < cutView.getDstRect().top) frameTop = cutView.getDstRect().top;
        else if (frameTop + dy + frameHeight > cutView.getDstRect().bottom)
            frameTop = cutView.getDstRect().bottom - frameHeight;
        else
            frameTop += dy;
        adjustEdge(frameLeft, frameTop, frameWidth, frameHeight);
        cutView.invalidate();
    }

    private void autoScalePic(MOVESTATE CUR_STATE) {
        float ratio = 1;
        //小于1/3或者大于2/3，自动缩放
        if (frameWidth < totalBound.width() * 1f / 3 && frameHeight < totalBound.height() * 1f / 3) {
            ratio = Math.min(totalBound.width() * 1f / 2 / frameWidth,
                    totalBound.height() * 1f / 2 / frameHeight);
        } else if (frameWidth > totalBound.width() * 2 / 3 || frameHeight > totalBound.height() * 2f / 3) {
            ratio = Math.min(totalBound.width() * 1f / 2 / frameWidth,
                    totalBound.height() * 1f / 2 / frameHeight);
        } else return;

        float centerX = frameLeft + frameWidth / 2;
        float centerY = frameTop + frameHeight / 2;
        ratio = adjustRatio(ratio);
        String[] pxy = cutView.getLocationAtPicture(centerX, centerY);
        cutView.scale(centerX, centerY, ratio);
        scale(centerX, centerY, ratio);
        cutView.attemptMoveFrame(pxy, centerX, centerY);
    }

    /**
     * 负责缩放，适配边界
     */
    public void scale(float cx, float cy, float ratio) {
        float[] xy = new float[2];
        GeoUtil.getScaledCoord(xy, cx, cy, frameLeft, frameTop, ratio);
        adjustEdge(xy[0], xy[1], frameWidth * ratio, frameHeight * ratio);
    }

    /**
     * 缩放时取的能缩放的最大/最小倍数
     *
     * @param ratio 初始倍数
     * @return 矫正后的倍数
     */
    private float adjustRatio(float ratio) {
        ratio = cutView.getUsableScaleSize(ratio);
        if (ratio * frameWidth <= MIN_WIDTH) {
            ratio = MIN_WIDTH / frameWidth;
        }
        if (ratio * frameHeight <= MIN_WIDTH) {
            ratio = MIN_WIDTH / frameHeight;
        }
        return ratio;
    }

    /**
     * 适配并设置好位置和长宽
     */
    private void adjustEdge(float nframeLeft, float nframeTop, float nframeWidth, float nframeHeight) {
        RectF inRect = new RectF(nframeLeft, nframeTop, nframeLeft + nframeWidth, nframeTop + nframeHeight);
        adjustRectInRect(inRect, cutView.getDstRect());
        frameLeft = inRect.left;
        frameTop = inRect.top;
        frameWidth = inRect.width();
        frameHeight = inRect.height();
        if (fixedRatio > 0) {//固定长宽比的情况,必须把长宽调整到相应的比例
            if (frameWidth * fixedRatio > frameHeight) {//以短边为准进行调整
                frameWidth = frameHeight / fixedRatio;
            } else {
                frameHeight = frameWidth * fixedRatio;
            }
        }
    }

    private void adjustRectInRect(RectF inRect, Rect edgeRect) {
        if (inRect.left < edgeRect.left)
            inRect.left = edgeRect.left;

        if (inRect.top < edgeRect.top)
            inRect.top = edgeRect.top;

        if (inRect.right > edgeRect.right)
            inRect.right = edgeRect.right;

        if (inRect.bottom > edgeRect.bottom)
            inRect.bottom = edgeRect.bottom;
    }


    /**
     * 直接传入总的canvas，里面进行移动再绘图
     */
    public void onDraw(Canvas canvas) {
        Log.e(TAG, "draw开始 ");

        //暗色未选中背景
        canvas.drawRect(0, 0, canvas.getWidth(), frameTop, noChosedPaint);//上下
        canvas.drawRect(0, frameTop + frameHeight, canvas.getWidth(), canvas.getHeight(), noChosedPaint);
        canvas.drawRect(0, frameTop, frameLeft, frameTop + frameHeight, noChosedPaint);//左右
        canvas.drawRect(frameLeft + frameWidth, frameTop, canvas.getWidth(), frameTop + frameHeight, noChosedPaint);

        canvas.translate(frameLeft, frameTop);
        drawFrame(canvas);
        canvas.restore();
    }

    //画出剪切框
    private void drawFrame(Canvas canvas) {
        for (int i = 0; i <= 3; i++) {//格子线
            if (i == 1 || i == 2) mLinePaint.setStrokeWidth(Util.dp2Px(0.5f));
            else mLinePaint.setStrokeWidth(Util.dp2Px(1.5f));
            canvas.drawLine(0 - 1.5f, frameHeight * i / 3, frameWidth + 1.5f, frameHeight * i / 3, mLinePaint);
            canvas.drawLine(frameWidth * i / 3, 0, frameWidth * i / 3, frameHeight, mLinePaint);
        }

        //画四个角
        //左上
        canvas.drawCircle(0, 0, cornerWidth * 3 / 4, mLumpPaint);
        //右上
        canvas.drawCircle(frameWidth, 0, cornerWidth * 3 / 4, mLumpPaint);
        //左下
        canvas.drawCircle(0, frameHeight, cornerWidth * 3 / 4, mLumpPaint);
        //右下
        canvas.drawCircle(frameWidth, frameHeight, cornerWidth * 3 / 4, mLumpPaint);


        //四条边上
        //上
        edgeShape.rewind();
        edgeShape.moveTo(-edgeWidth * 1.2f, 0);
        edgeShape.lineTo(0, -edgeWidth / 2);
        edgeShape.lineTo(edgeWidth * 1.2f, 0);
        edgeShape.lineTo(0, edgeWidth / 2);
        edgeShape.offset(frameWidth / 2, 0);
        canvas.drawPath(edgeShape, mLumpPaint);

        //下
        edgeShape.rewind();
        edgeShape.moveTo(-edgeWidth * 1.2f, 0);
        edgeShape.lineTo(0, edgeWidth / 2);
        edgeShape.lineTo(edgeWidth * 1.2f, 0);
        edgeShape.lineTo(0, -edgeWidth / 2);
        edgeShape.offset(frameWidth / 2, frameHeight);
        canvas.drawPath(edgeShape, mLumpPaint);

        //左边
        edgeShape.rewind();
        edgeShape.moveTo(0, -edgeWidth * 1.2f);
        edgeShape.lineTo(-edgeWidth / 2, 0);
        edgeShape.lineTo(0, edgeWidth * 1.2f);
        edgeShape.lineTo(edgeWidth / 2, 0);
        edgeShape.offset(0, frameHeight / 2);
        canvas.drawPath(edgeShape, mLumpPaint);

        //右边
        edgeShape.rewind();
        edgeShape.moveTo(0, -edgeWidth * 1f);
        edgeShape.lineTo(edgeWidth / 2, 0);
        edgeShape.lineTo(0, edgeWidth * 1f);
        edgeShape.lineTo(-edgeWidth / 2, 0);
        edgeShape.offset(frameWidth, frameHeight / 2);
        canvas.drawPath(edgeShape, mLumpPaint);

        //中间的数字
        String show = String.format(Locale.getDefault(), "%d × %d",
                Math.round(frameWidth / cutView.getTotalRatio()),
                Math.round(frameHeight / cutView.getTotalRatio()));
        float textWidth = textPaint.measureText(show);
        Util.P.le(TAG, "文字宽度： " + textWidth);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = frameHeight / 2 - fm.descent + (fm.bottom - fm.top) / 2;
        canvas.drawText(show, (frameWidth - textWidth) / 2, textY, textPaint);

        Util.P.le(TAG, "drawFrame完成");
    }

    /********************************************************************************/
    /**
     * 固定比例，让裁剪框满足最大的塞入到显示出的图片之中
     */

    void setFixedRatio(float fixedRatio) {
        Rect dstRect = cutView.getDstRect();
        this.fixedRatio = fixedRatio;
        float dstRatio = dstRect.height() * 1f / dstRect.width();

        if (fixedRatio > dstRatio) {//高宽比图片显示部分高，水平居中
            frameHeight = dstRect.height();
            frameWidth = (int) (frameHeight * fixedRatio + 0.5f);
            frameTop = dstRect.top;
            frameLeft = dstRect.left + (dstRect.width() - frameWidth) / 2;
        } else {//否则垂直居中
            frameWidth = dstRect.width();
            frameHeight = (int) (frameWidth / fixedRatio + 0.5f);
            frameLeft = dstRect.left;
            frameTop = dstRect.top + (dstRect.height() - frameHeight) / 2;
        }
    }

    void cancelFixedRatio() {
        this.fixedRatio = -1;
    }


    /**
     * 固定尺寸,用户操作上相当于固定比例，最后获取图片时缩放即可
     */
    public void setFixedSize(int width, int height, Rect picBound) {
        fixedWidth = width;
        fixedHeight = height;
        setFixedRatio(height * 1f / width);//用户操作上相当于固定比例，
    }

    public void cancelFixedSzie() {
        fixedWidth = fixedHeight = -1;
        cancelFixedRatio();
    }

    /******************************************************************************/ {
        /**  @Deprecated public void scale(float centerX, float centerY, float ratio, Rect dstRect) {
        float minRatio = Math.max(MIN_WIDTH / mWidth, MIN_WIDTH / mHeight);//最小的缩放比例
        ratio = Math.max(minRatio, ratio);

        float maxRatio = Math.min(dstRect.width() / mWidth, dstRect.height() / mHeight);//最大的
        ratio = Math.min(maxRatio, ratio);

        mWidth *= ratio;
        mHeight *= ratio;
        mLeft = GeoUtil.getScaledX(mLeft, mTop, centerX, centerY, ratio);
        mTop = GeoUtil.getScaledY(mLeft, mTop, centerX, centerY, ratio);

        if (mLeft < dstRect.left) mLeft = dstRect.left;//放大时超出外边界，缩小时超出内边界，上面处理
        if (mTop < dstRect.top) mTop = dstRect.top;
        }
         */
    }
}
