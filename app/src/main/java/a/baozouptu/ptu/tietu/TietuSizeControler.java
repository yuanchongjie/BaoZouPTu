package a.baozouptu.ptu.tietu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import a.baozouptu.base.dataAndLogic.AllData;

/**
 * Created by liuguicen on 2016/9/29.
 *
 * @description 贴图的FloatImageView的Bitmap操作类，
 * <p>（1）
 * <p>从sd卡中取出图片，能控制图片的最大尺寸，缩放过程中不超过这个尺寸；
 * <p>用参数表示出他们
 * <p>将图片画到原图上时，可以使用原始大小。
 * <p/>
 * <p>（2）
 * <p>图片所在的位置，因为要旋转，所以以它的中心点为准。初始化时放到底图的中心
 * <p>图片所在的范围，非水平的Rect，用于判断点击发生的位置
 * <p/>
 */
public class TietuSizeControler {
    /**
     * 获取合适大小的Bitmap，Bitmap占用的内存不能超过剩余内存，如果超过，则返回空
     * @return 如果Bitmap用的内存超过剩余的值，会返回空
     */
    public static Bitmap getSrcBitmap(Activity activity, String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);

        int totalWidth = AllData.screenWidth;
        int totalHeight = metric.heightPixels;

        int srcWidth = Math.min(options.outWidth,totalWidth);
        int srcHeight = Math.min(options.outHeight,totalHeight);

        options.inJustDecodeBounds = false;
        options.inDither = true;
        options.inPreferQualityOverSpeed = true;
        options.inSampleSize =  Math.min(options.outWidth / srcWidth, options.outHeight  / srcHeight);
        long totalSize=options.outWidth/options.inSampleSize*(options.outHeight/options.inSampleSize)*4;
        long realFreeMemory=Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory();//使用freeMemaory不对
        if(totalSize>realFreeMemory){
            return null;//内存超出了剩余内存
        }
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 获取tietu的FloatImageView的布局参数，大小为min(合适的值,图片的大小）；
     * 位置为图片范围内的一个随机值
     * @param srcWidth 原图的大小
     * @param srcHeight 原图的大小
     * @param picBound 显示的范围
     * @return 布局参数
     */
    public static  FrameLayout.LayoutParams getFeatParmas(int srcWidth,int srcHeight,Rect picBound) {
        //宽和高

        int exceptWidth=picBound.width()/2;//1/2图片宽
        if(exceptWidth>AllData.screenWidth/3)
            exceptWidth=AllData.screenWidth/3;//不能大于屏幕的1/3宽
        else if(exceptWidth<AllData.screenWidth/6)//太小
            exceptWidth=picBound.height()*3/4;

        int exceptHeight=picBound.height()/2;//1/2图片高
        if(exceptHeight>AllData.screenHeight/5)
            exceptHeight=AllData.screenHeight/5;//不能大于屏幕的1/6高
        else if(exceptHeight<AllData.screenHeight/10)//太小
            exceptHeight=picBound.height()*3/4;

        float ratio=Math.min(srcHeight*1f/exceptHeight,srcWidth*1f/exceptWidth);//保持长宽比，取小的一个
        exceptWidth=(int)(srcWidth*ratio);
        exceptHeight=(int)(srcHeight*ratio);
        FrameLayout.LayoutParams parmas=new FrameLayout.LayoutParams(exceptWidth,exceptHeight);

        //位置，随机数，需要图片范围内
        int mleft=(int)(Math.random()*(picBound.width()-exceptWidth));
        int mtop=(int)(Math.random()*(picBound.height()-exceptHeight));
        parmas.setMargins(picBound.left+mleft,picBound.top+mtop,
                0,0);

        return parmas;
    }

}
