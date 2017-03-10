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

import java.lang.ref.WeakReference;

import a.baozouptu.R;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.Util;
import a.baozouptu.common.view.FirstUseDialog;
import a.baozouptu.common.view.HorizontalListView;
import a.baozouptu.common.view.MySwitchButton;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.view.ColorBar;
import a.baozouptu.ptu.view.ColorLump;
import a.baozouptu.ptu.view.ColorPicker;
import a.baozouptu.ptu.view.PtuFrameLayout;

/**
 * 创建添加文字模块功能区的功能操作视图
 * Created by Administrator on 2016/5/24.
 */
class FunctionPopWindowBuilder {
    private FloatTextView floatTextView;
    private Context acContext;
    Typeface curTypeface = Typeface.MONOSPACE;
    private boolean isBold = false, isItalic = false, hasShadow = false;
    private TextFragment textFragment;
    private RubberView rubberView;

    /**
     * 使用弱引用持有字体功能视图，节省内存
     */
    private WeakReference<TypefacePopWindow> weakTypefacePopup;

    public FunctionPopWindowBuilder(Context context, FloatTextView floatTextView, TextFragment textFragment) {
        acContext = context;
        this.floatTextView = floatTextView;
        this.textFragment = textFragment;
    }

    void setTypefacePopWindow(View v) {
        if (weakTypefacePopup == null || weakTypefacePopup.get() == null) {
            weakTypefacePopup = new WeakReference<>(new TypefacePopWindow(acContext, this, floatTextView));
        }
//        注意这里contentView的监听器是持有TypefacePopWindow的，contentView被window持有，
//      window消失之后监听器当做强引用方式回收，这时相当于TypefacePopWindow没被引用了那样回收
        //既FunctionPopWindowBuilder还在，TypefacePopWindow相当于不存在了
        View contentView = weakTypefacePopup.get().createTypefacePopWindow();
        setLayout(v, contentView);
    }

    //    风格
    private View getStylePopView() {
        View contentView = LayoutInflater.from(acContext).inflate(R.layout.popwindow_text_style, null);
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

    //颜色
    void setColorPopWindow(View v, ColorPicker.ColorTarget colorTarget) {
        ColorPicker colorPicker = new ColorPicker(acContext);
        colorPicker.setColorTarget(colorTarget);
        colorPicker.addViewToGetColor(textFragment.ptuSeeView, textFragment.ptuSeeView.getSourceBm(),
                textFragment.ptuSeeView.getSrcRect(), textFragment.ptuSeeView.getDstRect());
        colorPicker.setAbsorbListener(new ColorPicker.AbsorbListener() {
            @Override
            public void startAbsorb(ColorPicker colorPicker) {
                if (!AllData.hasReadConfig.hasRead_absorb()) {
                    FirstUseDialog firstUseDialog = new FirstUseDialog(acContext);
                    firstUseDialog.createDialog("吸取颜色", "可在图片中吸取想要的颜色，吸取之后点击其它地方即可使用", new FirstUseDialog.ActionListener() {
                        @Override
                        public void onSure() {
                            AllData.hasReadConfig.write_absorb(true);
                        }
                    });
                }
            }

            @Override
            public boolean stopAbsorbColor() {
                return false;
            }
        });
        ColorPicker.showInDefaultLocation(acContext, colorPicker, v.getHeight() + Util.dp2Px(5), v);
    }


    //透明度
    void setToumingduPopWindow(View v) {
        View contentView = createToumingduPopView();
        setLayout(v, contentView);
    }

    private View createToumingduPopView() {
        final PtuFrameLayout ptuFrameLayout = ((PtuActivity) textFragment.getActivity()).getPtuFrame();
        View contentView = LayoutInflater.from(acContext).inflate(R.layout.popwindow_toumindu, null);
        SeekBar seekBar = (SeekBar) contentView.findViewById(R.id.seekbar_toumingdu);
        seekBar.setMax(100);
        if (floatTextView.isClickable())
            seekBar.setProgress((int) floatTextView.getAlpha());
        else seekBar.setProgress(rubberView.getRubberWidth());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (floatTextView.isClickable())
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
    void setLayout(View v, View contentView) {
        PopupWindow pop = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT,
                v.getHeight() + Util.dp2Px(5), true);
        pop.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        pop.setBackgroundDrawable(acContext.getResources().getDrawable(
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