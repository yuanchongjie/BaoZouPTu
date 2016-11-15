package a.baozouptu.ptu.cut;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.base.util.BitmapTool;
import a.baozouptu.base.util.Util;
import a.baozouptu.ptu.BaseFunction;
import a.baozouptu.ptu.PtuActivity;
import a.baozouptu.ptu.repealRedo.CutStepData;
import a.baozouptu.ptu.repealRedo.RepealRedoManager;
import a.baozouptu.ptu.repealRedo.StepData;
import a.baozouptu.ptu.view.PtuView;

public class CutFragment extends Fragment implements BaseFunction {
    private String TAG = "CutFragment";
    private Context mContext;
    private LinearLayout reset;
    private LinearLayout scale;
    private LinearLayout rotate;
    private PtuView ptuView;
    /**
     * 翻转
     */
    private LinearLayout reversal;
    private CutView cutView;

    @Override
    public void repeal() {
    }

    @Override
    public void redo(StepData sd) {
        ptuView.replaceSourceBm(BitmapTool.getLosslessBitmap(sd.picPath));
    }

    @Override
    public Bitmap getResultBm(float ratio) {
        return cutView.getResultBm();
    }

    @Override
    public StepData getResultData(float ratio) {
        StepData csd = new CutStepData();
        return csd;
    }

    @Override
    public void releaseResource() {
        cutView.releaseResource();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cut, null);
        mContext = getActivity();

        rotate = (LinearLayout) view.findViewById(R.id.cut_rotate);
        scale = (LinearLayout) view.findViewById(R.id.cut_scale);
        reset = (LinearLayout) view.findViewById(R.id.cut_reset);
        reversal = (LinearLayout) view.findViewById(R.id.cut_reversal);
        setClick();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setClick() {
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRotate();
            }
        });
        scale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutView.resetShow();
            }
        });

        reversal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupWindow popWindow = new PopupWindow(mContext);
                final int pad = 10;
                popWindow.setFocusable(true);
                popWindow.setOutsideTouchable(true);  //设置点击屏幕其它地方弹出框消失
                popWindow.setBackgroundDrawable(new BitmapDrawable());

                LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setBackgroundColor(Color.WHITE);
                linearLayout.setPadding(0,pad,0,pad);

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
                        Util.getDrawable(R.drawable.divider_dialog_save_set));


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.addView(vertical, params);
                linearLayout.addView(d1);
                linearLayout.addView(d3);
                linearLayout.addView(d2);
                linearLayout.addView(horizontal, params);

                int[] popWH = new int[2];
                Util.getMesureWH(linearLayout, popWH);
                popWindow.setContentView(linearLayout);
                popWindow.setWidth(view.getWidth());
                popWindow.setHeight(popWH[1]);
                //自己的高度，减去父布局的padding，再减去View的高度
                int top = -view.getHeight()
                        - ((ViewGroup) view.getParent()).getPaddingTop()
                        - popWH[1];
                popWindow.showAsDropDown(view, 0, top);
            }

            private TextView createItem(final int pad) {
                TextView tv = new TextView(mContext);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(20);
                tv.setTextColor(Util.getColor(R.color.text_deep_black));
                return tv;
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

    public void setPtuView(PtuView ptuView) {
        this.ptuView = ptuView;
    }
}
