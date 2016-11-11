package a.baozouptu.ptu.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.FloatView;
import a.baozouptu.ptu.cut.CutFragment;
import a.baozouptu.ptu.cut.CutFrameView;
import a.baozouptu.ptu.text.FloatTextView;
import a.baozouptu.ptu.tietu.TietuFrameLayout;

/**
 * 重绘子视图是一个重要的功能，应该写得简洁有力
 * Created by Administrator on 2016/5/30.
 */
public class PtuFrameLayout extends FrameLayout {
    private static final String TAG = "PtuFrameLayout";
    private String DEBUG_TAG = "PtuFrameLayout";
    private static int CURRENT_STATUS = 0;
    private static final int STATUS_INIT = 0;
    private static final int STATUS_MOVE_FLOAT = 1;
    private static final int STATUS_SCALE_FLOAT = 2;

    private FloatView floatView;
    Context mContext;
    private float lastFloatDis;
    private boolean onFloat = false;
    private float downX;
    private float downY;
    private float minMoveDis = Util.dp2Px(3);
    private long downTime;
    private float scaleCenterX;
    private float scaleCenterY;
    /**
     * 判断手指是否抬起过，因为down事件会不断触发，无法判断up与down的关系
     */
    private boolean hasUp = true;


    public PtuFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public FloatTextView initAddTextFloat(Rect picBound) {
        //设置floatText的基本属性
        floatView = new FloatTextView(mContext, picBound);

        //设置布局
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams((int) floatView.getmWidth(), (int) floatView.getmHeight());

        floatParams.setMargins((int) floatView.getfLeft(), (int) floatView.getfTop(),
                (int) (floatView.getfLeft() + floatView.getmWidth()),
                (int) (floatView.getfTop() + floatView.getmHeight()));
        addView((View) floatView, floatParams);
        return (FloatTextView) floatView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Util.P.le(DEBUG_TAG, "onTouchEvent");
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
                    Util.P.le("经过了up2");
                } else {//不是点击事件，将之前状态显示出来
                    floatView.changeShowState(floatView.getDownState());
                    Util.P.le("经过了up3", floatView.getDownState());
                }
                Util.P.le("经过了up4");
                hasUp = true;
                break;
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getChildCount() > 1) {
            View childView = getChildAt(1);
            boolean isConsume = false;

            float sx = ev.getX(), sy = ev.getY();
            if (childView instanceof FloatView &&
                            new RectF(childView.getLeft(), childView.getTop(),
                                    childView.getLeft() + childView.getWidth(), childView.getTop() + childView.getHeight())
                                    .contains(sx, sy) ) //是浮动图，这判断是否在内部
                    {
                ev.setLocation(sx - childView.getLeft(), sy - childView.getTop());
                isConsume = childView.dispatchTouchEvent(ev);
                if (isConsume)//消费了up事件，up置为true
                    hasUp = true;
            }else if(childView instanceof CutFrameView){
                ev.setLocation(sx - childView.getLeft(), sy - childView.getTop());
                isConsume=childView.dispatchTouchEvent(ev);
                if(!isConsume){
                    isConsume=getChildAt(0).dispatchTouchEvent(ev);
                }
            }
            else if(!(childView instanceof  FloatView)){//如果不是浮动图
                ev.setLocation(sx - childView.getLeft(), sy - childView.getTop());
                isConsume=childView.dispatchTouchEvent(ev);
            }
            //没有消费才分发事件，不然就不分发
            if (!isConsume) {
                ev.setLocation(sx, sy);
                onTouchEvent(ev);
            }
        }
        //只有PtuView时只将事件分发给它，坐标不用变换，他们大小一样，PtuView占满了整个布局
        else getChildAt(0).dispatchTouchEvent(ev);
        return true;
    }

    /**
     * 改变位置，不能在float为空时调用
     */
    public void changeLocation() {
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams((int) floatView.getmWidth(),
                        (int) floatView.getmHeight());
        floatParams.setMargins((int) floatView.getfLeft(), (int) floatView.getfTop(),
                (int) (floatView.getfLeft() + floatView.getmWidth()),
                (int) (floatView.getfTop() + floatView.getmHeight()));
        updateViewLayout((View) floatView, floatParams);
    }

    public TietuFrameLayout initAddImageFloat(Rect bound) {
        TietuFrameLayout tietuFrameLayout=new TietuFrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(bound.width(),bound.height(), Gravity.CENTER);
        tietuFrameLayout.setBackgroundColor(0x0000);
        addView(tietuFrameLayout,layoutParams);
        return tietuFrameLayout;
    }
}