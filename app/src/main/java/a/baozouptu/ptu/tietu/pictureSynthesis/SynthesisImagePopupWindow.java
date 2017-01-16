package a.baozouptu.ptu.tietu.pictureSynthesis;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import a.baozouptu.R;
import a.baozouptu.common.util.Util;
import a.baozouptu.user.userSetting.SettingActivity;
import a.baozouptu.user.userSetting.SettingContract;

/**
 * Created by LiuGuicen on 2017/1/14 0014.
 */

public class SynthesisImagePopupWindow {

    private PopupWindow popupWindow;

    public void show(View v, final Context context, View.OnClickListener clickListener) {
        popupWindow = new PopupWindow(context);
        View contentView = View.inflate(context, R.layout.layout_btn_synthsis, null);
        popupWindow.setContentView(contentView);
        popupWindow.setBackgroundDrawable(null);
        int[] wh=new int[2];
        Util.getMesureWH(contentView,wh);
        float offsetX = v.getWidth() - wh[0] - Util.dp2Px(8);
        float offsetY = v.getHeight() + wh[1] + Util.dp2Px(15);
        popupWindow.showAsDropDown(v, (int) (offsetX + 0.5f), -(int) (offsetY + 0.5f));
        contentView.setOnClickListener(clickListener);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
}