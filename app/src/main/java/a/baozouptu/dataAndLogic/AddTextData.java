package a.baozouptu.dataAndLogic;

import android.graphics.RectF;
import android.view.View;

/**
 * 主功能界面，存放二级功能下操作后获得的数据
 * 一个view，显示在底图上的view的内部有效区域,rect代表view有效区域在底图上的位置的rect，
 */
public class AddTextData {
    private View view;
    private RectF innerRect;
    private RectF outRect;

    public AddTextData(View view, RectF innerRect, RectF outRect) {
        this.view = view;
        this.innerRect = innerRect;
        this.outRect = outRect;
    }

    public RectF getInnerRect() {
        return innerRect;
    }

    public RectF getOutRect() {
        return outRect;
    }

    public View getView() {
        return view;
    }
}
