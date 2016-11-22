package a.baozouptu.ptu.draw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.FileTool;
import a.baozouptu.base.util.MU;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.RepealRedoListener;
import a.baozouptu.ptu.repealRedo.DrawStepData;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TextStepData;
import a.baozouptu.ptu.view.PtuView;

/**
 * 涂鸦View
 * Created by yonglong on 2016/7/2.
 */
public class DrawView extends View {

    private Context context;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Path mpicPath;
    private Paint mBitmapPaint;// 画布的画笔
    private Paint mPaint;// 真实的画笔
    private float mX, mY ,mPicX,mPicY;// 临时点坐标
    private static final float TOUCH_TOLERANCE = 4;
    // 保存Path路径的集合,用List集合来模拟栈
    private static List<DrawPath> savePath;
    private static List<DrawPath> picsavePath;
    // 保存已删除Path路径的集合
    private static List<DrawPath> deletePath;
    private static List<DrawPath> picdeletePath;
    // 记录Path路径的对象
    private DrawPath dp;
    private DrawPath picdp;
    private int screenWidth, screenHeight;
    private int currentColor = Color.RED;
    public int currentSize = 15;
    private int currentStyle = 0;
    //原图
    public PtuView ptuView =null;
    Rect totalBound=null;
    Rect picBound=null;
    private RepealRedoListener repealRedoListener;
    private Shader shader;

    public List<DrawPath> getResultData() {
        return picsavePath;
    }

    public class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }

    /**
     * @param context
     */
    public DrawView(Context context, Rect totalBound,PtuView ptuView) {
        super(context);
        this.context = context;
        this.ptuView = ptuView;
        this.totalBound = totalBound;
        this.picBound = ptuView.getPicBound();
        screenWidth = picBound.width();
        screenHeight = picBound.height();

        setLayerType(LAYER_TYPE_SOFTWARE, null);//设置默认样式，去除dis-in的黑色方框以及clear模式的黑线效果
        initCanvas();
        picsavePath = new ArrayList<>();
        savePath = new ArrayList<>();
        picdeletePath = new ArrayList<>();
        deletePath = new ArrayList<>();
    }

    public void initCanvas() {



        setPaintStyle();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        Bitmap originBm = null;
        //画布大小
//        Bitmap.createBitmap(ptuView.getDstRect().left, ptuView.getDstRect().top,
//                ptuView.getDstRect().width(), ptuView.getDstRect().height());
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.argb(0, 0, 0, 0));
        mCanvas = new Canvas(mBitmap);  //所有mCanvas画的东西都被保存在了mBitmap中
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    /**
     * Paint类样式说明
     * setMaskFilter(MaskFilter maskfilter); 设置MaskFilter，可以用不同的MaskFilter实现滤镜的效果，如滤化，立体等
     */
    /*
      MaskFilter类可以为Paint分配边缘效果。
     对MaskFilter的扩展可以对一个Paint边缘的alpha通道应用转换。Android包含了下面几种MaskFilter：
     BlurMaskFilter   指定了一个模糊的样式和半径来处理Paint的边缘。
     EmbossMaskFilter  指定了光源的方向和环境光强度来添加浮雕效果。
     要应用一个MaskFilter，可以使用setMaskFilter方法，并传递给它一个MaskFilter对象。下面的例子是对一个已经存在的Paint应用一个EmbossMaskFilter：
*/
    //初始化画笔样式
    private void setPaintStyle() {
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mPaint.setAntiAlias(true);//设置是否使用抗锯齿功能，会消耗较大资源，绘制图形速度会变慢。
        mPaint.setDither(true); //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        //初始
        mPaint.setStrokeWidth(currentSize);
        mPaint.setColor(currentColor);

        // 设置光源的方向
        float[] direction = new float[]{1.5f, 1.5f, 1.5f};
        //设置环境光亮度
        float light = 0.6f;
        // 选择要应用的反射等级
        float specular = 6;
        // 向mask应用一定级别的模糊
        float mask_blur = 4.2f;
        //浮雕
        EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, mask_blur);
        //模糊
        BlurMaskFilter blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

 /*
             * LinearGradient shader = new LinearGradient(0, 0, endX, endY, new
             * int[]{startColor, midleColor, endColor},new float[]{0 , 0.5f,
             * 1.0f}, TileMode.MIRROR);
             * 参数一为渐变起初点坐标x位置，参数二为y轴位置，参数三和四分辨对应渐变终点
             * 其中参数new int[]{startColor, midleColor,endColor}是参与渐变效果的颜色集合，
             * 其中参数new float[]{0 , 0.5f, 1.0f}是定义每个颜色处于的渐变相对位置， 这个参数可以为null，如果为null表示所有的颜色按顺序均匀的分布
             */
        Shader mShader = new LinearGradient(0, 0, 100, 100,
                new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW},

                null, Shader.TileMode.REPEAT);
        // Shader.TileMode三种模式
        // REPEAT:沿着渐变方向循环重复
        // CLAMP:如果在预先定义的范围外画的话，就重复边界的颜色
        // MIRROR:与REPEAT一样都是循环重复，但这个会对称重复

        switch (currentStyle) {
            case 0:
                //初始
                mPaint.setStrokeWidth(currentSize);
                mPaint.setColor(currentColor);
                break;
            case 1:
                //橡皮擦
                mPaint.setAlpha(0);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                mPaint.setColor(Color.TRANSPARENT);
                mPaint.setStrokeWidth(50);
                break;
            case 2:
                // 应用mask
                mPaint.setMaskFilter(emboss);
                break;
            case 3:
                // 应用mask
                mPaint.setMaskFilter(blur);
                break;
            case 4:
                mPaint.setShader(mShader);
                break;
            case 5:
                if(shader==null)
                    shader = new BitmapShader(BitmapFactory.decodeResource(getResources(), R.mipmap.ma), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                mPaint.setShader(shader);
                break;
            case 6:
                break;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        //canvas.drawColor(0xFFAAAAAA);
        // 将前面已经画过得显示出来
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        if (mPath != null) {
            // 实时的显示
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void pic_touch_start(float x, float y) {
        mpicPath.moveTo(x, y);
        mPicX = x;
        mPicY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(mY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也可以)
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            //mPath.lineTo(mX,mY);
            mX = x;
            mY = y;
        }
    }

    private void pic_touch_move(float x, float y) {
        float dx = Math.abs(x - mPicX);
        float dy = Math.abs(mPicY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也可以)
            mpicPath.quadTo(mPicX, mPicY, (x + mPicX) / 2, (y + mPicY) / 2);
            //mPath.lineTo(mX,mY);
            mPicX = x;
            mPicY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        //将一条完整的路径保存下来(相当于入栈操作)
        savePath.add(dp);
        mPath = null;// 重新置空

        if (savePath!=null&&savePath.size()>0){
            repealRedoListener.canRepeal(true);
        }else {
            repealRedoListener.canRepeal(false);
        }
        if (deletePath!=null&&deletePath.size()>0){
            repealRedoListener.canRedo(true);
        }else {
            repealRedoListener.canRedo(false);
        }
    }
    private void pic_touch_up() {
        mpicPath.lineTo(mPicX, mPicY);
        //将一条完整的路径保存下来(相当于入栈操作)
        picsavePath.add(picdp);
        mPath = null;// 重新置空
    }

    /**
     * 撤销
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            DrawPath picdrawPath = picsavePath.get(picsavePath.size() - 1);
            deletePath.add(drawPath);
            picdeletePath.add(picdrawPath);
            savePath.remove(savePath.size() - 1);
            picsavePath.remove(picsavePath.size()-1);

            if (savePath!=null&&savePath.size()>0){
                repealRedoListener.canRepeal(true);
            }else {
                repealRedoListener.canRepeal(false);
            }
            if (deletePath!=null&&deletePath.size()>0){
                repealRedoListener.canRedo(true);
            }else {
                repealRedoListener.canRedo(false);
            }

            redrawOnBitmap();
        }
    }

    /**
     * 重做
     */
    public void redo() {
        if (savePath != null && savePath.size() > 0) {
            repealRedoListener.canRedo(true);
            repealRedoListener.canRepeal(true);

            savePath.clear();
            picsavePath.clear();
            if (savePath!=null&&savePath.size()>0){
                repealRedoListener.canRepeal(true);
            }else {
                repealRedoListener.canRepeal(false);
            }
            if (deletePath!=null&&deletePath.size()>0){
                repealRedoListener.canRedo(true);
            }else {
                repealRedoListener.canRedo(false);
            }

            redrawOnBitmap();
        }
    }

    private void redrawOnBitmap() {

        /*mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布*/
        initCanvas();
        Iterator<DrawPath> iter = savePath.iterator();
        while (iter.hasNext()) {
            DrawPath drawPath = iter.next();
            mCanvas.drawPath(drawPath.path, drawPath.paint);
        }
        invalidate();// 刷新
    }

    /**
     * 恢复，恢复的核心就是将删除的那条路径重新添加到savapath中重新绘画即可
     */
    public void recover() {
        if (deletePath.size() > 0) {
            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
            DrawPath dp = deletePath.get(deletePath.size() - 1);
            DrawPath picdp = picdeletePath.get(picdeletePath.size() - 1);
            savePath.add(dp);
            picsavePath.add(picdp);
            //将取出的路径重绘在画布上
            mCanvas.drawPath(dp.path, dp.paint);
            //将该路径从删除的路径列表中去除
            deletePath.remove(deletePath.size() - 1);
            picdeletePath.remove(picdeletePath.size() - 1);

            if (savePath!=null&&savePath.size()>0){
                repealRedoListener.canRepeal(true);
            }else {
                repealRedoListener.canRepeal(false);
            }
            if (deletePath!=null&&deletePath.size()>0){
                repealRedoListener.canRedo(true);
            }else {
                repealRedoListener.canRedo(false);
            }

            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float[] pxy = PtuUtil.getLocationAtPicture(x + getLeft(), y + getTop(),
                ptuView.getSrcRect(), ptuView.getDstRect());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 每次down下去重新new一个Path
                mPath = new Path();
                mpicPath = new Path();
                picdp = new DrawPath();
                picdp.path = mpicPath;
                picdp.paint = mPaint;
                //每一次记录的路径对象是不一样的
                dp = new DrawPath();
                dp.path = mPath;
                dp.paint = mPaint;
                touch_start(x, y);
                pic_touch_start(pxy[0], pxy[1]);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                pic_touch_move(pxy[0],pxy[1]);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                pic_touch_up();
                invalidate();
                break;
        }
        return true;
    }


    //以下为样式修改内容
    //设置画笔样式
    public void selectPaintStyle(int which) {
        currentStyle = which;
        setPaintStyle();
    }

    //选择画笔大小
    public void selectPaintSize(int which) {
        //int fixed_size = Integer.parseInt(this.getResources().getStringArray(R.array.paintsize)[which]);
        currentSize = which;
        setPaintStyle();
    }

    //设置画笔颜色
    public void selectPaintColor(int which) {
        currentColor = which;
        setPaintStyle();
    }


    public DrawStepData getResultData(DrawView drawView) {

        DrawStepData tsd = new DrawStepData(PtuUtil.EDIT_DRAW);
        tsd.rotateAngle = 0;
        return tsd;
    }
    public void setRepealRedoListener(RepealRedoListener repealRedoListener) {
        this.repealRedoListener = repealRedoListener;
    }
}
