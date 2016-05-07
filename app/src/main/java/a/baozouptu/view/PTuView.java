package a.baozouptu.view;

import a.baozouptu.tools.DoubleClick;
import a.baozouptu.tools.P;

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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class PtuView extends View {

    final float maxRatio = 5;
    /**
     * 表示当前处理状态：缩放，移动等等
     */
    static int CURRENT_STATUS = 0;
    /**
     * 初始化状态
     */
    static final int STATUS_INIT = 0;
    /**
     * 移动状态
     */
    static final int STATUS_MOVE = 1;
    /**
     * 缩放状态
     */
    static final int STATUS_ZOOM_SMALL = 2;

    private static final int STATUS_ZOOM_BIG = 3;

    static final int STATUS_DRAW_PATH = 4;
    /**
     * 最近的x的位置
     */
    float lastX = -1;
    /**
     * 最近的y的位置
     */
    float lastY = -1;
    /**
     * x的移动距离
     */
    float totalTranX;
    /**
     * y的移动距离
     */
    float totalTranY = 0;
    /**
     * 当前用于缩放两个手指的距离
     */

    double currentDis;
    /**
     * 最近一次用于缩放两手指间的距离
     */
    double lastDis;

    /**
     * 中的缩放比例
     */
    float totalRatio = 1f;
    /**
     * 当前的缩放比例
     */
    float currentRatio = 1f;
    /**
     * context
     */
    Context context;
    /**
     * 原图片
     */
    Bitmap sourceBitmap;
    /**
     * 用于处理图片的矩阵
     */
    Matrix matrix = new Matrix();
    /**
     * 整个View的宽
     */
    int totalWidth;
    /**
     * 整个View的高
     */
    int totalHeight;
    /**
     * 原图片的宽度
     */
    int srcPicWidth;
    /**
     * 原图片的高度
     */
    int srcPicHeight;
    /**
     * 当前图片的宽
     */
    int curPicWidth;
    /**
     * 当前图片的高
     */
    int curPicHeight;
    /**
     * 右上角x坐标，以view的右上角为原点，（0,0）
     */
    int rtX;
    /**
     * 右上角y坐标，以view的右上角为原点，（0,0）
     */
    int rtY;

    Rect srcRect;
    Rect dstRect;

    Paint picPaint = new Paint();
    Bitmap bitmapToDraw;

    public PtuView(Context context, AttributeSet set) {
        super(context, set);
        this.context = context;
        CURRENT_STATUS = STATUS_INIT;
        picPaint.setColor(Color.RED);
        picPaint.setStrokeWidth(25);
        picPaint.setTextSize(25);
        srcRect = new Rect(0, 0, 1, 1);
        dstRect = new Rect(1, 2, 3, 4);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        totalWidth = w;
        totalHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }
    /*
    public void saveNewBitmap(String path) {
		File file = new File(path);
		if (!file.exists())
			try { 
				FileOutputStream fileOutputStream = new FileOutputStream(
						file.getPath());
				bitmapToDraw.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream);
				fileOutputStream.close();
				Log.e("saveBmp is succseed", "yes!");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
*/

    class Point {
        public float x, y;

        Point() {
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
            Toast.makeText(context, "图片不存在", Toast.LENGTH_SHORT).show();
            P.le("PTuView.initBitmap", "sourceBitmap出现空指针");
            return;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startPoint.x = event.getX();
                startPoint.y = event.getY();
                if (DoubleClick.isDoubleClick()) {
                    if (totalRatio < maxRatio) {//进行放大
                        currentRatio = maxRatio / totalRatio;
                        totalRatio = maxRatio;
                        CURRENT_STATUS = STATUS_ZOOM_BIG;
                        invalidate();
                    } else {
                        currentRatio = 1;
                        CURRENT_STATUS = STATUS_ZOOM_SMALL;
                        invalidate();
                    }
                }
                //myPath.startPath(path, event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                endPoint.x = event.getX();
                endPoint.y = event.getY();
                //myPath.addPoint(path,event.getX(), event.getY());
                CURRENT_STATUS = STATUS_DRAW_PATH;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private void scale(Canvas canvas,boolean fangda) {
        getParameter(true);
        getRect();
        canvas.drawBitmap(Bitmap.createScaledBitmap(sourceBitmap, curPicWidth, curPicHeight, true),
                srcRect, dstRect, picPaint);//将原图填充到底图上
    }

    private void getParameter(boolean fangda) {
        curPicWidth = (int) (curPicWidth * currentRatio);
        curPicHeight = (int) (curPicHeight * currentRatio);
        if (fangda) {
            rtX = rtX - (int) (Math.abs(rtX - startPoint.x) * (currentRatio - 1));
            rtY = rtY - (int) (Math.abs(rtY - startPoint.y) * (currentRatio - 1));
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
                init(canvas);
                break;
            case STATUS_DRAW_PATH:
                canvas.drawLine(startPoint.x, startPoint.y,
                        endPoint.x, endPoint.y, picPaint);
                startPoint.x = endPoint.x;
                startPoint.y = endPoint.y;
                break;
            case STATUS_ZOOM_BIG:
                scale(canvas,true);
                break;
            case STATUS_ZOOM_SMALL:
                toInit(canvas);
                break;
            default:
                break;
        }
        matrix.reset();
        canvas.drawBitmap(bitmapToDraw, matrix, null);//将底图绘制到View上面到
        P.le("PTuView.onDraw", "到达");
    }

    public void toInit(Canvas canvas) {
        srcPicWidth = sourceBitmap.getWidth();
        srcPicHeight = sourceBitmap.getHeight();

        totalRatio = Math.min(totalWidth * 1.0f / (srcPicWidth * 1.0f),
                totalHeight * 1.0f / (srcPicHeight * 1.0f));

        curPicWidth = (int) (srcPicWidth * totalRatio);
        curPicHeight = (int) (srcPicHeight * totalRatio);
        rtX = (totalWidth - curPicWidth) / 2;
        rtY = (totalHeight - curPicHeight) / 2;
        getRect();
        canvas.drawBitmap(Bitmap.createScaledBitmap(sourceBitmap, curPicWidth, curPicHeight, true),
                srcRect, dstRect, picPaint);//将原图填充到底图上
    }

    public void init(Canvas canvas) {
        srcPicWidth = sourceBitmap.getWidth();
        srcPicHeight = sourceBitmap.getHeight();

        totalRatio = Math.min(totalWidth * 1.0f / (srcPicWidth * 1.0f),
                totalHeight * 1.0f / (srcPicHeight * 1.0f));

        curPicWidth = (int) (srcPicWidth * totalRatio);
        curPicHeight = (int) (srcPicHeight * totalRatio);
        rtX = (totalWidth - curPicWidth) / 2;
        rtY = (totalHeight - curPicHeight) / 2;
        bitmapToDraw = Bitmap.createBitmap(totalWidth, totalHeight,
                Config.ARGB_8888);//创建一个空图做底图
        canvas.setBitmap(bitmapToDraw);//设置drawCanvas为底图
        getRect();
        canvas.drawBitmap(Bitmap.createScaledBitmap(sourceBitmap, curPicWidth, curPicHeight, true),
                srcRect, dstRect, picPaint);//将原图填充到底图上
    }

    private void getRect() {
        int drawX = rtX < 0 ? 0 : rtX, drawY = rtY < 0 ? 0 : rtY;
        int picX = rtX > 0 ? 0 : -rtX, picY = rtY > 0 ? 0 : -rtY;
        int drawWidth = curPicWidth > totalWidth ? totalWidth : curPicWidth;
        int drawHeight = curPicHeight > totalHeight ? totalHeight : curPicHeight;
        srcRect.set(picX, picY, picX + drawWidth, picY + drawHeight);
        dstRect.set(drawX, drawY, drawX + drawWidth, drawY + drawHeight);
    }
}
