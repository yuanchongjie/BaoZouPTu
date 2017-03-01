package a.baozouptu.common.appInfo;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import a.baozouptu.common.CrashHandler;
import a.baozouptu.common.MainActivity;
import a.baozouptu.common.dataAndLogic.AllData;
import a.baozouptu.common.util.Util;
import cn.bmob.v3.Bmob;

/**
 * 在mainifest中使用android:name=".MyApplication"，系统将会创建myapplication替代一般的application
 */
public class MyApplication extends Application {
    final static String TAG="MyApplication";
    public static MyApplication appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }


}
