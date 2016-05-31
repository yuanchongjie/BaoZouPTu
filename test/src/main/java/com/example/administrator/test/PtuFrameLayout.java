package com.example.administrator.test;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2016/5/30.
 */
public class PtuFrameLayout extends FrameLayout {
    private FloatTextView floatTextView;
    private int totalWidth, toTalHeight;
    private int startX = 300;
    private int startY = 300;
    private FrameLayout.LayoutParams floatParams;
    private int FTWidth = 300, FTHeight = 300;
    private Rect rect;
    Context mContext;
    private float lastX, lastY;
    /**
     * framelayout事件是否分发到PtuView上面
     */
    private boolean dispath = true;
    private boolean FTCanMove;

    public PtuFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        totalWidth = w;
        toTalHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }


    public void initAdd(int width, int height) {
        totalWidth = width;
        toTalHeight = height;
        startX = (totalWidth - FTWidth) / 2;
        startY = (toTalHeight - FTHeight) / 2;

        floatTextView = new FloatTextView(mContext);
        floatTextView.setWidth(FTWidth);
        floatTextView.setHeight(FTHeight);
        floatParams = new FrameLayout.LayoutParams(FTWidth, FTHeight);
        rect = new Rect(startX, startY, startX + FTWidth, startY + FTHeight);
        floatParams.setMargins(rect.left, rect.top, rect.right, rect.bottom);
        addView(floatTextView, floatParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                if (rect.contains((int) lastX, (int) lastY)) {
                    dispath = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                //如果点击事件发生在浮动view内部，则移动有效，移动它

                startX += x - lastX;
                startY += y - lastY;
                floatParams.setMargins(startX, startY, startX + FTWidth, startY + FTHeight);
                lastX = x;
                lastY = y;
                rect.set(startX, startY, startX + FTWidth, startY + FTHeight);
                moveFloatView();
                break;
            case MotionEvent.ACTION_UP:
                dispath = true;
                break;
        }
        return true;    }


    private void moveFloatView() {
        if (!dispath||FTCanMove) {
            removeView(floatTextView);
            addView(floatTextView, floatParams);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (dispath)
            getChildAt(0).dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void setFTCanMove(boolean FTCanMove) {
        this.FTCanMove = FTCanMove;
    }
}
