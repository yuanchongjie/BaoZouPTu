
package a.baozouptu.ptu.cut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import a.baozouptu.R;
import a.baozouptu.common.util.GeoUtil;
import a.baozouptu.common.util.MU;
import a.baozouptu.common.util.Util;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.view.PtuSeeView;
import a.baozouptu.ptu.view.TSRView;

/**
 * Created by liuguicen on 2016/8/5.
 */

public class CutView extends PtuSeeView implements TSRView {
    private final Context mContext;
    private final String TAG = "CutView";
    private Paint clearPaint;
    private Paint bmPaint;
    private Rect totalBound;
    //边界相关
    private GeoUtil.UnLevelRect mTotalBound;
    //原图片的各个参数
    private float sourceDx;
    private float sourceDy;
    private float sourceLastAngle;
    private float sourceTotalAngle;
    private float sourceRatio;
    private GeoUtil.UnLevelRect sourceBigBound;//旋转框的范围
    CutFrame frame;

    //   private final CutFrame frame;

    public CutView(Context context, Rect totalBound, Bitmap sourceBitmap) {
        super(context, sourceBitmap, totalBound);
        mContext = context;
        setBackgroundColor(Util.getColor(R.color.gray_light));
        MAX_RATIO *= 2;
        this.totalBound = new Rect(totalBound);
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        bmPaint = new Paint();
        bmPaint.setAntiAlias(true);
        bmPaint.setDither(true);

        //父类构造函数上一步dstRect已经获取到，表示图片的范围
        frame = new CutFrame(this, totalBound);//dstRect在父视图中
        sourceDx = sourceDy = 0;
        sourceRatio = 1;
        sourceLastAngle = sourceTotalAngle = 0;
        Log.e(TAG, "初始化完成");


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (frame.onTouchEvent(event)) return true;//是否交给Frame处理
        float x = event.getX(), y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                Util.P.le(TAG, "经过了down");
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                lastDis = GeoUtil.getDis(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                return true;
            case MotionEvent.ACTION_MOVE:

                if (event.getPointerCount() == 1) {
                    if (lastX == -10000) return true;
                    move(event.getX(), event.getY());
                } else {
                    //缩放
                    float endD = GeoUtil.getDis(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    float currentRatio = endD / lastDis;
                    lastDis = endD;

                    scale(frame.frameLeft + frame.frameWidth / 2, frame.frameTop + frame.frameHeight / 2, currentRatio);
                    if (currentRatio < 1)//缩小要变化，放大步需要
                    {
                        attemptScaleFrame();
                        attemptMoveFrame();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //剩下一个手指时，获取该手指的位置，后面跟着能进行移动
                if (event.getPointerCount() == 2) {
                    lastX = -10000;
                    lastY = -10000;
                }
                if (event.getPointerCount() == 3) {
                    int index = event.getActionIndex();

                    int i0, i1;
                    if (index == 0) {
                        i0 = 1;
                        i1 = 2;
                    } else if (index == 1) {
                        i0 = 0;
                        i1 = 2;
                    } else {
                        i0 = 0;
                        i1 = 1;
                    }
                    //缩放
                    lastDis = GeoUtil.getDis(event.getX(i0), event.getY(i0), event.getX(i1), event.getY(i1));
                }
            default:
                break;
        }
        return true;
    }

    /**
     * 调整边框，需要缩放就缩放，让它在范围之内
     */
    public void attemptScaleFrame() {
        if (dstRect.width() < frame.frameWidth || dstRect.height() < frame.frameHeight) {//缩放之后比边框小了
            float ratio = Math.min(dstRect.width() * 1f / frame.frameWidth, dstRect.height() * 1f / frame.frameHeight);
            frame.scale(frame.lastCenterX, frame.lastCenterY, ratio);
        }
    }

    /**
     * 调整边框，需要移动就移动，让它在范围之内
     */
    public void attemptMoveFrame() {
        float dx = 0, dy = 0;
        if (dstRect.left > frame.frameLeft)
            dx = dstRect.left - frame.frameLeft;
        else if (dstRect.right < frame.frameLeft + frame.frameWidth)
            dx = dstRect.right - (frame.frameLeft + frame.frameWidth);
        if (dstRect.top > frame.frameTop)
            dy = dstRect.top - frame.frameTop;
        else if (dstRect.bottom < frame.frameTop + frame.frameHeight)
            dy = dstRect.bottom - (frame.frameTop + frame.frameHeight);
        if (Math.abs(dx) > 0.5 || Math.abs(dy) > 0.5)
            frame.move(dx, dy);
    }

    /**
     * 调整边框，需要移动就移动，让它在范围之内
     * 在图片中的中心，和View中的中心
     */
    public void attemptMoveFrame(String[] pxy, float centerX, float centerY) {
        float x = Float.valueOf(MU.add(picLeft * 1f, MU.mu(pxy[0], totalRatio)));
        float y = Float.valueOf(MU.add(picTop * 1f, MU.mu(pxy[1], totalRatio)));
        if (Math.abs(x - centerX) > 1 || Math.abs(y - centerY) > 1) {//如果位置偏移，则移动，但必须在边框内部
            frame.move(x - centerX, y - centerY);
        }
    }

    private float getAngle(float x, float y, float x1, float y1) {
        float dx = x1 - x, dy = y1 - y;
        double angle = Math.atan2(dy, dx);
        return (float) Math.toDegrees(angle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        frame.onDraw(canvas);
        Log.e(TAG,"draw完成");
    }

    /**
     * 自动缩放时控制比例，不能太大或太小
     * @param ratio 期望的比例
     * @return 实际能缩放的比例
     */
    public float getUsableScaleSize(float ratio) {
        if (totalRatio * ratio <= initRatio) //最小
            ratio = initRatio / totalRatio;
        if (totalRatio * ratio > MAX_RATIO)
            ratio = MAX_RATIO / totalRatio;//总的缩放比例超出了最大范围
        return ratio;
    }

    public String[] getLocationAtPicture(float centerX, float centerY) {
        return PtuUtil.getLocationAtPicture(String.valueOf(centerX), String.valueOf(centerY),
                srcRect, dstRect);
    }

    public Bitmap getResultBm() {
        //原图
        if (srcRect.width() == sourceBitmap.getWidth() && srcRect.height() == sourceBitmap.getHeight() &&
                frame.frameWidth >= dstRect.width() - 1 && frame.frameHeight >= dstRect.height() - 1
                &&!frame.isFixedSize()) {
            return sourceBitmap.copy(sourceBitmap.getConfig(),true);
        } else {
            int left = Math.round((frame.frameLeft - picLeft) / totalRatio);
            int top = Math.round((frame.frameTop - picTop) / totalRatio);
            int rw = Math.round(frame.frameWidth / totalRatio);
            int rh = Math.round(frame.frameHeight / totalRatio);
            Matrix matrix=new Matrix();
            if(frame.isFixedSize()){
                matrix.postScale(frame.getFixedWidth()*1f/rw,frame.getFixedWidth()*1f/rw);
            }
            if(left<0)left=0;
            else if(top<0)top=0;
            if(left+rw>sourceBitmap.getWidth())rw=sourceBitmap.getWidth()-left;
            if(top+rh>sourceBitmap.getHeight())rh=sourceBitmap.getHeight()-top;
            return Bitmap.createBitmap(sourceBitmap, left, top, rw, rh,matrix,true);
        }
    }

    @Override
    public void releaseResource() {

    }

    /**
     * 比较常用的方法，将图片还原到开始的位置,情况，即长边与父布局长边对齐
     * <p>基本参数还原到初始化状态,可用于撤销重做等
     */
    public void resetShow() {
        totalRatio = initRatio;
        curPicWidth = Math.round(srcPicWidth * totalRatio);
        curPicHeight = Math.round(srcPicHeight * totalRatio);
        picLeft = (totalBound.width() - curPicWidth) / 2;
        picTop = (totalBound.height() - curPicHeight) / 2;
        getConvertParameter(curPicWidth, curPicHeight, picLeft, picTop);
        frame.reInit();
        invalidate();
    }

    @Override
    public void rotate(float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap dstbmp = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight(),
                matrix, true);
        replaceSourceBm(dstbmp);
        frame.reInit();
        invalidate();
    }

    public void reverse(int i) {
        Matrix matrix = new Matrix();
        if (i == 0) {
            matrix.postScale(1, -1);
        } else if (i == 1) {
            matrix.postScale(-1, 1);
        }
        Bitmap dstbmp = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight(),
                matrix, true);
        replaceSourceBm(dstbmp);
        frame.reInit();
        invalidate();
    }

    public void setFixedRatio(float fixedRatio) {
        frame.cancelFixedSize();//先取消其他的状态
        frame.setFixedRatio(fixedRatio);
        invalidate();
    }

    public void setFixedSize(int width, int height) {
        frame.cancelFixedRatio();//先取消其他的状态
        frame.setFixedSize(width,height);
    }

    {
        /**
         * 以CutFrame为边界，不能超出它的边界
         * 在不超过边界情况下设置好位置
         * 如果缩放，要在adjustSize后面调用
         *
         * @param dx 移动距离
         * @param dy 移动距离

        public void adjustEdge(float dx, float dy) {
        if (picLeft + dx > frame.frameLeft)//左
        {
        dx = frame.frameLeft - picLeft;
        }
        if (picTop + dy > frame.frameTop)//上
        {
        dy = frame.frameTop - picTop;
        }
        if (picLeft + dx + curPicWidth < frame.frameLeft + frame.frameWidth) {
        dx = Math.round(frame.frameLeft + frame.frameWidth - picLeft - curPicWidth);
        }
        if (picTop + dy + curPicHeight < frame.frameTop + frame.frameHeight) {
        dy = Math.round(frame.frameTop + frame.frameHeight - picTop - curPicHeight);
        }
        picLeft += dx;
        picTop += dy;
        }
        /**
        本版暂不实现，先使用父类PtuView的缩放方式
         @Deprecated public void move(float curX, float curY) {
         adjustEdge(curX - lastX, curY - lastY);
         lastX = curX;
         lastY = curY;
         getConvertParameter(curPicWidth, curPicHeight, picLeft, picTop);
         invalidate();
         }*/

        /**
         * 旋转没有关系,旋转的时候将旋转的图放到source的框中，
         */

        /**
         本版暂不实现，先使用父类PtuView的缩放方式
         @Deprecated public void scale(float currentRatio, float centerX, float centerY) {
         totalRatio *= currentRatio;

         //高精度的计算缩放后坐标
         float[] xy = new float[2];
         GeoUtil.getScaledCoord(xy, centerX, centerY, picLeft, picTop, currentRatio);
         float x = xy[0], y = xy[1];

         curPicWidth = Math.round(curPicWidth * currentRatio);
         curPicHeight = Math.round(curPicHeight * currentRatio);
         CURRENT_STATUS = STATUS_SCALE;
         //图片的边界不能在CutFrame的内部
         if (currentRatio > 1) {//放大，不用处理边界问题

         } else {
         if (x > frame.frameLeft)//左边界
         {
         x = frame.frameLeft;
         }
         //            右边界
         if (x + curPicWidth < frame.frameLeft + frame.frameWidth) {
         x = frame.frameLeft + frame.frameWidth - curPicWidth;
         }
         //                    上边界
         if (y > frame.frameTop) {
         y = frame.frameTop;
         }
         //                    下边界
         if (y + curPicHeight < frame.frameTop + frame.frameHeight) {
         y = frame.frameTop + frame.frameHeight - curPicHeight;
         }

         }
         picLeft = Math.round(x);
         picTop = Math.round(y);
         getConvertParameter(curPicWidth, curPicHeight, picLeft, picTop);
         invalidate();
         }
         */
    }

}
