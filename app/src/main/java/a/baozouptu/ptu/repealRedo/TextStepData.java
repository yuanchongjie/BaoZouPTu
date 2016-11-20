package a.baozouptu.ptu.repealRedo;

import android.graphics.Paint;
import android.graphics.Path;
import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/27.
 * 内部用list存下了多个的stepDate；
 */
public class TextStepData extends StepData {
    ArrayList<Pair<Path,Paint>> pathPaintList=new ArrayList<>();
    public void addRubberDate(ArrayList<Pair<Path,Paint>> pathPaintList){
        this.pathPaintList.addAll(pathPaintList);
    }
    public ArrayList<Pair<Path,Paint>> getRubberData(){
        return pathPaintList;
    }
    public TextStepData(int mode) {
        super(mode);
    }


}
