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

import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.RepealRedoListener;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
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

    private RepealRedoListener repealRedoListener;
    private ArrayList<Pair<Path,Paint>> pathPaintList;

    int color;
    int width;
    private final RepealRedoManager<Pair<Path, Paint>> repealRedoManager;


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
        paint.setStrokeCap(Paint.Cap.ROUND);

        picPaint=new Paint();
        picPaint.setDither(true);
        picPaint.setAntiAlias(true);
        picPaint.setColor(color);
        picPaint.setStrokeWidth(width*ptuView.getSrcRect().height()/ptuView.getDstRect().width());
        picPaint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        pathPaintList =new ArrayList<>();
        repealRedoManager = new RepealRedoManager<>(100);
        path=new Path();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX(),y=event.getY();
        float[] pxy=PtuUtil.getLocationAtPicture(x+getLeft(),y+getTop(),
                ptuView.getSrcRect(),ptuView.getDstRect());
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
                picPath.quadTo(plxy[0],plxy[1],pxy[0],pxy[1]);
                lastX=x;
                lastY=y;
                plxy[0]=lastX;
                plxy[1]=lastY;
                break;
            case MotionEvent.ACTION_UP:
                path.quadTo(lastX,lastY,x,y);
                picPath.quadTo(plxy[0],plxy[1],pxy[0],pxy[1]);

                repealRedoManager.commit(new Pair<>(path, paint));
                repealRedoListener.canRedo(true);
                repealRedoListener.canRepeal(repealRedoManager.canRepeal());
                pathPaintList.add(new Pair<>(picPath, picPaint));
                break;
        }
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int index = repealRedoManager.getCurrentIndex();
        for (int i = 0; i <= index; i++) {
            Pair<Path,Paint> sd = repealRedoManager.getStepdata(i);
            canvas.drawPath(sd.first,sd.second);
        }
        canvas.drawPath(path,paint);
        super.onDraw(canvas);
    }

    public void smallRedo(){
        if(repealRedoManager.canRedo()) {
            repealRedoManager.redo();
            repealRedoListener.canRedo(repealRedoManager.canRedo());
            repealRedoListener.canRepeal(repealRedoManager.canRepeal());
            invalidate();
        }
    }

    public void smallRepeal(){
        if(repealRedoManager.canRepeal()) {
            repealRedoManager.repealPrepare();
            repealRedoListener.canRedo(repealRedoManager.canRedo());
            repealRedoListener.canRepeal(repealRedoManager.canRepeal());
           invalidate();
        }
    }

    public ArrayList<Pair<Path,Paint>> getResultdata(){
        return pathPaintList;
    }

    void setColor(int color) {
        this.color = color;
    }

    void setWidth(int width) {
        this.width = width;
    }

    public void setRepealRedoListener(RepealRedoListener repealRedoListener) {
        this.repealRedoListener = repealRedoListener;
    }
}
