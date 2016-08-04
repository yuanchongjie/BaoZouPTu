package a.baozouptu.base.dataAndLogic;

import android.content.Context;
import android.os.Build;

import a.baozouptu.R;
import a.baozouptu.base.util.Util;

/**
 * 保存应用所需的一些常用通用的数据项
 *
 * @author acm_lgc
 * @version android 6.0,  sdk 23,jdk 1.8,2015.10
 */
public class AllDate {
    /**
     * 屏幕的宽度
     */
    public static int screenWidth;
    /**
     * 常用的图片的格式
     */
    public final static String[] normalPictureFormat = new String[]{"png", "gif", "bmp", "jpg", "jpeg", "tiff", "jpeg2000", "psd", "icon"};
    public static float thumbnailSize = 10000.0f;
    /**
     * 图片内存最小值 5K
     */
    public final static int PIC_FILE_SIZE_MIN = 1;
    /**
     * 图片内存最大值 80000K=80M
     */
    public final static int PIC_FILE_SIZE_MAX = 100000;

    public static int CURRENT_EDIT_MODE = 0;
    public static final int EDIT_NO = 0;
    public static final int EDIT_CUT = 1;
    /**
     * 包含三个元素
     * sd.floatTextView = floatTextView;
     * sd.innerRect = innerRect;
     * sd.boundRectInPic = boundRectInPic;
     */
    public static final int EDIT_TEXT = 2;
    public static final int EDIT_TIETU = 3;
    public static final int EDIT_DRAW = 4;
    public static final int EDTI_MAT = 5;

    public static int text_defualt_color;
    public static int text_choosed_color;
    private static Context globleContext = Util.MyApplication.getAppContext();
    public static long lastScanTime = 0;
    public static AsyncImageLoader3 imageLoader3 = AsyncImageLoader3.getInstatnce();

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text_defualt_color = globleContext.getResources().getColor(R.color.text_default_color, null);
            text_choosed_color = globleContext.getResources().getColor(R.color.text_chose_color, null);
        } else {
            text_defualt_color = globleContext.getResources().getColor(R.color.text_default_color);
            text_choosed_color = globleContext.getResources().getColor(R.color.text_chose_color);
        }
    }

}
