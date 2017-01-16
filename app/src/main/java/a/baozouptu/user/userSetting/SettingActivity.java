package a.baozouptu.user.userSetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import a.baozouptu.R;
import a.baozouptu.common.BaseActivity;

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
        mPresenter = new SettingPresenter(new SettingDataSouceImpl(getApplicationContext()), this);
        switch_sendShortcut = (SwitchCompat) findViewById(R.id.setting_shortcut_switch);
        switch_sendShortcut.setClickable(false);
        switch_sendShortcutExit = (SwitchCompat) findViewById(R.id.setting_shortcut_exit_switch);
        switch_sendShortcutExit.setClickable(false);
        switch_sharedWithout=(SwitchCompat)findViewById(R.id.setting_share_without_switch);
        switch_sharedWithout.setClickable(false);

        layout_sendShortCutNotify = (RelativeLayout) findViewById(R.id.setting_shortcut_notify);
        layout_sendShortCutNotifyExit = (RelativeLayout) findViewById(R.id.setting_shortcut_notitfy_exit);
        layout_share_without_label=(RelativeLayout)findViewById(R.id.setting_share_without_label);
        layout_good_comment = (RelativeLayout) findViewById(R.id.setting_good_comment);
        layout_comment_feedback = (RelativeLayout) findViewById(R.id.setting_layout_feedback);
        layout_about_app = (RelativeLayout) findViewById(R.id.setting_about_app);
        textClearCache = (TextView)findViewById(R.id.setting_clear_cache);
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
                startActivity(new Intent(SettingActivity.this,FeedbackActivity.class));
            }
        });
        layout_about_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,AboutAppActivity.class));
            }
        });
        layout_sendShortCutNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_sendShortcut.toggle();
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
                mPresenter.clearAppCache();
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
    public void setPresenter(SettingContract.Presenter presenter) {

    }
}
