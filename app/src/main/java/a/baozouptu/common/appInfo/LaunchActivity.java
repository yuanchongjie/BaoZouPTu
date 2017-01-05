package a.baozouptu.common.appInfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import a.baozouptu.common.CrashHandler;
import a.baozouptu.common.MainActivity;
import a.baozouptu.common.appInfo.InstallPolicy;
import a.baozouptu.common.dataAndLogic.AllData;
import cn.bmob.v3.Bmob;

import static a.baozouptu.common.dataAndLogic.AllData.appConfig;

/**
 * Created by liuguicen on 2016/8/15.
 *
 * @description
 */
public class LaunchActivity extends AppCompatActivity {
    public static String TAG="LaunchActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());//设置APP运行异常捕捉器

        permission();//首先得申请出所有权限
        AllData.init(getApplicationContext());//再获取或加载好必须的数据
        Bmob.initialize(getApplicationContext(), "3000c4af659e92854854c5b10f0824a2");//再是网络初始化

        new InstallPolicy().processPolicy();//执行第一次安装或更新新版本所需的东西
        startBackgroundService();
        test();
        this.finish();
    }

    /**
     * 启动后台服务，
     * @see AppIntentService
     *<p>1.在后台发送用户使用信息
     */
    private void startBackgroundService() {
        Intent intent=new Intent("start");
        intent.setAction("a.baozouptu.common.appInfo.AppIntentService");
        startService(intent);
    }

    private void test() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("test", "test");
        startActivity(intent);
        Log.e(TAG,"完成时间  "+System.currentTimeMillis());
        this.finish();
    }

    private void permission() {
        //权限请求
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()));
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(mPermissionList, 100);
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

