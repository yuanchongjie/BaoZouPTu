package a.baozouptu.control;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.tools.Util;
import a.baozouptu.view.ColorBar;
import a.baozouptu.view.ColorLump;
import a.baozouptu.view.HorizontalListView;

/**
 * Created by Administrator on 2016/5/1.
 */
public class AddTextFragment extends Fragment {
    Context mContext;
    LinearLayout toumingdu;
    LinearLayout style;
    LinearLayout color;
    LinearLayout typeface;
    LinearLayout special;
    LinearLayout bouble;
    private int lastColor = 0xff000000;
    private FunctionDialogBuilder textDialogBuilder;

    @Override
    public void onAttach(Context context) {

        Util.P.le(this, "onAttach" + this.getClass().getSimpleName());
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Util.P.le(this.getClass(), "onCreateView");
        mContext = getActivity();

        View view = inflater.inflate(R.layout.fragment_add_text_function, container, false);
        initView(view);
        textDialogBuilder = new FunctionDialogBuilder(mContext);
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
                Dialog tmdDialog = new Dialog(mContext);
                tmdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                tmdDialog.setContentView(R.layout.dialog_layout_toumindu);

                setDialogLayout(tmdDialog, v.getHeight());
                SeekBar tmdSeekbar = (SeekBar) tmdDialog.findViewById(R.id.seekbar_toumingdu);
                tmdDialog.show();
            }
        });
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog colorDialog = new Dialog(mContext);
                final int[] colors = new int[]{0xff000000, 0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffff00,
                        0xffffffff, 0xff555555, 0xff880088, 0xff008800, 0xff880000, 0xff000088, 0xff008888};
                colorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                colorDialog.setContentView(R.layout.dialog_layout_color);
                final ColorBar colorBar = (ColorBar) colorDialog.findViewById(R.id.color_picker);
                final ColorLump colorLump = (ColorLump) colorDialog.findViewById(R.id.chosed_color);
                colorLump.setColor(lastColor);
                colorBar.setOnColorChangerListener(new ColorBar.ColorChangeListener() {
                    @Override
                    public void colorChange(int color) {
                        colorLump.setColor(color);
                    }
                });
                final HorizontalListView colorList = (HorizontalListView) colorDialog.findViewById(R.id.color_list);

                colorList.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return colors.length;
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
                        linearLayout.setLayoutParams(
                                new HorizontalListView.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT, Util.dp2Px(30)));

                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        linearLayout.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                                Util.dp2Px(30f), Util.dp2Px(30));

                        ColorLump colorLump = new ColorLump(mContext);

                        colorLump.setColor(colors[position]);
                        linearLayout.addView(colorLump, mLayoutParams);
                        return colorLump;
                    }
                });
                colorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        colorLump.setColor(colors[position]);
                    }
                });
                setDialogLayout(colorDialog, v.getHeight());
                colorDialog.show();
            }
        });
        typeface.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textDialogBuilder.getTypefaceDialog(v.getHeight());
            }
        });
        style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialogBuilder.getTextStyleDialog(v.getHeight());
            }
        });
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
        lp.y = height + Util.dp2Px(mContext, 20);
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onDestroy() {
        Util.P.le(this.getClass(), "onDeastory");
        super.onDestroy();
    }
}
