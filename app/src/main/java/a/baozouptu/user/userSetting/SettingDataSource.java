package a.baozouptu.user.userSetting;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 * 通知数据的接口
 */

public interface SettingDataSource {
    void saveSendShortCutNotify(boolean isSend);
    /**
     * 应用退出后是否仍发送通知
     */
    void saveSendShortCutNotifyExit(boolean isSend);
    void saveSharedWithout(boolean isWith);
    boolean getSendShortcutNotify();
    boolean getSendShortcutNotifyExit();
    boolean getSharedWithout();
    float getAppCacheSize();
    void clearAppCache();
}
