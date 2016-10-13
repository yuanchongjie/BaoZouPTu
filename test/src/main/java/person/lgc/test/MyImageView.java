package person.lgc.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/10/4 0004.
 */
public class MyImageView extends ImageView {

    private final Paint p;

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(new RectF(0,0,canvas.getWidth(),canvas.getHeight()),p);
    }
}
