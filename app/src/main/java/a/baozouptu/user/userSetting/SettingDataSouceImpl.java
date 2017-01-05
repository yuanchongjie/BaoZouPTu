package a.baozouptu.user.userSetting;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 *
 */

public class SettingDataSouceImpl implements SettingDataSource {
    Context appContext;
    private static final String SEND_SHORTCUT_NOTIFY = "send_shortcut_notify";
    private static final String SEND_SHORTCUT_NOTIFY_EXIT = "send_shortcut_notify_exit";

    /**
     * 传入app的context
     */
    SettingDataSouceImpl(@NonNull Context appContext) {
        this.appContext = appContext;
        sp = appContext.getSharedPreferences("user_config", Context.MODE_PRIVATE);
    }

    SharedPreferences sp;

    @Override
    public void saveSendShortCutNotify(boolean isSend) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SEND_SHORTCUT_NOTIFY, isSend);
        editor.apply();
    }

    @Override
    public void saveSendShortCutNotifyExit(boolean isSend) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SEND_SHORTCUT_NOTIFY_EXIT, isSend);
        editor.apply();
    }

    @Override
    public boolean getSendShortcutNotify() {
        return sp.getBoolean(SEND_SHORTCUT_NOTIFY, true);
    }

    @Override
    public boolean getSendShortcutNotifyExit() {
        return sp.getBoolean(SEND_SHORTCUT_NOTIFY_EXIT, true);
    }
}
