package a.baozouptu.tools;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 几何操作的工具
 * Created by Administrator on 2016/6/1.
 */
public class GeoUtil {
    public static float getDis(float x1,float y1,float x2,float y2){
        float dx=x2-x1,dy=y2-y1;
        return (float)Math.sqrt(dx*dx+dy*dy);
    }
    /**
     * 利用matrix进行缩放
     *
     * @param tstartX 被缩放的x
     * @param tstartY 被缩放的y
     * @param scaleCenterX 缩放中心x
     * @param scaleCenterY 缩放中心y
     * @param scale 缩放倍数
     * @return 缩放后的x坐标
     */
    public static float getScaledX(float tstartX, float tstartY, float scaleCenterX, float scaleCenterY, float scale) {
        Matrix matrix = new Matrix();
        // 将Matrix移到到当前圆所在的位置，
        // 然后再以某个点为中心进行缩放
        matrix.preTranslate(tstartX, tstartY);
        matrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
       return values[Matrix.MTRANS_X];
    }
    /**
     * 利用matrix进行缩放
     *
     * @param tstartX 被缩放的x
     * @param tstartY 被缩放的y
     * @param scaleCenterX 缩放中心x
     * @param scaleCenterY 缩放中心y
     * @param scale 缩放倍数
     * @return 缩放后的Y坐标
     */
    public static float getScaledY(float tstartX, float tstartY, float scaleCenterX, float scaleCenterY, float scale) {
        Matrix matrix = new Matrix();
        // 将Matrix移到到当前圆所在的位置，
        // 然后再以某个点为中心进行缩放
        matrix.preTranslate(tstartX, tstartY);
        matrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MTRANS_Y];
    }
    public static Rect rectF2Rect(RectF rf){
        return new Rect((int)rf.left,(int)rf.top,(int)rf.right,(int)rf.bottom);
    }

}
