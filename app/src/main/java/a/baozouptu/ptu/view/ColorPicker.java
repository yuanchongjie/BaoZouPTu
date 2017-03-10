package a.baozouptu.ptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.Util;
import a.baozouptu.common.view.FirstUseDialog;
import a.baozouptu.ptu.PtuActivity;


/**
 * Created by LiuGuicen on 2017/3/2 0002.
 * 用PopUpWindow的形式创建一个颜色选择器
 * 多功能，不占用屏幕空间，
 * 可以吸取View上的颜色
 */
public class ColorPicker extends LinearLayout {
    private Context acContext;
    private List<View> viewsBeAbsorber;
    private List<Bitmap> bitmapsBeAbsorb;
    private List<Rect> srcBounds;
    private ArrayList<Rect> dstBounds;

    /**
     * 获取颜色的目标组件的接口
     */
    public interface ColorTarget {
        /**
         * 设定目标的颜色
         *
         * @param color 颜色值
         */
        void setColor(int color);

        /**
         * 获取目标当前的颜色，显示在颜色选择块中
         *
         * @return 目标当前的颜色
         */
        int getCurColor();
    }

    /**
     * 监听吸取颜色的数据，要从View中吸取颜色是设置
     */
    public interface AbsorbListener {
        /**
         * 点击吸取按钮开始吸取颜色时的回调，此时可以添加被吸取的View数据等
         *
         * @param colorPicker 通过他添加数据
         */
        void startAbsorb(ColorPicker colorPicker);

        /**
         * 点击被吸取试视图外的区域，或者再次点击吸取按钮,就会停止吸取颜色
         *
         * @return 停止后是否清空以前的被吸取view及数据
         */
        boolean stopAbsorbColor();
    }

    /**
     * 预先定义的颜色
     */
    private ArrayList<Integer> preColors = new ArrayList<>(Arrays.asList(
            0xff000000, 0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffff00,
            0xffffffff, 0xff555555, 0xff880088, 0xff008800, 0xff880000,
            0xff000088, 0xff008888));
    private ColorLump pickedLump;
    private ColorBar colorBar;
    private ImageView absorbView;

    /**
     * 监听吸取颜色的数据，要从View中吸取颜色是设置
     */
    public void setAbsorbListener(AbsorbListener absorbListener) {
        this.absorbListener = absorbListener;
    }

    private AbsorbListener absorbListener;
    public boolean isAbsorbColor = false;

    private ColorTarget colorTarget;

    public ColorPicker(Context acContext) {
        super(acContext);
        init(acContext, null);
    }

    public ColorPicker(Context acContext, AttributeSet attrs) {
        super(acContext, attrs);
        init(acContext, attrs);
    }

    public ColorPicker(Context acContext, AttributeSet attrs, int defStyleAttr) {
        super(acContext, attrs, defStyleAttr);
        init(acContext, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ColorPicker(Context acContext, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(acContext, attrs, defStyleAttr, defStyleRes);
        init(acContext, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        this.acContext = context;
        viewsBeAbsorber = new ArrayList<>();
        bitmapsBeAbsorb = new ArrayList<>();
        srcBounds = new ArrayList<>();
        dstBounds = new ArrayList<>();
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setOrientation(VERTICAL);
        createView();
    }

    /**
     * 设置要获取颜色的目标组件,相当于选择到颜色时的监听器
     *
     * @param colorTarget 获取颜色的目标组件
     * @see ColorTarget
     */
    public void setColorTarget(ColorTarget colorTarget) {
        this.colorTarget = colorTarget;
        if (colorTarget != null)
            pickedLump.setColor(colorTarget.getCurColor());
        else
            pickedLump.setColor(Color.WHITE);
    }

    private void setColor(int color) {
        if (colorTarget != null)
            colorTarget.setColor(color);
        pickedLump.setColor(color);
    }

    /**
     * 添加View 在View上吸取颜色
     *
     * @param view   被吸取颜色的View
     * @param bitmap View对应的Bitmap，Bitmap大小要和View相同，
     *               <p>可为空，如果为空，会获取View的缓存Bitmap，会消耗内存，可能会获取失败
     */
    public void addViewToGetColor(View view, Bitmap bitmap, Rect srcBound, Rect dstBound) {
        if (view == null) return;
        viewsBeAbsorber.add(view);
        bitmapsBeAbsorb.add(bitmap);
        srcBounds.add(srcBound);
        dstBounds.add(dstBound);
    }

    /**
     * 设置预定义颜色列表，如果为空表示没有
     * Set the predefine colors list，if null，don't show any one
     *
     * @param preColors 预定义颜色列表
     */
    public void setPreColors(ArrayList<Integer> preColors) {
        if (preColors == null)
            preColors.clear();
        this.preColors = preColors;
    }


    /**
     * 获取功能子视图
     *
     * @return
     */
    private View createView() {
        final View contentView = LayoutInflater.from(acContext).inflate(R.layout.layout_color_picker, this, true);
        initColorBar(contentView);//颜色选择条

        //initColorLump
        initColorLump(contentView);
        //颜色块列表
        initColorList(contentView);
        //吸取颜色
        initAbsorbView(contentView);

        return contentView;
    }

    /**
     * 初始化选择条
     */
    private void initColorBar(View contentView) {
        colorBar = (ColorBar) contentView.findViewById(R.id.color_picker_bar);
        colorBar.setOnColorChangerListener(
                new ColorBar.ColorChangeListener() {
                    @Override
                    public void colorChange(int color) {
                        pickedLump.setColor(color);
                        if (colorTarget != null)
                            colorTarget.setColor(color);
                    }
                });
        colorBar.setOnColorChosenListener(
                new ColorBar.ColorChosenListener() {
                    @Override
                    public void onColorPicked(int color) {
                        pickedLump.setColor(color);
                        if (colorTarget != null)
                            colorTarget.setColor(color);
                    }
                });
    }

    /**
     * 初始化颜色块
     */
    private void initColorLump(View contentView) {
        pickedLump = (ColorLump) contentView.findViewById(R.id.picked_color_lump);
        if (colorTarget != null)
            pickedLump.setColor(colorTarget.getCurColor());
        else
            pickedLump.setColor(Color.WHITE);
    }

    /**
     * 横向的颜色选择列表，里面是颜色选择块
     */
    private void initColorList(View contentView) {
        final RecyclerView colorListView = (RecyclerView) contentView.findViewById(R.id.color_picker_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(acContext, LinearLayout.HORIZONTAL, false);
        colorListView.setLayoutManager(layoutManager);
        ColorListAdapter colorListAdapter = new ColorListAdapter(preColors, acContext);
        colorListView.setAdapter(colorListAdapter);
    }

    /**
     * 吸取颜色
     */
    private void initAbsorbView(View contentView) {
        absorbView = (ImageView) contentView.findViewById(R.id.color_picker_absorb);
        absorbView.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isAbsorbColor) {
                            if (absorbListener != null)
                                absorbListener.startAbsorb(ColorPicker.this);
                            startAbsorbColor();
                        }
                    }
                }
        );
    }

    private void startAbsorbColor() {
        isAbsorbColor = true;
        absorbView.setImageDrawable(Util.getMyShosenIcon(R.mipmap.absorb));
        for (int i = 0; i < viewsBeAbsorber.size(); i++) {
            createAbsorbPopupWindow(viewsBeAbsorber.get(i), bitmapsBeAbsorb.get(i), srcBounds.get(i), dstBounds.get(i));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        clearAbsorbData();
        super.onDetachedFromWindow();
    }

    private void createAbsorbPopupWindow(View view, Bitmap bitmap, Rect srcBound, Rect dstBound) {
        AbsorbView absorbView = new AbsorbView(acContext, view, bitmap, srcBound, dstBound);
        absorbView.setAbsorbViewListener(new AbsorbView.AbsorbViewListener() {
            @Override
            public void onAbsorbColor(int color) {
                setColor(color);
            }

            @Override
            public void onStopAbsorb() {
                stopAbsorbColor();
            }
        });
        final PopupWindow popupWindow = new PopupWindow(acContext);

        popupWindow.setContentView(absorbView);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(Util.getDrawable(
                R.drawable.background_absorb_window));
        popupWindow.setWidth(view.getWidth());
        popupWindow.setHeight(view.getHeight());
        popupWindow.showAsDropDown(view, 0, -view.getHeight());
    }

    /**
     * 停止吸收颜色，吸收颜色的窗口关闭时会调用
     */
    private void stopAbsorbColor() {
        isAbsorbColor = false;
        absorbView.setImageDrawable(Util.getDrawable(R.mipmap.absorb));
        if (absorbListener != null) {
            if (absorbListener.stopAbsorbColor())
                clearAbsorbData();
        }
    }

    private void clearAbsorbData() {
        for (int i = viewsBeAbsorber.size() - 1; i >= 0; i--) {
            viewsBeAbsorber.remove(i);
            bitmapsBeAbsorb.remove(i);
            srcBounds.remove(i);
            dstBounds.remove(i);
        }
    }

    private class ColorListAdapter extends RecyclerView.Adapter<ColorListAdapter.ColorLumpHolder>
            implements View.OnClickListener {
        ArrayList<Integer> colorList;
        Context mContext;

        ColorListAdapter(ArrayList<Integer> colorList, Context context) {
            this.colorList = colorList;
            this.mContext = context;
        }

        @Override
        public ColorListAdapter.ColorLumpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ColorLump colorLump = new ColorLump(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    mContext.getResources().getDimensionPixelSize(R.dimen.color_lump_width),
                    ViewGroup.LayoutParams.MATCH_PARENT);
            colorLump.setLayoutParams(params);
            colorLump.setOnClickListener(this);
            return new ColorLumpHolder(colorLump);
        }

        @Override
        public void onBindViewHolder(ColorListAdapter.ColorLumpHolder holder, int position) {
            ColorLump lump = holder.lump;
            lump.setColor(colorList.get(position));
        }

        @Override
        public int getItemCount() {
            return ColorPicker.this.preColors.size();
        }

        @Override
        public void onClick(View v) {
            int color = ((ColorLump) v).getColor();
            ColorPicker.this.pickedLump.setColor(color);
            ColorPicker.this.pickedLump.invalidate();
            if (ColorPicker.this.colorTarget != null)
                ColorPicker.this.colorTarget.setColor(color);
        }


        class ColorLumpHolder extends RecyclerView.ViewHolder {
            ColorLump lump;

            public ColorLumpHolder(View itemView) {
                super(itemView);
                lump = (ColorLump) itemView;
            }
        }
    }

    /**
     * 将colorPick显示在默认的位置，采用PopupWindow的形式
     * 显示在view参数窗口的底部
     * 默认高度，75dp左右，最好是这么多
     *
     * @param view 用来获取Window Token的view
     */
    public static void showInDefaultLocation(Context acContext, ColorPicker colorPicker, int height, View view) {
        PopupWindow pop = new PopupWindow(colorPicker,
                WindowManager.LayoutParams.MATCH_PARENT,
                height, true);
        pop.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        pop.setBackgroundDrawable(acContext.getResources().getDrawable(
                R.drawable.text_popup_window_background));

        //防止与虚拟按键冲突
        //一定设置好参数之后再show,注意注意注意!!!!
        pop.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        pop.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
    }
}
