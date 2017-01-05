package a.baozouptu.ptu.tietu.pictureSynthesis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;

import org.apache.http.impl.client.TunnelRefusedException;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import a.baozouptu.common.util.Util;

/**
 * Created by LiuGuicen on 2016/12/31 0031.
 */

public class ChangeBmTest {

    private static boolean success=false;
    static  {
        if(success==false) {
            success = OpenCVLoader.initDebug();
            if (success == true) {
                Log.e(" ", "加载OpenCv成功");
            } else {

                Log.e(" ", "加载OpenCv失败");
            }
        }
    }


    public Bitmap changetBm(Bitmap under,Bitmap above,Rect inteRect){
        if(!success){
            Util.P.le("加载OpenCv失败，不能融合表情");
        }
        under=Bitmap.createBitmap(under,inteRect.left,inteRect.top,inteRect.width(),inteRect.height());
        above= Bitmap.createScaledBitmap(above,inteRect.width(),inteRect.height(),true);
        Log.e("创建缩放图完成"," ");
        under=degeEclosion(under,above);


        under=colorAnalogylize(under,above);
        return under;
    }

    /**
     * 使颜色相似化
     * 内部矩形不不能超出范围
     */
    private Bitmap colorAnalogylize(Bitmap under, Bitmap above) {
        Mat matU=new Mat();
        Utils.bitmapToMat(under,matU);
        Mat matA=new Mat();
        Utils.bitmapToMat(above,matA);
        Log.e(" "," 图片转换完成一半");
       // Core.divide(2,matU,matU);
       // Core.divide(2,matA,matA);
        Core.add(matA,matU,matU);
        Utils.matToBitmap(matU,under, true);
        matA.release();
        matU.release();
        return under;
    }

    /**
     * 边缘羽化处理
     */
    private Bitmap degeEclosion(Bitmap under,Bitmap above) {

        return under;
    }

}
