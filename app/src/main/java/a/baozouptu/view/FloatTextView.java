package a.baozouptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.FrameLayout;

import a.baozouptu.BuildConfig;
import a.baozouptu.R;
import a.baozouptu.tools.Util;

/**
 * Created by Administrator on 2016/5/29.
 */
public class FloatTextView extends TextView {
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
    private Context mContext;
    public int verPadding = Util.dp2Px(12);
    /**
     * 用与缩放的最近的位置
     */
    public float lastX, lastY;
    public float currentRatio;
    private Rect pvBoundRect;
    private float mTextSize = 30;
    private String mText = "请输入文字";
    private Paint mPaint = new Paint();
    private Bitmap editItemBitmap;
    public float mWidth, mHeight;
    private int horPadding = verPadding * 2;
    private static int SHOW_SATUS;
    /** 透明 */
    private static final int STATUS_TOUMING=1;
    /**只显示边框*/
    private static final int STATUS_RIM=2;
    /**显示子项目 */
    private static final int STATUS_ITEM=2;
    /**显示输入法，输入状态 */
    private static final int STATUS_INPUT=3;
    private int rimColor;

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

        editItemBitmap = new FloatItemEdit(mContext, (int) verPadding).getBitmap();

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
        setBackgroundColor(Color.RED);
        setPadding(verPadding * 2, verPadding, verPadding * 2, verPadding);

        //获取view的宽和高
        getRealSize();

        startX = (totalWidth - mWidth) / 2;
        startY = (totalHeight - mHeight) / 2;

        SHOW_SATUS=STATUS_INPUT;
        setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItem();
            }
        });

        initItems();
    }

    private void initItems() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rimColor = mContext.getResources().getColor(R.color.float_rim_color,null);
        }else
        {
            rimColor = mContext.getResources().getColor(R.color.float_rim_color);
        }
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
            mTextSize = 2;
            if (currentRatio < 1)
                currentRatio = 1.1f;
        }
        if (mWidth > pvBoundRect.right - pvBoundRect.left + 150) {
            float tr = (pvBoundRect.right - pvBoundRect.left + 150) / mWidth;
            if (tr < 0.999) tr = 0.999f;//会产生小抖动，来回缩放，提醒用户不能说放了
            currentRatio = tr;
        }
        if (mHeight > pvBoundRect.bottom - pvBoundRect.top + 150) {
            float tr = currentRatio = (pvBoundRect.bottom - pvBoundRect.top + 150) / mHeight;
            if (tr < 0.999) tr = 0.999f;
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
            // pvBoundRect = ((PtuView) (ptuFrameLayout.getChildAt(0))).getBound();
            pvBoundRect = new Rect(20, 100, 700, 1100);
        }
        if (startX + mWidth < pvBoundRect.left)
            startX = pvBoundRect.left - mWidth + verPadding;
        if (startY + mHeight < pvBoundRect.top)
            startY = pvBoundRect.top - mHeight + verPadding;
        if (startX > pvBoundRect.right)
            startX = pvBoundRect.right - verPadding;
        if (startY > pvBoundRect.bottom)
            startY = pvBoundRect.bottom - verPadding;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }


    public void hideBackGround() {
        if (SHOW_SATUS!=STATUS_TOUMING) {
            setBackgroundColor(0x00000000);
            setInputType(0);
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            SHOW_SATUS=STATUS_TOUMING;
        }
    }

    public void showItem() {
        if(SHOW_SATUS==STATUS_RIM){
            setBackgroundColor(Color.RED);
        }else if(SHOW_SATUS==STATUS_TOUMING) {
            SHOW_SATUS=STATUS_ITEM;
            setInputType(InputType.TYPE_CLASS_TEXT);
            setBackgroundColor(Color.RED);
        }else {//背景不透明了，显示输入法
            SHOW_SATUS=STATUS_INPUT;
        }
    }

    /**
     * 不显示floatview的边框，只显示其item
     */
    public void hideItem() {
        setBackgroundColor(0x00000000);
        SHOW_SATUS=STATUS_RIM;
    }

    public RectF getBoundRect() {
        return new RectF(startX,startY,startX+mWidth,startY+mHeight);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        switch (SHOW_SATUS){
            case STATUS_RIM:
                mPaint.setColor(rimColor);
                canvas.drawLine(verPadding/1,horPadding/2,
                        mWidth-verPadding/2,mHeight-verPadding/2,
                        mPaint);
            case STATUS_INPUT:

        }
        canvas.drawBitmap(editItemBitmap, new Rect(0, 0, verPadding, verPadding),
                new RectF(canvas.getWidth() - verPadding * 2, 0,
                        canvas.getWidth() - verPadding * 1,
                        verPadding * 2), mPaint);
        super.onDraw(canvas);
    }
}
