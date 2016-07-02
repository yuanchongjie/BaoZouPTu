package a.baozouptu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;

import a.baozouptu.R;
import a.baozouptu.tools.GeoUtil;
import a.baozouptu.tools.Util;

/**
 * 添加textView的顶部视图
 * Created by Administrator on 2016/5/29.
 */
public class FloatTextView extends EditText implements FloatView {
    private static String DEBUG_TAG = "FloatTextView";

    /**
     * 顶点的x，y坐标,相对于父组件的值，保证不会超出当前显示的图片的边界
     */
    private float fLeft, fTop;
    /**
     * 移动的顶点最后的位置
     */
    public float relativeX, relativeY;
    private static final String ITEM_EDTI = "edit";
    private static final String ITEM_BOTTOM_CENTER = "bcenter";

    private Context mContext;
    public int mPadding = Util.dp2Px(24);


    public float currentRatio;
    private Rect pvBoundRect;
    private float mTextSize = 30;
    private String mHint = "点击输入文字";
    private Paint mPaint = new Paint();
    /**
     * floatView的宽和高，包括padding，保证加上mleft，mtop之后不会超出原图片的边界
     */
    public float mWidth, mHeight;
    private static int SHOW_SATUS = STATUS_INPUT;
    private int rimColor;
    private int itemColor;
    private int downShowState;
    private float minMoveDis = Util.dp2Px(3);
    private long downTime = 0;
    private float downY;
    private float downX;
    private boolean hasUp = true;
    private int lastSelectionId;
    /**
     * 初始的位置，也是常用位置
     */
    private float initLeft, initTop;
    private String mText = "";
    private int mBackGroundColor=0xffffffff;

    public void setDownState() {
        downShowState = SHOW_SATUS;
    }

    public int getDownState() {
        return downShowState;
    }

    public int getPadding() {
        return mPadding;
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
     */
    public FloatTextView(Context mContext, Rect pvBoundRect) {
        super(mContext);
        this.mContext = mContext;
        init(pvBoundRect);
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

    public float getfTop() {
        return fTop;
    }

    public float getfLeft() {
        return fLeft;
    }

    RectF rect;

    public FloatTextView(Context context) {
        super(context);
        mContext = context;
    }

    private void init(final Rect pvBoundRect) {
        Util.P.le(DEBUG_TAG, "init");
        //获取ptuView的范围
        this.pvBoundRect = pvBoundRect;
        rect = new RectF(fLeft, fTop, fLeft + mWidth, fTop + mHeight);
/**
 * 设置文字布局
 */
        setGravity(Gravity.CENTER);
        final FrameLayout.LayoutParams layoutParms = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParms);

        setHint(mHint);
        setTextSize(mTextSize);
        setTextColor(Color.BLACK);
        setPadding(mPadding, mPadding, mPadding, mPadding);
        setBackground(null);
        setSelected(true);
        //获取view的宽和高
        getRealSize();

        //获取了实际的宽高之后才能获取初始位置
        initLeft = (pvBoundRect.left + pvBoundRect.right) / 2 - mWidth / 2;
        initTop = pvBoundRect.bottom - mHeight;
        fLeft = initLeft;
        fTop = initTop;

        initItems();
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastSelectionId = start + after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //原来的中心坐标
                float cx = fLeft + mWidth / 2, cy = fTop + mHeight / 2;
                mText = s.toString();
                setHint("");
                getRealSize();
                float tWidth = mWidth;
                //判断是否超出总长，超出时缩小
                float totalWidth = (pvBoundRect.right - pvBoundRect.left) * 2;
                if (mWidth > totalWidth) {
                    mTextSize = getTextSizeByWidth(mTextSize, totalWidth);
                    // mHeight*=(mWidth/tWidth);
                }

                float tHeight = mHeight;
                float totalHeight = (pvBoundRect.bottom - pvBoundRect.top) * 2;
                if (mHeight > totalHeight) {
                    mTextSize = getTextSizeByHeight(mTextSize, totalHeight);
                    // mWidth*=(mHeight/tHeight);
                }

                //按原来的的中心坐标缩放
                fLeft = cx - mWidth / 2;
                fTop = cy - mHeight / 2;
                ((PtuFrameLayout) getParent()).redrawFloat();
                if (lastSelectionId > 0) {
                    setCursorVisible(true);
                    requestFocus();
                    setSelection(lastSelectionId);
                }
            }
        });
        setLongClickable(false);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }


    public void initItems() {
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
     * 当view的某个影响宽高的参数改变之后，在view还没绘制之前获取到他的宽高，
     * <p>并且将其值赋给mwidth，mHeight，此方法只会改变mwidth，mHeight
     */
    private void getRealSize() {
        Util.P.le(DEBUG_TAG, "getRealSize");
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
        Util.P.le(DEBUG_TAG, "scale");
        float centerX = fLeft + mWidth / 2, centerY = fTop + mHeight / 2;
        adjustSize(ratio);
        fLeft = centerX - mWidth / 2;
        fTop = centerY - mHeight / 2;
        //发生缩放之后可能超出边界，然后适配边界
        adjustEdegeBound();
    }

    public void adjustSize(float ratio) {
        Util.P.le(DEBUG_TAG, "adjustSize");
        currentRatio = ratio;
        //尝试直接设置
        float tsize = mTextSize;
        tsize *= ratio;
        setTextSize(tsize);
        getRealSize();
        //根据范围调整比例
        if (mTextSize <= 2) {
            mTextSize = 3;
            if (currentRatio < 1)
                currentRatio = 1f;
        }
        //让长宽大约多出两个汉字
        float totalWidth = (pvBoundRect.right - pvBoundRect.left) * 2;
        if (mWidth > totalWidth) {
            float tr = totalWidth / mWidth;
            if (tr < 0.999) tr = 0.999f;//会产生小抖动，来回缩放，提醒用户不能说放了
            currentRatio = tr;
        }
        float totalHeight = (pvBoundRect.bottom - pvBoundRect.top) * 2;
        if (mHeight > totalHeight) {
            float tr = currentRatio = totalHeight / mHeight;
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
     * 拖动floatview，利用相对点，相对点变化，
     * view根据原来他与相对点的坐标差移动，避免了拖动
     * 处理的误差
     *
     * @param nx 新的对应点的x坐标
     * @param ny 新的对应点的y坐标
     */
    public void drag(float nx, float ny) {
        fLeft = nx - relativeX;
        fTop = ny - relativeY;
        adjustEdegeBound();
    }

    /**
     * 适配floatview的位置,不能超出图片的边界,不算padding的内部就不能超出边界
     * 超出之后移动startx，starty,不影响其它数据
     */
    public void adjustEdegeBound() {
        Util.P.le(DEBUG_TAG, "adjustEdegeBound");
        if (fLeft + mWidth - mPadding < pvBoundRect.left)//右边小于左边界
            fLeft = pvBoundRect.left - mWidth + mPadding;
        if (fTop + mHeight - mPadding < pvBoundRect.top)//下边小于上边界
            fTop = pvBoundRect.top - mHeight + mPadding;
        if (fLeft + mPadding > pvBoundRect.right)//左边大于右边界
            fLeft = pvBoundRect.right - mPadding;
        if (fTop + mPadding > pvBoundRect.bottom)//上边大于下边界
            fTop = pvBoundRect.bottom - mPadding;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    private int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public boolean prepareResultBitmap(float initRatio, RectF innerRect, RectF picRect) {
        if (mText.trim().equals("")) return false;
        float realRatio = 1 / initRatio;
        float centerX = fLeft + mWidth / 2, centerY = fTop + mHeight / 2;

        //计算出相对原始图片的位置
        float rLeft, rTop;
        float textLeft = fLeft + mPadding, textTop = fTop + mPadding,
                textWidth = mWidth - mPadding * 2, textHeight = mHeight - mPadding * 2;
        if (textLeft < pvBoundRect.left) rLeft = 0;
        else rLeft = textLeft - pvBoundRect.left;
        picRect.left = rLeft * realRatio;

        if (textTop < pvBoundRect.top) rTop = 0;
        else rTop = textTop - pvBoundRect.top;
        picRect.top = rTop * realRatio;

        //裁剪文字框,只留下在picture中的部分,
        // innerxxx:先计算出相对于内部文本的位置
        float innerLeft, innerTop, innerRight, innerBottom;
        if (textLeft < pvBoundRect.left) innerLeft = pvBoundRect.left - textLeft;
        else innerLeft = 0;
        if (textTop < pvBoundRect.top) innerTop = pvBoundRect.top - textTop;
        else innerTop = 0;

        if (textLeft + textWidth > pvBoundRect.right)
            innerRight = pvBoundRect.right - textLeft;
        else
            innerRight = textWidth;
        if (textTop + textHeight > pvBoundRect.bottom)
            innerBottom = pvBoundRect.bottom - textTop;
        else innerBottom = textHeight;

        //理想的缩放比例缩放后，裁剪的内部位置
        innerLeft = innerLeft * realRatio;
        innerRight = innerRight * realRatio;
        innerTop = innerTop * realRatio;
        innerBottom = innerBottom * realRatio;

        //picture上需要的位置
        picRect.right = picRect.left + (innerRight - innerLeft);
        picRect.bottom = picRect.top + (innerBottom - innerTop);

        //获取realratio比例下，内部text的宽高text
        float fTextWidth = textWidth * realRatio, fTextHeight = textHeight * realRatio;
        float fWidth = fTextWidth + mPadding * 2, fHeight = fTextHeight + mPadding * 2;

        //如果要获取的view过大 ，超过总内存的1/5  ，减小放大比例
        long limit = Runtime.getRuntime().maxMemory() / 5;
        if (fWidth * fHeight * 4 > limit) {
            innerLeft /= realRatio;//除去原来的比例
            innerTop /= realRatio;
            innerRight /= realRatio;
            innerBottom /= realRatio;
            float newRatio = (float) Math.sqrt(limit / (mWidth * mHeight * 4));
            //获取ratio比例下，内部text的宽高text
            innerLeft = innerLeft * newRatio;
            innerRight = innerRight * newRatio;
            innerTop = innerTop * newRatio;
            innerBottom = innerBottom * newRatio;
            fTextWidth = textWidth * newRatio;
            fWidth = fTextWidth + mPadding * 2;
        }
        innerRect.left = innerLeft + mPadding;
        innerRect.top = innerTop + mPadding;
        innerRect.right = innerRight + mPadding;
        innerRect.bottom = innerBottom + mPadding;
        //根据最终比例，相应的缩放图片，但不能完全匹配

        mTextSize = getTextSizeByWidth(mTextSize, fWidth);
        fLeft = centerX - mWidth / 2;
        fTop = centerY - mHeight / 2;
        setCursorVisible(false);
        requestFocus();
        SHOW_SATUS = STATUS_TOUMING;
        ((PtuFrameLayout) getParent()).redrawFloat();
        return true;
    }

    /**
     * 调整文字的大小是文本框的宽度与给定值误差在一定范围之内
     * 文本缩放时缩放程度小于实际宽高的缩放,另外，
     * 文本框正常受到字数的限制，每个字都增加一些，所以总厂不能线性增加，而是阶梯增加的
     * 所以循环增加的方式，让宽高达到要求
     * <p/>
     * <p>注意操作的同时textsize和mwidth，mheight已经设置好了</p>
     *
     * @param curSize 当前的文字大小
     * @param fWidth  最终要求的宽度
     * @return 达到要求的文字size
     */
    private float getTextSizeByWidth(float curSize, float fWidth) {
        //缩放只针对内部的文本框，所以以为本匡长宽做比例运算
        float textWidth = mWidth - mPadding * 2, fTextWidth = fWidth - mPadding * 2;
        while (textWidth < fTextWidth) {
            float add = (fTextWidth - textWidth) / (textWidth + 2) * curSize;//避免除以0，同时每次变化值少一点
            if (add < 0.1) add = 0.1f;
            curSize += add;
            setTextSize(curSize);
            getRealSize();
            textWidth = mWidth - mPadding * 2;
        }
        while (textWidth > fTextWidth) {
            float red = (textWidth - fTextWidth) / (textWidth + 2) * curSize;//避免除以0，同时每次变化值少一点
            if (red < 0.1) red = 0.1f;
            curSize -= red;
            if (curSize <= 1) {
                curSize = 1;
                break;
            }
            setTextSize(curSize);
            getRealSize();
            textWidth = mWidth - mPadding * 2;
        }
        return curSize;
    }

    /**
     * 调整文字的大小是文本框的高度与给定值误差在一定范围之内
     * 文本缩放时缩放程度小于实际高高的缩放,另外，
     * 文本框正常受到字数的限制，每个字都增加一些，所以总厂不能线性增加，而是阶梯增加的
     * 所以循环增加的方式，让高高达到要求
     * <p/>
     * <p>注意操作的同时textsize和mwidth，mheight已经设置好了</p>
     *
     * @param curSize 当前的文字大小
     * @param fHeight 最终要求的高度
     * @return 达到要求的文字size
     */
    private float getTextSizeByHeight(float curSize, float fHeight) {
        //缩放只针对内部的文本框，所以以为本匡长高做比例运算
        float textHeight = mHeight - mPadding * 2, fTextHeight = fHeight - mPadding * 2;
        while (textHeight < fTextHeight) {
            float add = (fTextHeight - textHeight) / (textHeight + 2) * curSize;//避免除以0，同时每次变化值少一点
            if (add < 0.1) add = 0.1f;
            curSize += add;
            setTextSize(curSize);
            getRealSize();
            textHeight = mHeight - mPadding * 2;
        }
        while (textHeight > fTextHeight) {
            float red = (textHeight - fTextHeight) / (textHeight + 2) * curSize;//避免除以0，同时每次变化值少一点
            if (red < 0.1) red = 0.1f;
            curSize -= red;
            if (curSize <= 1) {
                curSize = 1;
                break;
            }
            setTextSize(curSize);
            getRealSize();
            textHeight = mHeight - mPadding * 2;
        }
        return curSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Util.P.le(DEBUG_TAG, "onDraw()");
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

        mPaint.setColor(mBackGroundColor);
        canvas.drawRect(rimLeft,rimTop,rimRight,rimBottom,mPaint);

        if (SHOW_SATUS >= STATUS_RIM) {//要显示的东西不止边框
            mPaint.setColor(rimColor);
            mPaint.setStrokeWidth(3);
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
            drawItem(canvas, items.get(2));
            items.get(6).x = mWidth / 2 - mPadding / 2;
            items.get(6).y = rimBottom;
            drawItem(canvas, items.get(6));
        }
       super.onDraw(canvas);
    }

    /**
     *
     * @param canvas
     * @param item
     */
    public void drawItem(Canvas canvas, Item item) {
        canvas.drawBitmap(item.bitmap, new Rect(0, 0, mPadding, mPadding),
                new RectF(item.x, item.y,
                        item.x + mPadding,
                        item.y + mPadding),
                mPaint);
    }

    /**
     * 改变当前的显示状态，显示变换操作逻辑处理中心，有些复杂，要注意
     *
     * @param state
     */
    public void changeShowState(int state) {
        if (SHOW_SATUS != state) {
            if (SHOW_SATUS == STATUS_INPUT && state != STATUS_INPUT) {//如果当前是输入状态，改变到非输入状态，这需要取消输入法
                //    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.hideSoftInputFromWindow(this.getApplicationWindowToken(), 0);
            }
            //不需要重绘的情况
            if (SHOW_SATUS == STATUS_ITEM && state == STATUS_INPUT
                    || SHOW_SATUS == STATUS_INPUT && state == STATUS_ITEM)
                SHOW_SATUS = state;
            else {//需要重绘的情况
                SHOW_SATUS = state;
                invalidate();
            }
        }
        Util.P.le(DEBUG_TAG, "changeShowState=" + SHOW_SATUS);
    }

    /**
     * 点击发生在view上，根据具体位置和相应条件判断是否重回自己，告诉父图是否需要重新layout；
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (hasUp) hasUp = false;
                downX = event.getX();
                downY = event.getY();
                //两次点击时间必须大于双击时间+200，阻止双击事件
                if (System.currentTimeMillis() - downTime <= ViewConfiguration.getDoubleTapTimeout() + 200)
                    break;
                downTime = System.currentTimeMillis();
                //大于等于显示item时才分发事件，引起输入法
                if (SHOW_SATUS >= STATUS_ITEM)
                    super.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                //发生了点击事件,点击事件都应该给floatView消费掉，非点击事件交由ptuFrameLayout处理
                if (hasUp == false && GeoUtil.getDis(downX, downY, event.getX(), event.getY()) < minMoveDis
                        && System.currentTimeMillis() - downTime < 500) {
                    Util.P.le(DEBUG_TAG, "drawItem");
                    float x = event.getX();//变换为本view的坐标
                    float y = event.getY();
                    //内部子item的处理
                    //点击到了重置位置按钮
                    RectF item6Bound = new RectF(items.get(6).x - 10, items.get(6).y,
                            items.get(6).x + mPadding + 10, items.get(6).y + mPadding + 10);
                    if (SHOW_SATUS >= STATUS_ITEM && item6Bound.contains(x, y)) {
                        onClickBottomCenter();
                        return true;
                    }
                    RectF item2Bound = new RectF(items.get(2).x, items.get(2).y - 10,
                            items.get(2).x + mPadding + 10, items.get(2).y + mPadding);
                    if (SHOW_SATUS >= STATUS_ITEM && item2Bound.contains(x, y)) {
                        if (mBackGroundColor == 0x00000000)
                            mBackGroundColor = 0xffffffff;
                        else
                            mBackGroundColor = 0x00000000;
                        invalidate();
                        return true;
                    }
                    //点击到view的其他部分
                    if (SHOW_SATUS < STATUS_ITEM) {//没显示item时就不发送事件，不弹出输入法
                        changeShowState(STATUS_ITEM);
                        return true;
                    } else {//显示item时，就要弹出输入法
                        Util.P.le(DEBUG_TAG, "显示输入法了");
                        super.dispatchTouchEvent(event);
                        changeShowState(STATUS_INPUT);
                        return true;
                    }
                }
                hasUp = true;
        }
        return false;
    }

    public void onClickBottomCenter() {
        Util.P.le(DEBUG_TAG, "onClickBottomCenter");
        fLeft = initLeft;
        fTop = initTop;
        changeShowState(STATUS_ITEM);
        //请求重绘
        ((PtuFrameLayout) getParent()).redrawFloat();
    }
}
