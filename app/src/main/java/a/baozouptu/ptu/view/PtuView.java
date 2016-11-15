package a.baozouptu.ptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;

/**
 * 内部原理：添加什么东西上去就在sourceBitmap上面添加（用了一个全局的sourceCanvas，除此之外，此canvas不起其它作用）
 * <P>绘图时，根据手势会改变相应的参数，然后根据相应的参数，到sourceBitmap上面剪切一个子图下来，用BitmapDrawable显示出来</P>
 * <p>sourceBitmap可能会被替换，此时尺寸大小也可能会被改变</p>
 * 注意： 每次缩放要寻改的地方有三个，totalRatio，currentRatio
 */
public class PtuView extends View implements TSRView {
    String TAG = "PtuView";
    private boolean canDoubleCilick = true;
    private float minRatio;
    protected boolean canDiminish = true;
    private Rect totalBound;

    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
    }

    public boolean isCanRotate() {
        return canRotate;
    }

    boolean canRotate = false;
    /**
     * 每次刷新0.0002倍
     */
    public static final float SCALE_FREQUENCE = 0.0002f;
    protected static float MAX_RATIO = 8;

    /**
     * 最近的x的位置,上一次的x的位置，y的
     */
    protected float lastX = -1, lastY = -1;
    /**
     * 最近一次用于缩放两手指间的距离
     */
    protected float lastDis;

    /**
     * 中的缩放比例，其它的是辅助，放大时直接需要就是一个totalRatio
     */
    protected float totalRatio = 1f;
    /**
     * mContext
     */
    protected Context mContext;
    /**
     * 原图片
     */
    protected Bitmap sourceBitmap;
    /**
     * 用于处理图片的矩阵
     */
    private Matrix matrix = new Matrix();


    /**
     * 原图片的宽度,高度
     */
    protected int srcPicWidth = 10, srcPicHeight = 10;
    /**
     * 当前缩放比例下，以PtuView坐标系下（=纵坐标系）下右上角x坐标，y坐标，
     * 以view的右上角为原点，（0,0）
     */
    protected int picLeft = 0, picTop = 0;

    public Rect getSrcRect() {
        return srcRect;
    }

    public Rect getDstRect() {
        return dstRect;
    }

    /**
     * 图片的局部，要显示出来的部分
     */
    protected Rect srcRect = new Rect(0, 0, 1, 1);
    /**
     * 要绘制的总图在view的canvas上面的位置,
     */
    protected Rect dstRect = new Rect(1, 2, 3, 4);

    Paint picPaint;

    protected float initRatio = 1f;
    /**
     * 当前图片的宽和高
     */
    protected int curPicWidth = 10, curPicHeight = 10;
    protected Canvas sourceCanvas;

    public PtuView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public PtuView(Context context, AttributeSet set) {
        super(context, set);
        this.mContext = context;
        init();
    }

    /**
     * 一开始的初始化不能获取totalBound，需要显示出来以后才有totalBound的
     *
     * @param totalBound 总的FrameLayout图片的范围
     */

    public PtuView(Context context, Bitmap bitmap, Rect totalBound) {
        super(context);
        this.mContext = context;
        this.totalBound = totalBound;
        init();
        setBitmapAndInit(bitmap, totalBound);
    }

    private void init() {
        picPaint=new Paint();
        picPaint.setDither(true);
        canDiminish=true;
    }

    /**
     * 根据提供的缩放比例，将p图的图片缩放到原图*缩放比例大小，并返回
     *
     * @param finalRatio 缩放的比例
     */
    public Bitmap getFinalPicture(float finalRatio) {
        if (finalRatio != 1.0) {
            Bitmap bitmap = Bitmap.createScaledBitmap(sourceBitmap, Math.round(srcPicWidth * finalRatio),
                    Math.round(srcPicHeight * finalRatio), true);
            if (sourceBitmap != null && bitmap.equals(sourceBitmap)) {
                sourceBitmap.recycle();
                sourceBitmap = null;
            }
        }
        return sourceBitmap;
    }

    public float getInitRatio() {
        return initRatio;
    }

    public float getTotalRatio() {
        return totalRatio;
    }

    /**
     * 根据路径解析出图片
     * 获取原始bitmap的宽和高
     * <p>创建并设置好用于保存的Bitmap
     * <p>获取当前何种的Ratio
     */
    public void setBitmapAndInit(String path, Rect totalBound) {
        setBitmapAndInit(BitmapTool.getLosslessBitmap(path), totalBound);
    }

    /**
     * 图片可为空，为空时显示透明的图一张
     */
    public void setBitmapAndInit(@Nullable Bitmap bitmap, Rect totalBound) {
        this.totalBound = totalBound;
        if (bitmap == null)
            sourceBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        else sourceBitmap = bitmap;
        sourceCanvas = new Canvas(sourceBitmap);
        srcPicWidth = sourceBitmap.getWidth();
        srcPicHeight = sourceBitmap.getHeight();
        initRatio = Math.min(totalBound.width() * 1f / srcPicWidth,
                totalBound.height() * 1f / srcPicHeight);
        totalRatio = initRatio;
        minRatio = Math.min(totalBound.width() * 1f / 2 / srcPicWidth, totalBound.height() * 1f / 3 / srcPicHeight);
        curPicWidth = (int) (srcPicWidth * totalRatio + 0.5f);//简略的四舍五入
        curPicHeight = (int) (srcPicHeight * totalRatio + 0.5f);
        picLeft = (totalBound.width() - curPicWidth) / 2;
        picTop = (totalBound.height() - curPicHeight) / 2;
        getConvertParameter(curPicWidth, curPicHeight, picLeft, picTop);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                Util.P.le(TAG, "经过了down");
                if (Util.DoubleClick.isDoubleClick()) {
                    if (!canDoubleCilick) return true;//不支持双击
                    if (lastX < dstRect.left || lastX > dstRect.right || lastY < dstRect.top
                            || lastY > dstRect.bottom)//点击不在图片范围内
                        return true;
                    if (initRatio * 0.99 <= totalRatio && totalRatio < MAX_RATIO) {//进行放大
                        float curRatio = MAX_RATIO / totalRatio;
                        scale(event.getX(), event.getY(), curRatio);
                    } else {
                        resetShow();
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Util.DoubleClick.cancel();//多个手指，或者移动了，双击取消
                lastDis = GeoUtil.getDis(event.getX(0), event.getY(0), event.getX(1), event.getY(1));

                //移动
                lastX = (event.getX(0) + event.getX(1)) / 2;
                lastY = (event.getY(0) + event.getY(1)) / 2;
            case MotionEvent.ACTION_MOVE:
                Util.DoubleClick.cancel();
                if (event.getPointerCount() == 1) {
                    move(event.getX(), event.getY());
                } else {
                    //缩放
                    float endD = GeoUtil.getDis(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    float currentRatio = endD / lastDis;
                    lastDis = endD;
                    scale((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2, currentRatio);

                    //移动
                    move((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //剩下一个手指时，获取该手指的位置，后面跟着能进行移动
                if (event.getPointerCount() == 2) {
                    int index = event.getActionIndex() == 0 ? 1 : 0;
                    lastX = event.getX(index);
                    lastY = event.getY(index);
                    //当缩小范围超过最小值时
                    if (canDiminish && totalRatio < minRatio) {
                        scale(totalBound.width() / 2, totalBound.height() / 2, minRatio / totalRatio);
                    }
                }
                if (event.getPointerCount() == 3) {
                    int index = event.getActionIndex();

                    int i0, i1;
                    if (index == 0) {
                        i0 = 1;
                        i1 = 2;
                    } else if (index == 1) {
                        i0 = 0;
                        i1 = 2;
                    } else {
                        i0 = 0;
                        i1 = 1;
                    }
                    //缩放
                    lastDis = GeoUtil.getDis(event.getX(i0), event.getY(i0), event.getX(i1), event.getY(i1));

                    //移动
                    lastX = (event.getX(i0) + event.getX(i1)) / 2;
                    lastY = (event.getY(i0) + event.getY(i1)) / 2;
                }
            default:
                break;
        }
        return true;
    }

    public void move(float curX, float curY) {
        int tx = picLeft;
        picLeft += curX - lastX;
        lastX = curX;
        if (Math.abs(picLeft) + totalBound.width() > curPicWidth)//如果x超出界限，x方向就不移动了
            picLeft = tx;

        int ty = picTop;
        picTop += curY - lastY;
        lastY = curY;
        if (picTop >= 0 || Math.abs(picTop) + totalBound.height() > curPicHeight)//如果y超出界限，y方向就不移动了
            picTop = ty;

        if (picLeft == tx && picTop == ty)//x，y方向都移动不了，就不移动的标志
            return;
        getConvertParameter(curPicWidth, curPicHeight, picLeft, picTop);
        invalidate();
    }

    /**
     * 这里判断条件，探后只管缩放，不控制size大小
     * 缩放后调整到中间位置
     */
    public void scale(float scaleCenterX, float scaleCenterY, float currentRatio) {
        //对于缩放的几种限制情况，必须放到缩放函数内部，这样其它地方调用的时候才不会超出条件
        if (currentRatio > 1 - SCALE_FREQUENCE && currentRatio < 1 + SCALE_FREQUENCE)
            return;//本次缩放比例不够大
        if (totalRatio * currentRatio > MAX_RATIO)
            return;//总的缩放比例超出了最大范围
        if (!canDiminish && currentRatio * totalRatio < initRatio)//不支持比屏幕小时
            currentRatio = initRatio / totalRatio;

        // 获取当前图片的宽、高
        totalRatio *= currentRatio;
        curPicWidth = Math.round(srcPicWidth * totalRatio);
        curPicHeight = Math.round(srcPicHeight * totalRatio);

        //如果某一边超出边界，则使用手指的中心，否则使用图片的中心
        //缩放中心随手指移动，因为缩放时双指同时移动也会导致图片移动，故不采用固定的缩放中心

        //高精度的计算缩放后坐标
        float[] xy = new float[2];
        GeoUtil.getScaledCoord(xy, scaleCenterX, scaleCenterY, picLeft, picTop, currentRatio);
        picLeft = Math.round(xy[0]);
        picTop = Math.round(xy[1]);
        adjustEdge();
        getConvertParameter(curPicWidth, curPicHeight, picLeft, picTop);
        invalidate();
    }

    public void adjustEdge() {
        //当缩放到view内部是，调整图片的边界
        if (curPicWidth < totalBound.width()) picLeft = (totalBound.width() - curPicWidth) / 2;
        else {
            if (picLeft + curPicWidth < getRight()) picLeft = getRight() - curPicWidth;
            else if (picLeft > 0) picLeft = 0;
        }

        if (curPicHeight < totalBound.height()) picTop = (totalBound.height() - curPicHeight) / 2;
        else {
            if (picTop + curPicHeight < getBottom()) picTop = getBottom() - curPicHeight;
            else if (picTop > 0) picTop = 0;
        }
    }

    /**
     * @return 图片在PtuFrameLayout上的相对位置
     */
    public Rect getPicBound() {
        return dstRect;
    }

    /**
     * 绘制，这里根据不同的当前状态来绘制图片CURRENT_STATUS
     */
    @Override
    public void onDraw(Canvas canvas) {
        if (sourceBitmap == null) return;
        Bitmap tempBitmap;
        if (srcRect.right - srcRect.left > dstRect.right - dstRect.left) {
            float ratio = (dstRect.right - dstRect.left) * 1.0f / (srcRect.right - srcRect.left);
            matrix.reset();
            matrix.setScale(ratio, ratio);
            tempBitmap = Bitmap.createBitmap(sourceBitmap, srcRect.left, srcRect.top,
                    srcRect.right - srcRect.left, srcRect.bottom - srcRect.top, matrix, true);
        } else {
            tempBitmap = Bitmap.createBitmap(sourceBitmap, srcRect.left, srcRect.top,
                    srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
        BitmapDrawable tempDrawable = new BitmapDrawable(mContext.getResources(), tempBitmap);
        tempDrawable.setDither(true);
        tempDrawable.setAntiAlias(true);
        tempDrawable.setFilterBitmap(true);
        tempDrawable.setBounds(dstRect);
        tempDrawable.draw(canvas);//将底图绘制到View上面到
        if (tempBitmap != null) {
            tempBitmap.recycle();
        }
        super.onDraw(canvas);
        Log.e(TAG, "绘制完成");
    }

    /**
     * 比较常用的方法，将图片还原到开始的位置,情况，即长边与父布局长边对齐
     * <p>基本参数还原到初始化状态,可用于撤销重做等
     */
    public void resetShow() {
        totalRatio = initRatio;
        curPicWidth = Math.round(srcPicWidth * totalRatio);
        curPicHeight = Math.round(srcPicHeight * totalRatio);
        picLeft = (totalBound.width() - curPicWidth) / 2;
        picTop = (totalBound.height() - curPicHeight) / 2;
        getConvertParameter(curPicWidth, curPicHeight, picLeft, picTop);
        invalidate();
    }

    /**
     * addBitmap以及缩放的bitmap会立即回收
     *
     * @param addBitmap   需要添加的floatBitmap的局部
     * @param boundRect   rect代表view有效区域在底图上的位置的rect，相对于原始图片的左上角上下左右边的距离
     * @param rotateAngle 浮动视图旋转的角度
     */
    public void addBitmap(Bitmap addBitmap, RectF boundRect, float rotateAngle) {
        RepealRedoManager.addBm2Canvas(sourceCanvas, addBitmap, boundRect, rotateAngle);
        resetShow();
        Util.P.le(TAG, "将图添加到PtuView成功");
    }

    /**
     * 将原始的图片换掉,并且回收原始图片的资源，
     * <p> 会处理图片大小不同的情况
     * 会显示出来
     */
    public void replaceSourceBm(Bitmap newBm) {
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
            sourceBitmap = null;
        }
        setBitmapAndInit(newBm, totalBound);
        invalidate();
    }
/**
 *
 */
    /**
     * 将原始的图片换掉,不回收原始图片的资源，
     * <p> 会处理图片大小不同的情况
     * 会显示出来
     */
    public void replaceSourceBmNoRecycle(Bitmap newBm) {
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
            sourceBitmap = null;
        }
        setBitmapAndInit(newBm, totalBound);
        invalidate();
    }
    /**
     * 获取变换后的参数
     * 获取当前的宽高，
     * <p>要绘制出的宽高，
     * <p>缩放后图片右上角顶点的位置
     * <p>绘画时用到的矩形，原图裁剪矩形srcRect，在画布上的位置矩形dstRect
     * <p/>
     */
    protected void getConvertParameter(int curPicWidth, int curPicHeight, int picLeft, int picTop) {
        // 显示在屏幕上绘制的宽度、高度,目标矩形
        int leftInView = picLeft < 0 ? 0 : picLeft, topInView = picTop < 0 ? 0 : picTop;
        //图片宽与view宽构成两条线段平行相交问题，求交集，用右边小者减左边大者
        int drawWidth = Math.min(totalBound.right, picLeft + curPicWidth) - Math.max(0, picLeft);
        int drawHeight = Math.min(totalBound.bottom, picTop + curPicHeight) - Math.max(0, picTop);
        dstRect.set(leftInView, topInView, leftInView + drawWidth, topInView + drawHeight);

        //图片上的位置，源矩形
        int leftInPic = picLeft > 0 ? 0 : -picLeft, topInPic = picTop > 0 ? 0 : -picTop;
        int x = Math.round(leftInPic / totalRatio), y = Math.round(topInPic / totalRatio);
        int x1 = x + Math.round(drawWidth / totalRatio), y1 = y + Math.round(drawHeight / totalRatio);
        srcRect.set(x, y, x1, y1);
        //srcRect的边界不能超过bitmap的边界
        if (srcRect.left < 0) srcRect.left = 0;
        if (srcRect.top < 0) srcRect.top = 0;
        if (srcRect.right > srcPicWidth) srcRect.right = srcPicWidth;
        if (srcRect.bottom > srcPicHeight) srcRect.bottom = srcPicHeight;
    }

    /**
     * 释放资源，目前只有SourceBitmap一个
     */
    public void releaseResource() {
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
            sourceBitmap = null;
        }
    }

    public Bitmap getSourceBm() {
        return sourceBitmap;
    }

    public void setCanDoubleClick(boolean b) {
        canDoubleCilick = b;
    }

    /**
     * 必须在setBitmapAndInit后面调用
     *
     * @param canLessThanScreen 是否能小于屏幕
     */
    public void setCanLessThanScreen(boolean canLessThanScreen) {
        canDiminish = canLessThanScreen;
    }

    public void setTotalBound(Rect totalBound) {
        this.totalBound = totalBound;
    }


    @Override
    public void rotate(float angle) {
    }

    @Override
    public void adjustEdge(float dx, float dy) {

    }

    @Override
    public void adjustSize(float ratio) {

    }
}
