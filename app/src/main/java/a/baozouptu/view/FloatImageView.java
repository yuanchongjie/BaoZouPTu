package a.baozouptu.view;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016/6/30.
 */
public class FloatImageView implements FloatView {
    @Override
    public boolean prepareResultBitmap(float initRatio, RectF innerRect, RectF picRect) {
        return false;
    }

    @Override
    public void scale(float ratio) {

    }

    @Override
    public void adjustSize(float ratio) {

    }

    @Override
    public void adjustEdegeBound() {

    }

    @Override
    public void drag(float nx, float ny) {

    }

    @Override
    public void drawItem(Canvas canvas, Item item) {

    }

    @Override
    public void setDownState() {

    }

    @Override
    public int getDownState() {
        return 0;
    }

    @Override
    public void initItems() {

    }

    @Override
    public void onClickBottomCenter() {

    }
}
