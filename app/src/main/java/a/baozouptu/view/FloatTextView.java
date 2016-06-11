package a.baozouptu.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
     * 总的宽和高
     */

    /**
     * 顶点的x，y坐标
     */
    private float mLeft, mTop;
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

    public float getmTop() {
        Util.P.le(DEBUG_TAG, "");
        return mTop;
    }

    public float getmLeft() {
        Util.P.le(DEBUG_TAG, "");
        return mLeft;
    }

    RectF rect;

    public FloatTextView(Context context) {
        super(context);
        mContext = context;
    }

    private void init(Rect pvBoundRect) {
        Util.P.le(DEBUG_TAG, "init");
        //获取ptuView的范围
        this.pvBoundRect = pvBoundRect;
        rect = new RectF(mLeft, mTop, mLeft + mWidth, mTop + mHeight);
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
        mLeft = initLeft;
        mTop = initTop;

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
                float cx = mLeft + mWidth / 2, cy = mTop + mHeight / 2;
                Util.P.le(mWidth, mHeight);
                mText = s.toString();
                setHint("");
                getRealSize();
                //按原来的的中心坐标缩放
                mWidth = getMeasuredWidth();
                mHeight = getMeasuredHeight();
                mLeft = cx - mWidth / 2;
                mTop = cy - mHeight / 2;
                Util.P.le(mWidth, mHeight);
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


    private void initItems() {
        Util.P.le(DEBUG_TAG, "initItems");
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
        float centerX = mLeft + mWidth / 2, centerY = mTop + mHeight / 2;
        adjustSize(ratio);
        mLeft = centerX - mWidth / 2;
        mTop = centerY - mHeight / 2;
        //发生缩放之后可能超出边界，然后适配边界
        adjustEdegeBound();
    }

    private void adjustSize(float ratio) {
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
     * 拖动floatview，利用相对点，相对点变化，
     * view根据原来他与相对点的坐标差移动，避免了拖动
     * 处理的误差
     *
     * @param nx 新的对应点的x坐标
     * @param ny 新的对应点的y坐标
     */
    public void drag(float nx, float ny) {
        Util.P.le(DEBUG_TAG, "drag");
        mLeft = nx - relativeX;
        mTop = ny - relativeY;
        adjustEdegeBound();
    }

    /**
     * 适配floatview的位置,不能超出图片的边界
     * 超出之后移动startx，starty,不影响其它数据
     */
    private void adjustEdegeBound() {
        Util.P.le(DEBUG_TAG, "adjustEdegeBound");
        if (mLeft + mWidth < pvBoundRect.left)
            mLeft = pvBoundRect.left - mWidth + mPadding;
        if (mTop + mHeight < pvBoundRect.top)
            mTop = pvBoundRect.top - mHeight + mPadding;
        if (mLeft > pvBoundRect.right)
            mLeft = pvBoundRect.right - mPadding;
        if (mTop > pvBoundRect.bottom)
            mTop = pvBoundRect.bottom - mPadding;
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

    /**
     * <p> 获取子功能生成点的bitmap，以及bitmap的大小，位置的相关参数
     * <p> 传入initRatio：ptuView上的图片一开始缩放的比例，
     * <p> finalRatio最终需要缩放的比例，
     * <p>方法内部再用RealRatio=finalRation/initRation,算出实际的缩放比例,
     * <p>然后得出子功能获得参数：
     * <p>相对left：rleft=（FloatView的letf-ptuView的图片的left）*realRatio,rtop一样
     * <p>FloatView的宽mwidth*=realRatio获取的实际的宽，高一样
     * <p>最后采用相应方法获取相应大小的Bitmap对象，
     * <p>将参数装入Boundle，返回bitmap对象
     * <p/>
     * <p>此方法会改变floatTextView的大小，textSize，显示状态，
     * <p>但作为最会返回bitmap，floatTextView销毁了，就没有关系了
     *
     * @param bitmapPara 子功能获取的bitmap的参数,0为获取图片相对原始图片的左边距，1为获取图片相对原图片的上边距，
     *                   <p>2为获取图片的宽，3为获取图片的高度
     *@return 是否能成功获取图片
     */
    public boolean prepareResultBitmap(float initRatio, float finalRatio, float[] bitmapPara) {
        if (mText.equals("")) return false;
        float pvWidth = pvBoundRect.right - pvBoundRect.left,
                pvHeight = pvBoundRect.bottom - pvBoundRect.top;
        float realRatio = finalRatio / initRatio;
        float centerX = mLeft + mWidth / 2, centerY = mTop + mHeight / 2;

        //计算出相对原始图片的位置
        float rLeft, rTop;
        if (mLeft < pvBoundRect.left) rLeft = 0;
        else if (mLeft > pvBoundRect.right) rLeft = pvWidth;
        else rLeft = mLeft - pvBoundRect.left;

        if (mTop < pvBoundRect.top) rTop = 0;
        else if (mTop > pvBoundRect.bottom) rTop = pvHeight;
        else rTop = mTop - pvBoundRect.top;

        /**
         * 裁剪文字框,只留下在picture中的部分
         */
        float innerLeft, innerTop, innerRight, innerBottom;
        if (mLeft < pvBoundRect.left) innerLeft = pvBoundRect.left - mLeft;
        else innerLeft = 0;
        if (mTop < pvBoundRect.top) innerTop = pvBoundRect.top - mTop;
        else innerTop = 0;
        bitmapPara[0] = rLeft / initRatio;

        if (mLeft + mWidth > pvBoundRect.right)
            innerRight = pvBoundRect.right - mLeft;
        else
            innerRight = mWidth;
        if (mTop + mHeight > pvBoundRect.bottom)
            innerBottom = pvBoundRect.bottom - mTop;
        else innerBottom = mHeight;
        bitmapPara[1] = rTop / initRatio;

        //获取realratio比例下，浮动视图的图片
        float fWidth = mWidth * realRatio, fHeight = mHeight * realRatio;//最终的宽高
        float tSize = mTextSize, tWidth = mWidth, tHeight = mHeight;
        //文本缩放时缩放程度小于实际宽高的缩放
        //所以循环增加的方式，让宽高达到要求
        while (mWidth < fWidth) {
            mTextSize += (((fWidth - mWidth) / 10) + 1); //每次增加十分之一的差值+1
            setTextSize(mTextSize);
            getRealSize();
        }
        while (mWidth > fWidth) {
            mTextSize -= (((mWidth - fWidth) / 10) + 1);
            if (mTextSize <= 1) {
                mTextSize += (((mWidth - fWidth) / 10) + 1);
                break;
            }
            setTextSize(mTextSize);
            getRealSize();
        }
        realRatio = (mWidth / tWidth);//注意realRatio发生了变化，使用新的RealRatio
        mHeight = tHeight * realRatio;

        mLeft = centerX - mWidth / 2;
        mTop = centerY - mHeight / 2;
        setTextSize(mTextSize);
        setCursorVisible(false);
        requestFocus();
        SHOW_SATUS = STATUS_TOUMING;
        ((PtuFrameLayout) getParent()).redrawFloat();

        innerLeft *= realRatio;
        innerRight *= realRatio;
        innerTop *= realRatio;
        innerBottom *= realRatio;
        //处理边界溢出，避免异常
        innerRight = Math.min(innerRight, mWidth);
        innerBottom = Math.min(innerBottom, mHeight);

        innerLeft = Math.max(0, innerLeft);
        innerLeft = Math.min(Math.min(mWidth, innerLeft), innerRight);
        innerTop = Math.max(0, innerTop);
        innerTop = Math.min(Math.min(mHeight, innerTop), innerBottom);
        bitmapPara[2] = innerRight - innerLeft;
        bitmapPara[3] = innerBottom - innerTop;
       return true;
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
            drawItem(canvas, items.get(2));
            items.get(6).x = mWidth / 2 - mPadding / 2;
            items.get(6).y = rimBottom;
            drawItem(canvas, items.get(6));
        }
        super.onDraw(canvas);
    }


    private void drawItem(Canvas canvas, Item item) {
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
                    RectF item6Bound = new RectF(items.get(6).x - 10, items.get(6).y - 10,
                            items.get(6).x + mPadding + 10, items.get(6).y + mPadding + 10);
                    if (SHOW_SATUS >= STATUS_ITEM && item6Bound.contains(x, y)) {
                        onClickBottomCenter();
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

    private void onClickBottomCenter() {
        Util.P.le(DEBUG_TAG, "onClickBottomCenter");
        mLeft = initLeft;
        mTop = initTop;
        changeShowState(STATUS_ITEM);
        //请求重绘
        ((PtuFrameLayout) getParent()).redrawFloat();
    }
}
