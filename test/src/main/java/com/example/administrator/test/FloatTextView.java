package com.example.administrator.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/5/29.
 */
public class FloatTextView extends TextView {
    /**
     * view的宽和高
     */
    int currentWidth = 200, currentHeight = 200;
    /**
     * view的缩放中心坐标
     */
    int scalCenterX, scalCenterY;
    /**
     * 顶点的x，y坐标
     */
    int startX, startY;
    /**
     * 最近一次用于缩放两手指间的距离
     */
    private float lastDis;

    /**
     * 中的缩放比例，其它的是辅助，放大时直接需要就是一个totalRatio
     */
    private float totalRatio = 1f;
    /**
     * 当前的缩放比例
     */
    private float currentRatio = 1f;
    /**
     * context
     */
    private Context context;
    /**
     * 用于处理图片的矩阵
     */
    private Matrix matrix = new Matrix();
    /**
     * 整个View的宽
     */
    private int totalWidth;
    /**
     * 整个View的高
     */
    private int totalHeight;

    /**
     * 当前的操作状态
     */
    private static int CURRENT_STATUS = 0;
    private static final int STATUS_TRANSLATE = 1;
    private static final int STATUS_SCALE = 2;
    private static final int STATUS_ROTATE = 3;
    private final Context mContext;

    private class TRSOPERATION {
        /**
         * @param scaleRatio 缩放的比例
         */
        public void scale(float scaleRatio) {
            currentWidth *= scaleRatio;
            currentHeight *= scaleRatio;
            setScaledCoord(startX, startY, scalCenterX, scalCenterY, scaleRatio);
        }

        /**
         * 利用matrix进行缩放
         *
         * @param tstartX
         * @param tstartY
         * @param scaleCenterX
         * @param scaleCenterY
         * @param scale
         */
        private void setScaledCoord(float tstartX, float tstartY, float scaleCenterX, float scaleCenterY, float scale) {
            Matrix matrix = new Matrix();
            // 将Matrix移到到当前圆所在的位置，
            // 然后再以某个点为中心进行缩放
            matrix.preTranslate(tstartX, tstartY);
            matrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
            float[] values = new float[9];
            matrix.getValues(values);
            startX = (int) values[Matrix.MTRANS_X];
            startY = (int) values[Matrix.MTRANS_Y];
            CURRENT_STATUS = STATUS_SCALE;
            invalidate();
        }

        /**
         * 移动
         */
        public void translate(int tx, int ty) {
            startX += tx;
            startY += ty;
            CURRENT_STATUS = STATUS_TRANSLATE;
            invalidate();
        }

        /**
         * 绕自己的中心旋转
         */
        public void rotateByCenter(int angle) {
            CURRENT_STATUS = STATUS_ROTATE;
        }
    }

    TRSOPERATION trs = new TRSOPERATION();

    public FloatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    public FloatTextView(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void setHeight(int pixels) {
        currentHeight = pixels;
        super.setHeight(pixels);
    }

    @Override
    public void setWidth(int pixels) {
        currentWidth = pixels;
        super.setWidth(pixels);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        totalWidth = w;
        totalHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void scale(float scaleRatio) {
        trs.scale(scaleRatio);
    }

    public void rotateByCenter(int angle) {
        trs.rotateByCenter(angle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(200, 200);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Util.P.le(getClass(),"ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Util.P.le(getClass(),"ACTION_DOWN");
            case MotionEvent.ACTION_UP:
                Util.P.le(getClass(),"ACTION_UP");
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.setBitmap();
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getWidth();
        canvas.drawColor(Color.RED);
        super.onDraw(canvas);
    }
}
