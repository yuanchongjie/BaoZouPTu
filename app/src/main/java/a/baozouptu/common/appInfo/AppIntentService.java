package a.baozouptu.common.appInfo;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import a.baozouptu.common.CrashLog;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.user.DeviceInfos;

/**
 * Created by LiuGuicen on 2016/12/25 0025.
 */

public class AppIntentService extends IntentService {
    final static String TAG = "AppIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AppIntentService() {
        super("AppIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent: 后台服务启动了");
        //上传用户活跃信息信息
        new SimpleUser().myUpdate();

        //上传设备信息
        if (!AllData.appConfig.hasSendDeviceInfos())
            new DeviceInfos().serviceCreate();

        //发送一下crash信息
        if (CrashLog.hasNew()) {
            new CrashLog().serviceCreate();
        }
    }

}
