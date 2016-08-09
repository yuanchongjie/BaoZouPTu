package a.baozouptu.ptu.repealRedo;

import a.baozouptu.ptu.text.FloatTextView;

/**
 * Created by Administrator on 2016/7/27.
 */
public class TextStepData extends StepData {
    public FloatTextView floatTextView;
    public TextStepData(int mode){
        super(mode);
    }
    public void setFloatTextView(FloatTextView floatTextView) {
        this.floatTextView = floatTextView;
    }
}
