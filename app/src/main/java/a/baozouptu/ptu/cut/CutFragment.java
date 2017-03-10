package a.baozouptu.ptu.cut;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.common.util.BitmapTool;
import a.baozouptu.common.util.FileTool;
import a.baozouptu.common.util.Util;
import a.baozouptu.ptu.BasePtuFragment;
import a.baozouptu.ptu.PtuUtil;
import a.baozouptu.ptu.repealRedo.CutStepData;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.view.PtuSeeView;
import rx.Subscriber;

public class CutFragment extends BasePtuFragment {
    private String TAG = "CutFragment";
    private Context mContext;
    private PtuSeeView ptuSeeView;
    private CutView cutView;

    private ViewGroup fixedSize;
    private ViewGroup fixedRatio;
    private ViewGroup rotate;
    private ViewGroup reversal;

    private ViewGroup[] layoutList;
    private Drawable[] drawableList;
    private Drawable[] chosenDrawableList;

    private int chosenId = -1;

    private static final String[] RATIO_NAMES = new String[]{
            "1:1", "3:2", "2:3", "4:3", "3:4", "16:9", "9:16", "自定义", "自由"
    };
    private static final float[] RATIOS = new float[]{
            1, 3f / 2, 2f / 3, 4f / 3, 3f / 4, 16f / 9, 9f / 16, 1, 1
    };

    private SizeRatioDialog sizeRatioDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawableList = new Drawable[]
                {
                        Util.getDrawable(R.mipmap.fixed_size).mutate(),
                        Util.getDrawable(R.mipmap.scale).mutate(),
                        Util.getDrawable(R.mipmap.rotate).mutate(),
                        Util.getDrawable(R.mipmap.reversal).mutate()
                };

        chosenDrawableList = new Drawable[]
                {
                        getStateDrawable(Util.getDrawable(R.mipmap.fixed_size).mutate()
                                , getStateList(), PorterDuff.Mode.SRC_IN),
                        getStateDrawable(Util.getDrawable(R.mipmap.scale).mutate(),
                                getStateList(), PorterDuff.Mode.SRC_IN),
                        getStateDrawable(Util.getDrawable(R.mipmap.rotate).mutate(),
                                getStateList(), PorterDuff.Mode.SRC_IN),
                        getStateDrawable(Util.getDrawable(R.mipmap.reversal).mutate(),
                                getStateList(), PorterDuff.Mode.SRC_IN)
                };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cut, null);
        mContext = getActivity();

        fixedSize = (ViewGroup) view.findViewById(R.id.cut_fix_size);
        fixedRatio = (ViewGroup) view.findViewById(R.id.cut_fixed_ratio);
        rotate = (ViewGroup) view.findViewById(R.id.cut_rotate);
        reversal = (ViewGroup) view.findViewById(R.id.cut_reversal);

        layoutList = new ViewGroup[]{
                fixedSize,
                fixedRatio,
                rotate,
                reversal
        };
        setClick();
        return view;
    }

    /**
     * 设置点击变色的
     */
    private Drawable getStateDrawable(Drawable src, ColorStateList colors, PorterDuff.Mode mode) {
        Drawable drawable = DrawableCompat.wrap(src);
        DrawableCompat.setTintList(drawable, colors);
        DrawableCompat.setTintMode(drawable, mode);
        return drawable;
    }

    private ColorStateList getStateList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColorStateList(R.color.imageview_tint_function, null);
        } else {
            return getResources().getColorStateList(R.color.imageview_tint_function);
        }
    }


    @Override
    public Bitmap getResultBm(float ratio) {
        return cutView.getResultBm();
    }

    @Override
    public StepData getResultDataAndDraw(float ratio) {
        //获取并保存数据
        StepData csd = new CutStepData(PtuUtil.EDIT_CUT);
        Bitmap resultBm = getResultBm(1);
        String tempPath = FileTool.createTempPicPath();
        BitmapTool.asySaveTempBm(tempPath, resultBm, new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        });
        csd.picPath = tempPath;

        //重新绘制
        ptuSeeView.replaceSourceBm(resultBm);
        return csd;
    }

    @Override
    public void addBigStep(StepData sd) {
        ptuSeeView.replaceSourceBm(BitmapTool.getLosslessBitmap(sd.picPath));
    }

    @Override
    public void releaseResource() {
        cutView.releaseResource();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setClick() {
        fixedSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImageAppearance(0, (ViewGroup) view);
                final int pad = 10;
                final PopupWindow popWindow = getPopwindow(view);

                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setBackgroundColor(Color.WHITE);
                linearLayout.setPadding(0, pad, 0, pad);

                final TextView custom = createItem(pad);
                custom.setText("自定义");
                custom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userDefinedSize();
                        custom.setTextColor(Util.getColor(R.color.text_checked_color));
                        popWindow.dismiss();
                    }
                });

                final TextView free = createItem(pad);
                free.setText("自由");
                free.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        free.setTextColor(Util.getColor(R.color.text_checked_color));
                        popWindow.dismiss();
                    }
                });
                TextView d1 = new TextView(mContext);
                d1.setHeight(pad);
                TextView d2 = new TextView(mContext);
                d2.setHeight(pad);
                TextView d3 = new TextView(mContext);
                d3.setHeight(1);
                d3.setBackground(
                        Util.getDrawable(R.drawable.divider_cut_chose));


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.addView(custom, params);
                linearLayout.addView(d1);
                linearLayout.addView(d3);
                linearLayout.addView(d2);
                linearLayout.addView(free, params);

                setPopWindow(popWindow, view, linearLayout);
            }
        });

        fixedRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImageAppearance(1, (ViewGroup) view);

                final PopupWindow popupWindow = getPopwindow(view);
                FrameLayout choseRatioLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.cut_chose_ratio, null);
                ListView listView = (ListView) choseRatioLayout.findViewById(R.id.cut_choose_ratio_list);
                listView.setAdapter(new ChoseRatioAdapter(mContext, RATIO_NAMES, view.getWidth()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == RATIO_NAMES.length - 2) {
                            userDefinedRatio();
                        } else if (position == RATIO_NAMES.length - 1) {
                            cutView.resetShow();
                        } else
                            cutView.setFixedRatio(RATIOS[position]);
                        popupWindow.dismiss();
                    }
                });
                setPopWindow(popupWindow, view, choseRatioLayout);
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImageAppearance(2, (ViewGroup) v);
                onClickRotate();
            }
        });

        reversal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImageAppearance(3, (ViewGroup) view);

                final int pad = 10;
                final PopupWindow popWindow = getPopwindow(view);

                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setBackgroundColor(Color.WHITE);
                linearLayout.setPadding(0, pad, 0, pad);

                final TextView vertical = createItem(pad);
                vertical.setText("垂直");
                vertical.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vertical.setTextColor(Util.getColor(R.color.text_checked_color));
                        cutView.reverse(0);
                        popWindow.dismiss();
                    }
                });

                final TextView horizontal = createItem(pad);
                horizontal.setText("水平");
                horizontal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        horizontal.setTextColor(Util.getColor(R.color.text_checked_color));
                        cutView.reverse(1);
                        popWindow.dismiss();
                    }
                });
                TextView d1 = new TextView(mContext);
                d1.setHeight(pad);
                TextView d2 = new TextView(mContext);
                d2.setHeight(pad);
                TextView d3 = new TextView(mContext);
                d3.setHeight(1);
                d3.setBackground(
                        Util.getDrawable(R.drawable.divider_cut_chose));


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.addView(vertical, params);
                linearLayout.addView(d1);
                linearLayout.addView(d3);
                linearLayout.addView(d2);
                linearLayout.addView(horizontal, params);

                setPopWindow(popWindow, view, linearLayout);
            }
        });
    }

    private void userDefinedRatio() {
        sizeRatioDialog = new SizeRatioDialog(mContext, 1);
        sizeRatioDialog.createDialog();
        sizeRatioDialog.setActionListener(new SizeRatioDialog.ActionListener() {
            @Override
            public void onSure(float w, float h) {
                cutView.setFixedRatio(h / w);
            }
        });
    }

    private void userDefinedSize() {
        sizeRatioDialog = new SizeRatioDialog(mContext, 0);
        sizeRatioDialog.createDialog();
        sizeRatioDialog.setActionListener(new SizeRatioDialog.ActionListener() {
            @Override
            public void onSure(float w, float h) {
                cutView.setFixedSize((int) (w + 0.5f), (int) (h + 0.5f));
            }
        });
    }

    private void onClickRotate() {
        cutView.rotate(90);
    }

    public void setRealRedoManager(RepealRedoManager realRedoManager) {
    }

    public View createCutView(Context context, Rect totalBound, Bitmap sourceBm) {
        cutView = new CutView(context, totalBound, sourceBm);
        cutView.setCanDoubleClick(false);
        cutView.setCanLessThanScreen(false);
        return cutView;
    }

    public void setPtuSeeView(PtuSeeView ptuSeeView) {
        this.ptuSeeView = ptuSeeView;
    }

    private TextView createItem(final int pad) {
        TextView tv = new TextView(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);
        tv.setTextColor(Util.getColor(R.color.text_deep_black));
        return tv;
    }

    private PopupWindow getPopwindow(View view) {
        final PopupWindow popWindow = new PopupWindow(mContext);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);  //设置点击屏幕其它地方弹出框消失
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        return popWindow;
    }

    private void setPopWindow(PopupWindow popWindow, View view, ViewGroup layout) {
        int[] popWH = new int[2];
        Util.getMesureWH(layout, popWH);
        popWindow.setContentView(layout);
        popWindow.setWidth(view.getWidth());
        popWindow.setHeight(popWH[1]);
        //自己的高度，减去父布局的padding，再减去View的高度
        int top = -view.getHeight()
                - ((ViewGroup) view.getParent()).getPaddingTop()
                - popWH[1];
        popWindow.showAsDropDown(view, 0, top);
    }

    private void changeImageAppearance(int i, ViewGroup group) {
        if (chosenId != -1) {
            ((ImageView) layoutList[chosenId].getChildAt(0)).setImageDrawable(drawableList[chosenId]);
            chosenId = -1;
        }
        chosenId = i;
        ((ImageView) group.getChildAt(0)).setImageDrawable(chosenDrawableList[chosenId]);
    }

    @Override
    public void smallRepeal() {
    }

    @Override
    public void smallRedo() {

    }
    @Override
    public void clear(){
        if(sizeRatioDialog!=null){
            sizeRatioDialog.dismissDialog();
        }
    }

}
