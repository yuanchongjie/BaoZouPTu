package a.baozouptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import a.baozouptu.R;
import a.baozouptu.tools.BitmapTool;
import a.baozouptu.tools.Util;

/**
 * Created by Administrator on 2016/6/30.
 */
public class FloatImageView extends View implements FloatView {
    private static String DEBUG_TAG = "FloatImageView";

    /**
     * 原图片的宽度,高度
     */
    private int srcPicWidth, srcPicHeight;
    /**
     * 当前图片的宽和高
     */
    private int curPicWidth, curPicHeight;
    /**
     * 移动的顶点最后的位置
     */
    public float relativeX, relativeY;
    private static final String ITEM_ROTATE = "rotate";

    private Context mContext;
    public int mPadding = Util.dp2Px(24);


    private Rect picBoundRect;
    public float mWidth, mHeight;


    private Paint mPaint = new Paint();

    /**
     * floatView的宽和高，包括padding，保证加上mleft，mtop之后不会超出原图片的边界
     */
    private static int SHOW_SATUS = STATUS_ITEM;
    private int rimColor;
    private int itemColor;

    private float minMoveDis = Util.dp2Px(3);
    private long downTime = 0;
    private float downY;
    private float downX;
    private boolean hasUp = true;
    private int lastSelectionId;

    Item[] items = new Item[]{null, null, null, null, null, null, null, null};
    /**
     * 内部的bitmap相关的域
     */
    private Bitmap sourceBitmap;
    private BitmapTool bitmapTool = new BitmapTool();
    private float rotateAngle;
    private float totalRatio;
    private Canvas sourceCanvas;
    private Bitmap tietuDitu;
    private int picWidth;
    private int picHeight;
    private int tietuWidth;
    private int tietuHeight;
    private int curTHeight;
    private int curTWidth;
    private int centerY;
    private int centerX;
    private float lastRatio;
    private Bitmap tempBitmap;


    public FloatImageView(Context context) {
        super(context);
        mContext = context;
    }

    public FloatImageView(Context context, Rect pvBoundRect) {
        super(context);
        mContext = context;
        this.picBoundRect = pvBoundRect;
    }

    @Override
    public void initItems() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rimColor = mContext.getResources().getColor(R.color.float_rim_color, null);
            itemColor = mContext.getResources().getColor(R.color.float_item_color, null);
        } else {
            rimColor = mContext.getResources().getColor(R.color.float_rim_color);
            itemColor = mContext.getResources().getColor(R.color.float_item_color);
        }
        FloatItemBitmap floatItemBitmap = new FloatItemBitmap();
        Item item = new Item(-1, -1, ITEM_ROTATE);
        item.bitmap = floatItemBitmap.getRotateBitmap(mContext, mPadding, itemColor);
        items[7] = item;
    }

    /**
     * 设置贴图的bitmap并初始化
     *
     * @param bitmap
     */
    public void setSourceBitmapAndInit(Bitmap bitmap) {
        sourceBitmap = bitmap;
        init();
        adjustSize();
        invalidate();
    }


    /**
     * 设置贴图的bitmap并初始化
     *
     * @param path
     */
    public void setSourceBitmapAndInit(String path) {
        setSourceBitmapAndInit(bitmapTool.getLosslessBitmap(path));
    }


    /**
     * 初始化
     */
    private void init() {
        lastRatio=totalRatio = 1;
        rotateAngle = 0;

        picWidth = picBoundRect.right - picBoundRect.left;
        picHeight = picBoundRect.bottom - picBoundRect.top;
        tietuWidth = sourceBitmap.getWidth();
        tietuHeight = sourceBitmap.getHeight();
        centerX =picWidth/2;
        centerY =picHeight/2;

        tietuDitu = Bitmap.createBitmap(picWidth,
                picHeight, Bitmap.Config.ARGB_8888);
        sourceCanvas = new Canvas(tietuDitu);

    }


    private void adjustSize() {
        curTWidth = (int)(tietuWidth * totalRatio);
        curTHeight = (int)(tietuHeight * totalRatio);

        if (curTWidth > picWidth && curTHeight > picHeight) {
            totalRatio = Math.max((float) picWidth / (float) curTWidth,
                    (float) picHeight / (float) curTHeight);
            //总的缩放比例改变，当前宽高发生改变
            curTWidth = (int)(tietuWidth * totalRatio);
            curTHeight = (int)(tietuHeight * totalRatio);
        }
    }

    private void adjustBound() {

    }

    @Override
    public float getmWidth() {
        return mWidth;
    }

    @Override
    public float getmHeight() {
        return mHeight;
    }

    @Deprecated
    @Override
    public float getfTop() {
        return 0;
    }

    @Deprecated
    @Override
    public float getfLeft() {
        return 0;
    }

    @Override
    public float getRelativeX() {
        return relativeX;
    }

    @Override
    public float getRelativeY() {
        return relativeY;
    }

    @Override
    public void setRelativeX(float relativeX) {
        this.relativeX = relativeX;
    }

    @Override
    public void setRelativeY(float relativeY) {
        this.relativeY = relativeY;


    }

    @Override
    public int getShowState() {
        return SHOW_SATUS;
    }

    @Override
    public void changeShowState(int state) {
        if (SHOW_SATUS != state) {
            SHOW_SATUS = state;
            invalidate();
        }
        Util.P.le(DEBUG_TAG, "changeShowState=" + SHOW_SATUS);
    }

    @Override
    public boolean prepareResultBitmap(float initRatio, RectF innerRect, RectF picRect) {
        return false;
    }

    @Override
    public void scale(float ratio) {

    }

    @Override
    public void adjustSize(float ratio) {

    }

    @Override
    public void adjustEdegeBound() {

    }

    @Override
    public void drag(float nx, float ny) {

    }

    @Override
    public void drawItem(Canvas canvas, Item item) {

    }

    @Override
    public void setDownState() {
    }

    @Override
    public int getDownState() {
        return 0;
    }

    @Deprecated
    @Override
    public void onClickBottomCenter() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(rotateAngle);
        //先将图绘制到底图上面，再将底图绘制到view上面
        if(totalRatio!=lastRatio) {//如果要绘制的图的大小发生变化
            tempBitmap=sourceBitmap.createScaledBitmap(
                    sourceBitmap,curTWidth,curTHeight,true);
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), tempBitmap);
        bitmapDrawable.setBounds(centerX-curTWidth/2,centerY-curTHeight/2,centerX+curTWidth/2,centerY+curTHeight/2);
        bitmapDrawable.draw(canvas);
        canvas.restore();
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
