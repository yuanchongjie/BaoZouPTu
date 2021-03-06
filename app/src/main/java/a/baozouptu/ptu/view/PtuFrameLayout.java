package a.baozouptu.ptu.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import a.baozouptu.common.util.GeoUtil;
import a.baozouptu.common.util.Util;
import a.baozouptu.ptu.FloatView;
import a.baozouptu.ptu.text.FloatTextView;
import a.baozouptu.ptu.tietu.TietuFrameLayout;

/**
 * 重绘子视图是一个重要的功能，应该写得简洁有力
 * Created by Administrator on 2016/5/30.
 */
public class PtuFrameLayout extends FrameLayout {
    private static final String TAG = "PtuFrameLayout";
    private static int CURRENT_STATUS = 0;
    private static final int STATUS_INIT = 0;
    private static final int STATUS_MOVE_FLOAT = 1;
    private static final int STATUS_SCALE_FLOAT = 2;

    private FloatTextView floatView;
    Context mContext;
    private float lastFloatDis;
    private boolean onFloat = false;
    private float downX;
    private float downY;
    private float minMoveDis;
    private long downTime;
    private float scaleCenterX;
    private float scaleCenterY;
    /**
     * 判断手指是否抬起过，因为down事件会不断触发，无法判断up与down的关系
     */
    private boolean hasUp = true;

    public PtuFrameLayout(Context context) {
        super(context);
        init();
    }

    public PtuFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PtuFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        //以下是在IED中显示是不能依赖的代码，使用isInEditMode返回
        if (isInEditMode()) {
            return;
        }
        minMoveDis = Util.dp2Px(3);
    }

    public FloatTextView initAddTextFloat(Rect picBound) {
        //设置floatText的基本属性
        floatView = new FloatTextView(mContext, picBound);

        //设置布局
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams(Math.round(floatView.getmWidth()), Math.round(floatView.getmHeight()));

        floatParams.setMargins(Math.round(floatView.getfLeft()), Math.round(floatView.getfTop()),
                Math.round(floatView.getfLeft() + floatView.getmWidth()),
                Math.round(floatView.getfTop() + floatView.getmHeight()));
        addView((View) floatView, floatParams);
        return (FloatTextView) floatView;
    }

    /**
     * 策略是：
     * 如果顶层是FloatTextView,做特殊处理，
     * 否则就不做特殊处理
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getChildCount() > 1 && getChildAt(getChildCount() - 1) instanceof FloatTextView) {//对于文字框特殊处理，其它照旧
            boolean isConsume = false;
            FloatTextView floatText = ((FloatTextView) getChildAt(getChildCount() - 1));
            float sx = ev.getX(), sy = ev.getY();
            if (floatText instanceof FloatTextView) {
                if (!floatText.isClickable()) {//使用RubberView时
                    View childView = getChildAt(getChildCount() - 2);
                    ev.setLocation(sx - childView.getLeft(), sy - childView.getTop());
                    isConsume = childView.dispatchTouchEvent(ev);
                } else if (new RectF(floatText.getLeft(), floatText.getTop(),
                        floatText.getLeft() + floatText.getWidth(), floatText.getTop() + floatText.getHeight())
                        .contains(sx, sy)) //是文字框，并且在内部
                {
                    ev.setLocation(sx - floatText.getLeft(), sy - floatText.getTop());
                    isConsume = floatText.dispatchTouchEvent(ev);
                    if (isConsume)//消费了up事件，up置为true
                        hasUp = true;
                }
                //没有消费才分发事件，不然就不分发
                if (!isConsume) {
                    ev.setLocation(sx, sy);
                    onTouchEvent(ev);
                }
            }
            return true;
        } else //只有PtuView时只将事件分发给它，坐标不用变换，他们大小一样，PtuView占满了整个布局
            return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!(getChildAt(getChildCount() - 1) instanceof FloatTextView))
            return super.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (hasUp) {
                    hasUp = false;
                    floatView.setDownState();
                }
                Util.P.le("经过了down", floatView.getDownState());

                //获取点击下去时的数据，位置，时间，
                downX = event.getX();
                downY = event.getY();
                downTime = System.currentTimeMillis();

                floatView.setRelativeX(event.getX() - floatView.getfLeft());
                floatView.setRelativeY(event.getY() - floatView.getfTop());
                CURRENT_STATUS = STATUS_MOVE_FLOAT;

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                CURRENT_STATUS = STATUS_SCALE_FLOAT;
                lastFloatDis = GeoUtil.getDis(event.getX(0), event.getY(0),
                        event.getX(1), event.getY(1));
                scaleCenterX = (event.getX(0) + event.getX(1)) / 2;
                scaleCenterY = (event.getY(0) + event.getY(1)) / 2;
                floatView.setRelativeX(scaleCenterX - floatView.getfLeft());
                floatView.setRelativeY(scaleCenterY - floatView.getfTop());

            case MotionEvent.ACTION_MOVE:
//             一个神坑， 有些手机屏幕不太稳定，点击时一定发生位移，所以要变成状态4，弹出输入法gg
                if (GeoUtil.getDis(event.getX(), event.getY(), downX, downY) < 4)
                    break;
                floatView.changeShowState(FloatView.STATUS_RIM);
                if (event.getPointerCount() == 1 &&
                        CURRENT_STATUS == STATUS_MOVE_FLOAT) {//是在移动浮动view
                    floatView.drag(event.getX(), event.getY());
                    changeLocation();
                } else if (event.getPointerCount() >= 2) {
                    //以缩放中心的为相对坐标，用于一边缩放一边移动
                    float ncenterX = (event.getX(0) + event.getX(1)) / 2,
                            ncenterY = (event.getY(0) + event.getY(1)) / 2;
                    //缩放时移动距离大于2dp时移动
                    if (GeoUtil.getDis(scaleCenterX, scaleCenterY, ncenterX, ncenterY) > Util.dp2Px(1))
                        floatView.drag(ncenterX, ncenterY);
                    scaleCenterX = ncenterX;
                    scaleCenterY = ncenterY;

                    //增加的距离
                    float endFloatDis = GeoUtil.getDis(event.getX(0), event.getY(0),
                            event.getX(1), event.getY(1));
                    float ratio = (floatView.getmWidth() + (endFloatDis - lastFloatDis)) / floatView.getmWidth();
                    if (ratio == 1.0) break;
                    floatView.scale(ratio);
                    changeLocation();
                    lastFloatDis = endFloatDis;

                }
                break;
            case MotionEvent.ACTION_UP:
                Util.P.le("经过了up");
                //点击事件
                if (hasUp == false && GeoUtil.getDis(downX, downY, event.getX(), event.getY()) < minMoveDis
                        && System.currentTimeMillis() - downTime < 500) {
                    //点击发生在floatView之外
                    if (!(new RectF(floatView.getfLeft(), floatView.getfTop(),
                            floatView.getfLeft() + floatView.getmWidth(), floatView.getfTop() + floatView.getmHeight())
                            .contains(event.getX(), event.getY())))
                        floatView.changeShowState(floatView.STATUS_TOUMING);
                } else {//不是点击事件，将之前状态显示出来
                    floatView.changeShowState(floatView.getDownState());
                }
                hasUp = true;
                break;
        }
        return false;
    }


    /**
     * 改变位置，不能在float为空时调用
     */
    public void changeLocation() {
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams(Math.round(floatView.getmWidth()),
                        Math.round(floatView.getmHeight()));
        floatParams.setMargins(Math.round(floatView.getfLeft()), Math.round(floatView.getfTop()),
                Math.round(floatView.getfLeft() + floatView.getmWidth()),
                Math.round(floatView.getfTop() + floatView.getmHeight()));
        updateViewLayout((View) floatView, floatParams);
    }

    public TietuFrameLayout initAddImageFloat(Rect bound) {
        TietuFrameLayout tietuFrameLayout = new TietuFrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(bound.width(), bound.height(), Gravity.CENTER);
        tietuFrameLayout.setBackgroundColor(0x0000);
        addView(tietuFrameLayout, layoutParams);
        return tietuFrameLayout;
    }

}