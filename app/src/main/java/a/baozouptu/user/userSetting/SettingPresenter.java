package a.baozouptu.user.userSetting;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class SettingPresenter implements SettingContract.Presenter {
    SettingDataSource dataSource;
    SettingContract.View view;

    SettingPresenter(SettingDataSource settingDataSource, SettingContract.View settingView) {
        dataSource = settingDataSource;
        view = settingView;
    }

    @Override
    public void saveSendShortCutExit(boolean isSend) {
        dataSource.saveSendShortCutNotifyExit(isSend);
    }


    @Override
    public void onShortCutNotifyChanged(boolean checked) {
        dataSource.saveSendShortCutNotify(checked);
        if (!checked) {
            view.switchSendShortCutNotifyExit(false);
            dataSource.saveSendShortCutNotifyExit(false);
        }
    }

    @Override
    public void start() {
        boolean sendShortcut = dataSource.getSendShortcutNotify();
        if (sendShortcut) {
            view.switchSendShortCutNotify(sendShortcut);
            view.switchSendShortCutNotifyExit(dataSource.getSendShortcutNotifyExit());
        } else {
            view.switchSendShortCutNotify(false);
            view.switchSendShortCutNotifyExit(false);
        }
    }
}
