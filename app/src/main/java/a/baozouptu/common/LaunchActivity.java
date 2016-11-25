package a.baozouptu.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import a.baozouptu.base.MainActivity;
import a.baozouptu.base.dataAndLogic.AllData;

/**
 * Created by liuguicen on 2016/8/15.
 *
 * @description
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new InstallPolicy().processPolicy();
//        Bmob.initialize(this,"3000c4af659e92854854c5b10f0824a2");
        permission();
        setCrashHandler();
        test();
        this.finish();
    }

    private void setCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }

    private void test() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("test", "test");
        startActivity(intent);
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

