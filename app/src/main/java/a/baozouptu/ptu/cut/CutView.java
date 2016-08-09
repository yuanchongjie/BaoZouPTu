package a.baozouptu.ptu.cut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;

import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.view.PtuView;
import a.baozouptu.ptu.view.TSRView;

/**
 * Created by liuguicen on 2016/8/5.
 *
 * @description
 */
public class CutView extends PtuView implements TSRView {
    private final Context mContext;
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

    private boolean intercept = false;

    private final CutFrameView cutFrameView;

    public CutView(Context context, Bitmap sourceBitmap, Rect totalBound) {
        super(context);
        mContext = context;
        this.totalBound = new Rect(totalBound);
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        bmPaint = new Paint();
        bmPaint.setAntiAlias(true);
        bmPaint.setDither(true);

        super.setBitmapAndInit(sourceBitmap, totalBound.width(), totalBound.height());
        initialDraw();
        cutFrameView = new CutFrameView(dstRect);

        sourceDx = sourceDy = 0;
        sourceRatio = 1;
        sourceLastAngle = sourceTotalAngle = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX(), y = event.getY();
                if (cutFrameView.isInCornerLump(x, y)) {
                    intercept = true;
                } else if (cutFrameView.isInEdgeLine(x, y)) {
                    intercept = true;

                } else if (cutFrameView.isInCenterLump(x, y)) {
                    intercept = true;

                } else {
                    intercept = false;
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //旋转
                sourceLastAngle = getAngle(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    //旋转
                    float curAngle = getAngle(event.getX(0), event.getY(0),
                            event.getX(1), event.getY(1));
                    sourceTotalAngle += (curAngle - sourceLastAngle);
                    sourceLastAngle = curAngle;
                    rotate(sourceTotalAngle);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            default:
                break;
        }
        if (!intercept)
            super.onTouchEvent(event);
        return true;
    }


    /**
     * 旋转没有关系,旋转的时候将旋转的图放到source的框中，
     */
    @Override
    public void rotate(float angle) {
        sourceTotalAngle += angle;
        //清除原图中的像素
        sourceCanvas.drawPaint(clearPaint);
        //旋转canvas，然后把原图画上去，就相当于旋转了
        sourceCanvas.save();
        sourceCanvas.rotate(sourceTotalAngle);
        sourceCanvas.drawBitmap(getBaseBitmap(), 0, 0, bmPaint);
        sourceCanvas.restore();
    }

    private Bitmap getBaseBitmap() {
        return RepealRedoManager.getInstanceTotal(-1).getBaseBitmap();
    }


    private float getAngle(float x, float y, float x1, float y1) {
        float dx = x1 - x, dy = y1 - y;
        double angle = Math.atan2(dy, dx);
        return (float) Math.toDegrees(angle);
    }

    public void reset() {
    }
}
