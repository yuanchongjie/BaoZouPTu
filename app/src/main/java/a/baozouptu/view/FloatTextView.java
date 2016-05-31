package a.baozouptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import a.baozouptu.tools.UnlevelRect;

/**
 * Created by Administrator on 2016/5/29.
 */
public class FloatTextView extends TextView {
    /**
     * view的宽和高
     */
    public int mWidth = 300, mHeight = 300;
    /**
     * view的缩放中心坐标
     */
    public int scalCenterX, scalCenterY;
    /**
     * 顶点的x，y坐标
     */
    public int startX, startY;
    /**
     * 移动的顶点最后的位置
     */
    public float lastX, lastY;
    /**
     * 当前的操作状态
     */
    private static int CURRENT_STATUS = 0;
    private static final int STATUS_TRANSLATE = 1;
    private static final int STATUS_SCALE = 2;
    private static final int STATUS_ROTATE = 3;
    private Context mContext;

    public Bitmap getBitmap() {
        return bitmapToView;
    }

    public float getLastY() {
        return lastY;
    }

    public float getLastX() {
        return lastX;
    }

    public int getStartY() {
        return startY;
    }

    public int getStartX() {
        return startX;
    }

    public int getmHeight() {
        return mHeight;
    }

    public int getmWidth() {
        return mWidth;
    }

    public int getScalCenterY() {
        return scalCenterY;
    }

    public int getScalCenterX() {
        return scalCenterX;
    }

    /**
     * 含有view内容的bitmap
     */
    public Bitmap bitmapToView;
    Canvas secondCanvas;
    UnlevelRect realRect;
    Rect totalRect;

    public FloatTextView(Context context) {
        super(context);
        mContext = context;
    }

    public FloatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public FloatTextView(Context context, int width, int height, int startX, int startY) {
        super(context);
        mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        this.startX = startX;
        this.startY = startY;

    }

    /**
     * 初始化数据
     */
    private void init() {
        realRect = new UnlevelRect(startX, startY, startX + mWidth, startY + mHeight);
        totalRect = new Rect(startX, startY, startX + mWidth, startY + mHeight);
        bitmapToView = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        secondCanvas = new Canvas(bitmapToView);
        Paint textPaint=new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(20);
        secondCanvas.drawColor(Color.RED);
        secondCanvas.drawText("默认文字",0,20,textPaint);
    }

    public FloatTextView(Context context, float totalWidth, float totalHeight) {
        super(context);
        startX = (int) (totalWidth - mWidth) / 2;
        startY = (int) (totalHeight - mHeight) / 2;
        init();
    }

    private class TRSOPERATION {
        /**
         * @param scaleRatio 缩放的比例
         */
        public void scale(float scaleRatio) {
            mWidth *= scaleRatio;
            mHeight *= scaleRatio;
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

    public void scale(float scaleRatio) {
        trs.scale(scaleRatio);
    }

    public void rotateByCenter(int angle) {
        trs.rotateByCenter(angle);
    }

}
