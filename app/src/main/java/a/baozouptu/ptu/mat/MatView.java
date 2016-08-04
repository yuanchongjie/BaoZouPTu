package a.baozouptu.ptu.mat;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

import a.baozouptu.ptu.view.PtuView;

/**
 * Created by liuguicen on 2016/8/2.
 *
 * @description
 */
public class MatView extends PtuView {

    private static final int NO = 0;
    private int CUR_STATUS;
    private static final int PEN = 0x00000008;
    private static final int SMEAR = 2;
    private static final int SHAPE = 3;
    private static final int RUBBER = 4;
    private static final int PEN_MOVE = 0x00000009;
    private static final int PEN_DRAW_LINE = 0x0000000a;

    Context mContext;
    Rect totalBound;
    Rect picBound;
    private Pen pen;

    /**
     * @param totalBound 整个PtuFragment的bound
     */
    public MatView(Context context, Rect totalBound) {
        super(context);
        mContext=context;
        this.totalBound = new Rect(totalBound);
        CUR_STATUS = PEN;
        pen = new Pen(context, totalBound);

        super.canDoubleClick(false);
        super.setCanMinish(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
//                处于pen状态,点击发生在钢笔上面
                if ((CUR_STATUS & PEN) != 0) {
                    if (pen.contain(event.getX(), event.getY())) {
                        pen.onClick();
                        if (pen.isDrawLine())
                            CUR_STATUS = PEN_DRAW_LINE;
                        else
                            CUR_STATUS = PEN_MOVE;
                        pen.startAt(event.getX(), event.getY());
                    } else {
                        CUR_STATUS = PEN;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float h = event.getHistorySize();
                for (int i = 0; i < h; i++) {
                    float x = event.getHistoricalX(i), y = event.getHistoricalY(i);
                    if ((CUR_STATUS & PEN) != 0) {
                        if (pen.contain(x, y)) {
                            pen.move(x, y, srcRect, dstRect);
                        } else {//移出了pen
                            CUR_STATUS = PEN;
                            pen.reStart();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if ((CUR_STATUS & PEN) != 0) {
                    pen.reStart();
                }
                break;
        }
        if (CUR_STATUS == NO)
            return super.onTouchEvent(event);
        invalidate();
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (CUR_STATUS) {
            case PEN:
            case PEN_MOVE:
                pen.drawPen(canvas);
                break;
            case PEN_DRAW_LINE:
                pen.drawPen(canvas);
                pen.drawLine(sourceCanvas);
        }
    }


    public void startDrawLine() {
        if (CUR_STATUS != PEN) {
            CUR_STATUS = PEN;
            if (pen != null)
                pen.reSet(totalBound);
            else
                pen = new Pen(mContext,totalBound);
            invalidate();
        }
    }

    public void startSmear() {
        if (CUR_STATUS != SMEAR) {
            CUR_STATUS = SMEAR;
            invalidate();
        }
    }

    public void startMatByShape() {
        if (CUR_STATUS != SHAPE) {
            CUR_STATUS = SHAPE;
            invalidate();
        }
    }

    public void startRubber() {
        if (CUR_STATUS != RUBBER) {
            CUR_STATUS = RUBBER;
            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        releaseResource();
        super.onDetachedFromWindow();
    }

    @Override
    public void releaseResource() {
        pen.releaseResource();
    }
}
