package a.baozouptu.chosePicture.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.chosePicture.ChoosePicContract;
import a.baozouptu.common.util.Util;

/**
 * Created by LiuGuicen on 2017/1/17 0017.
 */

public class LongPicPopupWindow {

    /**
     * @param view     被点击的view
     * @param position 注意是常用图片时position要先转换
     * @return
     */
    public static boolean setPicPopWindow(final ChoosePicContract.PicPresenter presenter,
                                          final ChoosePicContract.View activity,
                                          View view,
                                          final int position) {
        final PopupWindow popWindowFile = new PopupWindow((Context) activity);
        LinearLayout linearLayout = new LinearLayout((Context) activity);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setDividerPadding(10);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(Util.getDrawable(R.drawable.divider_picture_opration));
        linearLayout.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT));
        linearLayout.setPadding(Util.dp2Px(2), Util.dp2Px(2), Util.dp2Px(2), Util.dp2Px(2));
        TextView frequentlyTextView = new TextView((Context) activity);
        frequentlyTextView.setGravity(Gravity.CENTER);
        frequentlyTextView.setWidth(view.getWidth() / 2);
        frequentlyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        final String path = presenter.getCurrentPath(position);
        if (presenter.isInPrefer(path)) {
            frequentlyTextView.setText("取消");
            final int finalPosition = position;
            frequentlyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindowFile.dismiss();
                    presenter.deletePreferPath(path, finalPosition);
                }
            });
        } else {
            frequentlyTextView.setText("喜爱");
            frequentlyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindowFile.dismiss();
                    presenter.addPreferPath(path);
                }
            });
        }
        frequentlyTextView.setTextSize(22);
        frequentlyTextView.setTextColor(Util.getColor(R.color.text_deep_black));

        linearLayout.addView(frequentlyTextView);

        TextView deleteTextView = new TextView((Context) activity);

        deleteTextView.setGravity(Gravity.CENTER);
        deleteTextView.setWidth(view.getWidth() / 2);
        deleteTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        deleteTextView.setText("删除");
        deleteTextView.setTextSize(22);
        deleteTextView.setTextColor(Util.getColor(R.color.text_deep_black));

        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindowFile.dismiss();
                activity.deleteOnePic(presenter.getCurrentPath(position));
            }
        });
        linearLayout.addView(deleteTextView);


        int[] popWH = new int[2];
        Util.getMesureWH(linearLayout, popWH);
        popWindowFile.setContentView(linearLayout);
        popWindowFile.setWidth(view.getWidth());
        popWindowFile.setHeight(popWH[1]);
        popWindowFile.setFocusable(true);
        popWindowFile.setBackgroundDrawable(Util.getDrawable(
                R.drawable.background_pic_operation));
        popWindowFile.showAsDropDown(view, 0,
                -view.getHeight() + (int) (((Context) activity).getResources().
                        getDimension(R.dimen.choose_pic_item_divider) * 3 + 0.5f));
        return true;
    }

}
