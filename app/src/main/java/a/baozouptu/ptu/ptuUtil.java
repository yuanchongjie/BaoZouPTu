package a.baozouptu.ptu;

import android.graphics.Rect;

import a.baozouptu.base.util.MU;

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
     * @return 在原图中的位置,字符串的形式
     */
    public static String[] getLocationAtPicture(float px, float py, Rect srcRect, Rect dstRect) {
        return getLocationAtPicture(Float.toString(px), Float.toString(py), srcRect, dstRect);
    }
    /**
     * 精确计算view中的点在原图片中的位置
     *
     * @param px      在FrameLayout中的相对位置
     * @param py      在FrameLayout中的相对位置
     * @param srcRect 画图片时，从原图片中扣下来的图所在的矩形
     * @param dstRect 画到PtuFrame的frameLayout中的矩形的位置
     * @return 在原图中的位置,字符串的形式
     */
    public static String[] getLocationAtPicture(String px, String py, Rect srcRect, Rect dstRect) {
        px = MU.su(px, Float.toString(dstRect.left));
        py = MU.su(py, Float.toString(dstRect.top));
        String x, y;

        //采用精确计算
        String srcWidth = MU.su(Float.toString(srcRect.right), Float.toString(srcRect.left));
        String dstWidth = MU.su(Float.toString(dstRect.right), Float.toString(dstRect.left));
        String ratio = MU.di(srcWidth, dstWidth);
        String px1 = MU.mu(px, ratio);
        String py1 = MU.mu(py, ratio);
        x = MU.add(px1, Float.toString(srcRect.left));
        y = MU.add(py1, Float.toString(srcRect.top));
        return new String[]{x, y};
    }

}
