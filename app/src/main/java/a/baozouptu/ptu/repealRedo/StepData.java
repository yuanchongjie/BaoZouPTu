package a.baozouptu.ptu.repealRedo;

import android.graphics.RectF;

import a.baozouptu.ptu.view.FloatTextView;

/**
 * Created by Administrator on 2016/7/8.
 */
public class StepData  {
    public int EDIT_MODE;
    public FloatTextView floatTextView;
    public String path;
    public int locationX;
    public int locationY;
    /**
     * rect代表view有效区域在底图上的位置的rect，相对于原始图片的左上角上下左右边的距离
     */
    public RectF boundRectInPic;
    public float angle;
    public RectF innerRect;

    public StepData(){
        
    }
    public StepData(int editMode){
        this.EDIT_MODE=editMode;
    }

    public void setFloatTextView(FloatTextView floatTextView) {
        this.floatTextView = floatTextView;
    }
}
