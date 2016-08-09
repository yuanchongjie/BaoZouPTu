package a.baozouptu.base.util;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 几何操作的工具
 * Created by Administrator on 2016/6/1.
 */
public class GeoUtil {
    public static float getDis(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1, dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 利用matrix进行缩放
     *
     * @param scalingX      被缩放的x
     * @param scalingY      被缩放的y
     * @param scaleCenterX 缩放中心x
     * @param scaleCenterY 缩放中心y
     * @param scale        缩放倍数
     * @return 缩放后的x坐标
     */
    public static float getScaledX(float scalingX, float scalingY, float scaleCenterX, float scaleCenterY, float scale) {
        Matrix matrix = new Matrix();
        // 将Matrix移到到当前圆所在的位置，
        // 然后再以某个点为中心进行缩放
        matrix.preTranslate(scalingX, scalingY);
        matrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MTRANS_X];
    }

    /**
     * 利用matrix进行缩放
     *
     * @param scalingX      被缩放的x
     * @param scalingY      被缩放的y
     * @param scaleCenterX 缩放中心x
     * @param scaleCenterY 缩放中心y
     * @param scale        缩放倍数
     * @return 缩放后的Y坐标
     */
    public static float getScaledY(float scalingX, float scalingY, float scaleCenterX, float scaleCenterY, float scale) {
        Matrix matrix = new Matrix();
        // 将Matrix移到到当前圆所在的位置，
        // 然后再以某个点为中心进行缩放
        matrix.preTranslate(scalingX, scalingY);
        matrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MTRANS_Y];
    }

    public static Rect rectF2Rect(RectF rf) {
        return new Rect((int) rf.left, (int) rf.top, (int) rf.right, (int) rf.bottom);
    }

    /**
     * 非水平的矩形,可以为斜的的那种矩形，注意构造时必须按顺时针或逆时针放入四个点
     * Created by Administrator on 2016/5/31.
     */
    public static class UnLevelRect {
        float x1 = 0, y1 = 0, x2 = 0, y2 = 0, x3 = 0, y3 = 0, x4 = 0, y4 = 0;

        /**
         * 注意构造时必须按顺时针或逆时针放入四个点
         */
        public UnLevelRect(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
            this.x1 = x1;
            this.y1 = y1;

            this.y2 = y2;
            this.x2 = x2;

            this.x3 = x3;
            this.y3 = y3;

            this.x4 = x4;
            this.y4 = y4;
        }

        /**
         * 水平的矩形，使用左上角和右下角设置,注意构造时必须按顺时针或逆时针放入四个点
         */
        public UnLevelRect(float x1, float y1, float x3, float y3) {
            this.x1 = x1;
            this.y1 = y1;
            this.x3 = x3;
            this.y3 = y3;
            x2 = x3;
            y2 = y1;
            x4 = x1;
            y4 = y3;
        }

        public UnLevelRect() {

        }

        /**
         * 水平的矩形，使用左上角和右下角设置,注意构造时必须按顺时针或逆时针放入四个点
         */
        public UnLevelRect(PointF p1, PointF p2, PointF p3, PointF p4) {
            new UnLevelRect(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);
        }

        public UnLevelRect(UnLevelRect t) {
            x1=t.x1;y1=t.y1;
            x2=t.x2;y2=t.y2;
            x3=t.x3;y3=t.y3;
            x4=t.x4;y4=t.y4;
        }

        public void set(PointF p1, PointF p2, PointF p3, PointF p4) {
            set(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);
        }

        public float getLeft(){
            return Math.min(Math.min(x1,x2),Math.min(x3,x4));
        }
        public float getRight(){
            return Math.max(Math.max(x1,x2),Math.max(x3,x4));
        }
        public float getTop(){
            return Math.min(Math.min(y1,y2),Math.min(y3,y4));
        }
        public float getButtom(){
            return Math.max(Math.max(y1,y2),Math.max(y3,y4));
        }
        public void set(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
            this.x1 = x1;
            this.y2 = y2;
            this.x2 = x2;
            this.y1 = y1;
            this.x3 = x3;
            this.y4 = y4;
            this.x4 = x4;
            this.y3 = y3;
        }

        public boolean contain(float x, float y) {
            if (isLeft(x1, y1, x2, y2, x, y) * isLeft(x1, y1, x2, y2, x3, y3) >= 0
                    && isLeft(x2, y2, x3, y3, x, y) * isLeft(x2, y2, x3, y3, x4, y4) >= 0
                    && isLeft(x3, y3, x4, y4, x, y) * isLeft(x3, y3, x4, y4, x1, y1) >= 0
                    && isLeft(x4, y4, x1, y1, x, y) * isLeft(x4, y4, x1, y1, x2, y2) >= 0)
                return true;
            return false;
        }

        private float isLeft(float x1, float y1, float x2, float y2, float x3, float y3) {
            float x_1 = x2 - x1, y_1 = y2 - y1, x_2 = x3 - x1, y_2 = y3 - y1;
            return x_1 * y_2 - x_2 * y_1;
        }

        public void translate(float dx, float dy) {
            x1 += dx;
            y1 += dy;
            x2 += dx;
            y2 += dy;
            x3 += dx;
            y3 += dy;
            x4 += dx;
            y4 += dy;
        }

        /**
         * 平移系统的平行的矩形
         *
         * @param rect
         * @param dx
         * @param dy
         */
        public void translateFormRect(RectF rect, float dx, float dy) {
            rect.left += dx;
            rect.top += dy;
            rect.right += dx;
            rect.bottom += dy;
        }

    }

    /**
     * 坐标旋转公式
     * x0= (x - rx0)*cos(a) - (y - ry0)*sin(a) + rx0 ;
     * y0= (x - rx0)*sin(a) + (y - ry0)*cos(a) + ry0 ;
     *
     * @param p0 旋转中心点
     * @param a  顺时针为正
     */
    public static PointF getCooderAfterRotate(PointF p0, PointF p1, float a) {
        a = (float) (a * Math.PI / 180);
        PointF p2 = new PointF();
        p2.x = (float) ((p1.x - p0.x) * Math.cos(a) - (p1.y - p0.y) * Math.sin(a) + p0.x);
        p2.y = (float) ((p1.x - p0.x) * Math.sin(a) + (p1.y - p0.y) * Math.cos(a) + p0.y);
        return p2;
    }

}
