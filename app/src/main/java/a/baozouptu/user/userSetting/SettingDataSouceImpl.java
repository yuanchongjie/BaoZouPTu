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
    private static final String SHARED_WHTHOUT_LABEL="shared_without_label";

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
    public void saveSharedWithout(boolean isWith) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SHARED_WHTHOUT_LABEL, isWith);
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

    /**
     * @return 默认是要带 false
     */
    @Override
    public boolean getSharedWithout() {
        return sp.getBoolean(SHARED_WHTHOUT_LABEL,false);//默认是要带 false
    }

    @Override
    public float getAppCacheSize() {
        return 0;
    }

    @Override
    public void clearAppCache() {

    }
}
