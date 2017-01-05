package a.baozouptu.ptu.text;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import a.baozouptu.R;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.Util;
import a.baozouptu.common.view.HorizontalListView;
import a.baozouptu.common.view.MySwitchButton;
import a.baozouptu.network.FileDownloader;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.view.ColorBar;
import a.baozouptu.ptu.view.ColorLump;
import a.baozouptu.ptu.view.PtuFrameLayout;

/**
 * 创建添加文字模块功能区的功能操作视图
 * Created by Administrator on 2016/5/24.
 */
public class FunctionPopWindowBuilder {
    private Typeface curTypeface = Typeface.MONOSPACE;
    private FloatTextView floatTextView;
    private Context mContext;
    private boolean isBold = false, isItalic = false, hasShadow = false;
    private int lastFontId = 0;
    private TextFragment textFragment;
    private RubberView rubberView;
    private ArrayList<Typeface> typefaceList;

    public FunctionPopWindowBuilder(Context context, FloatTextView floatTextView, TextFragment textFragment) {
        mContext = context;
        this.floatTextView = floatTextView;
        this.textFragment = textFragment;
    }

    void setTypefacePopWindow(View v) {
        initTypeface();
        View contentView = createTypefacePopWindow();
        setLayout(v, contentView);
    }

    private void initTypeface() {
        if (typefaceList == null) {
            typefaceList = new ArrayList<>();
            typefaceList.add(null);
        }
        for (int i = 1; i < FileDownloader.typefaceNames.size(); i++) {
            try {
                Typeface typeface = Typeface.createFromFile(AllData.zitiDir + FileDownloader.typefaceNames.get(i));
                typefaceList.add(typeface);
            } catch (Exception e) {
                typefaceList.add(null);
                //如果是损坏的文件，删除它
                File file=new File(AllData.zitiDir+FileDownloader.typefaceNames.get(i));
                if(file.exists())
                    file.delete();
            }
        }
    }

    private View createTypefacePopWindow() {

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_text_typeface, null);
        HorizontalListView horizontalListView = (HorizontalListView) contentView.findViewById(R.id.hList_text_type);

        horizontalListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return FileDownloader.typefaceChinese.size();
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
                TextView textView = new TextView(mContext);
                textView.setTextSize(25);
                /**设置颜色*/
                if (position == lastFontId) {
                    textView.setTextColor(Util.getColor(R.color.text_checked_color));
                } else {
                    textView.setTextColor(Util.getColor(R.color.text_default_color));
                }

                //设置字体
                textView.setTag(FileDownloader.typefaceNames.get(position));
                if (position == 0) {
                    textView.setTextSize(30);//注意这里，英文字号增大了一些
                } else {
                    Typeface typeface = typefaceList.get(position);
                    if (typeface != null)
                        textView.setTypeface(typeface);
                }

//其他的属性
                textView.setGravity(Gravity.CENTER);
                textView.setText(FileDownloader.typefaceChinese.get(position));
                HorizontalListView.LayoutParams layoutParams = new HorizontalListView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER);

                return textView;
            }
        });
        horizontalListView.setOnItemClickListener(
                new HorizontalListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            curTypeface = Typeface.MONOSPACE;
                            floatTextView.setTypeface(curTypeface);
                            floatTextView.updateSize();
                        } else {
                            try {
                                curTypeface = typefaceList.get(position);
                                if (typefaceList.get(position) == null) {  //如果字体不存在，就进行下载，下载完成之后更新typeface列表，以及视图
                                    FileDownloader.getInstance().downloadZiti(mContext, FileDownloader.typefaceNames.get(position)
                                            , typefaceList,(TextView)view);
                                    return;
                                } else {
                                    floatTextView.setTypeface(curTypeface);
                                    floatTextView.updateSize();
                                }
                            } catch (Exception e) {

                            }
                        }
                        //切换颜色
                        if (lastFontId != position) {
                            ((TextView) view).setTextColor(Util.getColor(R.color.text_checked_color));
                            TextView textView = (TextView) ((HorizontalListView)
                                    view.getParent()).findViewWithTag(FileDownloader.typefaceNames.get(lastFontId));
                            textView.setTextColor(Util.getColor(R.color.text_default_color));
                            lastFontId = position;
                        }
                    }
                }
        );
        horizontalListView.setDividerWidth(Util.dp2Px(10));
        return contentView;
    }

    void setStylePopWindow(View v) {

        View contentView = getStylePopView();
        //粗体
        MySwitchButton switchBold = (MySwitchButton) contentView.findViewById(R.id.switch_button_bold);
        switchBold.setState(isBold);
        switchBold.setOnSlideListener(new MySwitchButton.SlideListener() {
            @Override
            public void open() {
                if (isItalic) {
                    floatTextView.setTypeface(curTypeface, Typeface.BOLD_ITALIC);
                    floatTextView.updateSize();
                } else {
                    floatTextView.setTypeface(curTypeface, Typeface.BOLD);
                    floatTextView.updateSize();
                }
                isBold = true;
            }

            @Override
            public void close() {
                if (isItalic) {
                    floatTextView.setTypeface(curTypeface, Typeface.ITALIC);
                    floatTextView.updateSize();
                } else {
                    floatTextView.setTypeface(curTypeface, Typeface.NORMAL);
                    floatTextView.updateSize();
                }
                isBold = false;
            }
        });
        //斜体
        MySwitchButton switchItalic = (MySwitchButton) contentView.findViewById(R.id.switch_button_text_italic);
        switchItalic.setState(isItalic);
        switchItalic.setOnSlideListener(new MySwitchButton.SlideListener() {
            @Override
            public void open() {
                if (isBold) {
                    floatTextView.setTypeface(curTypeface, Typeface.BOLD_ITALIC);//斜体，中文有效
                    floatTextView.updateSize();
                } else {
                    floatTextView.setTypeface(curTypeface, Typeface.ITALIC);//斜体，中文有效
                    floatTextView.updateSize();
                }
                isItalic = true;
            }

            @Override
            public void close() {
                if (isBold) {
                    floatTextView.setTypeface(curTypeface, Typeface.BOLD);
                } else {
                    floatTextView.setTypeface(curTypeface, Typeface.NORMAL);
                    floatTextView.updateSize();
                }
                isItalic = false;
            }
        });
        MySwitchButton switchShadow = (MySwitchButton) contentView.findViewById(R.id.switch_button_text_shadow);
        switchShadow.setState(hasShadow);
        switchShadow.setOnSlideListener(new MySwitchButton.SlideListener() {
            @Override
            public void open() {
                floatTextView.setShadowLayer(5, 5, 5, Color.GRAY);
                floatTextView.updateSize();
                hasShadow = true;
            }

            @Override
            public void close() {
                floatTextView.setShadowLayer(0, 0, 0, Color.GRAY);
                floatTextView.updateSize();
                hasShadow = false;
            }
        });
        setLayout(v, contentView);
    }

    private View getStylePopView() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_text_style, null);
        return contentView;
    }

    void setColorPopWindow(View v) {
        View contentView = getColorPopView();
        setLayout(v, contentView);
    }

    /**
     * 获取功能子视图
     *
     * @return
     */
    private View getColorPopView() {
        final PtuFrameLayout ptuFrameLayout = ((PtuActivity) textFragment.getActivity()).getPtuFrame();
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_chose_color, null);
        //颜色选择条
        final ColorBar colorBar = (ColorBar) contentView.findViewById(R.id.color_picker);
        //颜色块
        final ColorLump colorLump = (ColorLump) contentView.findViewById(R.id.chosed_color);
        if (ptuFrameLayout.getChildAt(ptuFrameLayout.getChildCount() - 1) instanceof FloatTextView)
            colorLump.setColor(floatTextView.getTextColors().getDefaultColor());
        else colorLump.setColor(rubberView.getColor());
        colorBar.setOnColorChangerListener(new ColorBar.ColorChangeListener() {
            @Override
            public void colorChange(int color) {
                colorLump.setColor(color);
                if (ptuFrameLayout.getChildAt(ptuFrameLayout.getChildCount() - 1) instanceof FloatTextView)
                    floatTextView.setTextColor(color);
                else rubberView.setColor(color);
            }
        });
        colorBar.setOnColorChosedListener(new ColorBar.ColorChosedListener() {
            @Override
            public void colorChosed(int color) {

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
                if (ptuFrameLayout.getChildAt(ptuFrameLayout.getChildCount() - 1) instanceof FloatTextView)
                    floatTextView.setTextColor(colors[position]);
                else rubberView.setColor(colors[position]);
            }
        });
        return contentView;
    }


    void setToumingduPopWindow(View v) {
        View contentView = createTouminduPopView();
        setLayout(v, contentView);
    }

    private View createTouminduPopView() {
        final PtuFrameLayout ptuFrameLayout = ((PtuActivity) textFragment.getActivity()).getPtuFrame();
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_toumindu, null);
        SeekBar seekBar = (SeekBar) contentView.findViewById(R.id.seekbar_toumingdu);
        seekBar.setMax(100);
        if (ptuFrameLayout.getChildAt(ptuFrameLayout.getChildCount() - 1) instanceof FloatTextView)
            seekBar.setProgress((int) floatTextView.getAlpha());
        else seekBar.setProgress(rubberView.getRubberWidth());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (ptuFrameLayout.getChildAt(ptuFrameLayout.getChildCount() - 1) instanceof FloatTextView)
                    floatTextView.setAlpha(1 - (float) progress / 100.0f);
                else rubberView.setRubberWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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

        //防止与虚拟按键冲突
        //一定设置好参数之后再show,注意注意注意!!!!
        pop.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        pop.showAtLocation(v, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
    }

    public void setRubberView(RubberView rubberView) {
        this.rubberView = rubberView;
    }
}