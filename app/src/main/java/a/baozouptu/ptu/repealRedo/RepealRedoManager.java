package a.baozouptu.ptu.repealRedo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import a.baozouptu.base.dataAndLogic.AllDate;
import a.baozouptu.base.util.GeoUtil;
import a.baozouptu.base.util.Util;

/**
 * Created by Administrator on 2016/7/28.
 */
public class RepealRedoManager {

    public static void addStep() {

    }

    public static Bitmap addBm2Bm(Bitmap baseBitmap, Bitmap addBitmap, RectF boundRect, float rotateAngle) {
        Canvas c = new Canvas(baseBitmap);
        addBm2Canvas(c, addBitmap, boundRect, rotateAngle);
        return baseBitmap;
    }

    public static Canvas addBm2Canvas(Canvas baseCanvas, Bitmap addBitmap, RectF boundRect, float rotateAngle) {
        int width = (int) (boundRect.right - boundRect.left);
        int height = (int) (boundRect.bottom - boundRect.top);
        Bitmap realBm = null;
        if (addBitmap.getWidth() != width) {
            realBm = Bitmap.createScaledBitmap(addBitmap, width, height, true);
            addBitmap.recycle();
        } else {
            realBm = addBitmap;
        }

        float centerX = (boundRect.left + boundRect.right) / 2, centerY = (boundRect.bottom + boundRect.top) / 2;
        //将realBm到图上
        BitmapDrawable addDrawable = new BitmapDrawable(Util.MyApplication.getAppContext().getResources(), realBm);
        addDrawable.setDither(true);
        addDrawable.setAntiAlias(true);
        addDrawable.setFilterBitmap(true);
        baseCanvas.rotate(rotateAngle, centerX, centerY);//旋转
        addDrawable.setBounds(GeoUtil.rectF2Rect(boundRect));
        addDrawable.draw(baseCanvas);
        baseCanvas.save();
        baseCanvas.restore();

        addBitmap.recycle();
        realBm.recycle();
        return baseCanvas;
    }
}
