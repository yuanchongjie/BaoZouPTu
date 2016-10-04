package a.baozouptu.ptu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private LinearLayout saveLayout;
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
        button.setBackground(Util.getDrawable(R.drawable.btn_pressable_background_oval));
        return button;
    }

    /**
     * @param top_btn_width 宽度
     * @param dividerWidth  左边距
     * @return
     */
    public ImageButton createCancel(int top_btn_width, int dividerWidth) {
        cancel = createBaseToolbarBtn(top_btn_width);
        cancel.setImageBitmap(IconBitmapCreator.createCancelBitmap(
                top_btn_width,
                Util.getColor(R.color.cancel_btn)));
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
        if (cancel.getParent() != null) {
            ((ViewGroup) cancel.getParent()).removeView(cancel);
        }
        addView(cancel, leftParams);
    }

    public void removeCancel() {
        removeView(cancel);
    }

    public View createReturn(int top_btn_width, int dividerWidth) {
        returnLayout = (LinearLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.layout_ptu_return, null);
        returnLayout.setBackground(Util.getDrawable(R.drawable.btn_pressable_background_rectangle));
        ImageView returnImage = (ImageView) returnLayout.findViewById(R.id.ptu_return_image);
        returnImage.setImageBitmap(IconBitmapCreator.createReturnIcon(
                top_btn_width,
                Util.getColor(R.color.white)));

        return returnLayout;
    }

    public void addReturn() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(Util.dp2Px(12), 0, 0, 0);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        if (returnLayout.getParent() != null) {
            ((ViewGroup) returnLayout.getParent()).removeView(returnLayout);
        }
        addView(returnLayout, params);
    }

    public void removeReturn() {
        removeView(returnLayout);
    }

    public View createSaveSet(int top_btn_width, int dividerWidth) {
        saveLayout = (LinearLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.layout_ptu_save_set, null);
        saveLayout.setBackground(Util.getDrawable(R.drawable.btn_pressable_background_rectangle));
        return saveLayout;
    }

    public void addSaveSet() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.setMargins(0, 0, Util.dp2Px(16), 0);
        if (saveLayout.getParent() != null) {
            ((ViewGroup) saveLayout.getParent()).removeView(saveLayout);
        }
        addView(saveLayout, params);
    }

    public void removeSaveSet() {
        removeView(saveLayout);
    }

    public ImageButton createSure(int top_btn_width, int dividerWidth) {
//        添加最右边的按钮
        sure = createBaseToolbarBtn(top_btn_width);
        sure.setImageBitmap(IconBitmapCreator.createSureBitmap(
                top_btn_width,
                Util.getColor(R.color.sure_btn)));
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
        if (sure.getParent() != null) {
            ((ViewGroup) sure.getParent()).removeView(sure);
        }
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
        canRepealBm = IconBitmapCreator.createRepealBitmap(
                topBtnWidth,
                Util.getColor(R.color.can_repeal_redo));
        canotRepealBm = IconBitmapCreator.createRepealBitmap(
                topBtnWidth,
                Util.getColor(R.color.canot_repeal_redo));
        repealBtn.setImageBitmap(canotRepealBm);
        btns[0] = repealBtn;
        linearLayout.addView(repealBtn, new ViewGroup.LayoutParams(topBtnWidth, topBtnWidth));

        //设置repeal和redo的button
        addDivider(linearLayout, dividerWidth);
        redoBtn = createBaseToolbarBtn(topBtnWidth);
        canRedoBm = IconBitmapCreator.createRedoBitmap(
                topBtnWidth,
                Util.getColor(R.color.can_repeal_redo));
        canotRedoBm = IconBitmapCreator.createRedoBitmap(
                topBtnWidth,
                Util.getColor(R.color.canot_repeal_redo));

        redoBtn.setImageBitmap(canotRedoBm);
        btns[1] = redoBtn;

        linearLayout.addView(redoBtn, new ViewGroup.LayoutParams(topBtnWidth, topBtnWidth));

        //去发送按钮
        addDivider(linearLayout, dividerWidth);
        ImageButton goSend = createBaseToolbarBtn(topBtnWidth);
        goSend.setImageBitmap(IconBitmapCreator.createSendBitmap(
                topBtnWidth,
                Util.getColor(R.color.can_repeal_redo)));
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
