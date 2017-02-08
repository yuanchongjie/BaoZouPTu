package a.baozouptu.common.dataAndLogic;

import android.content.Context;
import android.os.Environment;

import a.baozouptu.common.appInfo.AppConfig;
import a.baozouptu.common.appInfo.MyApplication;
import a.baozouptu.user.userSetting.SettingDataSource;

/**
 * 保存应用所需的一些常用通用的数据项
 *
 * @author acm_lgc
 * @version android 6.0,  sdk 23,jdk 1.8,2015.10
 */
public class AllData {
    /**
     * 屏幕的宽度
     */
    public static int screenWidth,screenHeight;
    /**
     * 常用的图片的格式
     */
    public final static String[] normalPictureFormat = new String[]{"png", "gif", "bmp", "jpg", "jpeg", "tiff", "jpeg2000", "psd", "icon"};
    public static float thumbnailSize = 10000.0f;
    /**
     * 图片内存最小值,单位byte
     */
    public final static int PIC_FILE_SIZE_MIN = 1*5000;
    /**
     * 图片内存最大值,单位byte
     */
    public final static int PIC_FILE_SIZE_MAX = 100000*1000;

    public static AsyncImageLoader3 imageLoader3 = AsyncImageLoader3.getInstance();
    public static final String TAG="LOL";
    public static final String appFilePathDefault = Environment.getExternalStorageDirectory().getAbsolutePath() + "/暴走P图/";
    public static final String zitiDir=appFilePathDefault+"字体/";
    public static final String picDir=appFilePathDefault+"暴走P图-制作表情/";
    public static final Context appContext= MyApplication.appContext;

    //一些基本配置
    public static AppConfig appConfig;
    public static HasReadConfig hasReadConfig;
    public static SettingDataSource settingDataSource;
}
