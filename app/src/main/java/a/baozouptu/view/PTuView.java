package a.baozouptu.view;

import a.baozouptu.R;
import a.baozouptu.tools.BitmapTool;
import a.baozouptu.tools.GeoUtil;
import a.baozouptu.tools.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 注意： 每次缩放要寻改的地方有三个，totalRatio，currentRatio,CURRENT_STATUS
 */
public class PtuView extends View {
    String DEBUG_TAG = "PtuView";
    /**
     * 每次刷新0.0002倍
     */
    public static final float SCALE_FREQUENCE = 0.0002f;
    private static final float MAX_RATIO = 8;
    /**
     * 表示当前处理状态：缩放，移动等等
     */
    private static int CURRENT_STATUS = 0;
    /**
     * 初始化状态
     */
    private static final int STATUS_INIT = 0;
    /**
     * 移动状态
     */
    private static final int STATUS_MOVE = 1;
    /**
     * 缩放状态
     */
    private static final int STATUS_SCALE = 3;

    /**
     * 最近的x的位置,上一次的x的位置，y的
     */
    private float lastX = -1, lastY = -1;
    /**
     * 最近一次用于缩放两手指间的距离
     */
    private float lastDis;

    /**
     * 中的缩放比例，其它的是辅助，放大时直接需要就是一个totalRatio
     */
    private float totalRatio = 1f;
    /**
     * mContext
     */
    private Context mContext;
    /**
     * 原图片
     */
    private Bitmap sourceBitmap;
    /**
     * 用于处理图片的矩阵
     */
    private Matrix matrix = new Matrix();
    /**
     * 整个View的宽,高
     */
    private int totalWidth, totalHeight;
    /**
     * 原图片的宽度,高度
     */
    private int srcPicWidth, srcPicHeight;
    /**
     * 右上角x坐标，以view的右上角为原点，（0,0）
     */
    private int picLeft;
    /**
     * 右上角y坐标，以view的右上角为原点，（0,0）
     */
    private int picTop;
    /**
     * 图片的局部，要现实出来的部分
     */
    Rect srcRect = new Rect(0, 0, 1, 1);
    /**
     * 要绘制的总图在view的canvas上面的位置,
     * <P>也即是在bitmapToView上的位置
     * <p>也是在secondcanvas上的位置</p>
     */
    Rect dstRect = new Rect(1, 2, 3, 4);

    Paint picPaint = new Paint();
    /**
     * 显示到view上的bitmap，长宽为ptuView的总长，背景透明，刷新时会将背景刷新
     */
    Bitmap bitmapToview;
    /**
     * 将内容绘制到底图上，view再讲地图绘制到自己的canvas上面
     * 其宽和高为view的宽和高
     */
    Canvas secondCanvas;

    private float initRatio = 1f;
    private Bitmap tempBitmap;
    private BitmapDrawable tempDrawable;
    /**
     * 当前图片的宽和高
     */
    private int curPicWidth, curPicHeight;
    private Canvas sourceCanvas;

    public PtuView(Context context) {
        super(context);
        this.mContext = context;
    }

    public PtuView(Context context, AttributeSet set) {
        super(context, set);
        this.mContext = context;
        CURRENT_STATUS = STATUS_INIT;
        picPaint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        totalWidth = w;
        totalHeight = h;
        bitmapToview = Bitmap.createBitmap(totalWidth, totalHeight,
                Config.ARGB_8888);//创建一个空图做底图
        secondCanvas = new Canvas(bitmapToview);//设置drawCanvas为底图
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 根据提供的缩放比例，将p图的图片缩放到原图*缩放比例大小，并返回
     *
     * @param finalRatio 缩放的比例
     */
    public Bitmap getFinalPicture(float finalRatio) {
        /*Bitmap bitmap = Bitmap.createScaledBitmap(sourceBitmap, (int) (sourceBitmap.getWidth() * finalRatio),
                (int) (sourceBitmap.getHeight() * finalRatio), true);
        if (bitmap.equals(sourceBitmap))
            sourceBitmap.recycle();
        bitmapToview.recycle();*/
        return sourceBitmap;
    }

    public float getInitRatio() {
        return initRatio;
    }

    /**
     * 根据路径解析出图片
     * 获取原始bitmap的宽和高
     * <p>创建并设置好用于保存的Bitmap
     * <p>获取当前何种的Ratio
     *
     * @param path2
     */
    public void setBitmapAndInit(String path2) {
        sourceBitmap = new BitmapTool().getLosslessBitmap(path2);
        if (sourceBitmap == null) {
            Toast.makeText(mContext, "图片不存在", Toast.LENGTH_SHORT).show();
            Util.P.le("PTuView.setBitmapAndInit", "sourceBitmap出现空指针");
            return;
        }
        sourceCanvas=new Canvas(sourceBitmap);
        srcPicWidth = sourceBitmap.getWidth();
        srcPicHeight = sourceBitmap.getHeight();
        CURRENT_STATUS = STATUS_INIT;
        invalidate();
    }

    /**
     * 这个必须在onsizechaged后面调用，那是{@code totalWidth}和{@code totalHeight}才会获取到
     */
    public void initialDraw() {
        totalRatio = Math.min(totalWidth * 1.0f / (srcPicWidth * 1.0f),
                totalHeight * 1.0f / (srcPicHeight * 1.0f));
        initRatio = totalRatio;
        curPicWidth = (int) (srcPicWidth * totalRatio);
        curPicHeight = (int) (srcPicHeight * totalRatio);
        picLeft = (totalWidth - curPicWidth) / 2;
        picTop = (totalHeight - curPicHeight) / 2;
        getConvertParameter(curPicWidth, curPicHeight);
        drawToBitmapToView();
        //secondCanvas.drawBitmap(sourceBitmap, srcRect, dstRect, picPaint);//将原图填充到底图上
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                Util.P.le(DEBUG_TAG, "经过了down");
                if (Util.DoubleClick.isDoubleClick()) {
                    if (lastX < dstRect.left || lastX > dstRect.right || lastY < dstRect.top
                            || lastY > dstRect.bottom)//点击不在图片范围内
                        return true;
                    if (initRatio * 0.99 <= totalRatio && totalRatio < MAX_RATIO) {//进行放大
                        float curRatio = MAX_RATIO / totalRatio;
                        totalRatio = MAX_RATIO;
                        CURRENT_STATUS = STATUS_SCALE;
                        scalePic(event.getX(), event.getY(), event.getX(), event.getY(), curRatio);
                    } else {
                        resetDraw();
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Util.DoubleClick.cancel();//多个手指，或者移动了，双击取消
                lastDis = GeoUtil.getDis(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
            case MotionEvent.ACTION_MOVE:
                Util.DoubleClick.cancel();
                if (event.getPointerCount() == 1) {
                    CURRENT_STATUS = STATUS_MOVE;
                    movePic(event.getX(), event.getY());
                } else {
                    float endD = GeoUtil.getDis(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    float currentRatio = endD / lastDis;
                    lastDis = endD;

                    if (currentRatio > 1 - SCALE_FREQUENCE && currentRatio < 1 + SCALE_FREQUENCE)
                        return true;//本次缩放比例不够大
                    if (totalRatio * currentRatio > MAX_RATIO ||
                            totalRatio * currentRatio * srcPicWidth < totalWidth / 2
                                    && totalRatio * currentRatio * srcPicHeight < totalHeight / 2)
                        return true;//总的缩放比例超出了最大最小范围

                    CURRENT_STATUS = STATUS_SCALE;
                    totalRatio *= currentRatio;
                    scalePic(event, currentRatio);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //剩下一个手指时，获取该手指的位置，后面跟着能进行移动
                if (event.getPointerCount() == 2) {
                    int index = event.getActionIndex() == 0 ? 1 : 0;
                    lastX = event.getX(index);
                    lastY = event.getY(index);
                }
            default:
                break;
        }
        return true;
    }

    private void movePic(float curX, float curY) {

        int tx = picLeft;
        picLeft += curX - lastX;
        lastX = curX;
        if (picLeft >= 0 || Math.abs(picLeft) + totalWidth > curPicWidth)//如果x超出界限，x方向就不移动了
            picLeft = tx;

        int ty = picTop;
        picTop += curY - lastY;
        lastY = curY;
        if (picTop >= 0 || Math.abs(picTop) + totalHeight > curPicHeight)//如果y超出界限，y方向就不移动了
            picTop = ty;

        if (picLeft == tx && picTop == ty)//x，y方向都移动不了，就不移动的标志
            return;
        getConvertParameter((int) (srcPicWidth * totalRatio), (int) (srcPicHeight * totalRatio));
        drawToBitmapToView();
        invalidate();
    }

    /**
     * 要先计算出totalRatio
     *
     * @param event
     * @param currentRatio
     */
    private void scalePic(MotionEvent event, float currentRatio) {
        scalePic(event.getX(0), event.getY(0), event.getX(1), event.getY(1), currentRatio);
    }

    /**
     * 要先计算出totalRatio
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param currentRatio
     */
    private void scalePic(float x1, float y1, float x2, float y2, float currentRatio) {
        // 获取当前图片的宽、高
        Util.P.le(DEBUG_TAG, "缩放图片开始");
        curPicWidth = (int) (srcPicWidth * totalRatio);
        curPicHeight = (int) (srcPicHeight * totalRatio);

        //如果某一边超出边界，则使用手指的中心，否则使用图片的中心
        //缩放中心随手指移动，因为缩放时双指同时移动也会导致图片移动，故不采用固定的缩放中心
        float scaleCenterX = (x1 + x2) / 2, scaleCenterY = (y1 + y2) / 2;

        float x = GeoUtil.getScaledX(picLeft, picTop, scaleCenterX, scaleCenterY, currentRatio);
        float y = GeoUtil.getScaledY(picLeft, picTop, scaleCenterX, scaleCenterY, currentRatio);
        picLeft = (int) x;
        picTop = (int) y;

        //当缩放到view内部是，调整图片的边界
        if (curPicWidth < totalWidth) picLeft = (totalWidth - curPicWidth) / 2;
        else if (picLeft > 0 && curPicWidth > totalWidth) picLeft = 0;

        if (curPicHeight < totalHeight) picTop = (totalHeight - curPicHeight) / 2;
        else if (picTop > 0 && curPicHeight > totalHeight) picTop = 0;

        getConvertParameter(curPicWidth, curPicHeight);
        drawToBitmapToView();
        invalidate();
        Util.P.le(DEBUG_TAG, "缩放完成");
    }

    /**
     * 获取变换后的参数
     * 获取当前的宽高，
     * <p>要绘制出的宽高，
     * <p>缩放后图片右上角顶点的位置
     * <p>绘画时用到的矩形，原图裁剪矩形srcRect，在画布上的位置矩形dstRect
     * <p/>
     *
     * @param curPicWidth
     * @param curPicHeight
     */
    private void getConvertParameter(int curPicWidth, int curPicHeight) {
        Util.P.le(DEBUG_TAG, "获取参数开始");
        // 显示在屏幕上绘制的宽度、高度
        int drawWidth = curPicWidth > totalWidth ? totalWidth : curPicWidth;
        int drawHeight = curPicHeight > totalHeight ? totalHeight : curPicHeight;

        int leftInView = picLeft < 0 ? 0 : picLeft, topInView = picTop < 0 ? 0 : picTop;
        int leftInPic = picLeft > 0 ? 0 : -picLeft, topInPic = picTop > 0 ? 0 : -picTop;
        int x = (int) (leftInPic / totalRatio), y = (int) (topInPic / totalRatio);
        int x1 = x + (int) (drawWidth / totalRatio), y1 = y + (int) (drawHeight / totalRatio);
        if (x1 > sourceBitmap.getWidth()) x1 = sourceBitmap.getWidth();
        if (y1 > sourceBitmap.getHeight()) y1 = sourceBitmap.getHeight();
        srcRect.set(x, y, x1, y1);
        dstRect.set(leftInView, topInView, leftInView + drawWidth, topInView + drawHeight);
        Util.P.le(DEBUG_TAG, "获取参数完成");
    }


    /**
     * 图片在PtuFrameLayout上的相对位置
     *
     * @return
     */
    public Rect getBound() {
        return dstRect;
    }

    /**
     * 绘制，这里根据不同的当前状态来绘制图片CURRENT_STATUS
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (CURRENT_STATUS) {
            case STATUS_INIT:
                initialDraw();
                break;
            default:
                break;
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmapToview);
        bitmapDrawable.setBounds(0, 0, bitmapToview.getWidth(), bitmapToview.getHeight());
        bitmapDrawable.setDither(true);
        bitmapDrawable.setAntiAlias(true);
        bitmapDrawable.setFilterBitmap(true);
        bitmapDrawable.draw(canvas);//将底图绘制到View上面到
        //canvas.drawBitmap(bitmapToview, matrix, null);
    }

    /**
     * 比较常用的方法，将图片还原到开始的位置，即长边与父布局长边对齐
     */
    public void resetDraw() {
        totalRatio = initRatio;
        curPicWidth = (int) (srcPicWidth * totalRatio);
        curPicHeight = (int) (srcPicHeight * totalRatio);
        picLeft = (totalWidth - curPicWidth) / 2;
        picTop = (totalHeight - curPicHeight) / 2;
        getConvertParameter(curPicWidth, curPicHeight);
        drawToBitmapToView();
        invalidate();
    }

    private void drawToBitmapToView() {
        bitmapToview.eraseColor(Color.alpha(00));
        tempBitmap = Bitmap.createBitmap(sourceBitmap, srcRect.left, srcRect.top,
                srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        tempDrawable = new BitmapDrawable(mContext.getResources(), tempBitmap);
        tempDrawable.setDither(true);
        tempDrawable.setAntiAlias(true);
        tempDrawable.setFilterBitmap(true);
        tempDrawable.setBounds(dstRect);
        tempDrawable.draw(secondCanvas);
        tempBitmap.recycle();
    }

    /**
     * 注意addBitmap大小和boundRect的一致
     * @param addBitmap    需要添加的floatBitmap的局部
     * @param boundRect rect代表view有效区域在底图上的位置的rect，相对于原始图片的左上角上下左右边的距离
     */
    public void addBitmap(Bitmap addBitmap, RectF boundRect) {
        BitmapDrawable addDrawable = new BitmapDrawable(mContext.getResources(), addBitmap);
        addDrawable.setDither(true);
        addDrawable.setAntiAlias(true);
        addDrawable.setFilterBitmap(true);
        addDrawable.setBounds(GeoUtil.rectF2Rect(boundRect));
        addDrawable.draw(sourceCanvas);
        addBitmap.recycle();
        resetDraw();
    }

    @Override
    protected void onDetachedFromWindow() {
        bitmapToview.recycle();
        sourceBitmap.recycle();
        super.onDetachedFromWindow();
    }
}
