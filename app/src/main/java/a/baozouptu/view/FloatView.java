package a.baozouptu.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/5/31.
 */
public interface FloatView {
    /**
     * 透明
     */
    int STATUS_TOUMING = 1;
    /**
     * 只显示边框
     */
    int STATUS_RIM = 2;
    /**
     * 显示子项目
     */
    int STATUS_ITEM = 3;
    /**
     * 显示输入法，输入状态
     */
    int STATUS_INPUT = 4;

    /**
     * 表示item信息的类
     */
    class Item {
        float x;
        float y;
        String name;
        Bitmap bitmap;

        Item(float x, float y, String name) {
            this.x = x;
            this.y = y;
            this.name = name;
        }
    }

    /**
     * 获取中的宽度
     *
     * @return
     */
    float getmWidth();

    /**
     * 获取总的高度
     *
     * @return
     */
    float getmHeight();

    /**
     * 获取顶部的位置
     *
     * @return
     */
    float getfTop();

    /**
     * 获取左边的位置
     *
     * @return
     */
    float getfLeft();

    /**
     * 获取x的相对位移
     *
     * @return
     */
    float getRelativeX();

    /**
     * 获取y的相对位移
     *
     * @return
     */
    float getRelativeY();

    /**
     * 设置y的相对位移
     *
     * @return
     */
    void setRelativeY(float relativeY);

    /**
     * 设置y的相对位移
     *
     * @return
     */
    void setRelativeX(float relativeX);

    /**
     * 获取视图的显示状态：边框，item等
     *
     * @return
     */
    int getShowState();

    /**
     * 改变当前的显示状态，显示变换操作逻辑处理中心，有些复杂，要注意
     *
     * @param state <p>STATUS_TOUMING = 1;//透明
     *              <p>int STATUS_RIM = 2;边框
     *              <p>STATUS_ITEM = 3;//子项目
     *              <p>STATUS_INPUT = 4;//输入法
     */
    void changeShowState(int state);


    /**
     * <p>原始图片被缩放了，现在把floatview反着缩放回去，使底图回到原始大小时floatview的相对大小不变
     * <p> 获取子功能生成点的bitmap，以及bitmap的大小，位置的相关参数，
     * <p> 传入initRatio：ptuView上的图片一开始缩放的比例，
     * <p>方法内部再用RealRatio=1/initRation,算出实际的缩放比例,
     * <p>然后得出子功能获得参数：
     * <p>相对left：rleft=（FloatView的letf-ptuView的图片的left）*realRatio,rtop一样
     * <p>FloatView的宽mwidth*=realRatio获取的实际的宽，高一样
     * <p>此方法会改变floatTextView的大小，textSize，显示状态，
     * <p/>
     * <p>另外，获取的图片view可能会过大，造成内存溢出，通过innerRect表示真实尺寸
     * <p>outRect表示需要的尺寸，最后绘制时缩放，减小内存溢出的可能
     *
     * @param innerRect 显示在底图上的view的内部有效区域,相对于缩放到最终倍数的floatView左上角的距离
     * @param picRect   rect代表view有效区域在底图上的位置的rect，相对于原始图片的左上角上下左右边的距离
     *                  <p>outRect大小和innerRect大小相同的</p>
     * @return 是否能成功获取图片
     */
    boolean prepareResultBitmap(float initRatio, RectF innerRect, RectF picRect);

    /**
     * 缩放视图，重置视图的宽和高，然后重绘
     *
     * @param ratio
     */
    void scale(float ratio);

    void adjustSize(float ratio);

    /**
     * 适配floatview的位置,不能超出图片的边界,不算padding的内部就不能超出边界
     * 超出之后移动startx，starty,不影响其它数据
     */
    void adjustEdegeBound();

    /**
     * 拖动floatview，利用相对点，相对点变化，
     * view根据原来他与相对点的坐标差移动，避免了拖动
     * 处理的误差
     *
     * @param nx 新的对应点的x坐标
     * @param ny 新的对应点的y坐标
     */
    void drag(float nx, float ny);

    void drawItem(Canvas canvas, Item item);

    void setDownState();

    int getDownState();

    void initItems();

    void onClickBottomCenter();
}
