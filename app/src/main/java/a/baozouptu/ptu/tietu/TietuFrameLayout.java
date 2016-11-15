package a.baozouptu.ptu.tietu;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.view.PtuView;

/**
 * Created by liuguicen on 2016/10/2.
 *
 * @description 放置tietu的图片的FrameLayout，用于支持多个贴图在同一界面下的
 * 移动，缩放，旋转操作
 */
public class TietuFrameLayout extends FrameLayout {
    private static final String TAG = "TietuFrameLayout";
    FloatImageView chosedView;
    private float lastX = -1;
    private float lastY = -1;
    private float lastDis = 0;
    private float lastAngle = 0;
    TouchEventProcessor touchPro;
    private TietuChangeListener tietuChangeListener;

    public interface TietuChangeListener {
        void onTietuRemove(FloatImageView view);
    }

    public void setOnTietuRemoveListener(TietuChangeListener tietuChangeListener) {
        this.tietuChangeListener = tietuChangeListener;
    }

    public TietuFrameLayout(Context context) {
        super(context);
        init();
    }


    public TietuFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        touchPro = new TouchEventProcessor(this);
    }

    public void addView(FloatImageView child, FrameLayout.LayoutParams params) {
        if (chosedView != null)
            chosedView.setShowRim(false);
        chosedView = child;
        super.addView(child, params);
    }

    @Override
    public void removeView(View view) {
        if (chosedView != null && chosedView.equals(view))
            chosedView = null;
        super.removeView(view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        float x0 = event.getX(0), y0 = event.getY(0);
        float x1 = 0, y1 = 0;
        if (event.getPointerCount() > 1) {
            x1 = event.getX(1);
            y1 = event.getY(1);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                Util.DoubleClick.isDoubleClick();
                Util.P.le(TAG, "经过了down");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //缩放
                Util.DoubleClick.cancel();//点击事件取消
                lastDis = GeoUtil.getDis(x0, y0, x1, y1);

                //旋转
                lastAngle = getAngle(x0, y0, x1, y1);
                //移动
                lastX = (x0 + x1) / 2;
                lastY = (y0 + y1) / 2;
            case MotionEvent.ACTION_MOVE:
                Util.DoubleClick.cancel();
                //移动
                if (event.getPointerCount() == 1) {
                    moveTietu(chosedView, x - lastX, y - lastY);
                    lastX = x;
                    lastY = y;
                } else {
                    //缩放
                    float endD = GeoUtil.getDis(x0, y0, x1, y1);
                    float currentRatio = endD / lastDis;
                    lastDis = endD;
                    scale(chosedView, currentRatio);
                    //旋转
                    float curAngle = getAngle(x0, y0,
                            x1, y1);
                    if (chosedView != null)
                        chosedView.setRotation(chosedView.getRotation() + curAngle - lastAngle);
                    lastAngle = curAngle;
                    //移动
                    float nx = (x0 + x1) / 2, ny = (y0 + y1) / 2;
                    moveTietu(chosedView, nx - lastX, ny - lastY);
                    lastX = nx;
                    lastY = ny;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //剩下一个手指时，获取该手指的位置，后面跟着能进行移动
                int index = event.getActionIndex();

                if (event.getPointerCount() == 2) {
                    lastX = event.getX(index == 0 ? 1 : 0);
                    lastY = event.getY(index == 0 ? 1 : 0);
                } else if (event.getPointerCount() == 3) {
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

                    //旋转
                    lastAngle = getAngle(event.getX(i0), event.getY(i0), event.getX(i1), event.getY(i1));

                    //移动
                    lastX = (event.getX(i0) + event.getX(i1)) / 2;
                    lastY = (event.getY(i0) + event.getY(i1)) / 2;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchPro.clickProcess(chosedView, event);
                break;

            //如果点击到了tietu上面

            default:
                break;
        }
        return true;
    }

    /**
     * 移动某个TietuView
     *
     * @param x 移动距离
     */
    private void moveTietu(FloatImageView chosedView, float x, float y) {
        if (chosedView == null || !chosedView.showRim) return;//边框没显示出来，不移动

        TietuFrameLayout.LayoutParams parmas = (TietuFrameLayout.LayoutParams) chosedView.getLayoutParams();
        parmas.leftMargin += x;
        parmas.topMargin += y;
        adjustBound(parmas, chosedView);

//只会移动状态不会变化
        updateViewLayout(chosedView, parmas);
    }

    private void adjustBound(LayoutParams parmas, FloatImageView chosedView) {
        View ptuView = ((FrameLayout) getParent()).getChildAt(0);
        if (ptuView instanceof PtuView) {
            int pad = FloatImageView.pad;
            Rect picBound = ((PtuView) ptuView).getPicBound();
            parmas.leftMargin = Math.max(parmas.leftMargin,
                    picBound.left + pad - chosedView.getWidth());//左边界判断

            parmas.topMargin = Math.max(parmas.topMargin,
                    picBound.top - (chosedView.getHeight() - pad));//上边界
            parmas.leftMargin = Math.min(parmas.leftMargin, picBound.right - pad);//右边界
            parmas.topMargin = Math.min(parmas.topMargin, picBound.bottom - pad);//下边界
            Util.P.le(TAG, "适配TietuView边界完成");
        }
    }

    private void scale(FloatImageView chosedView, float currentRatio) {
        if (chosedView == null || !chosedView.showRim) return;//边框没显示出来，不缩放
        currentRatio = adjustSize(chosedView, currentRatio);
        TietuFrameLayout.LayoutParams parmas = (TietuFrameLayout.LayoutParams) chosedView.getLayoutParams();
        //=当前位置-当前距离 chosedView.getWidth()/2 缩放后多出来的currentRatio-1距离
        parmas.leftMargin = Math.round (chosedView.getLeft() - chosedView.getWidth() / 2 * (currentRatio - 1));
        parmas.topMargin = Math.round (chosedView.getTop() - chosedView.getHeight() / 2 * (currentRatio - 1));
        parmas.width = Math.round (chosedView.getWidth() * currentRatio);
        parmas.height = Math.round (parmas.width * chosedView.getHWRatio());
        updateViewLayout(chosedView, parmas);
        Util.P.le(TAG, "长宽比 " + parmas.width * 1f / parmas.height);
    }

    private float adjustSize(FloatImageView chosedView, float currentRatio) {
        currentRatio = Math.max(currentRatio, FloatImageView.minWidth * 1f / chosedView.getWidth());//大于最小宽
        currentRatio = Math.min(currentRatio, AllData.screenWidth * 1.2f / chosedView.getWidth());//小于最大宽
        currentRatio = Math.max(currentRatio, FloatImageView.minHeight * 1f / chosedView.getHeight());//大于最小高
        currentRatio = Math.min(currentRatio, AllData.screenHeight * 1.2f / chosedView.getHeight());
        return currentRatio;
    }

    public void removeFloatView(FloatImageView chosedView) {
        if (tietuChangeListener != null)
            tietuChangeListener.onTietuRemove(chosedView);
        removeView(chosedView);
    }

    /**
     * View被选中的时候，会先判断是否与选中相同，相同者不变化
     * 否则显示边框
     */
    void onChosedView(FloatImageView childView) {
        if (childView.equals(chosedView))
            chosedView.setShowRim(true);
        else {
            if (chosedView != null)
                chosedView.setShowRim(false);//处理原来选中的View
            chosedView = childView;
//        首先将View更新到最前面
            LayoutParams layoutParams = (FrameLayout.LayoutParams) childView.getLayoutParams();
            super.removeView(childView);
            childView.setShowRim(true);
            super.addView(childView, layoutParams);
        }
    }

    /**
     * 获取线段向量的角度
     */
    private float getAngle(float x, float y, float x1, float y1) {
        float dx = x1 - x, dy = y1 - y;
        double angle = Math.atan2(dy, dx);
        return (float) Math.toDegrees(angle);
    }
}
