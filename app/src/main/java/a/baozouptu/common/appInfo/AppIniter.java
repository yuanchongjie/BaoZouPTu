package a.baozouptu.common.appInfo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import a.baozouptu.common.CrashHandler;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.user.userSetting.SettingDataSourceImpl;
import cn.bmob.v3.Bmob;

/**
 * Created by LiuGuicen on 2017/1/18 0018.
 */

public class AppIniter {
    Activity firstAc;

    public AppIniter(Activity activity) {
        firstAc = activity;
    }

    public void init() {
        permission();//首先得申请出所有权限
        //初始化全局数据
        AllData.appConfig = new AppConfig(MyApplication.appContext);
        AllData.hasReadConfig = new HasReadConfig();
        AllData.settingDataSource = new SettingDataSourceImpl(MyApplication.appContext);
        Bmob.initialize(MyApplication.appContext, "3000c4af659e92854854c5b10f0824a2");//再是网络初始化

        new InstallPolicy().processPolicy();//执行第一次安装或更新新版本所需的东西
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());//设置APP运行异常捕捉器
        startBackgroundService();
        Log.e("------------", "init: 应用初始化成功");
    }

    /**
     * 启动后台服务，
     *
     * @see AppIntentService
     * <p>1.在后台发送用户使用信息
     */
    private void startBackgroundService() {
        Intent intent = new Intent("start");
        intent.setAction("a.baozouptu.common.appInfo.AppIntentService");
        firstAc.startService(intent);
    }

    private void permission() {
        //权限请求
        PackageManager pm = firstAc.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, firstAc.getPackageName()));
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                firstAc.requestPermissions(mPermissionList, 100);
            }
        }
    }

    //android 6.0权限请求
    String[] mPermissionList = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };
}
