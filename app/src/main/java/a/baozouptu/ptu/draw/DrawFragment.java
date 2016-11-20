package a.baozouptu.ptu.draw;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import a.baozouptu.R;
import a.baozouptu.base.util.Util;
import a.baozouptu.base.view.HorizontalListView;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.text.TextFragment;
import a.baozouptu.ptu.view.ColorBar;
import a.baozouptu.ptu.view.ColorLump;

/**
 * Created by Administrator on 2016/7/25.
 */
public class DrawFragment extends Fragment implements DrawBaseFunction, View.OnClickListener {
    private String TAG = "DrawFragment";
    private Context mContext;
    private LinearLayout style;
    private LinearLayout size;
    private LinearLayout color;
    private DrawView drawView;
    private View view;
    private  FunctionPopWindowBuilder drawPopupBuilder;
    private int lastColor = 0xff000000;

    @Override
    public void smallRepeal() {
        drawView.undo();
        Log.e(TAG,
                "repealPrepare");
    }

    @Override
    public void smallRedo() {

    }

    @Override
    public void repeal() {

    }

    @Override
    public void redo(StepData sd) {
        drawView.redo();
        Log.e(TAG, "redo");
    }

    @Override
    public void setButtonBm(Bitmap buttonBm) {

    }

    @Override
    public Bitmap getResultBm(float ratio) {
        return null;
    }

    @Override
    public StepData getResultData(float ratio) {
        return null;
    }

    @Override
    public void addBigStep(StepData sd) {

    }

    @Override
    public void releaseResource() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw, null);
        mContext = getActivity();
        drawPopupBuilder = new  FunctionPopWindowBuilder(mContext);
        color = (LinearLayout) view.findViewById(R.id.draw_color);
        style = (LinearLayout) view.findViewById(R.id.draw_style);
        size = (LinearLayout) view.findViewById(R.id.draw_size);
        setClick();
        ((PtuActivity) getActivity()).ptuView.setCanDoubleClick(false);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((PtuActivity) getActivity()).ptuView.setCanDoubleClick(true);
    }

    private void setClick() {
        color.setOnClickListener(this);
        style.setOnClickListener(this);
        size.setOnClickListener(this);
    }

    public View createDrawView(Context context, Rect totalBound, Rect picBound) {
        drawView = new DrawView(context, totalBound);
        return drawView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.draw_color:
                drawPopupBuilder.setColorPopWindow(v);
                break;
            case R.id.draw_size:
                drawPopupBuilder.setSizePopWindow(v);
                break;
            case R.id.draw_style:
                showMoreDialog(v);
                break;

        }
    }
    //弹出选择画笔或橡皮擦的对话框
    private int select_paint_style_index = 0;
    public void showMoreDialog(View parent){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("选择画笔或橡皮擦：");
        alertDialogBuilder.setSingleChoiceItems(R.array.paintstyle, select_paint_style_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_style_index = which;
                drawView.selectPaintStyle(which);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    /**
     * 创建添加文字模块功能区的功能操作视图
     * Created by Administrator on 2016/5/24.
     */
    public class FunctionPopWindowBuilder {
        Context mContext;
        boolean isBold = false, isItalic = false, hasShadow = false;
        int lastFontId = 0;

        public FunctionPopWindowBuilder(Context context) {
            mContext = context;
        }


        void setStylePopWindow(View v) {

            View contentView = getStylePopView();

            setLayout(v, contentView);
        }

        private View getStylePopView() {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_text_style, null);
            return contentView;
        }

        public void setColorPopWindow(View v) {
            View contentView = getColorPopView();
            setLayout(v, contentView);
        }

        /**
         * 获取功能子视图
         *
         * @return
         */
        private View getColorPopView() {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_chose_color, null);
            //颜色选择条
            final ColorBar colorBar = (ColorBar) contentView.findViewById(R.id.color_picker);
            //颜色块
            final ColorLump colorLump = (ColorLump) contentView.findViewById(R.id.chosed_color);
            colorLump.setColor(lastColor);
            colorBar.setOnColorChangerListener(new ColorBar.ColorChangeListener() {
                @Override
                public void colorChange(int color) {
                    colorLump.setColor(color);
                }
            });
            colorBar.setOnColorChosedListener(new ColorBar.ColorChosedListener() {
                @Override
                public void colorChosed(int color) {
                    drawView.selectPaintColor(color);
                }
            });
            /**
             * 预先定义的颜色
             */
            final int[] colors = new int[]{0xff000000, 0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffff00,
                    0xffffffff, 0xff555555, 0xff880088, 0xff008800, 0xff880000, 0xff000088, 0xff008888};
            /**
             * 横向的颜色选择列表，里面是颜色选择块
             */
            final HorizontalListView colorList = (HorizontalListView) contentView.findViewById(R.id.color_list);

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
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    colorLump.setLayoutParams(mLayoutParams);
                    return colorLump;
                }
            });
            colorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    colorLump.setColor(colors[position]);
                    drawView.selectPaintColor(colors[position]);
                }
            });
            return contentView;
        }


        public void setSizePopWindow(View v) {
            View contentView = createSizePopView();
            setLayout(v, contentView);
        }

        private View createSizePopView() {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_toumindu, null);
            SeekBar seekBar = (SeekBar) contentView.findViewById(R.id.seekbar_toumingdu);
            seekBar.setMax(100);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    drawView.selectPaintSize(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    drawView.selectPaintSize(seekBar.getProgress());
                }
            });
            return contentView;
        }

        /**
         * 设置功能子视图的布局
         * 注意这里popupwindow的高度要加上view所在布局的padding
         */
        private void setLayout(View v, View contentView) {
            PopupWindow pop = new PopupWindow(contentView,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    v.getHeight() + Util.dp2Px(5), true);
            pop.setTouchable(true);
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            pop.setBackgroundDrawable(mContext.getResources().getDrawable(
                    R.drawable.text_popup_window_background));
            // 设置好参数之后再show
            pop.showAtLocation(v, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
            pop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

}
