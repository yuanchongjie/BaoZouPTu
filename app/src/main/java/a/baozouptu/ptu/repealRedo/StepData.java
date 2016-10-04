package a.baozouptu.ptu.repealRedo;

import android.graphics.RectF;

/**
 * Created by Administrator on 2016/7/8.
 */
public class StepData {
    /**
     * 来自PtuActivity中的几个常量
     */
    public int EDIT_MODE;

    /**
     * rect代表view有效区域在底图上的位置的rect，相对于原始图片的左上角上下左右边的距离
     */
    public RectF boundRectInPic = new RectF();
    public float rotateAngle;
    public String picPath;
    public StepData() {
    }

    public StepData(int editMode) {
        this.EDIT_MODE = editMode;
    }
}
