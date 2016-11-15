package a.baozouptu.ptu.tietu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import a.baozouptu.R;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.MicroButtonData;
import a.baozouptu.ptu.view.IconBitmapCreator;

/**
 * Created by liuguicen on 2016/10/2.
 *
 * @description
 */
public class FloatImageView extends ImageView {
    private static final String TAG = "FloatImageView";
    public static int pad = Util.dp2Px(10);
    private static Bitmap iconBitmap = IconBitmapCreator.createCancelBitmap(pad * 2, Color.WHITE, Util.getColor(R.color.mat_pen_line2));
    public static final int minWidth = 9;
    public static final int minHeight = 16;
    MicroButtonData[] items;
    Paint itemPaint;
    /**
     * 是否显示边框
     */
    public boolean showRim;
    private Path rim;
    private Paint rimPaint;
    private String picPath;
    private Bitmap srcBitmap;

    public FloatImageView(Context context) {
        super(context);
        init();
    }

    public FloatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //item方面
        setPadding(pad, pad, pad, pad);
        initItem();
        initRim();
    }

    private void initItem() {
        items = new MicroButtonData[8];
        items[2] = new MicroButtonData(-1, -1, " ");
        items[2].bitmap = FloatImageView.iconBitmap;
        itemPaint = new Paint();
        itemPaint.setAntiAlias(true);
        itemPaint.setDither(true);
    }

    private void initRim() {
        showRim = true;
        rim = new Path();
        rimPaint = new Paint();
        rimPaint.setAntiAlias(true);
        rimPaint.setDither(true);
        rimPaint.setStrokeWidth(7f);
        rimPaint.setColor(0xa0ffffff);
        rimPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 设置边框显示或隐藏,判断与当前状态是否相同，不相同才重绘，高效一些
     */
    public void setShowRim(boolean isShow) {
        if (showRim != isShow) {
            showRim = isShow;
            invalidate();
        }
    }

    /**
     * 切换边框状态，显示或隐藏边框,
     */
    public void switchShowRim() {
        showRim = !showRim;
        invalidate();
    }

    /**
     * 点击是否发生在FloatImageView的取消按钮上
     *
     * @param x 在view的位置，不是父布局的
     * @param y 在view的位置，不是父布局的
     * @return 点击是否发生取消按钮上
     */
    public boolean isOnCancel(float x, float y) {
        if (!showRim) return false;//边框没显示出来，返回false
        RectF itemBound = new RectF();
//        item方面的,取消item
        int r = Math.round(getPaddingTop()*1.5f);
        itemBound.left = getWidth() - r * 2;
        itemBound.top = 0;
        itemBound.right = getWidth();
        itemBound.bottom = r * 2;
        if (itemBound.contains(x, y))
            return true;
        return false;
    }
    /**
     * 返回false,父布局的onTouchEvent一定会被调用
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Util.P.le(TAG, "onDraw方法被调用");
        if (showRim) {
            //画边框
            int r = getPaddingTop();
            rim.reset();
            rim.moveTo(r, r);
            rim.lineTo(getWidth() - r, r);
            rim.lineTo(getWidth() - r, getHeight() - r);
            rim.lineTo(r, getHeight() - r);
            rim.close();
            canvas.drawPath(rim, rimPaint);

            //画item的icon
            //取消item
            int itop = 0;
            int ileft = getWidth() - r * 2;
            canvas.drawBitmap(items[2].bitmap, ileft, itop, itemPaint);
        }
    }

    public void releaseResourse() {
        if(srcBitmap!=null)   srcBitmap.recycle();
        srcBitmap =null;
    }


    public String getPicPath() {
        return picPath;
    }

    /**
     *
     * @return 获取高除以宽的比
     */
    public float getHWRatio(){
        return srcBitmap.getHeight()*1f/srcBitmap.getWidth();
    }

    public void setImageBitmapAndPath(Bitmap srcBitmap, String path) {
        this.srcBitmap =srcBitmap;
        picPath=path;
        setImageBitmap(srcBitmap);
    }
}
