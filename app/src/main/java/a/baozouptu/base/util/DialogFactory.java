package a.baozouptu.base.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import a.baozouptu.R;

/**
 * Created by liuguicen on 2016/8/30.
 *
 * @description
 */
public class DialogFactory {
    /**
     * 没有标题，只有确定有效
     * @param context
     * @param msg
     * @param sure
     */
    public static void noTitle(Context context, String msg, AlertDialog.OnClickListener sure){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        Dialog dialog = builder.setMessage(msg)
                .setNegativeButton(R.string.sure,sure)
                .setPositiveButton(R.string.cancel,null)
                .create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
}