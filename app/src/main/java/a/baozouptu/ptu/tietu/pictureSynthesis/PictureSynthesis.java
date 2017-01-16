package a.baozouptu.ptu.tietu.pictureSynthesis;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;


/**
 * Created by LiuGuicen on 2016/12/31 0031.
 */

public class PictureSynthesis {
    public static final String TAG = "PictureSynthesis";
    public static boolean success = false;

    static {
        if (!success) {
            System.loadLibrary("imageSynthesis");
            Log.e(TAG, "static initializer: 成功");
            success = true;
        }
    }

    /**
     * @param under    显示出的大小
     * @param above    实际的大小
     * @param inteRect 相交的矩形
     * @return 底部的结果图
     */
    public Bitmap changeBm(Bitmap under, Bitmap above, Rect inteRect) {
        above = Bitmap.createScaledBitmap(above, inteRect.width(), inteRect.height(), true);
        Log.e("创建缩放图完成", " ");
        int[] intersetion_rect = new int[]{inteRect.left, inteRect.top, inteRect.right, inteRect.bottom};
        Log.e(TAG, "under原来的宽：" + under.getWidth());
        Log.e(TAG, "under原来的高：" + under.getHeight());
        Log.e(TAG, "above原来的宽：" + above.getWidth());
        Log.e(TAG, "above原来的高：" + above.getHeight());

        int[] rePixes = synthesisBm(under, above, intersetion_rect);

        //只转换上面
        if (rePixes == null) {
            Log.e(TAG, "changeBm: 图像融合失败");
            return null;
        }

        Log.e(TAG, "转换bitmap成功");
        above.setPixels(rePixes, 0, above.getWidth(), 0, 0, above.getWidth(),above.getHeight());
        return above;
        /*if (rePixs.length != under.getWidth() * under.getHeight()) {
            Log.e(TAG, "changeBm: 获取数据出错");
            return under;
        }
        Log.e(TAG, "转换bitmap成功");
        under.setPixels(rePixs, 0, under.getWidth(), 0, 0, under.getWidth(), under.getHeight());
        return under;*/
    }

    /**
     * @param under             底部的bm
     * @param above             顶部的bm
     * @param intersection_rect 必须是in_rect依次是 左上右下
     * @return bm的pixes int数组
     */
    private native int[] synthesisBm(Object under, Object above, int[] intersection_rect);

}
//javah -d ../jni  -encoding UTF-8  a.baozouptu.ptu.tietu.pictureSynthesis.PictureSynthesis
