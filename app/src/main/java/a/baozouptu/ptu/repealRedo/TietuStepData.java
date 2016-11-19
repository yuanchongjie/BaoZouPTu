package a.baozouptu.ptu.repealRedo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 2016/7/27.
 */
public class TietuStepData extends  StepData implements Iterable {
    private ArrayList<StepData> tietuList = new ArrayList<>();

    public TietuStepData(int mode) {
        super(mode);
    }

    public void addOneTietu(StepData sd) {
        tietuList.add(sd);
    }

    @Override
    public Iterator<StepData> iterator() {
        return tietuList.iterator();
    }
}
