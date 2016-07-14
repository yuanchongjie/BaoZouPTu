package a.baozouptu.dataAndLogic;

import android.graphics.RectF;
import android.os.Bundle;

import a.baozouptu.view.FloatTextView;

/**
 * Created by Administrator on 2016/7/8.
 */
public class StepData  {
    public int EDIT_MODE;
    public FloatTextView floatTextView;
    public String path;
    public int locationX;
    public int locationY;
    public RectF boundRectInPic;
    public float angle;
    public RectF innerRect;

    public StepData(){
        
    }
    public StepData(int editMode){
        this.EDIT_MODE=editMode;
    }

    public void setFloatTextView(a.baozouptu.view.FloatTextView floatTextView) {
        this.floatTextView = floatTextView;
    }
}
