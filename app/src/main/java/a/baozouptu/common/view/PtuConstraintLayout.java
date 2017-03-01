package a.baozouptu.common.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import a.baozouptu.common.util.Util;


/**
 * Created by LiuGuicen on 2017/2/18 0018.
 */

public class PtuConstraintLayout extends ConstraintLayout {

    public PtuConstraintLayout(Context context) {
        super(context);
    }

    public PtuConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PtuConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View recyclerView = findViewWithTag("tietuRecyclerView");
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN && recyclerView != null) {
            View tietuExpression = findViewWithTag("tietuExpression");
            View tietuProperty = findViewWithTag("tietuProperty");
            int[] xy = new int[2];
            getLocationOnScreen(xy);
            xy[0] += event.getX();
            xy[1] += event.getY();
            if (!Util.pointInView(xy[0], xy[1], recyclerView)
                    && !Util.pointInView(xy[0], xy[1], tietuExpression)
                    && !Util.pointInView(xy[0], xy[1], tietuProperty)
                    ) {
                ((RecyclerView) recyclerView).setAdapter(null);
                removeView(recyclerView);
            }
        }
        return super.dispatchTouchEvent(event);
    }


    public void addTietuListView(RecyclerView tietuRecyclerView, View fragmentView) {
        tietuRecyclerView.setTag("tietuRecyclerView");
        addView(tietuRecyclerView, getTietuListLayoutParams(fragmentView));
    }

    private LayoutParams getTietuListLayoutParams(View fragmentView) {
        LayoutParams layoutParams = new LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, fragmentView.getHeight() + 30);
        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToTop = ((View) fragmentView.getParent()).getId();
        layoutParams.setMargins(0, 0, 0, 5 * 3);
        return layoutParams;
    }


}
