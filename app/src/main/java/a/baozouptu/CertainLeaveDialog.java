package a.baozouptu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;

/**
 * Created by Administrator on 2016/11/22 0022.
 */

public class CertainLeaveDialog {
    Context mContext;
    AlertDialog dialog;

    public interface ActionListener {
        void onSure();
    }

    public CertainLeaveDialog(Context context) {
        this.mContext = context;
    }

    public void createDialog(String title, String msg, final ActionListener actionListener) {
//判断对话框是否已经存在了,防止重复点击
        if (dialog != null && dialog.isShowing()) return;
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setMessage("已经修改了图片,确定离开吗？")
                .setPositiveButton("离开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actionListener.onSure();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

}
