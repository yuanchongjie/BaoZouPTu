package a.baozouptu.base.util;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
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

        FrameLayout layout=new FrameLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(),bitmap.getHeight()));
        ImageView image = new ImageView(context);
        image.setImageBitmap(bitmap);
        image.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(),bitmap.getHeight()));
        layout.addView(image);
        dialog.setContentView(layout);

        dialog.getWindow().setLayout(bitmap.getWidth(),bitmap.getHeight());
        dialog.show();
    }
}
