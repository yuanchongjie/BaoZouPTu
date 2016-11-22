package a.baozouptu.ptu.repealRedo;

import android.graphics.Paint;
import android.graphics.Path;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import a.baozouptu.ptu.draw.DrawView;

/**
 * Created by Administrator on 2016/7/30.
 */
public class DrawStepData extends StepData{
    public DrawStepData(int editMode) {
        super(editMode);
    }
    List<DrawView.DrawPath> savePath = new ArrayList<>();

    public List<DrawView.DrawPath> getSavePath() {
        return savePath;
    }

    public void setSavePath(List<DrawView.DrawPath> savePath) {
        this.savePath=savePath;
    }
}
