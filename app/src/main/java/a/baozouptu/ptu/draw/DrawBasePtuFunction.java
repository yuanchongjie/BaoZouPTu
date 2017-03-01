package a.baozouptu.ptu.draw;

import android.graphics.Bitmap;

import a.baozouptu.ptu.BasePtuFragment;
import a.baozouptu.ptu.BasePtuFunction;

/**
 * ptu操作的基本功能，相应的Fragment实现
 * Created by Administrator on 2016/7/27.
 */
public abstract class DrawBasePtuFunction extends BasePtuFragment {

    /**
     * 画图时设置底图，默认是白色的低
     */
    abstract void setButtonBm(Bitmap buttonBm);

}
