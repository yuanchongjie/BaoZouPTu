package a.baozouptu.ptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import a.baozouptu.R;
import a.baozouptu.base.util.Util;

public class PtuTopRelativeLayout extends RelativeLayout {
    Context mContext;
    private ImageButton cancel;
    private ImageButton sure;
    private LayoutParams rightParams;
    private ImageButton repealBtn;
    private Bitmap canRepealBm;
    private Bitmap canotRepealBm;
    private ImageButton redoBtn;
    private Bitmap canotRedoBm;
    private Bitmap canRedoBm;
    private LayoutParams leftParams;
    private LinearLayout returnLayout;
    private TextView save;
    private LayoutParams saveParams;

    public PtuTopRelativeLayout(Context context) {
        super(context);
        mContext = context;
    }

    public PtuTopRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    private ImageButton createBaseToolbarBtn(int width) {
        ImageButton button = new ImageButton(mContext);
        button.setBackground(getResources().getDrawable(R.drawable.ptu_top_btn_background));
        button.setLayoutParams(new ViewGroup.LayoutParams(width, width));
        return button;
    }

    /**
     * @param top_btn_width 宽度
     * @param dividerWidth  左边距
     * @return
     */
    public ImageButton createCancel(int top_btn_width, int dividerWidth) {
        cancel = createBaseToolbarBtn(top_btn_width);
        cancel.setImageBitmap(IconBitmapCreator.createCancelBitmap(mContext,
                top_btn_width,
                Util.getColor(mContext, R.color.text_color1)));
        if (leftParams == null) {
            leftParams = new LayoutParams(top_btn_width, top_btn_width);
            leftParams.setMargins(dividerWidth, 0, 0, 0);
            leftParams.addRule(RelativeLayout.CENTER_VERTICAL);
            leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            leftParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        }
        return cancel;
    }

    public void addCancel() {
        addView(cancel, leftParams);
    }

    public void removeCancle() {
        removeView(cancel);
    }

    public View createReturn(int top_btn_width, int dividerWidth) {
        returnLayout = (LinearLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.layout_ptu_return, null);

        ImageView returnImage = (ImageView) returnLayout.findViewById(R.id.ptu_return_image);
        returnImage.setImageBitmap(IconBitmapCreator.createReturnIcon(mContext,
                top_btn_width,
                Util.getColor(mContext, R.color.text_color1)));

        return returnLayout;
    }

    public void addReturn() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(returnLayout, params);
    }

    public void removeReturn() {
        removeView(returnLayout);
    }

    public View createSaveSet(int top_btn_width, int dividerWidth) {
        save = new TextView(mContext);
        save.setText("保存");
        save.setTextSize(17);
        save.setGravity(Gravity.CENTER);
        saveParams = new LayoutParams(top_btn_width, top_btn_width);
        saveParams.setMargins(0, 0, Util.dp2Px(8), 0);
        saveParams.addRule(RelativeLayout.CENTER_VERTICAL);
        saveParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        saveParams.addRule(RelativeLayout.ALIGN_END);
        return save;
    }

    public void addSaveSet() {
        addView(save, saveParams);
    }

    public void removeSaveSet() {
        removeView(save);
    }

    public ImageButton createSure(int top_btn_width, int dividerWidth) {
//        添加最右边的按钮
        sure = createBaseToolbarBtn(top_btn_width);
        sure.setImageBitmap(IconBitmapCreator.createSureBitmap(mContext,
                top_btn_width,
                Util.getColor(mContext, R.color.text_color1)));
        if (rightParams == null) {
            rightParams = new LayoutParams(top_btn_width, top_btn_width);
            rightParams.setMargins(0, 0, dividerWidth, 0);
            rightParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rightParams.addRule(RelativeLayout.ALIGN_END);
        }
        return sure;
    }

    public void addSure() {
        addView(sure, rightParams);
    }

    public void removeSure() {
        removeView(sure);
    }

    /**
     * @param canRepeal 能否撤销
     */
    public void setRepealBtnColor(boolean canRepeal) {
        if (canRepeal) {
            repealBtn.setImageBitmap(canRepealBm);
        } else {
            repealBtn.setImageBitmap(canotRepealBm);
        }
    }

    /**
     * 能否重做
     */
    public void setRedoBtnColor(boolean canRedo) {
        if (canRedo) {
            redoBtn.setImageBitmap(canRedoBm);
        } else {
            redoBtn.setImageBitmap(canotRedoBm);
        }
    }

    public ImageButton[] createCenterView(int topBtnWidth, int dividerWidth) {
        ImageButton[] btns = new ImageButton[3];
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ptu_top_center);
        repealBtn = createBaseToolbarBtn(topBtnWidth);
        canRepealBm = IconBitmapCreator.createRepealBitmap(mContext,
                topBtnWidth,
                Util.getColor(mContext, R.color.can_repeal_redo));
        canotRepealBm = IconBitmapCreator.createRepealBitmap(mContext,
                topBtnWidth,
                Util.getColor(mContext, R.color.canot_repeal_redo));
        repealBtn.setImageBitmap(canotRepealBm);
        btns[0] = repealBtn;

        linearLayout.addView(repealBtn, new ViewGroup.LayoutParams(topBtnWidth, topBtnWidth));

        //设置repeal和redo的button
        addDivider(linearLayout, dividerWidth);
        redoBtn = createBaseToolbarBtn(topBtnWidth);
        canRedoBm = IconBitmapCreator.createRedoBitmap(mContext,
                topBtnWidth,
                Util.getColor(mContext, R.color.can_repeal_redo));
        canotRedoBm = IconBitmapCreator.createRedoBitmap(mContext,
                topBtnWidth,
                Util.getColor(mContext, R.color.canot_repeal_redo));

        redoBtn.setImageBitmap(canotRedoBm);
        btns[1] = redoBtn;

        linearLayout.addView(redoBtn, new ViewGroup.LayoutParams(topBtnWidth, topBtnWidth));

        //去发送按钮
        addDivider(linearLayout, dividerWidth);
        ImageButton goSend = createBaseToolbarBtn(topBtnWidth);
        goSend.setImageBitmap(IconBitmapCreator.createSendBitmap(mContext,
                topBtnWidth,
                Util.getColor(mContext, R.color.text_color1)));
        linearLayout.addView(goSend, new ViewGroup.LayoutParams(topBtnWidth, topBtnWidth));
        btns[2] = goSend;
        return btns;
    }

    private void addDivider(LinearLayout linear, int width) {
        FrameLayout fm = new FrameLayout(mContext);
        fm.setLayoutParams(new ViewGroup.LayoutParams(
                width, ViewGroup.LayoutParams.MATCH_PARENT));
        linear.addView(fm);
    }
}
