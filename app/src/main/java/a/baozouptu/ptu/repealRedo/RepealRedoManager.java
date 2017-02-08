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

import a.baozouptu.common.appInfo.MyApplication;
import a.baozouptu.common.util.FileTool;
import a.baozouptu.common.util.GeoUtil;
import a.baozouptu.common.util.Util;

/**
 * Created by Administrator on 2016/7/28.
 */
public class RepealRedoManager<T> {
    private static final String TAG = "RepealRedoManager";
    //撤销重做的最大步数
    private int maxStep = 5;
    private LinkedList<T> stepList;
    private ListIterator<T> iter;
    private Bitmap baseBitmap;
    private boolean hasChangePic;
    private boolean hasSavePic;

    public RepealRedoManager(int maxStep) {
        this.maxStep = maxStep;
        stepList = new LinkedList<>();
        iter = stepList.listIterator();
        hasSavePic=false;
    }


    /**
     * 提交操作，返回是否需要超出最大步数，
     * <p>超出则删除最列表开始的stepData，
     * <p>然后需要将BaseBitmap前进一步，
     */
    public T commit(T sd) {
        hasSavePic=false;
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
            T resd = iter.next();
            iter.remove();
            while (iter.hasNext()) {
                iter.next();
            }
            return resd;
        }
        return null;
    }
    public int getCurrentIndex() {
        return iter.previousIndex();
    }

    public boolean canRedo() {
        return iter.hasNext();
    }

    /**
     * 返回redo数据结果，并且当前指针前进一步
     */
    public T redo() {
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    public boolean canRepeal() {
        return iter.hasPrevious();
    }



    public T getStepdata(int i) {
        return stepList.get(i);
    }

    /**
     * 当前指针向前一步
     */
    public void repealPrepare() {
        iter.previous();
    }

    public void clear(Context context) {
        String path = FileTool.createTempPicPath(context);
        String parentPath = path.substring(0,
                path.lastIndexOf('/'));
        FileTool.deleteDir(new File(parentPath));
        stepList.clear();
        iter = stepList.listIterator();
    }

    public void init() {
        iter = stepList.listIterator();
    }

    public Bitmap getBaseBitmap() {
        return baseBitmap;
    }

    public static Bitmap addBm2Bm(Bitmap baseBitmap, Bitmap addBitmap, RectF boundRect, float rotateAngle) {
        Canvas c = new Canvas(baseBitmap);
        addBm2Canvas(c, addBitmap, boundRect, rotateAngle);
        return baseBitmap;
    }

    public static Canvas addBm2Canvas(Canvas baseCanvas, Bitmap addBitmap, RectF boundRect, float rotateAngle) {
        baseCanvas.save();
        float centerX = (boundRect.left + boundRect.right) / 2, centerY = (boundRect.bottom + boundRect.top) / 2;
        //将realBm到图上
        BitmapDrawable addDrawable = new BitmapDrawable(MyApplication.appContext.getResources(), addBitmap);
        addDrawable.setDither(true);
        addDrawable.setAntiAlias(true);
        addDrawable.setFilterBitmap(true);
        baseCanvas.rotate(rotateAngle, centerX, centerY);//旋转
        addDrawable.setBounds(GeoUtil.rectF2Rect(boundRect));
        addDrawable.draw(baseCanvas);
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
            innerBitmap[0] = Bitmap.createBitmap(viewBitmap, Math.round(innerRect.left), Math.round(innerRect.top),
                    Math.round(innerRect.right - innerRect.left), Math.round(innerRect.bottom - innerRect.top));//获取floatview内部的内容
            viewBitmap.recycle();
            viewBitmap = null;
        } catch (OutOfMemoryError e) {
            innerBitmap[0].recycle();
            innerBitmap[0] = null;
            e.printStackTrace();
        }
        Util.P.le(TAG, "getInnerBmFromView完成");
        return innerBitmap[0];
    }
    public void setBaseBm(Bitmap baseBitmap) {
        this.baseBitmap = baseBitmap;
    }

    /**
     * 待用
     *
     * @return
     */
    public boolean hasChangePic() {
        if(hasSavePic)return false;
        if (hasChangePic)
            return true;
        else if (getCurrentIndex() >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setHasSavePic(boolean hasSavePic) {
        this.hasSavePic = hasSavePic;
    }
}
