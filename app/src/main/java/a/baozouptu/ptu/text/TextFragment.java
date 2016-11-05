package a.baozouptu.ptu.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.repealRedo.TextStepData;
import a.baozouptu.ptu.view.ColorBar;
import a.baozouptu.ptu.view.ColorLump;
import a.baozouptu.base.view.HorizontalListView;
import a.baozouptu.base.view.MySwitchButton;
import a.baozouptu.ptu.view.PtuView;

/**
 * 添加文字功能的fragment
 * Created by Administrator on 2016/5/1.
 */
public class TextFragment extends Fragment {
    PtuActivity mAcitivty;
    LinearLayout toumingdu;
    LinearLayout style;
    LinearLayout color;
    LinearLayout typeface;
    LinearLayout special;
    LinearLayout bouble;
    private int lastColor = 0xff000000;
    private FunctionPopWindowBuilder textPopupBuilder;
    private FloatTextView floatTextView;
    private Typeface curTypeface = Typeface.MONOSPACE;
    private String TAG = "TextFragment";


    public static void addBigStep(Bitmap bm, StepData sd) {
        TextStepData tsd = (TextStepData) sd;
        RepealRedoManager.addBm2Bm(bm, BitmapTool.getLosslessBitmap(tsd.picPath),
                tsd.boundRectInPic, tsd.rotateAngle);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAcitivty = (PtuActivity) getActivity();//onAttach貌似不会执行，需要在这里获取context
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        initView(view);
        textPopupBuilder = new FunctionPopWindowBuilder(mAcitivty);
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
                textPopupBuilder.setToumingduPopWindow(v);
            }
        });
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPopupBuilder.setColorPopWindow(v);
            }
        });
        typeface.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textPopupBuilder.setTypefacePopWindow(v);
            }
        });
        style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPopupBuilder.setStylePopWindow(v);
            }
        });
    }

    public void setFloatView(FloatTextView floatView) {
        this.floatTextView = floatView;
        floatTextView.setTypeface(Typeface.DEFAULT);
    }

    public TextStepData getResultData(PtuView ptuView) {
        return floatTextView.getResultData(ptuView);
    }

    public Bitmap getResultBm() {
        return floatTextView.getResultBm();
    }

    public void releaseResource() {
        floatTextView.releaseResource();
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

        void setTypefacePopWindow(View v) {
            View contentView = createTypefacePopWindow();
            setLayout(v, contentView);
        }

        private View createTypefacePopWindow() {

            View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_text_typeface, null);
            HorizontalListView horizontalListView = (HorizontalListView) contentView.findViewById(R.id.hList_text_type);
            final String[] typefacePath;
            final String[] typefaceNames = new String[]{"mono", "楷体", "默认", "更多"};

            horizontalListView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return typefaceNames.length;
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
                    if (position == lastFontId) {
                        textView.setTextColor(Util.getColor(R.color.text_checked_color));
                    } else {
                        textView.setTextColor(Util.getColor(R.color.text_default_color));
                    }
                    textView.setGravity(Gravity.CENTER);
                    textView.setTag(typefaceNames[position]);
                    if (position == 0) {
                        textView.setTextSize(30);//注意这里，英文字号增大了一些
                        textView.setTypeface(Typeface.MONOSPACE);
                    } else if (position == typefaceNames.length - 1) {
                        textView.setTypeface(Typeface.MONOSPACE);
                        textView.setTextColor(0xffaabbbb);
                    } else if (position == 1) {
                        try {
                            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/kaiti.TTF");
                            textView.setTypeface(typeface);
                        } catch (Exception e) {
                            Toast.makeText(mContext, "获取新字体失败", Toast.LENGTH_SHORT).show();
                        }
                    } else if (position == 2) {
                        textView.setTypeface(Typeface.DEFAULT);
                    }
                    textView.setText(typefaceNames[position]);
                    HorizontalListView.LayoutParams layoutParams = new HorizontalListView.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    textView.setLayoutParams(layoutParams);
                    textView.setGravity(Gravity.CENTER);

                    return textView;
                }
            });
            horizontalListView.setOnItemClickListener(new HorizontalListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == typefaceNames.length - 1) {
                        Toast.makeText(mContext, "暂不支持", Toast.LENGTH_SHORT).show();
                    } else {
                        if (position == 0) {
                            curTypeface = Typeface.MONOSPACE;
                            floatTextView.setTypeface(curTypeface);
                            floatTextView.updateSize();
                        } else if (position == 1) {
                            try {
                                curTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/kaiti.TTF");
                                floatTextView.setTypeface(curTypeface);
                                floatTextView.updateSize();
                            } catch (Exception e) {
                                Toast.makeText(mContext, "获取新字体失败", Toast.LENGTH_SHORT).show();
                            }
                        } else if (position == 2) {
                            curTypeface = Typeface.DEFAULT;
                            floatTextView.setTypeface(curTypeface);
                            floatTextView.updateSize();
                        }
                        if (lastFontId != position) {
                            ((TextView) view).setTextColor(AllData.text_choosed_color);
                            TextView textView = (TextView) ((HorizontalListView) view.getParent()).findViewWithTag(typefaceNames[lastFontId]);
                            textView.setTextColor(AllData.text_defualt_color);
                            lastFontId = position;
                        }
                    }
                }
            });
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
                    floatTextView.setTextColor(color);
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
                    floatTextView.setTextColor(colors[position]);
                }
            });
            return contentView;
        }


        public void setToumingduPopWindow(View v) {
            View contentView = createTouminduPopView();
            setLayout(v, contentView);
        }

        private View createTouminduPopView() {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.popwindow_toumindu, null);
            SeekBar seekBar = (SeekBar) contentView.findViewById(R.id.seekbar_toumingdu);
            seekBar.setMax(100);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    floatTextView.setAlpha(1 - (float) progress / 100.0f);
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
            // 设置好参数之后再show
            pop.showAtLocation(v, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
            pop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    public void onDestroy() {
        //textPopBuilder的状态都取消了
        textPopupBuilder = null;
        super.onDestroy();
    }
}
