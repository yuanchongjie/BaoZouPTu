package a.baozouptu.chosePicture;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import a.baozouptu.R;
import a.baozouptu.common.util.Util;
import a.baozouptu.user.userSetting.SettingActivity;

/**
 * Created by LiuGuicen on 2017/1/7 0007.
 */

public class TosettingPopupWindow {
    public static void show(View v, final Context context) {
        final PopupWindow popupWindow = new PopupWindow(context);
        View contentView=View.inflate(context, R.layout.layout_window_to_setting, null);
        popupWindow.setContentView(contentView);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(Util.getDrawable(
                R.drawable.background_round_corner_test));
        float offsetY=((ViewGroup)v.getParent()).getHeight()-v.getY()-v.getHeight();
        popupWindow.showAsDropDown(v, 0, (int)(offsetY+0.5f));
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                context.startActivity(new Intent(context,SettingActivity.class));
            }
        });
    }

}
