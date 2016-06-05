package a.baozouptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.input.InputManager;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;

import a.baozouptu.R;
import a.baozouptu.tools.Util;

/**
 * Created by Administrator on 2016/5/29.
 */
public class FloatTextView extends EditText implements FloatView {
    /**
     * 总的宽和高
     */
    public float totalWidth, totalHeight;

    /**
     * 顶点的x，y坐标
     */
    private float startX, startY;
    /**
     * 移动的顶点最后的位置
     */
    public float relativeX, relativeY;
    /**
     * 当前的操作状态
     */
    private static int CURRENT_STATUS = 0;
    private static final int STATUS_TRANSLATE = 1;
    private static final int STATUS_SCALE = 2;
    private static final int STATUS_ROTATE = 3;
    private static String ITEM_NAME;
    private static final String ITEM_EDTI = "edit";
    private static final String ITEM_BOTTOM_CENTER = "bcenter";

    private Context mContext;
    public int mPadding = Util.dp2Px(24);
    /**
     * 用与缩放的最近的位置
     */
    public float lastX, lastY;
    public float currentRatio;
    private Rect pvBoundRect;
    private float mTextSize = 30;
    private String mText = "点击输入文字";
    private Paint mPaint = new Paint();
    public float mWidth, mHeight;
    private static int SHOW_SATUS;
    private int rimColor;
    private int itemColor;
    private int downShowState;

    public void setDownState() {
        downShowState = SHOW_SATUS;
    }

    public int getDownState() {
        return downShowState;
    }

    /**
     * 表示item信息的类
     */
    private class Item {
        float x;
        float y;
        String name;
        Bitmap bitmap;

        Item(float x, float y, String name) {
            this.x = x;
            this.y = y;
            this.name = name;
        }
    }

    ArrayList<Item> items = new ArrayList<>(8);
    /**
     * 边框的上下左右位置,注意这个位置相对与文本的边界发生了偏移
     */
    private float rimLeft, rimTop, rimRight, rimBottom;

    /**
     * 传入布局容器的宽高，确定view的位置
     *
     * @param mContext
     * @param totalWidth
     * @param totalHeight
     */
    public FloatTextView(Context mContext, int totalWidth, int totalHeight) {
        super(mContext);
        this.mContext = mContext;
        init(totalWidth, totalHeight);
    }

    public Bitmap getEditItemBitmap() {
        return bitmapToView;
    }

    public int getShowState() {
        return SHOW_SATUS;
    }

    public float getRelativeY() {
        return relativeY;
    }

    public float getRelativeX() {
        return relativeX;
    }

    public float getStartY() {
        return startY;
    }

    public float getStartX() {
        return startX;
    }


    /**
     * 含有view内容的bitmap
     */
    public Bitmap bitmapToView;
    RectF rect;

    public FloatTextView(Context context) {
        super(context);
        mContext = context;
    }

    private void init(float totalWidth, float totalHeight) {
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;

        rect = new RectF(startX, startY, startX + mWidth, startY + mHeight);
/**
 * 设置文字布局
 */
        setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams layoutParms = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParms);

        setText(mText);
        setTextSize(mTextSize);
        setTextColor(Color.BLACK);
        setPadding(mPadding, mPadding, mPadding, mPadding);
        setBackground(null);

        //获取view的宽和高
        getRealSize();

        startX = (totalWidth - mWidth) / 2;
        startY = totalHeight - mHeight;

        SHOW_SATUS = STATUS_INPUT;

        initItems();
    }

    private void initItems() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rimColor = mContext.getResources().getColor(R.color.float_rim_color, null);
            itemColor = mContext.getResources().getColor(R.color.float_item_color, null);
        } else {
            rimColor = mContext.getResources().getColor(R.color.float_rim_color);
            itemColor = mContext.getResources().getColor(R.color.float_item_color);
        }
        items.add(null);//第0个
        items.add(null);//第1个
        FloatItemBitmap floatItemBitmap = new FloatItemBitmap();
        Item item = new Item(-1, -1, ITEM_EDTI);
        item.bitmap = floatItemBitmap.getEditBitmap(mContext, mPadding, itemColor);
        items.add(item);//第2个
        items.add(null);//第3个
        items.add(null);//第4个
        items.add(null);//第5个
        item = new Item(-1, -1, ITEM_BOTTOM_CENTER);
        item.bitmap = floatItemBitmap.getToBottomCenterBitmap(mContext, mPadding, itemColor);
        items.add(item);//第6个
        items.add(null);//第7个
    }

    /**
     * 在view还没回执之前获取到他的宽高，并且将其值赋给
     * mwidth，mHeight
     */
    private void getRealSize() {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        this.measure(width, height);
        mWidth = this.getMeasuredWidth();
        mHeight = this.getMeasuredHeight();
    }

    /**
     * 缩放视图，重置视图的宽和高，然后重绘
     *
     * @param ratio
     */
    public void scale(float ratio) {
        float centerX = startX + mWidth / 2, centerY = startY + mHeight / 2;
        adjustSize(ratio);
        startX = centerX - mWidth / 2;
        startY = centerY - mHeight / 2;
        //发生缩放之后可能超出边界，然后适配边界
        adjustEdegeBound();
    }

    /**
     * 拖动floatview，利用相对点，相对点变化，
     * view根据原来他与相对点的坐标差移动，避免了拖动
     * 处理的误差
     *
     * @param nx 新的对应点的x坐标
     * @param ny 新的对应点的y坐标
     */
    public void drag(float nx, float ny) {
        startX = nx - relativeX;
        startY = ny - relativeY;
        adjustEdegeBound();
    }

    private void adjustSize(float ratio) {
        currentRatio = ratio;
        //尝试直接设置
        float tsize = mTextSize;
        tsize *= ratio;
        setTextSize(tsize);
        getRealSize();
        //获取ptuView的范围
        if (pvBoundRect == null) {
            PtuFrameLayout ptuFrameLayout = (PtuFrameLayout) getParent();
            //pvBoundRect = ((PtuView) (ptuFrameLayout.getChildAt(0))).getBound();
            pvBoundRect = new Rect(20, 100, 700, 1100);
        }
        //根据范围调整比例
        if (mTextSize <= 2) {
            mTextSize = 3;
            if (currentRatio < 1)
                currentRatio = 1f;
        }
        if (mWidth > pvBoundRect.right - pvBoundRect.left + 150) {
            float tr = (pvBoundRect.right - pvBoundRect.left + 150) / mWidth;
            if (tr < 0.999) tr = 0.999f;//会产生小抖动，来回缩放，提醒用户不能说放了
            currentRatio = tr;
        }
        if (mHeight > pvBoundRect.bottom - pvBoundRect.top + 150) {
            float tr = currentRatio = (pvBoundRect.bottom - pvBoundRect.top + 150) / mHeight;
            if (tr < 0.996) tr = 0.996f;
            currentRatio = tr;
        }
        //比例设置为调整后的大小
        mTextSize *= currentRatio;
        if (currentRatio == ratio)//没有变化，直接返回
            return;

        setTextSize(mTextSize);
        getRealSize();
    }

    /**
     * 适配floatview的位置,不能超出图片的边界
     * 超出之后移动startx，starty,不影响其它数据
     */
    private void adjustEdegeBound() {
        //获取ptuView的范围
        if (pvBoundRect == null) {
            PtuFrameLayout ptuFrameLayout = (PtuFrameLayout) getParent();
            pvBoundRect = ((PtuView) (ptuFrameLayout.getChildAt(0))).getBound();
        }
        if (startX + mWidth < pvBoundRect.left)
            startX = pvBoundRect.left - mWidth + mPadding;
        if (startY + mHeight < pvBoundRect.top)
            startY = pvBoundRect.top - mHeight + mPadding;
        if (startX > pvBoundRect.right)
            startX = pvBoundRect.right - mPadding;
        if (startY > pvBoundRect.bottom)
            startY = pvBoundRect.bottom - mPadding;
    }

    public void changeShowState(int state) {
        if (SHOW_SATUS != state) {
            SHOW_SATUS = state;
            invalidate();
        }
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (SHOW_SATUS >= STATUS_RIM) {//要显示的东西不止边框
            mPaint.setColor(rimColor);
            mPaint.setStrokeWidth(3);

            mPaint.setTextSize(mTextSize);
            Paint.FontMetrics fm = mPaint.getFontMetrics();
            /**
             * 文本默认不局中，计算中间位置
             */
            float md = sp2px(fm.ascent - fm.top), nd = sp2px(fm.bottom - fm.descent), dd = md - nd;
            rimLeft = mPadding - md;
            rimTop = mPadding + dd;
            rimRight = mWidth - mPadding + md;
            rimBottom = mHeight - mPadding;
            //上边的线
            canvas.drawLine(rimLeft, rimTop, rimRight, rimTop, mPaint);
            //左边的线
            canvas.drawLine(rimLeft, rimTop, rimLeft, rimBottom, mPaint);
            //下边的线
            canvas.drawLine(rimLeft, rimBottom, rimRight, rimBottom, mPaint);
            //右边的线
            canvas.drawLine(rimRight, rimTop, rimRight, rimBottom, mPaint);
        }
        if (SHOW_SATUS >= STATUS_ITEM) {
            mPaint.setColor(itemColor);
            items.get(2).x = rimRight - mPadding / 2;
            items.get(2).y = rimTop - mPadding / 2;
            drawItems(canvas, items.get(2));
            items.get(6).x = mWidth / 2 - mPadding / 2;
            items.get(6).y = rimBottom;
            drawItems(canvas, items.get(6));
        }
        super.onDraw(canvas);
    }

    private void drawItems(Canvas canvas, Item item) {
        canvas.drawBitmap(item.bitmap, new Rect(0, 0, mPadding, mPadding),
                new RectF(item.x, item.y,
                        item.x + mPadding,
                        item.y + mPadding),
                mPaint);
    }

    /**
     * 判断点击的发生的位置，根据相应条件判断是否重回自己，告诉父图是否需要重新layout；
     *
     * @param x
     * @param y
     * @return
     */
    public boolean showLayoutOrRefreshByClick(float x, float y) {
        x -= startX;
        y -= startY;//变换为本view的坐标
        if (new RectF(items.get(6).x, items.get(2).y,
                items.get(6).x + mPadding,
                items.get(6).y + mPadding).contains(x, y)) {
            onClickBottomCenter();
            return true;
        }//点击发生在这个floatView之外
        else if (x < 0 || y < 0 || x > mWidth || y > mHeight) {
            if(SHOW_SATUS==STATUS_INPUT){//取消输入法
                InputMethodManager imm = ( InputMethodManager ) mContext.getSystemService( Context.INPUT_METHOD_SERVICE );
                if ( imm.isActive( ) ) {
                    imm.hideSoftInputFromWindow( this.getApplicationWindowToken( ) , 0 );
                }
            }
            changeShowState(STATUS_TOUMING);
        } else if (SHOW_SATUS < STATUS_INPUT) {
            changeShowState(STATUS_INPUT);
        }
        return false;
    }

    private void onClickBottomCenter() {
        startX = (totalWidth - mWidth) / 2;
        startY = totalHeight - mHeight;
        SHOW_SATUS=STATUS_INPUT;
    }
}
