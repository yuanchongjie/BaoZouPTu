package a.baozouptu.user.userSetting;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import a.baozouptu.R;
import a.baozouptu.common.BaseActivity;
import a.baozouptu.common.util.CustomToast;

/**
 * Created by LiuGuicen on 2017/1/4 0004.
 */

public class SettingActivity extends BaseActivity implements SettingContract.View {
    SettingContract.Presenter mPresenter;
    private SwitchCompat switch_sendShortcut;
    private SwitchCompat switch_sendShortcutExit;
    private SwitchCompat switch_sharedWithout;
    private RelativeLayout layout_sendShortCutNotify;
    private RelativeLayout layout_sendShortCutNotifyExit;
    private RelativeLayout layout_share_without_label;
    private RelativeLayout layout_good_comment;
    private RelativeLayout layout_comment_feedback;
    private RelativeLayout layout_about_app;
    private TextView textClearCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPresenter = new SettingPresenter(new SettingDataSourceImpl(getApplicationContext()), this);
        switch_sendShortcut = (SwitchCompat) findViewById(R.id.setting_shortcut_switch);
        switch_sendShortcut.setClickable(false);
        switch_sendShortcutExit = (SwitchCompat) findViewById(R.id.setting_shortcut_exit_switch);
        switch_sendShortcutExit.setClickable(false);
        switch_sharedWithout = (SwitchCompat) findViewById(R.id.setting_share_without_switch);
        switch_sharedWithout.setClickable(false);

        layout_sendShortCutNotify = (RelativeLayout) findViewById(R.id.setting_shortcut_notify);
        layout_sendShortCutNotifyExit = (RelativeLayout) findViewById(R.id.setting_shortcut_notitfy_exit);
        layout_share_without_label = (RelativeLayout) findViewById(R.id.setting_share_without_label);
        layout_good_comment = (RelativeLayout) findViewById(R.id.setting_good_comment);
        layout_comment_feedback = (RelativeLayout) findViewById(R.id.setting_layout_feedback);
        layout_about_app = (RelativeLayout) findViewById(R.id.setting_about_app);
        textClearCache = (TextView) findViewById(R.id.setting_clear_cache);
        setClick();
    }

    private void setClick() {
        findViewById(R.id.setting_return_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_good_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.gotoMark();
            }

        });
        layout_comment_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, FeedBackActivity.class));
            }
        });
        layout_about_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, AboutAppActivity.class));
            }
        });
        layout_sendShortCutNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_sendShortcut.toggle();
                NotificationManager nm = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(0);
                mPresenter.onShortCutNotifyChanged(switch_sendShortcut.isChecked());
            }
        });
        layout_sendShortCutNotifyExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_sendShortcutExit.toggle();
                mPresenter.saveSendShortCutExit(switch_sendShortcutExit.isChecked());
            }
        });
        layout_share_without_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_sharedWithout.toggle();
                mPresenter.saveSharedWithout(switch_sharedWithout.isChecked());
            }
        });
        textClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clearAppData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void switchSendShortCutNotify(boolean isSend) {
        switch_sendShortcut.setChecked(isSend);
    }

    @Override
    public void switchSendShortCutNotifyExit(boolean isSend) {
        switch_sendShortcutExit.setChecked(isSend);
    }

    @Override
    public void switchSharedWithout(boolean isWith) {
        switch_sharedWithout.setChecked(isWith);
    }

    @Override
    public void showAppCache(String cacheString) {
        textClearCache.setText(cacheString);
    }

    @Override
    public void showClearDialog(String[] infos, boolean[] preChosen) {
        final boolean[] userChosenItems = Arrays.copyOf(preChosen, preChosen.length);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("清除缓存")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.realClearData(userChosenItems);
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMultiChoiceItems(infos, preChosen, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        userChosenItems[which] = isChecked;
                    }
                })
                .create()
                .show();
    }

    @Override
    public void showClearResult(String res) {
        CustomToast.makeText(this, res, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(SettingContract.Presenter presenter) {
    }
}
