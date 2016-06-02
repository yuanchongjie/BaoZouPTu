package a.baozouptu.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import a.baozouptu.R;

/**
 * Created by Administrator on 2016/6/1.
 */
public class FloatItemEdit {

    private final Bitmap bitmap;

    @TargetApi(Build.VERSION_CODES.M)
    FloatItemEdit(Context context, int width){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        bitmap = Bitmap.createBitmap(width,width, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            paint.setColor(context.getResources().getColor(R.color.float_child_item,null));
        }else{
            paint.setColor(context.getResources().getColor(R.color.float_child_item));
        }
        canvas.drawOval(new RectF(0,0,(float)width,(float)width),paint);
    }
    public Bitmap getBitmap(){
        return bitmap;
    }

}
