package a.baozouptu.ptu.tietu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by liuguicen on 2016/10/2.
 *
 * @description 放置tietu的图片的FrameLayout，用于支持多个贴图在同一界面下的
 * 移动，缩放，旋转操作
 *
 */
public class TietuFrameLayout extends FrameLayout {
    FloatImageView chosedView;
    public TietuFrameLayout(Context context) {
        super(context);
    }

    public TietuFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addView(FloatImageView child, FrameLayout.LayoutParams params) {
        if(chosedView!=null)
            chosedView.setShowRim(false);
        chosedView=child;
        super.addView(child, params);
    }

    @Override
    public void removeView(View view) {
        if(chosedView.equals(view))
            chosedView=null;
        super.removeView(view);
    }
   /* *//**
     * 改变位置，不能在float为空时调用
     *//*
    public void changeLocation() {
        FrameLayout.LayoutParams floatParams =
                new FrameLayout.LayoutParams((int) chosedView.getWidth(),
                        (int) chosedView.getHeight());
        floatParams.setMargins((int) floatView.getfLeft(), (int) floatView.getfTop(),
                (int) (floatView.getfLeft() + floatView.getmWidth()),
                (int) (floatView.getfTop() + floatView.getmHeight()));
        updateViewLayout((View) floatView, floatParams);
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }
}
