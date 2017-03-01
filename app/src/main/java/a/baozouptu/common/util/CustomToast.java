
package a.baozouptu.common.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import a.baozouptu.R;
import a.baozouptu.common.appInfo.MyApplication;
import a.baozouptu.common.dataAndLogic.AllData;


/**
 * Created by LiuGuicen on 2017/1/6 0006.
 */


public class CustomToast {

    public static Toast makeText(Activity act, String msg, int time) {
        return makeText(msg, time);
    }

    public static Toast makeText(String msg, int time) {
        Toast sToast;
        LayoutInflater inflater = LayoutInflater.from(MyApplication.appContext);
        View layout = inflater.inflate(R.layout.layout_custom_toast, null);

        TextView textView = (TextView) layout.findViewById(R.id.tv_toast);
        textView.setText(" " + msg + " ");

        sToast = new Toast(MyApplication.appContext);
        sToast.setView(layout);
        sToast.setGravity(Gravity.BOTTOM, 0, 300);

        sToast.setDuration(time);
        sToast.show();
        return sToast;
    }
}

