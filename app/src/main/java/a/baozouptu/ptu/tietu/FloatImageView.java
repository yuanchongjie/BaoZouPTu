package a.baozouptu.ptu.tietu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

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
    private static int pad = Util.dp2Px(10);
    private static Bitmap bitmap = IconBitmapCreator.createCancelBitmap(pad, Color.WHITE, Color.BLUE);
    MicroButtonData[] items;
    Paint itemPaint;

    /**
     * 是否显示边框
     */
    public boolean showRim;
    private Path rim;
    private Paint rimPaint;
    private String picPath;

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
        initItem();
        initRim();
    }

    private void initItem() {
        items = new MicroButtonData[8];
        items[2] = new MicroButtonData(-1, -1, " ");
        items[2].bitmap = FloatImageView.bitmap;
        itemPaint = new Paint();
        itemPaint.setAntiAlias(true);
        itemPaint.setDither(true);
    }

    private void initRim() {
        showRim = true;
        rim = new Path();
        rimPaint = new Paint();
        rimPaint.setDither(true);
        rimPaint.setColor(0x8aaa);
        rimPaint.setStyle(Paint.Style.STROKE);
        rimPaint.setAntiAlias(true);
    }

    /**
     * 设置边框显示或隐藏
     */
    public void setShowRim(boolean isShow) {
        showRim = isShow;
        invalidate();
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
//        item方面的
        int r = getPaddingTop();
        itemBound.top = 0;
        itemBound.bottom = r * 2;
        itemBound.left = getRight() - r * 2;
        itemBound.right = getRight();
        if (itemBound.contains(x, y))
            return true;
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showRim) {
            //画边框
            int r = getPaddingTop();
            rim.moveTo(r, r);
            rim.lineTo(getWidth() - r, r);
            rim.lineTo(r, getHeight() - r);
            rim.lineTo(getWidth() - r, getHeight() - r);
            rim.close();
            canvas.drawPath(rim, rimPaint);

            //画item
            int itop = 0;
            int ileft = getWidth() - r * 2;
            canvas.drawBitmap(items[2].bitmap, itop, ileft, itemPaint);
        }
    }

    public Bitmap getSourceBitmap() {

        return BitmapTool.getLosslessBitmap(picPath);
    }

    public void setSourceBitmap(Activity activity,String path) {
        setImageBitmap(TietuSizeControler.getSrcBitmap(activity,path));
    }

    public void releaseResourse() {

    }

    public String getPicPath() {
        return picPath;
    }
}
