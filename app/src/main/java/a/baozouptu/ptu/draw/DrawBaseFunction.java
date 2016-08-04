package a.baozouptu.ptu.draw;

import android.graphics.Bitmap;
import android.graphics.Path;

import a.baozouptu.ptu.BaseFunction;
import android.graphics.Region;
/**
 * ptu操作的基本功能，相应的Fragment实现
 * Created by Administrator on 2016/7/27.
 */
public interface DrawBaseFunction extends BaseFunction {


    /**
     * 画图时设置底图，默认是白色的低
     */
    void setButtonBm(Bitmap buttonBm);



}
