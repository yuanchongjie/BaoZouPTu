package a.baozouptu.ptu;

import android.graphics.Rect;

import a.baozouptu.base.util.MathUtil;

/**
 * p图过程中用到的一些工具
 * Created by liuguicen on 2016/8/5.
 *
 * @description
 */
public class PtuUtil {
    /**
     * 精确计算view中的点在原图片中的位置
     *
     * @param px      在FrameLayout中的相对位置
     * @param py      在FrameLayout中的相对位置
     * @param srcRect 画图片时，从原图片中扣下来的图所在的矩形
     * @param dstRect 画到PtuFrame的frameLayout中的矩形的位置
     * @return 在原图中的位置
     */
    public static float[] getLocationAtPicture(float px, float py, Rect srcRect, Rect dstRect) {
        px -= dstRect.left;
        py -= dstRect.top;
        float x, y;

        //采用精确计算
        String srcWidth = MathUtil.subtract(Float.toString(srcRect.right), Float.toString(srcRect.left));
        String dstWidth = MathUtil.subtract(Float.toString(dstRect.right), Float.toString(dstRect.left));
        String ratio = MathUtil.divide(srcWidth, dstWidth);
        String px1 = MathUtil.multiply(Float.toString(px), ratio);
        String py1 = MathUtil.multiply(Float.toString(py), ratio);
        x = Float.valueOf(MathUtil.add(px1, Float.toString(srcRect.left)));
        y = Float.valueOf(MathUtil.add(py1, Float.toString(srcRect.top)));

        return new float[]{x, y};
    }

}
