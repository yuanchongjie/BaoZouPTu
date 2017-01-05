package a.baozouptu.ptu.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import a.baozouptu.common.util.Util;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.RepealRedoListener;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.view.PtuFrameLayout;
import a.baozouptu.ptu.view.PtuView;

/**
 * Created by Administrator on 2016/11/19 0019.
 */

public class RubberView extends View {

    private PtuView ptuView;
    float lastX, lastY;
    private Paint paint;
    private Paint picPaint;
    private Path path;
    private Path picPath;
    private boolean isUp = false;

    private RepealRedoListener repealRedoListener;

    int color;
    int width;
    private final RepealRedoManager<Pair<Path, Paint>> repealRedoManager;
    private final RepealRedoManager<Pair<Path, Paint>> picRR_manager;


    public RubberView(Context context, PtuView ptuView) {
        super(context);
        this.ptuView = ptuView;
        setBackground(null);
        color = Color.WHITE;
        width = Util.dp2Px(20);

        paint = getNewPaint(color,width);
        picPaint =getNewPaint(color,width * ptuView.getSrcRect().height() * 1f / ptuView.getDstRect().height());

        repealRedoManager = new RepealRedoManager<>(100);
        picRR_manager = new RepealRedoManager<>(100);
        path = new Path();
        picPath = new Path();
    }

    private Paint getNewPaint(int  color, float width) {
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    float[] plxy = new float[2];

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (((PtuFrameLayout) getParent()).getChildCount() > 2)
            return false;
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        float[] pxy = PtuUtil.getLocationAtPicture(x + getLeft(), y + getTop(),
                ptuView.getSrcRect(), ptuView.getDstRect());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isUp = false;
                path = new Path();
                picPath = new Path();

                lastX = x;
                lastY = y;
                plxy[0] = pxy[0];
                plxy[1] = pxy[1];
                path.moveTo(x, y);
                picPath.moveTo(pxy[0], pxy[1]);
                break;
            case MotionEvent.ACTION_MOVE:
                path.quadTo(lastX, lastY, x, y);
                picPath.quadTo(plxy[0], plxy[1], pxy[0], pxy[1]);
                lastX = x;
                lastY = y;
                plxy[0] = pxy[0];
                plxy[1] = pxy[1];
                break;
            case MotionEvent.ACTION_UP:
                isUp = true;
                path.quadTo(lastX, lastY, x, y);
                picPath.quadTo(plxy[0], plxy[1], pxy[0], pxy[1]);
                repealRedoManager.commit(new Pair<>(path, paint));
                picRR_manager.commit(new Pair<>(picPath, picPaint));
                repealRedoListener.canRedo(repealRedoManager.canRedo());
                repealRedoListener.canRepeal(repealRedoManager.canRepeal());
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int index = repealRedoManager.getCurrentIndex();
        for (int i = 0; i <= index; i++) {
            Pair<Path, Paint> sd = repealRedoManager.getStepdata(i);
            canvas.drawPath(sd.first, sd.second);
        }
        if (!isUp)//没有到up，最后一笔的数据没有加到撤消重做列表中，所以要单独画出来
            canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }

    public void smallRedo() {
        if (repealRedoManager.canRedo()) {
            repealRedoManager.redo();
            picRR_manager.redo();
            repealRedoListener.canRedo(repealRedoManager.canRedo());
            repealRedoListener.canRepeal(repealRedoManager.canRepeal());
            invalidate();
        }
    }

    public void smallRepeal() {
        if (repealRedoManager.canRepeal()) {
            repealRedoManager.repealPrepare();
            picRR_manager.repealPrepare();
            repealRedoListener.canRedo(repealRedoManager.canRedo());
            repealRedoListener.canRepeal(repealRedoManager.canRepeal());
            invalidate();
        }
    }

    public ArrayList<Pair<Path, Paint>> getResultData() {
        ArrayList<Pair<Path, Paint>> pathPaintList = new ArrayList<>();
        int index = repealRedoManager.getCurrentIndex();
        for (int i = 0; i <= index; i++) {
            pathPaintList.add(picRR_manager.getStepdata(i));
        }
        return pathPaintList;
    }

    void setColor(int color) {
        this.color = color;
        paint =getNewPaint(color,width);
        picPaint= getNewPaint(color,width * ptuView.getSrcRect().height() * 1f / ptuView.getDstRect().height());
    }

    void setRubberWidth(int width) {
        this.width = width;
        paint =getNewPaint(color,width);
        picPaint= getNewPaint(color,width * ptuView.getSrcRect().height() * 1f / ptuView.getDstRect().height());
    }

    int getRubberWidth() {
        return (int) paint.getStrokeWidth();
    }

    public void setRepealRedoListener(RepealRedoListener repealRedoListener) {
        this.repealRedoListener = repealRedoListener;
    }

    public int getColor() {
        return color;
    }
}
