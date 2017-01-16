package a.baozouptu.user.userSetting;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.CustomToast;

/**
 * Created by LiuGuicen on 2017/1/5 0005.
 */

public class SettingPresenter implements SettingContract.Presenter {
    SettingDataSource dataSource;
    SettingContract.View view;
    Context mContext;

    SettingPresenter(SettingDataSource settingDataSource, SettingContract.View settingView) {
        dataSource = settingDataSource;
        view = settingView;
        mContext = (Context) view;
    }

    @Override
    public void saveSendShortCutExit(boolean isSend) {
        dataSource.saveSendShortCutNotifyExit(isSend);
    }

    @Override
    public void saveSharedWithout(boolean isWith) {
        dataSource.saveSharedWithout(isWith);
    }


    @Override
    public void onShortCutNotifyChanged(boolean checked) {
        dataSource.saveSendShortCutNotify(checked);
        if (!checked) {
            view.switchSendShortCutNotifyExit(false);
            dataSource.saveSendShortCutNotifyExit(false);
        }
    }

    private String getAppCache() {
        return "清除缓存" + "(" + dataSource.getAppCacheSize() + "M)";
    }

    @Override
    public void clearAppCache() {
        dataSource.clearAppCache();
    }

    @Override
    public void gotoMark() {
        goToMarket(mContext, mContext.getPackageName());
    }

    /**
     * @param packageName 本应用的包名称，
     */
    private void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            //默认弹出所有应用市场的选择框，这里设置为qq
            goToMarket.setClassName("com.tencent.android.qqdownloader", "com.tencent.pangu.link.LinkProxyActivity");
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            CustomToast.makeText((Activity) view, "未安装相关应用市场", Toast.LENGTH_SHORT).show();
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
        boolean sharedWithout = dataSource.getSharedWithout();
        view.switchSharedWithout(sharedWithout);
        view.showAppCache(getAppCache());
    }
}
