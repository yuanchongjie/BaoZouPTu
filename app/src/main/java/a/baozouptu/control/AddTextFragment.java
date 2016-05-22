package a.baozouptu.control;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import a.baozouptu.R;
import a.baozouptu.tools.Util;

/**
 * Created by Administrator on 2016/5/1.
 */
public class AddTextFragment extends Fragment {
    Context mcontext;
    LinearLayout toumingdu;
    LinearLayout style;
    LinearLayout color;
    LinearLayout typeface;
    LinearLayout special;
    LinearLayout bouble;

    @Override
    public void onAttach(Context context) {

        Util.P.le(this, "onAttach" + this.getClass().getSimpleName());
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Util.P.le(this.getClass(), "onCreateView");
        mcontext = getActivity();
        View view = inflater.inflate(R.layout.fragment_add_text_function, container, false);
        initView(view);
        setClick();
        return view;
    }

    private void initView(View view) {
        toumingdu = (LinearLayout) view.findViewById(R.id.add_text_toumingdu);
        style = (LinearLayout) view.findViewById(R.id.add_text_style);
        color = (LinearLayout) view.findViewById(R.id.add_text_color);
        typeface = (LinearLayout) view.findViewById(R.id.add_text_typeface);
        special = (LinearLayout) view.findViewById(R.id.add_text_special);
        bouble = (LinearLayout) view.findViewById(R.id.add_text_bubble);
    }

    private void setClick() {
        toumingdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog tmdDialog = new Dialog(mcontext);
                tmdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                tmdDialog.setContentView(R.layout.dialog_layout_toumindu);

                setDialogLayout(tmdDialog,v.getHeight());
                SeekBar tmdSeekbar = (SeekBar) tmdDialog.findViewById(R.id.seekbar_toumingdu);
                tmdDialog.show();
            }
        });
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog colorDialog = new Dialog(mcontext);
                colorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                colorDialog.setContentView(R.layout.dialog_layout_color);


                setDialogLayout(colorDialog,v.getHeight());

                SeekBar tmdSeekbar = (SeekBar) colorDialog.findViewById(R.id.seekbar_toumingdu);
                colorDialog.show();
            }
        });
    }

    /**
     *
     * @param dialog
     * @param height 相对高度
     */
    void setDialogLayout(Dialog dialog,int height) {
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        //dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_background);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        lp.y = height+ Util.dp2Px(mcontext, 20);
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onDestroy() {
        Util.P.le(this.getClass(), "onDeastory");
        super.onDestroy();
    }
}
