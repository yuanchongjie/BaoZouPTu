package a.baozouptu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import a.baozouptu.tools.GeoUtil;
import a.baozouptu.tools.Util;

/**
 * 重绘子视图是一个重要的功能，应该写得简洁有力
 * Created by Administrator on 2016/5/30.
 */
public class PtuFrameLayout extends FrameLayout {
    private String DEBUG_TAG ="PtuFrameLayout";
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

    public FloatTextView initAddFloat(Rect ptuViewBound) {
        Util.P.le(DEBUG_TAG,"initAddFloat");
        //设置floatText的基本属性
        floatView = new FloatTextView(mContext, ptuViewBound);

        //设置布局
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams((int) floatView.mWidth, (int) floatView.mHeight);

        floatParams.setMargins((int) floatView.getmLeft(), (int) floatView.getmTop(),
                (int) (floatView.getmLeft() + floatView.mWidth),
                (int) (floatView.getmTop() + floatView.mHeight));
        addView(floatView, floatParams);
        return floatView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Util.P.le(DEBUG_TAG,"onTouchEvent");
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (hasUp) {
                    hasUp = false;
                        floatView.setDownState();
                }
                Util.P.le("经过了down",floatView.getDownState());

                //获取点击下去时的数据，位置，时间，
                downX = event.getX();
                downY = event.getY();
                downTime = System.currentTimeMillis();

                floatView.relativeX = event.getX() - floatView.getmLeft();
                floatView.relativeY = event.getY() - floatView.getmTop();
                CURRENT_STATUS = STATUS_MOVE_FLOAT;

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                CURRENT_STATUS = STATUS_SCALE_FLOAT;
                lastFloatDis = GeoUtil.getDis(event.getX(0), event.getY(0),
                        event.getX(1), event.getY(1));
                scaleCenterX = (event.getX(0) + event.getX(1)) / 2;
                scaleCenterY = (event.getY(0) + event.getY(1)) / 2;
                floatView.relativeX = scaleCenterX - floatView.getmLeft();
                floatView.relativeY = scaleCenterY - floatView.getmTop();

            case MotionEvent.ACTION_MOVE:
                floatView.changeShowState(FloatView.STATUS_RIM);
                if (event.getPointerCount() == 1 &&
                        CURRENT_STATUS == STATUS_MOVE_FLOAT) {//是在移动浮动view
                    floatView.drag(event.getX(), event.getY());
                    redrawFloat();
                } else if(event.getPointerCount()>=2){
                //以缩放中心的为相对坐标，用于一边缩放一边移动
                float ncenterX = (event.getX(0) + event.getX(1)) / 2,
                        ncenterY = (event.getY(0) + event.getY(1)) / 2;
                //缩放时移动距离大于2dp时移动
                if (GeoUtil.getDis(scaleCenterX, scaleCenterY, ncenterX, ncenterY) > Util.dp2Px(2))
                    floatView.drag(ncenterX, ncenterY);
                scaleCenterX = ncenterX;
                scaleCenterY = ncenterY;

                //增加的距离
                float endFloatDis = GeoUtil.getDis(event.getX(0), event.getY(0),
                        event.getX(1), event.getY(1));
                float ratio = (floatView.mWidth + (endFloatDis - lastFloatDis)) / floatView.mWidth;
                if (ratio == 1.0) break;
                floatView.scale(ratio);
                redrawFloat();
                lastFloatDis = endFloatDis;

            }
                break;
            case MotionEvent.ACTION_UP:
                Util.P.le("经过了up");
                //点击事件
                if (hasUp == false && GeoUtil.getDis(downX, downY, event.getX(), event.getY()) < minMoveDis
                        && System.currentTimeMillis() - downTime < 500) {
                    //点击发生在floatView之外
                    if (!(new RectF(floatView.getmLeft(),floatView.getmTop(),
                            floatView.getLeft()+floatView.mWidth,floatView.getmTop()+floatView.mHeight)
                            .contains(event.getX(),event.getY())))
                        floatView.changeShowState(floatView.STATUS_TOUMING);
                    Util.P.le("经过了up2");
                } else {//不是点击事件，将之前状态显示出来
                    floatView.changeShowState(floatView.getDownState());
                    Util.P.le("经过了up3",floatView.getDownState());
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
        if(getChildCount()>1){
            boolean isConsume=false;
            float sx=ev.getX(),sy=ev.getY();
            if ((new RectF(floatView.getmLeft(),floatView.getmTop(),
                    floatView.getLeft()+floatView.mWidth,floatView.getmTop()+floatView.mHeight)
                    .contains(ev.getX(),ev.getY()))) {
                ev.setLocation(ev.getX()-floatView.getmLeft(),ev.getY()-floatView.getmTop());
                isConsume=getChildAt(1).dispatchTouchEvent(ev);
                if(isConsume)//消费了up事件，up置为true
                    hasUp=true;
            }
            //没有消费才分发事件，不然就不分发
            if(!isConsume){
                ev.setLocation(sx,sy);
                onTouchEvent(ev);
            }
        }
        //只有PtuView时只将事件分发给它，坐标不用变换，他们大小一样，PtuView占满了整个布局
        else getChildAt(0).dispatchTouchEvent(ev);
        return true;
    }

    public void redrawFloat() {
        Util.P.le(DEBUG_TAG,"redrawFloat");
        removeView(floatView);
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams((int) floatView.mWidth,
                        (int) floatView.mHeight);
        floatParams.setMargins((int) floatView.getmLeft(), (int) floatView.getmTop(),
                (int) (floatView.getmLeft() + floatView.mWidth),
                (int) (floatView.getmTop() + floatView.mHeight));
        addView(floatView, floatParams);
    }
}