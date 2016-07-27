package a.baozouptu.ptu.draw;

import android.graphics.Bitmap;

/**
 * ptu操作的基本功能，相应的Fragment实现
 * Created by Administrator on 2016/7/27.
 */
public interface BaseFunction {
    /**
     * 撤销
     */

    void repeal();

    /**
     * 重做
     */

    void redo();

    /**
     * 画图时设置底图，默认是白色的低
     */
    void setButtonBm(Bitmap buttonBm);

    /**
     * 获取操作之后最终的bitmap
     *
     * @param ratio 缩放的比例
     * @return
     */
    Bitmap getResultBm(float ratio);

}
