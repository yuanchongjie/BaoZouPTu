package a.baozouptu.ptu;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import a.baozouptu.ptu.repealRedo.StepData;

/**
 * ptu操作的基本功能，相应的Fragment实现
 * Created by Administrator on 2016/7/27.
 */
public interface BaseFunction {
    /**
     * 子功能撤销
     */
    void smallRepeal();


    /**
     * 子功能重做
     */
    void smallRedo();

    /**
     * 获取操作之后最终的bitmap
     *
     * @param ratio 缩放的比例
     * @return
     */
    Bitmap getResultBm(float ratio);

    /**
     * 提供撤销重做所需的数据
     *
     * @param ratio
     * @return stepDate的子类
     */
    StepData getResultData(float ratio);

    /**
     * 做一大步功能
     */
    void addBigStep(StepData sd);

    void releaseResource();
}
