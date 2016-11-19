package a.baozouptu.base.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.view.PtuView;

/**
 * Created by Administrator on 2016/11/19 0019.
 */

public class RubberView extends View {

    private Canvas sourceCanvas;
    private PtuView ptuView;
    float lastX,lastY;
    private Paint paint;
    private Paint picPaint;
    private Path path;
    private Path picPath;

    private ArrayList<Pair<Path,Paint>> pathPaintList;

    int color;
    int width;


    public RubberView(Context context, PtuView ptuView){
        super(context);
        this.ptuView=ptuView;
        sourceCanvas=new Canvas(ptuView.getSourceBm());
        setBackground(null);
        color= Color.WHITE;
        width=15;
        paint=new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);

        picPaint=new Paint();
        picPaint.setDither(true);
        picPaint.setAntiAlias(true);
        picPaint.setColor(color);
        picPaint.setStrokeWidth(width*ptuView.getSrcRect().height()/ptuView.getDstRect().width());
        picPaint.setStyle(Paint.Style.STROKE);

        pathPaintList =new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX(),y=event.getY();
        float[] pxy=PtuUtil.getLocationAtPicture(x,y,ptuView.getSrcRect(),ptuView.getDstRect());
        float[] plxy=new float[2];
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                path=new Path();
                picPath=new Path();
                lastX=x;
                lastY=y;
                plxy[0]=lastX;
                plxy[1]=lastY;
                path.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.quadTo(lastX,lastY,x,y);
                lastX=x;
                lastY=y;
                plxy[0]=lastX;
                plxy[1]=lastY;
                picPath.quadTo(plxy[0],plxy[1],pxy[0],pxy[1]);
                break;
            case MotionEvent.ACTION_UP:
                pathPaintList.add(new Pair<>(picPath, picPaint));
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path,paint);
        super.onDraw(canvas);
    }

    void setColor(int color) {
        this.color = color;
    }

    void setWidth(int width) {
        this.width = width;
    }
}
