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
import android.widget.PopupWindow;
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
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();//onAttach貌似不会执行，需要在这里获取context
        View view = inflater.inflate(R.layout.fragment_add_text_function, container, false);
        initView(view);
        textDialogBuilder = new FunctionDialogBuilder(mContext);
        setClick();
        return view;
    }


    /**
     * 获取addTextFragment上的view组件
     *
     * @param view
     */
    private void initView(View view) {
        toumingdu = (LinearLayout) view.findViewById(R.id.add_text_toumingdu);
        style = (LinearLayout) view.findViewById(R.id.add_text_style);
        color = (LinearLayout) view.findViewById(R.id.add_text_color);
        typeface = (LinearLayout) view.findViewById(R.id.add_text_typeface);
        special = (LinearLayout) view.findViewById(R.id.add_text_special);
        bouble = (LinearLayout) view.findViewById(R.id.add_text_bubble);
    }

    /**
     * 设置点击功能后的反应
     */
    private void setClick() {
        toumingdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog tmdDialog = new Dialog(mContext);
                tmdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                tmdDialog.setContentView(R.layout.popwindow_toumindu);

                setDialogLayout(tmdDialog, v.getHeight());
                SeekBar tmdSeekbar = (SeekBar) tmdDialog.findViewById(R.id.seekbar_toumingdu);
                tmdDialog.show();
            }
        });
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View contentView=LayoutInflater.from(mContext).inflate(R.layout.test,null);

              /*  //颜色选择条
                final ColorBar colorBar = (ColorBar) colorDialog.findViewById(R.id.color_picker);
//                 颜色块
                final ColorLump colorLump = (ColorLump) colorDialog.findViewById(R.id.chosed_color);
                colorLump.setColor(lastColor);
                colorBar.setOnColorChangerListener(new ColorBar.ColorChangeListener() {
                    @Override
                    public void colorChange(int color) {
                        colorLump.setColor(color);
                    }
                });
                *//**
                 * 预先定义的颜色
                 *//*
                final int[] colors = new int[]{0xff000000, 0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffff00,
                        0xffffffff, 0xff555555, 0xff880088, 0xff008800, 0xff880000, 0xff000088, 0xff008888};
                *//**
                 * 横向的颜色选择列表，里面是颜色选择块
                 *//*
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
                        //创建颜色块
                        ColorLump colorLump = new ColorLump(mContext);
                        colorLump.setColor(colors[position]);

                        //item的设置布局
                        HorizontalListView.LayoutParams mLayoutParams = new HorizontalListView.LayoutParams(
                                mContext.getResources().getDimensionPixelSize(R.dimen.color_lump_width),
                                mContext.getResources().getDimensionPixelSize(R.dimen.color_lump_width));
                        colorLump.setLayoutParams(mLayoutParams);
                        return colorLump;
                    }
                });
                colorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        colorLump.setColor(colors[position]);
                    }
                });
*/

                PopupWindow colorPop = new PopupWindow(contentView,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT, true);

                colorPop.setTouchable(true);

                // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
                // 我觉得这里是API的一个bug
                colorPop.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.qian));

                // 设置好参数之后再show
                colorPop.showAtLocation(v,Gravity.LEFT|Gravity.BOTTOM,0,0);
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
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        //dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_background);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onDestroy() {
        Util.P.le(this.getClass(), "onDeastory");
        super.onDestroy();
    }
}
