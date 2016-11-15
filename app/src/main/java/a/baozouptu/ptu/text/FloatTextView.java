package a.baozouptu.ptu.text;

import android.content.Context;
import android.graphics.Bitmap;
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
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.FileTool;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.MU;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.FloatView;
import a.baozouptu.ptu.MicroButtonData;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.repealRedo.TextStepData;
import a.baozouptu.ptu.view.IconBitmapCreator;
import a.baozouptu.ptu.view.PtuFrameLayout;
import a.baozouptu.ptu.view.PtuView;

/**
 * 添加textView的顶部视图
 * Created by Administrator on 2016/5/29.
 */
public class FloatTextView extends EditText implements FloatView {
    private static String TAG = "FloatTextView";

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
    private static final String ITEM_JUSTIFY = "对齐";
    private Context mContext;
    public int mPadding = Util.dp2Px(24);


    public float currentRatio;
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
    private Rect picBound;
    private String mText = "";
    private int mBackGroundColor = 0x00000000;
    private Bitmap[] justifyBm;
    private Bitmap resultBm;

    public void setDownState() {
        downShowState = SHOW_SATUS;
    }

    public int getDownState() {
        return downShowState;
    }

    public int getPadding() {
        return mPadding;
    }


    ArrayList<MicroButtonData> items = new ArrayList<>(8);
    /**
     * 边框的上下左右位置,相对于文本框的左上角,注意这个位置相对与文本的边界发生了偏移
     */
    private float rimLeft, rimTop, rimRight, rimBottom;

    /**
     * 传入布局容器的宽高，确定view的位置
     *
     * @param picBound
     */
    public FloatTextView(Context mContext, Rect picBound) {
        super(mContext);
        this.mContext = mContext;
        init(picBound);
    }

    public int getShowState() {
        return SHOW_SATUS;
    }

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


    public float getRelativeX() {
        return relativeX;
    }

    @Override
    public float getmWidth() {
        return mWidth;
    }

    @Override
    public float getmHeight() {
        return mHeight;
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

    private void init(final Rect picBound) {
        Util.P.le(TAG, "init");
        //获取ptuView的范围
        this.picBound = picBound;
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
        getSizePreliminarily();

        //获取了实际的宽高之后才能获取初始位置
        fLeft = (picBound.left + picBound.right) / 2 - mWidth / 2;
        fTop = picBound.bottom - mHeight;
        setRim();
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
                getSizePreliminarily();
                float tWidth = mWidth;
                //判断是否超出总长，超出时缩小
                float totalWidth = (picBound.right - picBound.left) * 2;
                if (mWidth > totalWidth) {
                    mTextSize = getTextSizeByWidth(mTextSize, totalWidth);
                    // mHeight*=(mWidth/tWidth);
                }

                float tHeight = mHeight;
                float totalHeight = (picBound.bottom - picBound.top) * 2;
                if (mHeight > totalHeight) {
                    mTextSize = getTextSizeByHeight(mTextSize, totalHeight);
                    // mWidth*=(mHeight/tHeight);
                }

                //按原来的的中心坐标缩放
                fLeft = cx - mWidth / 2;
                fTop = cy - mHeight / 2;
                ((PtuFrameLayout) getParent()).changeLocation();
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void initItems() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rimColor = mContext.getResources().getColor(R.color.float_rim_color, null);
            itemColor = mContext.getResources().getColor(R.color.float_item_color, null);
        } else {
            rimColor = mContext.getResources().
                    getColor(R.color.float_rim_color);
            itemColor = mContext.getResources().getColor(R.color.float_item_color);
        }
        items.add(null);//第0个

        justifyBm = new Bitmap[3];
        justifyBm[0] = IconBitmapCreator.createJustifyIcon(mPadding, itemColor, 0x00000000, 0);
        justifyBm[1] = IconBitmapCreator.createJustifyIcon(mPadding, itemColor, 0x00000000, 1);
        justifyBm[2] = IconBitmapCreator.createJustifyIcon(mPadding, itemColor, 0x00000000, 2);
        MicroButtonData item = new MicroButtonData(-1, -1, ITEM_JUSTIFY);
        item.mode = 0;
        item.bitmap = justifyBm[0];
        items.add(item);//第1个

        item = new MicroButtonData(-1, -1, ITEM_EDTI);
        item.bitmap = IconBitmapCreator.getEditBitmap(mPadding, itemColor);
        items.add(item);//第2个
        items.add(null);//第3个
        items.add(null);//第4个
        items.add(null);//第5个
        item = new MicroButtonData(-1, -1, ITEM_BOTTOM_CENTER);
        item.bitmap = IconBitmapCreator.CreateToBottomCenterBitmap(mPadding, itemColor);
        items.add(item);//第6个
        items.add(null);//第7个
    }

    /**
     * 重要当view的某个影响宽高的参数改变之后，在view还没绘制之前提前获取宽高
     * <p>并且将其值赋给mwidth，mHeight，此方法只会改变mwidth，mHeight
     */
    private void getSizePreliminarily() {
        Util.P.le(TAG, "getSizePreliminarily");
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
        Util.P.le(TAG, "scale");
        float centerX = fLeft + mWidth / 2, centerY = fTop + mHeight / 2;
        adjustSize(ratio);
        fLeft = centerX - mWidth / 2;
        fTop = centerY - mHeight / 2;
        //发生缩放之后可能超出边界，然后适配边界
        adjustEdgeBound();
    }

    public float adjustSize(float ratio) {
        Util.P.le(TAG, "adjustSize");
        currentRatio = ratio;
        //尝试直接设置
        float tsize = mTextSize;
        tsize *= ratio;
        setTextSize(tsize);
        getSizePreliminarily();
        //根据范围调整比例
        if (mTextSize <= 2) {
            mTextSize = 3;
            if (currentRatio < 1)
                currentRatio = 1f;
        }
        //让长宽大约多出两个汉字
        float totalWidth = (picBound.right - picBound.left) * 2;
        if (mWidth > totalWidth) {
            float tr = totalWidth / mWidth;
            if (tr < 0.999) tr = 0.999f;//会产生小抖动，来回缩放，提醒用户不能说放了
            currentRatio = tr;
        }
        float totalHeight = (picBound.bottom - picBound.top) * 2;
        if (mHeight > totalHeight) {
            float tr = currentRatio = totalHeight / mHeight;
            if (tr < 0.996) tr = 0.996f;
            currentRatio = tr;
        }
        //比例设置为调整后的大小
        mTextSize *= currentRatio;
        if (currentRatio == ratio)//没有变化，直接返回
            return -1;

        setTextSize(mTextSize);
        getSizePreliminarily();

        return currentRatio;
    }

    @Override
    public boolean adjustEdgeBound(float nx, float ny) {
        return false;
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
        adjustEdgeBound();
    }

    /**
     * 适配floatview的位置,不能超出图片的边界,不算padding的内部就不能超出边界
     * 超出之后移动startx，starty,不影响其它数据
     */
    public boolean adjustEdgeBound() {
        Util.P.le(TAG, "adjustEdgeBound");
        setRim();
        if (fLeft + rimRight < picBound.left)//右边小于左边界
            fLeft = picBound.left - rimRight;
        if (fTop + rimBottom < picBound.top)//下边小于上边界
            fTop = picBound.top - rimBottom;
        if (fLeft + rimLeft > picBound.right)//左边大于右边界
            fLeft = picBound.right - rimLeft;
        if (fTop + rimTop > picBound.bottom)//上边大于下边界
            fTop = picBound.bottom - rimTop;
        return true;
    }

    private void setRim() {
        mPaint.setTextSize(mTextSize);

        Paint.FontMetrics fm = mPaint.getFontMetrics();
        /**
         * 文本默认不局中，计算中间位置
         */
        float md = sp2px(fm.ascent - fm.top), nd = sp2px(fm.bottom - fm.descent), dd = md - nd;
        rimLeft = mPadding;
        rimTop = mPadding;
        rimRight = mWidth - mPadding;
        rimBottom = mHeight - mPadding;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    private int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(spValue * fontScale + 0.5f);
    }

    /**
     * <p>原始图片被缩放了，现在把floatview反着缩放回去，使底图回到原始大小时floatview的相对大小不变
     * <p> 获取子功能生成点的bitmap，以及bitmap的大小，位置的相关参数，
     * <p> 传入initRatio：ptuView上的图片一开始缩放的比例，
     * <p>方法内部再用RealRatio=1/initRation,算出实际的缩放比例,
     * <p>然后得出子功能获得参数：
     * <p>相对left：rleft=（FloatView的letf-ptuView的图片的left）*realRatio,rtop一样
     * <p>FloatView的宽mwidth*=realRatio获取的实际的宽，高一样
     * <p>此方法会改变floatTextView的大小，textSize，显示状态，
     * <p/>
     * <p>另外，获取的图片view可能会过大，造成内存溢出，通过innerRect表示真实尺寸
     * <p>outRect表示需要的尺寸，最后绘制时缩放，减小内存溢出的可能
     *
     * @param ptuView 底图图片视图，用于获取地图的各种信息。
     * @return 成功返回数据，否则返回空
     */
    public TextStepData getResultData(PtuView ptuView) {
        if (mText.trim().equals("")) return null;
        //文本在view中的位置
         /*代表view有效区域在底图上的位置的rect，相对于原始图片的左上角上下左右边的距离*/
        RectF boundRectInPic = new RectF();
        getBoundInPic(ptuView, boundRectInPic);

        String realRatio = MU.di(Double.toString(1), Float.toString(ptuView.getTotalRatio()));
        Bitmap textViewBm = generateProximateScaleBm(realRatio);
        resultBm = Bitmap.createBitmap(textViewBm, Math.round(rimLeft), Math.round(rimTop),
                Math.round(rimRight - rimLeft), Math.round(rimBottom - rimTop));
        textViewBm.recycle();
        textViewBm = null;
        String path = FileTool.createTempPicPath(mContext);
        BitmapTool.saveBitmap(mContext, resultBm, path);
        TextStepData tsd = new TextStepData(PtuActivity.EDIT_TEXT);
        tsd.picPath = path;
        tsd.boundRectInPic = boundRectInPic;
        tsd.rotateAngle = 0;
        Util.P.le(TAG, "获取添加文字的Bitmap和相关数据成功");
        return tsd;
    }


    /**
     * 获取当前的文字在底图原图片中的位置
     *
     * @param boundRectInPic 代表view有效区域在底图上的位置的rect，相对于原始图片的左上角上下左右边的距离
     *                       <p>outRect大小和innerRect大小相同的</p>
     */
    private void getBoundInPic(PtuView ptuView, RectF boundRectInPic) {
        String textLeft = MU.add(fLeft, rimLeft);
        String textTop = MU.add(fTop, rimTop);
        String textRight = MU.add(fLeft, rimRight);
        String textBottom = MU.add(fTop, rimBottom);
        //先计算出文字部分在当前整个PtuView中的位置

        String[] temp = PtuUtil.getLocationAtPicture(textLeft, textTop, ptuView.getSrcRect(), ptuView.getDstRect());
        boundRectInPic.left = Float.valueOf(temp[0]);
        boundRectInPic.top = Float.valueOf(temp[1]);
        temp = PtuUtil.getLocationAtPicture(textRight, textBottom, ptuView.getSrcRect(), ptuView.getDstRect());
        boundRectInPic.right = Float.valueOf(temp[0]);
        boundRectInPic.bottom = Float.valueOf(temp[1]);
    }

    /**
     * 根据需要缩放的比例，缩放floatTextView，使其大小尽量接近需要的比例，
     * <p>因为文字size的关系，不能保证完全的满足要求缩放，故得到一个近似的大小</p>
     *
     * @param realRatio 需要缩放的的比例
     * @return 整个FloatTextView的bitmap
     */
    //获取realratio比例下，内部text的宽高text
    private Bitmap generateProximateScaleBm(String realRatio) {
        Util.P.le(TAG, "开始转化FloatTextView成Bitmap");
        Util.P.le(TAG, "当前剩余内存" + Runtime.getRuntime().freeMemory());
        float centerX = fLeft + mWidth / 2;
        float centerY = fTop + mHeight / 2;
        String textWidth = MU.su(mWidth, MU.mu(mPadding, 2f));//目前文本的宽度
        String textHeight = MU.su(mHeight, MU.mu(mPadding, 2f));//目前文本的高度

        String fTextWidth = MU.mu(textWidth, realRatio);
        String fTextHeight = MU.mu(textHeight, realRatio);
        String fWidth = MU.add(fTextWidth, MU.mu(mPadding, 2f));
        String fHeight = MU.add(fTextHeight, MU.mu(mPadding, 2f));
        String limit = MU.di(Runtime.getRuntime().maxMemory() * 1d, 5f);
        String size = MU.mu(MU.mu(fWidth, fHeight), Float.toString(4));
        if (MU.co(size, limit) > 0) {
            float newRatio = (float) Math.sqrt(Double.valueOf(
                    MU.di(limit, (MU.mu(
                            MU.mu(mWidth, mHeight),
                            4f))
                    )));
            fTextWidth = MU.mu(textWidth, newRatio);
            fTextHeight = MU.mu(textHeight, newRatio);
            fWidth = MU.add(fTextWidth, MU.mu(mPadding, 2f));
            fHeight = MU.add(fTextHeight, MU.mu(mPadding, 2f));
        }
        mTextSize = getTextSizeByWidth(mTextSize, Float.valueOf(fWidth));
        fLeft = centerX - mWidth / 2;
        fTop = centerY - mHeight / 2;
        ((PtuFrameLayout) getParent()).changeLocation();
        Bitmap bitmap = Bitmap.createBitmap(Math.round(mWidth), Math.round(mHeight), Bitmap.Config.ARGB_8888);
        SHOW_SATUS = STATUS_TOUMING;
        requestLayout();
        setHeight(Math.round(mHeight));
        setWidth(Math.round(mHeight));
        ((PtuFrameLayout) getParent()).measure(((PtuFrameLayout) getParent()).getWidth(),
                ((PtuFrameLayout) getParent()).getHeight());
        ((PtuFrameLayout) getParent()).layout(0, 0, ((PtuFrameLayout) getParent()).getWidth(),
                ((PtuFrameLayout) getParent()).getHeight());
        draw(new Canvas(bitmap));
        Util.P.le(TAG, "转化FloatTextView成Bitmap成功");
        return bitmap;
    }

    /**
     * 调整文字的大小使文本框的宽度与给定值误差在一定范围之内
     * 文本缩放时缩放程度小于实际宽高的缩放,另外，
     * 文本框正常受到字数的限制，每个字都增加一些，所
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
            getSizePreliminarily();
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
            getSizePreliminarily();
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
            getSizePreliminarily();
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
            getSizePreliminarily();
            textHeight = mHeight - mPadding * 2;
        }
        return curSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setRim();
        mPaint.setColor(mBackGroundColor);
        canvas.drawRect(rimLeft, rimTop, rimRight, rimBottom, mPaint);
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
            items.get(1).x = mWidth / 2 - mPadding / 2;
            items.get(1).y = rimTop - mPadding + mPadding / 8;
            drawItem(canvas, items.get(1));

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
     * @param canvas
     * @param item
     */
    public void drawItem(Canvas canvas, MicroButtonData item) {
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
        Util.P.le(TAG, "changeShowState=" + SHOW_SATUS);
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
                if (!hasUp && GeoUtil.getDis(downX, downY, event.getX(), event.getY()) < minMoveDis
                        && System.currentTimeMillis() - downTime < 500) {
                    Util.P.le(TAG, "drawItem");
                    float x = event.getX();//变换为本view的坐标
                    float y = event.getY();

                    //内部子item的处理

                    RectF itemBound = new RectF(items.get(1).x - 10, items.get(1).y - 10,
                            items.get(1).x + mPadding + 10, items.get(1).y + mPadding);
                    if (SHOW_SATUS >= STATUS_ITEM && itemBound.contains(x, y)) {
                        onClickJustify();
                        return true;
                    }

                    itemBound.set(items.get(2).x, items.get(2).y - 10,
                            items.get(2).x + mPadding + 10, items.get(2).y + mPadding);
                    if (SHOW_SATUS >= STATUS_ITEM && itemBound.contains(x, y)) {
                        if (mBackGroundColor == 0x00000000)
                            mBackGroundColor = 0xffffffff;
                        else
                            mBackGroundColor = 0x00000000;
                        invalidate();
                        return true;
                    }
                    //点击到了重置位置按钮
                    itemBound.set(items.get(6).x - 10, items.get(6).y,
                            items.get(6).x + mPadding + 10, items.get(6).y + mPadding + 10);
                    if (SHOW_SATUS >= STATUS_ITEM && itemBound.contains(x, y)) {
                        onClickBottomCenter();
                        return true;
                    }

                    //点击到view的其他部分
                    if (SHOW_SATUS < STATUS_ITEM) {//没显示item时就不发送事件，不弹出输入法
                        changeShowState(STATUS_ITEM);
                        return true;
                    } else {//显示item时，就要弹出输入法
                        Util.P.le(TAG, "显示输入法了");
                        super.dispatchTouchEvent(event);
                        changeShowState(STATUS_INPUT);
                        return true;
                    }
                }
                hasUp = true;
        }
        return false;
    }

    private void onClickJustify() {
        Util.P.le(TAG, "onClickBottomCenter");
        items.get(1).mode = (items.get(1).mode + 1) % 3;
        items.get(1).bitmap = justifyBm[items.get(1).mode];
        if (items.get(1).mode == 0) {
            setGravity(Gravity.CENTER);
        } else if (items.get(1).mode == 1) {
            setGravity(Gravity.LEFT);
        } else if (items.get(1).mode == 2) {
            setGravity(Gravity.RIGHT);
        }
    }

    public void onClickBottomCenter() {
        Util.P.le(TAG, "onClickBottomCenter");
        fLeft = (picBound.left + picBound.right) / 2 - mWidth / 2;
        fTop = picBound.bottom - mHeight;
        changeShowState(STATUS_ITEM);
        //请求重绘
        ((PtuFrameLayout) getParent()).changeLocation();
    }

    /**
     * 当view的某个影响宽高的参数改变之后，需要改变边框，提前获取它的宽高，并更新再父视图上
     */
    public void updateSize() {
        float centerX = fLeft + mWidth / 2, centerY = fTop + mHeight / 2;
        getSizePreliminarily();
        fLeft = centerX - mWidth / 2;
        fTop = centerY - mHeight / 2;
        ((PtuFrameLayout) getParent()).changeLocation();
    }

    public Bitmap getResultBm() {
        return resultBm;
    }

    public void releaseResource() {
        if (resultBm != null) {
            resultBm.recycle();
            resultBm = null;
        }
    }
}
