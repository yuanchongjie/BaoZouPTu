package a.baozouptu.base.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import a.baozouptu.R;

/**
 * Created by Administrator on 2016/11/19 0019.
 */

public class FirstUseDialog {
    private AlertDialog dialog;
    private Context mContext;

    public interface ActionListener {
        void onSure();
    }

    public FirstUseDialog(Context context) {
        mContext = context;
    }

    public void createDialog(String title, String msg, final ActionListener actionListener) {
        //判断对话框是否已经存在了,防止重复点击
        if (dialog != null && dialog.isShowing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_first_use, null);
        TextView msgView = (TextView) view.findViewById(R.id.first_use_msg);
        msgView.setText(msg);
        TextView btn = (TextView) view.findViewById(R.id.first_use_sure);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onSure();
                dialog.dismiss();
            }
        });
        dialog = builder.setView(view)
                .create();
        dialog.getWindow();

        dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
}
