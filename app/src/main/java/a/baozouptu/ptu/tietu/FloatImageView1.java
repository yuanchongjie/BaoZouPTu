package a.baozouptu.ptu.tietu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;

import a.baozouptu.R;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.FloatView;
import a.baozouptu.ptu.MicroButtonData;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TietuStepData;
import a.baozouptu.ptu.view.IconBitmapCreator;

/**
 * Created by liuguicen on 2016/6/30.
 *
 * @description 浮动的视图
 */
public class FloatImageView1 extends View implements FloatView {
    private static String DEBUG_TAG = "FloatImageView";

    /**
     * 原图片的宽度,高度
     */
    private int srcPicWidth, srcPicHeight;
    /**
     * 移动的顶点最后的位置
     */
    public float relativeX, relativeY;
    private static final String ITEM_ROTATE = "rotateTo";

    private Context mContext;
    public int mPadding = Util.dp2Px(24);

    private Rect picBoundRect;

    /**
     * floatView的宽和高，包括padding，保证加上mleft，mtop之后不会超出原图片的边界
     */
    private static int SHOW_SATUS = STATUS_ITEM;
    private int rimColor;
    private int itemColor;

    private float minMoveDis = Util.dp2Px(3);
    private long downTime = 0;
    private boolean hasUp = true;
    private int lastSelectionId;

    MicroButtonData[] items = new MicroButtonData[]{null, null, null, null, null, null, null, null};

    /**
     * 内部的bitmap相关的域
     */
    private Bitmap sourceBitmap;
    private Bitmap tietuDitu;
    private Bitmap tempBitmap;

    /**
     * 图片宽高
     */
    private int picWidth, picHeight;
    /**
     * 贴图原始宽高
     */
    private int tietuWidth, tietuHeight;
    /**
     * 当前贴图宽高
     */
    private int curTHeight, curTWidth;
    /**
     * 贴图当前中心的位置
     */
    private int centerY, centerX;
    /**
     * ptuView总的宽高
     */
    private int totalHeight, totalWidth;

    private float totalRotateAngle;
    private float lastRatio;
    private float totalRatio;
    private float lastX, lastY;
    private float lastDis;
    private float lastAngle = 0;
    private String mPath;


    public FloatImageView1(Context context) {
        super(context);
        mContext = context;
    }

    public FloatImageView1(Context context, Rect pvBoundRect, int totalWidth, int totalHeight) {
        super(context);
        mContext = context;
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.picBoundRect = pvBoundRect;
    }

    @Override
    public void initItems() {
        rimColor = Util.getColor(R.color.float_rim_color);
        itemColor = Util.getColor(R.color.float_item_color);
        MicroButtonData item = new MicroButtonData(-1, -1, ITEM_ROTATE);
        item.bitmap = IconBitmapCreator.getRotateBitmap(mPadding, itemColor);
        items[7] = item;
    }

    /**
     * 设置贴图的bitmap并初始化
     * @return 设置贴图图片是否成功
     */
    public boolean setBitmapAndInit(Bitmap bitmap) {
        if(bitmap==null)return false;
        sourceBitmap = bitmap;
        init();
        invalidate();
        return true;
    }


    /**
     * 设置贴图的bitmap并初始化
     */
    public boolean setBitmapAndInit(String path) {
        mPath = path;
        return setBitmapAndInit(BitmapTool.getLosslessBitmap(path));
    }


    /**
     * 初始化
     */
    private void init() {
        tietuWidth=10;
        tietuHeight=10;
        lastRatio = totalRatio = 1;
        tempBitmap = sourceBitmap;
        totalRotateAngle = 0;

        picWidth = picBoundRect.right - picBoundRect.left;
        picHeight = picBoundRect.bottom - picBoundRect.top;
        tietuWidth = sourceBitmap.getWidth();
        tietuHeight = sourceBitmap.getHeight();
        centerX = totalWidth / 2;
        centerY = totalHeight / 2;
        curTWidth = (int) (tietuWidth * totalRatio);
        curTHeight = (int) (tietuHeight * totalRatio);

        tietuDitu = Bitmap.createBitmap(totalWidth,
                totalHeight, Bitmap.Config.ARGB_8888);

        if (tietuWidth > picWidth || tietuHeight > picHeight) {
            totalRatio = Math.min(picWidth * 1.0f / tietuWidth,
                    picHeight * 1.0f / tietuHeight);
            curTWidth = (int) (tietuWidth * totalRatio);
            curTHeight = (int) (tietuHeight * totalRatio);
        }
    }

    /**
     * 传入尝试要缩放的的倍数，看是否能缩放到该倍数，
     * <p>只做测试，不改变数据
     * <p>缩放程度小于1/200就不缩放
     * <p>缩放的后的大小太小不缩放</p>
     *
     * @param nr 尝试要缩放的的倍数
     * @return 能缩放的倍数
     */
    @Override
    public float adjustSize(float nr) {

        if (tietuWidth * nr > totalWidth * 1.5)
            nr = Math.min(nr, totalWidth * 1.5f / tietuWidth);
        if (tietuWidth * nr < 10)
            nr = Math.max(nr, 10.0f / tietuWidth);
        if (tietuHeight * nr > totalHeight * 1.5)
            nr = Math.min(nr, totalHeight * 1.5f / tietuHeight);
        if (tietuHeight * nr < 10)
            nr = 10.0f / tietuHeight;
        if (Math.abs(nr - totalRatio) < 0.005) {
            return -1;
        }
        return nr;
    }

    @Override
    public float getmWidth() {
        return totalWidth;
    }

    @Override
    public float getmHeight() {
        return totalHeight;
    }

    @Override
    public float getfTop() {
        return 0;
    }

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

    /**
     * @param picInitRatio ptuView上图片的初始缩放比例
     */
    public StepData getResultData(float picInitRatio) {
        TietuStepData sd = new TietuStepData(PtuActivity.EDIT_TIETU);
        sd.picPath = mPath;
        return sd;
    }

    public Bitmap getSourceBitmap() {
        return sourceBitmap;
    }

    private void moveTietu(float x, float y) {
        float dx = x - lastX, dy = y - lastY;
        lastX = x;
        lastY = y;
        if (adjustEdgeBound(centerX + dx, centerY + dy))//如果发生移动
        {
            invalidate();
        }
    }

    @Override
    public void scale(float ratio) {
        curTWidth = (int) (tietuWidth * totalRatio);
        curTHeight = (int) (tietuHeight * totalRatio);
        adjustEdgeBound(centerX, centerY);
        invalidate();
    }

    private void rotate() {
        invalidate();
    }


    private float getAngle(float x, float y, float x1, float y1) {
        float dx = x1 - x, dy = y1 - y;
        double angle = Math.atan2(dy, dx);
        return (float) Math.toDegrees(angle);
    }

    /**
     * 适配边界，能移动返回true，否则false
     * 会改变数据centerX，centerY
     *
     * @param nx
     * @param ny
     * @return
     */
    @Override
    public boolean adjustEdgeBound(float nx, float ny) {
        if (nx - curTWidth / 2 > picBoundRect.right)//左边界超出右边
            nx = picBoundRect.right + curTWidth / 2;
        if (nx + curTWidth / 2 < picBoundRect.left)//右边界超出了左边
            nx = picBoundRect.left - curTWidth / 2;
        if (ny - curTHeight / 2 > picBoundRect.bottom)//上边界超出了下边
            ny = picBoundRect.bottom + curTHeight / 2;
        if (ny + curTHeight / 2 < picBoundRect.top)//下边界超出了上边
            ny = picBoundRect.top - curTHeight / 2;

        if ((int) nx == centerX && (int) ny == centerY) {
            return false;
        } else {
            centerX = (int) nx;
            centerY = (int) ny;
            return true;
        }
    }

    @Override
    public void drag(float nx, float ny) {

    }

    @Override
    public void drawItem(Canvas canvas, MicroButtonData item) {

    }

    @Override
    public void setDownState() {
    }

    @Override
    public int getDownState() {
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                Util.P.le(DEBUG_TAG, "经过了down");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //缩放
                lastDis = GeoUtil.getDis(event.getX(0), event.getY(0), event.getX(1), event.getY(1));

                //旋转
                lastAngle = getAngle(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                //移动
                lastX = (event.getX(0) + event.getX(1)) / 2;
                lastY = (event.getY(0) + event.getY(1)) / 2;
            case MotionEvent.ACTION_MOVE:
                //移动
                if (event.getPointerCount() == 1) {
                    moveTietu(event.getX(), event.getY());
                } else {
                    //缩放
                    float endD = GeoUtil.getDis(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    float currentRatio = endD / lastDis;
                    lastDis = endD;
                    float ratio = adjustSize(totalRatio * currentRatio);
                    if (ratio != -1 && ratio != totalRatio) {
                        totalRatio = ratio;
                        scale(totalRatio);
                    }
                    //旋转
                    float curAngle = getAngle(event.getX(0), event.getY(0),
                            event.getX(1), event.getY(1));
                    totalRotateAngle += (curAngle - lastAngle);
                    lastAngle = curAngle;
                    rotate();
                    //移动
                    moveTietu((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //剩下一个手指时，获取该手指的位置，后面跟着能进行移动
                if (event.getPointerCount() == 2) {
                    int index = event.getActionIndex() == 0 ? 1 : 0;
                    lastX = event.getX(index);
                    lastY = event.getY(index);
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

                    //旋转
                    lastAngle = getAngle(event.getX(i0), event.getY(i0), event.getX(i1), event.getY(i1));

                    //移动
                    lastX = (event.getX(i0) + event.getX(i1)) / 2;
                    lastY = (event.getY(i0) + event.getY(i1)) / 2;
                }
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(sourceBitmap==null)return;//临时代码
        canvas.rotate(totalRotateAngle, centerX, centerY);
        //先将图绘制到底图上面，再将底图绘制到view上面
        if (totalRatio != lastRatio) {//如果发生了缩放
            if (tempBitmap != null && tempBitmap != sourceBitmap) {
                tempBitmap.recycle();
                tempBitmap = null;
            }
            tempBitmap = Bitmap.createScaledBitmap(
                    sourceBitmap, curTWidth, curTHeight, true);
            lastRatio = totalRatio;
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), tempBitmap);
        bitmapDrawable.setBounds(centerX - curTWidth / 2, centerY - curTHeight / 2,
                centerX + curTWidth / 2, centerY + curTHeight / 2);
        bitmapDrawable.draw(canvas);
        canvas.restore();
        super.onDraw(canvas);
    }

    public void releaseResourse() {
        if (sourceBitmap != null) {
            sourceBitmap.recycle();
            sourceBitmap = null;
        }
        if (tietuDitu != null) {
            tietuDitu.recycle();
            tietuDitu = null;
        }
        if (tempBitmap != null) {
            tempBitmap.recycle();
            tempBitmap = null;
        }
    }

    public static void addBigStep(Bitmap bm, StepData sd) {
        TietuStepData ttsd = (TietuStepData) sd;
        Bitmap imageBitmap = BitmapTool.getLosslessBitmap(ttsd.picPath);
        RepealRedoManager.addBm2Bm(bm, imageBitmap, sd.boundRectInPic, sd.rotateAngle);
        imageBitmap.recycle();
        imageBitmap = null;
    }
}