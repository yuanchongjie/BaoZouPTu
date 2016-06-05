package a.baozouptu.view;

import android.content.Context;
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
    private static int CURRENT_STATUS = 0;
    private static final int STATUS_INIT = 0;
    private static final int STATUS_MOVE_FLOAT = 1;
    private static final int STATUS_SCALE_FLOAT = 2;

    private FloatTextView floatView;
    private int totalWidth, totalHeight;
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

    public FloatTextView initAddFloat(int totalWidth, int totalHeight) {
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        //设置floatText的基本属性
        floatView = new FloatTextView(mContext, totalWidth, totalHeight);

        //设置布局
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams((int) floatView.mWidth, (int) floatView.mHeight);

        floatParams.setMargins((int) floatView.getStartX(), (int) floatView.getStartY(),
                (int) (floatView.getStartX() + floatView.mWidth),
                (int) (floatView.getStartY() + floatView.mHeight));
        addView(floatView, floatParams);
        return floatView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (hasUp) {
                    hasUp = false;
                    floatView.setDownState();
                }
                floatView.changeShowState(FloatView.STATUS_RIM);

                //获取点击下去时的数据，位置，时间，
                downX = event.getX();
                downY = event.getY();
                downTime = System.currentTimeMillis();

                floatView.relativeX = event.getX() - floatView.getStartX();
                floatView.relativeY = event.getY() - floatView.getStartY();
                CURRENT_STATUS = STATUS_MOVE_FLOAT;

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                CURRENT_STATUS = STATUS_SCALE_FLOAT;
                lastFloatDis = GeoUtil.getDis(event.getX(0), event.getY(0),
                        event.getX(1), event.getY(1));
                scaleCenterX = (event.getX(0) + event.getX(1)) / 2;
                scaleCenterY = (event.getY(0) + event.getY(1)) / 2;
                floatView.relativeX = scaleCenterX - floatView.getStartX();
                floatView.relativeY = scaleCenterY - floatView.getStartY();
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
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

                } else if (event.getPointerCount() == 1 &&
                        CURRENT_STATUS == STATUS_MOVE_FLOAT) {//是在移动浮动view
                    floatView.drag(event.getX(), event.getY());
                    redrawFloat();
                } else {

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 1) {
                } else if (event.getPointerCount() == 2) {
                }
            case MotionEvent.ACTION_UP:
                //点击事件
                if (hasUp == false && GeoUtil.getDis(downX, downY, event.getX(), event.getY()) < minMoveDis
                        && System.currentTimeMillis() - downTime < 500) {
                    if (floatView.showLayoutOrRefreshByClick(event.getX(), event.getY()))//返回true表示需要重绘
                        redrawFloat();
                } else {//不是点击事件，将之前状态显示出来
                    floatView.changeShowState(floatView.getDownState());
                }
                hasUp = true;
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        //拥有浮动视图时，处理点击事件，否则tpuview全权处理了
        if (getChildCount() > 1) onTouchEvent(ev);
        return true;
    }

    public void redrawFloat() {
        removeView(floatView);
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams((int) floatView.mWidth,
                        (int) floatView.mHeight);
        floatParams.setMargins((int) floatView.getStartX(), (int) floatView.getStartY(),
                (int) (floatView.getStartX() + floatView.mWidth),
                (int) (floatView.getStartY() + floatView.mHeight));
        addView(floatView, floatParams);
    }
}