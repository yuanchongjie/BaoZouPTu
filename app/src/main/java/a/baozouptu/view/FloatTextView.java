package a.baozouptu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import a.baozouptu.tools.Util;

/**
 * Created by Administrator on 2016/5/29.
 */
public class FloatTextView extends TextView {
    /**
     * view的宽和高
     */
    int mWidth, mHeight;
    /**
     * view的缩放中心坐标
     */
    int scalCenterX, scalCenterY;
    /**
     * 顶点的x，y坐标
     */
    int startX, startY;

    /**
     * 当前的操作状态
     */
    private static int CURRENT_STATUS = 0;
    private static final int STATUS_TRANSLATE = 1;
    private static final int STATUS_SCALE = 2;
    private static final int STATUS_ROTATE = 3;

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void scale(float scaleRatio) {
        trs.scale(scaleRatio);
    }
    public void rotateByCenter(int angle) {
        trs.rotateByCenter(angle);
    }
    public FloatTextView(Context context){
        super(context);
    }
    public FloatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);
        super.onDraw(canvas);
    }
}
