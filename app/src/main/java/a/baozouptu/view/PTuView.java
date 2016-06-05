package a.baozouptu.view;

import a.baozouptu.control.PTuActivity;
import a.baozouptu.tools.Util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PtuView extends View {
    /**
     * 每次刷新0.0002倍
     */
    public static final float SCALE_FREQUENCE = 0.0002f;
    private static final float MIN_RATIO = 0.3f;
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
    private static final int STATUS_ZOOM_BIG = 2;
    private static final int STATUS_ZOOM_SMALL = 3;
    private static final int STATUS_DRAW_PATH = 4;

    /**
     * 最近的x的位置
     */
    private float lastX = -1;
    /**
     * 最近的y的位置
     */
    private float lastY = -1;
    /**
     * x的移动距离
     */
    private float totalTranX;
    /**
     * y的移动距离
     */
    private float totalTranY = 0;
    /**
     * 当前用于缩放两个手指的距离
     */

    private double currentDis;
    /**
     * 最近一次用于缩放两手指间的距离
     */
    private float lastDis;

    /**
     * 中的缩放比例，其它的是辅助，放大时直接需要就是一个totalRatio
     */
    private float totalRatio = 1f;
    /**
     * 当前的缩放比例
     */
    private float currentRatio = 1f;
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
     * 整个View的宽
     */
    private int totalWidth;
    /**
     * 整个View的高
     */
    private int totalHeight;
    /**
     * 原图片的宽度
     */
    private int srcPicWidth;
    /**
     * 原图片的高度
     */
    private int srcPicHeight;
    /**
     * 当前图片的宽
     */
    private int curPicWidth;
    /**
     * 当前图片的高
     */
    private int curPicHeight;
    /**
     * 右上角x坐标，以view的右上角为原点，（0,0）
     */
    private int coX;
    /**
     * 右上角y坐标，以view的右上角为原点，（0,0）
     */
    private int coY;
    /**
     * 图片的局部，要现实出来的部分
     */
    Rect srcRect = new Rect(0, 0, 1, 1);
    /**
     * 图片在canvas上面的位置
     */
    Rect dstRect = new Rect(1, 2, 3, 4);

    Paint picPaint = new Paint();
    Bitmap bitmapToview;
    /**
     * 将内容绘制到底图上，view再讲地图绘制到自己的canvas上面
     */
    Canvas secondCanvas;
    /**
     * 显示在屏幕上绘制的宽度
     */
    private int drawWidth;
    /**
     * 显示在屏幕上绘制的高度
     */
    private int drawHeight;

    /**
     * 是否可以touch
     */
    boolean touchable = true;

    public PtuView(Context context) {
        super(context);
        this.mContext = context;
    }

    public PtuView(Context context, AttributeSet set) {
        super(context, set);
        this.mContext = context;
        CURRENT_STATUS = STATUS_INIT;
        picPaint.setColor(Color.RED);
        picPaint.setStrokeWidth(25);
        picPaint.setTextSize(25);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        totalWidth = w;
        totalHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    /**
     * 保存生成的图片
     * @param path
     */
    public void saveNewBitmap(String path) {
		/*File file = new File(path);
		if (!file.exists())
			try { 
				FileOutputStream fileOutputStream = new FileOutputStream(
						file.getPath());
				bitmapToview.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream);
				fileOutputStream.close();
				Log.e("saveBmp is succseed", "yes!");
			} catch (Exception e) {
				e.printStackTrace();
			}*/
	}


    class Point {
        public float x, y;

        public void set(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    Point startPoint = new Point();
    Point endPoint = new Point();
    /**
     * 设置原图片
     *
     * @param Bitmap
     * 从调用的ACtivity中传过来的bitmap
     */
    Path path = new Path();

    /**
     * 根据路径解析出图片
     *
     * @param path2
     */
    public void initBitmap(String path2) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferQualityOverSpeed = true;
        sourceBitmap = BitmapFactory.decodeFile(path2);

        if (sourceBitmap == null) {
            Toast.makeText(mContext, "图片不存在", Toast.LENGTH_SHORT).show();
            Util.P.le("PTuView.initBitmap", "sourceBitmap出现空指针");
            return;
        }
        CURRENT_STATUS=STATUS_INIT;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!touchable) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startPoint.x = event.getX();
                startPoint.y = event.getY();

                if (Util.DoubleClick.isDoubleClick()) {
                    if (startPoint.x < dstRect.left || startPoint.x > dstRect.right || startPoint.y < dstRect.top
                            || startPoint.y > dstRect.bottom)
                        return true;
                    if (1.0 < totalRatio && totalRatio < MAX_RATIO) {//进行放大
                        currentRatio = MAX_RATIO / totalRatio;
                        totalRatio = MAX_RATIO;
                        CURRENT_STATUS = STATUS_ZOOM_BIG;
                    } else {
                        currentRatio = 1 / MAX_RATIO;
                        totalRatio = Math.min(totalWidth * 1.0f / (srcPicWidth * 1.0f),
                                totalHeight * 1.0f / (srcPicHeight * 1.0f));
                        CURRENT_STATUS = STATUS_INIT;
                    }
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                lastDis = getScaleDisAndCenter(event);
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    float endD = getScaleDisAndCenter(event);
                    currentRatio = endD / lastDis;
                    if (currentRatio > 1 - SCALE_FREQUENCE && currentRatio < 1 + SCALE_FREQUENCE)
                        return true;//缩放倍数太小

                    if (totalRatio * currentRatio > MAX_RATIO ||
                            totalRatio * currentRatio * srcPicWidth < totalWidth / 2
                                    && totalRatio * currentRatio * srcPicHeight < totalHeight / 2)
                        return true;//总倍数太大
                    totalRatio *= currentRatio;
                    if (currentRatio >= 1)
                        CURRENT_STATUS = STATUS_ZOOM_BIG;
                    else
                        CURRENT_STATUS = STATUS_ZOOM_SMALL;
                    lastDis = endD;
                } else if (event.getPointerCount() == 1) {

                    endPoint.x = event.getX();
                    endPoint.y = event.getY();
                    if (startPoint.x == -1)
                        startPoint.set(endPoint.x, endPoint.y);
                    CURRENT_STATUS = STATUS_MOVE;
                } else return true;
                invalidate();//myPath.addPoint(path,event.getX(), event.getY(
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() <= 2) {
                    startPoint.x = -1;
                    startPoint.y = -1;
                }
            default:
                break;
        }
        return true;
    }

    /**
     * 获取缩放的点之间的位置
     * <p>设置缩放的中心
     *
     * @param event
     * @return 返回两个触摸点（点0和点1）间的距离
     */
    private float getScaleDisAndCenter(MotionEvent event) {
        float x1 = event.getX(0), y1 = event.getY(0);
        float x2 = event.getX(1), y2 = event.getY(1);
        //如果某一边超出边界，则使用手指的中心，否则使用图片的中心
        if (coX > 0) startPoint.x = totalWidth / 2;
        else startPoint.x = (x1 + x2) / 2;
        if (coY > 0) startPoint.y = totalHeight / 2;
        else startPoint.y = (y1 + y2) / 2;

        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private void scalePic() {
        bitmapToview.eraseColor(Color.alpha(00));
        getParameter();
        getRect();
        secondCanvas.drawBitmap(sourceBitmap, srcRect, dstRect, picPaint);//将原图填充到底图上
    }

    /**
     * 获取当前的宽高，
     * <p>要绘制出的宽高，
     * <p>缩放后图片右上角顶点的位置
     */
    private void getParameter() {
        curPicWidth = (int) (srcPicWidth * totalRatio);
        curPicHeight = (int) (srcPicHeight * totalRatio);
        drawWidth = curPicWidth > totalWidth ? totalWidth : curPicWidth;
        drawHeight = curPicHeight > totalHeight ? totalHeight : curPicHeight;
        switch (CURRENT_STATUS) {//放大的计算
            case STATUS_ZOOM_BIG:
                coX = coX - (int) ((startPoint.x - coX) * (currentRatio - 1));
                coY = coY - (int) ((startPoint.y - coY) * (currentRatio - 1));
                if (coX > 0) coX = (totalWidth - curPicWidth) / 2;//校正偏差
                if (coY > 0) coY = (totalHeight - curPicHeight) / 2;//校正偏差
                break;
            case STATUS_ZOOM_SMALL://缩小的计算
                int dx = (int) (startPoint.x - coX), dy = (int) (startPoint.y - coY);
                dx = (int) (dx * currentRatio);
                dy = (int) (dy * currentRatio);
                coX = (int) (startPoint.x - dx);
                coY = (int) (startPoint.y - dy);
                if (coX > 0) coX = (totalWidth - curPicWidth) / 2;//校正偏差
                if (coY > 0) coY = (totalHeight - curPicHeight) / 2;//校正偏差
                break;
            case STATUS_INIT:
                coX = (totalWidth - curPicWidth) / 2;
                coY = (totalHeight - curPicHeight) / 2;
                break;
        }
    }


    /**
     * 绘制，这里根据不同的当前状态来绘制图片CURRENT_STATUS
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (CURRENT_STATUS) {
            case STATUS_INIT:
                initAndFirstDraw();
                break;
            case STATUS_DRAW_PATH:
                secondCanvas.drawLine(startPoint.x, startPoint.y,
                        endPoint.x, endPoint.y, picPaint);
                startPoint.x = endPoint.x;
                startPoint.y = endPoint.y;
                break;
            case STATUS_ZOOM_BIG:
                scalePic();//别合并，不同
                break;
            case STATUS_ZOOM_SMALL:
                scalePic();
                break;
            case STATUS_MOVE:
                movePic();
                break;
            default:
                break;
        }
        canvas.drawBitmap(bitmapToview, matrix, null);//将底图绘制到View上面到
    }

    /**
     * 图片在PtuFrameLayout上的相对位置
     * @return
     */
    public Rect getBound() {
        return dstRect;
    }

    private void movePic() {
        bitmapToview.eraseColor(Color.alpha(00));
        int t = coX;
        coX += endPoint.x - startPoint.x;
        if (coX >= 0 || Math.abs(coX) + totalWidth > curPicWidth)//如果x超出界限，就不移动了
            coX = t;

        t = coY;
        coY += endPoint.y - startPoint.y;
        if (coY >= 0 || Math.abs(coY) + totalHeight > curPicHeight)//如果y超出界限，就不移动了
            coY = t;
        startPoint.x = endPoint.x;
        startPoint.y = endPoint.y;
        getRect();
        secondCanvas.drawBitmap(sourceBitmap, srcRect, dstRect, picPaint);
    }

    /**
     * 获取原始bitmap的宽和高
     * <p>创建并设置好用于保存的Bitmap
     * <p>获取当前何种的Ratio
     */
    public void initAndFirstDraw() {
        if (sourceBitmap == null) {
            Toast.makeText(mContext, "图片不存在", Toast.LENGTH_SHORT).show();
            Util.P.le("PTuView.initBitmap", "sourceBitmap出现空指针");
            return;
        }
        srcPicWidth = sourceBitmap.getWidth();
        srcPicHeight = sourceBitmap.getHeight();
        bitmapToview = Bitmap.createBitmap(totalWidth, totalHeight,
                Config.ARGB_8888);//创建一个空图做底图
        secondCanvas = new Canvas(bitmapToview);//设置drawCanvas为底图
        currentRatio = totalRatio = Math.min(totalWidth * 1.0f / (srcPicWidth * 1.0f),
                totalHeight * 1.0f / (srcPicHeight * 1.0f));

        getParameter();
        getRect();
        secondCanvas.drawBitmap(sourceBitmap, srcRect, dstRect, picPaint);//将原图填充到底图上
    }

    private void getRect() {
        int drawX = coX < 0 ? 0 : coX, drawY = coY < 0 ? 0 : coY;
        int picX = coX > 0 ? 0 : -coX, picY = coY > 0 ? 0 : -coY;
        int x = (int) (picX / totalRatio), y = (int) (picY / totalRatio);
        int x1 = (int) ((picX + drawWidth) / totalRatio), y1 = (int) ((picY + drawHeight) / totalRatio);
        srcRect.set(x, y, x1, y1);
        dstRect.set(drawX, drawY, drawX + drawWidth, drawY + drawHeight);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Util.P.le(super.dispatchTouchEvent(event));
        return touchable;
    }

    @Override
    protected void onDetachedFromWindow() {
        bitmapToview.recycle();
        sourceBitmap.recycle();
        super.onDetachedFromWindow();
    }
}
