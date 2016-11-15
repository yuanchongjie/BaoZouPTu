package a.baozouptu.ptu.view;

/**
 * Created by Administrator on 2016/7/28.
 *
 * @description 支持平移(Translate)、缩放(Scale)、旋转(Rotate）的View
 */
public interface TSRView {
    void scale(float centerX, float centerY, float ratio);

    void move(float dx, float dy);

    void rotate(float angle);

    /**
     * 如果缩放，要在adjustSize后面调用
     * 在不超过边界情况下设置好位置
     * @param dx 移动距离
     * @param dy 移动距离
     */
    void adjustEdge(float dx, float dy);

    void adjustSize(float ratio);
}
