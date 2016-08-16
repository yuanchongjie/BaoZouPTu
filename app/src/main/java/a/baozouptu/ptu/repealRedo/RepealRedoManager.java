package a.baozouptu.ptu.repealRedo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;

import a.baozouptu.base.util.FileTool;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;

/**
 * Created by Administrator on 2016/7/28.
 */
public class RepealRedoManager {
    private static final String TAG = "RepealRedoManager";
    LinkedList<StepData> stepList;
    private ListIterator<StepData> iter;
    //撤销重做的最大步数
    private static int maxStep = 5;
    Bitmap baseBitmap;
    boolean hasChangePic;
    private static RepealRedoManager instanceTotal;

    private RepealRedoManager(int maxStep) {
        this.maxStep = maxStep;
        stepList = new LinkedList<>();
        iter = stepList.listIterator();
    }

    /**
     * @param externalMaxStep 构造器输入负数表示使用默认最大步数，5
     * @return
     */
    public static RepealRedoManager getInstanceTotal(int externalMaxStep) {
        if (instanceTotal == null)
            instanceTotal =new  RepealRedoManager(externalMaxStep < 0 ? maxStep : externalMaxStep);
        return instanceTotal;
    }

    /**
     * 提交操作，返回是否需要超出最大步数，
     * <p>超出则删除最列表开始的stepData，
     * <p>然后需要将BaseBitmap前进一步，
     *
     * @param sd
     * @return
     */
    public StepData commit(StepData sd) {
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
        iter.add(sd);
        if (stepList.size() > maxStep) {
            hasChangePic = true;
            while (iter.hasPrevious()) {
                iter.previous();
            }
            StepData resd = iter.next();
            iter.remove();
            while (iter.hasNext()) {
                iter.next();
            }
            return resd;
        }
        return null;
    }

    public Bitmap getBaseBitmap() {
        return baseBitmap;
    }

    public int getCurrentIndex() {
        return iter.previousIndex();
    }

    public boolean canRedo() {
        return iter.hasNext();
    }

    public StepData redo() {
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    public boolean canRepeal() {
        return iter.hasPrevious();
    }


    public static Bitmap addBm2Bm(Bitmap baseBitmap, Bitmap addBitmap, RectF boundRect, float rotateAngle) {
        Canvas c = new Canvas(baseBitmap);
        addBm2Canvas(c, addBitmap, boundRect, rotateAngle);
        return baseBitmap;
    }

    public static Canvas addBm2Canvas(Canvas baseCanvas, Bitmap addBitmap, RectF boundRect, float rotateAngle) {

        float centerX = (boundRect.left + boundRect.right) / 2, centerY = (boundRect.bottom + boundRect.top) / 2;
        //将realBm到图上
        BitmapDrawable addDrawable = new BitmapDrawable(Util.MyApplication.getAppContext().getResources(), addBitmap);
        addDrawable.setDither(true);
        addDrawable.setAntiAlias(true);
        addDrawable.setFilterBitmap(true);
        baseCanvas.rotate(rotateAngle, centerX, centerY);//旋转
        addDrawable.setBounds(GeoUtil.rectF2Rect(boundRect));
        addDrawable.draw(baseCanvas);
        baseCanvas.save();
        baseCanvas.restore();

        return baseCanvas;
    }


    /**
     * view的显示在图片上的部分的截图
     *
     * @param view
     * @param innerRect view的显示在图片上的部分的区域
     * @return view的显示在图片上的部分的截图
     */
    public static Bitmap getInnerBmFromView(View view, RectF innerRect) {
        final Bitmap[] innerBitmap = new Bitmap[1];
        try {
            Bitmap viewBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                    Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(viewBitmap));
            innerBitmap[0] = Bitmap.createBitmap(viewBitmap, (int) innerRect.left, (int) innerRect.top,
                    (int) (innerRect.right - innerRect.left), (int) (innerRect.bottom - innerRect.top));//获取floatview内部的内容
            viewBitmap.recycle();
        } catch (OutOfMemoryError e) {
            innerBitmap[0].recycle();
            e.printStackTrace();
        }
        Util.P.le(TAG, "getInnerBmFromView完成");
        return innerBitmap[0];
    }

    public StepData getStepdata(int i) {
        return stepList.get(i);
    }

    public void setBaseBm(Bitmap baseBitmap) {
        this.baseBitmap = baseBitmap;
    }

    public void repeal() {
        iter.previous();
    }

    /**
     * 待用
     *
     * @return
     */
    public boolean hasChangePic() {
        if (hasChangePic == true)
            return true;
        else if (getCurrentIndex() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void clear(Context context) {
        String path=FileTool.createTempPicPath(context);
        String parentPath = path.substring(0,
                path.lastIndexOf('/'));
        FileTool.deleteDir(new File(parentPath));
        stepList.clear();
    }
}
