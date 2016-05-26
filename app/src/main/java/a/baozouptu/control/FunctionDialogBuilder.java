package a.baozouptu.control;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.tools.Util;
import a.baozouptu.view.HorizontalListView;
import a.baozouptu.view.MySwitchButton;

/**
 * Created by Administrator on 2016/5/24.
 */
public class FunctionDialogBuilder {
    Context mContext;

    public FunctionDialogBuilder(Context context) {
        mContext = context;
    }

    void getTypefaceDialog(int baseViewHeight) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_text_type);
        HorizontalListView horizontalListView = (HorizontalListView) dialog.findViewById(R.id.hList_text_type);
        final Typeface[] typefaces = new Typeface[]{Typeface.DEFAULT, Typeface.DEFAULT_BOLD,
                Typeface.MONOSPACE, Typeface.SANS_SERIF};
        final String[] textStyleNames = new String[]{"默认", "粗体", "等宽", "serif"};
        horizontalListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return typefaces.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setLayoutParams(new HorizontalListView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, Util.dp2Px(30)));
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);

                TextView textView = new TextView(mContext);

                textView.setText(textStyleNames[position]);
                textView.setTextSize(20);
                textView.setTextColor(mContext.getResources().getColor(R.color.text_color1));
                if (position < typefaces.length) textView.setTypeface(typefaces[position]);
                else textView.setTypeface(typefaces[0]);
                LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                        Util.dp2Px(60), Util.dp2Px(60));
                linearLayout.addView(textView, mLayoutParams);
                return linearLayout;
            }
        });
        setDialogLayout(dialog, baseViewHeight);
        dialog.show();

    }

    void getTextStyleDialog(int baseViewHeight) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_text_style);

        setDialogLayout(dialog, baseViewHeight);
        dialog.show();
    }

    /**
     * @param dialog
     * @param height 相对高度
     */
    void setDialogLayout(Dialog dialog, int height) {
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        //dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_background);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y = height + Util.dp2Px(mContext, 20);
        dialogWindow.setAttributes(lp);
    }
}
