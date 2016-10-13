package a.baozouptu.ptu.tietu;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Method;
import java.util.ArrayList;

import a.baozouptu.base.util.Util;

/**
 * 触摸事件处理器，TietuFrameLayout的
 * Created by Administrator on 2016/10/5 0005.
 */
class TouchEventProcessor {
    private float lastX = -1;
    private float lastY = -1;
    private float lastDis = 0;
    private float lastAngle = 0;
    private TietuFrameLayout layout;

    TouchEventProcessor(TietuFrameLayout layout) {
        this.layout = layout;
    }

    /**
     * 消费返回true
     */
    public boolean clickProcess(FloatImageView chosedView, MotionEvent event) {
        float x = event.getX(), y = event.getY();
        if (Util.DoubleClick.isDoubleClick())//判断发生点击事件
        {
            if (chosedView.isOnCancel(x - chosedView.getLeft(), x - chosedView.getTop()))//如果点击到了取消
            {
                layout.removeFloatView(chosedView);
                return true;
            } else {
                int count = layout.getChildCount();
                Rect rect = new Rect();
                for (int i = count - 1; i >= 0; i--) {
                    View child = layout.getChildAt(i);
                    rect.left = child.getLeft();
                    rect.right = child.getRight();
                    rect.top = child.getTop();
                    rect.bottom = child.getBottom();
                    if (rect.contains((int) x, (int) y)) {
                        layout.onChosedView((FloatImageView)child);
                        return true;
                    }
                }
            }
//            点击没有发生在tietu图片上面
            chosedView.setShowRim(false);
        }
        return true;
    }
}