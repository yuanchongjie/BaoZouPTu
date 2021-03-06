package a.baozouptu.ptu.tietu;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.GeoUtil;
import a.baozouptu.common.util.Util;
import a.baozouptu.ptu.view.PtuSeeView;

/**
 * Created by liuguicen on 2016/10/2.
 *
 * @description 放置tietu的图片的FrameLayout，用于支持多个贴图在同一界面下的
 * 移动，缩放，旋转操作
 */
public class TietuFrameLayout extends FrameLayout {
    private static final String TAG = "TietuFrameLayout";
    FloatImageView chosenView;
    private float lastX = -1;
    private float lastY = -1;
    private float lastDis = 0;
    private float lastAngle = 0;
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
    }

    public void addView(FloatImageView child, FrameLayout.LayoutParams params) {
        if (chosenView != null)
            chosenView.setShowRim(false);
        chosenView = child;
        super.addView(child, params);
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
                if (GeoUtil.getDis(event.getX(), event.getY(), lastX, lastY) > 5)//这里doubleclick用来判断点击，距离很近不取消，远就取消它
                    Util.DoubleClick.cancel();
                //移动
                if (event.getPointerCount() == 1) {
                    moveTietu(chosenView, x - lastX, y - lastY);
                    lastX = x;
                    lastY = y;
                } else {
                    //缩放
                    float endD = GeoUtil.getDis(x0, y0, x1, y1);
                    float currentRatio = endD / lastDis;
                    lastDis = endD;
                    scale(chosenView, currentRatio);
                    //旋转
                    float curAngle = getAngle(x0, y0,
                            x1, y1);
                    if (chosenView != null && chosenView.showRim)
                        chosenView.setRotation(chosenView.getRotation() + curAngle - lastAngle);
                    lastAngle = curAngle;
                    //移动
                    float nx = (x0 + x1) / 2, ny = (y0 + y1) / 2;
                    moveTietu(chosenView, nx - lastX, ny - lastY);
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
                onEventUp(event);
                break;
            //如果点击到了tietu上面
            default:
                break;
        }
        return true;
    }

    public boolean onEventUp(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        if (Util.DoubleClick.isDoubleClick())//判断发生点击事件
        {
            if (chosenView != null && chosenView.isOnCancel(x - chosenView.getLeft(), y - chosenView.getTop()))//如果点击到了取消
            {
                removeFloatView(chosenView);
                return true;
            } else {
                int count = getChildCount();
                Rect rect = new Rect();
                for (int i = count - 1; i >= 0; i--) {
                    View child = getChildAt(i);
                    rect.left = child.getLeft();
                    rect.right = child.getRight();
                    rect.top = child.getTop();
                    rect.bottom = child.getBottom();
                    if (rect.contains((int) x, (int) y)) {
                        onChosenView((FloatImageView) child);
                        return true;
                    }
                }
            }
//            点击没有发生在tietu图片上面
            if (chosenView != null)
                chosenView.setShowRim(false);
        }
        return true;
    }

    /**
     * 移动某个TietuView
     *
     * @param x 移动距离
     */
    private void moveTietu(FloatImageView chosenView, float x, float y) {
        if (chosenView == null || !chosenView.showRim) return;//边框没显示出来，不移动

        TietuFrameLayout.LayoutParams parmas = (TietuFrameLayout.LayoutParams) chosenView.getLayoutParams();
        parmas.leftMargin += x;
        parmas.topMargin += y;
        adjustBound(parmas, chosenView);

//只会移动状态不会变化
        updateViewLayout(chosenView, parmas);
    }

    private void adjustBound(LayoutParams params, FloatImageView chosenView) {
        View ptuView = ((FrameLayout) getParent()).getChildAt(0);
        if (ptuView instanceof PtuSeeView) {
            int pad = FloatImageView.pad;
            Rect picBound = ((PtuSeeView) ptuView).getPicBound();
            params.leftMargin = Math.max(params.leftMargin,
                    picBound.left + pad - chosenView.getWidth());//左边界判断

            params.topMargin = Math.max(params.topMargin,
                    picBound.top - (chosenView.getHeight() - pad));//上边界
            params.leftMargin = Math.min(params.leftMargin, picBound.right - pad);//右边界
            params.topMargin = Math.min(params.topMargin, picBound.bottom - pad);//下边界
        }
    }

    /**
     * 缩放还给其它地方使用
     *
     * @param chosenView   被缩放的FloatImageView
     * @param currentRatio 需要缩放的比例
     */
    public void scale(FloatImageView chosenView, float currentRatio) {
        if (chosenView == null || !chosenView.showRim) return;//边框没显示出来，不缩放
        currentRatio = adjustSize(chosenView, currentRatio);
        chosenView.scaleRatio *= currentRatio;
        TietuFrameLayout.LayoutParams params = (TietuFrameLayout.LayoutParams) chosenView.getLayoutParams();
        //根据中心缩放：=当前位置=原中心-1/2长宽
        params.width = Math.round(chosenView.calculateTotalWidth(chosenView.scaleRatio));
        params.height = Math.round(chosenView.calculateTotalHeight(chosenView.scaleRatio));
        params.leftMargin = Math.round(chosenView.getCenterX() - params.width / 2);
        params.topMargin = Math.round(chosenView.getCenterY() - params.height / 2);
        updateViewLayout(chosenView, params);
    }

    private float adjustSize(FloatImageView chosenView, float currentRatio) {
        currentRatio = Math.max(currentRatio, FloatImageView.minWidth * 1f / chosenView.getWidth());//大于最小宽
        currentRatio = Math.min(currentRatio, AllData.screenWidth * 1.2f / chosenView.getWidth());//小于最大宽
        currentRatio = Math.max(currentRatio, FloatImageView.minHeight * 1f / chosenView.getHeight());//大于最小高
        currentRatio = Math.min(currentRatio, AllData.screenHeight * 1.2f / chosenView.getHeight());
        return currentRatio;
    }

    public void removeFloatView(FloatImageView chosenView) {
        if (tietuChangeListener != null)
            tietuChangeListener.onTietuRemove(chosenView);
        removeView(chosenView);
    }

    /**
     * View被选中的时候，会先判断是否与选中相同，相同则不变化
     * 否则显示边框
     */
    void onChosenView(FloatImageView childView) {
        if (childView == chosenView)
            chosenView.setShowRim(true);
        else {
            if (chosenView != null)
                chosenView.setShowRim(false);//处理原来选中的View
            chosenView = childView;
//        首先将View更新到最前面
            LayoutParams layoutParams = (FrameLayout.LayoutParams) childView.getLayoutParams();
            super.removeView(childView);
            childView.setShowRim(true);
            addView(childView, layoutParams);
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

    @Override
    public void onViewRemoved(View child) {
        if (child == chosenView) chosenView = null;
        super.onViewRemoved(child);
    }
}
