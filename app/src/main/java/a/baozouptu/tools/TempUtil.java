package a.baozouptu.tools;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import a.baozouptu.R;

/**
 * Created by Administrator on 2016/7/2.
 */
public class TempUtil {
    public static void showBitmapInDialog(Context context,Bitmap bitmap) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(),bitmap.getHeight()));
        ImageView image = new ImageView(context);
        image.setImageBitmap(bitmap);
        image.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(),bitmap.getHeight()));
        linearLayout.addView(image);
        dialog.setContentView(linearLayout);

        dialog.getWindow().setLayout(bitmap.getWidth()*2,bitmap.getHeight()*2);
        dialog.show();
    }
}
