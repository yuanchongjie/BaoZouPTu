package a.baozouptu.base;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import a.baozouptu.R;
import a.baozouptu.base.dataAndLogic.AllData;
import a.baozouptu.base.dataAndLogic.MyDatabase;
import a.baozouptu.base.util.Util;
import a.baozouptu.chosePicture.ChosePictureActivity;
import a.baozouptu.chosePicture.ProcessUsuallyPicPath;
import a.baozouptu.ptu.PtuActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_STOREGE = 0;
    public static String TAG = MainActivity.class.getSimpleName();
    private int[] fab = {R.id.fab, R.id.fab1, R.id.fab2, R.id.fab3};
    private FloatingActionButton[] fab_btn = new FloatingActionButton[fab.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//关闭通知
//nm.stop(0);

        boolean isTest = true;
        if (isTest) {
         //   new NetWorkTest().test();
            test();
            sendNotify();
        } else {
            setContentView(R.layout.activity_main);
            initData();
            initToolbar();

            initview();
            sendNotify();
            Util.P.le(TAG, "OnCreate完成");
        }
    }

    private void initData() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        AllData.screenWidth = metric.widthPixels; // 屏幕宽度（像素）
        AllData.screenHeight = metric.heightPixels;
    }

    /**
     * 傻逼360开发平台
     */
    private void test() {

        //  testDB1();
        //  testDB();
        initData();
        //  if (checkVersion()) {
        Intent intent = new Intent(this, ChosePictureActivity.class);
        intent.putExtra("test", "test");
        startActivityForResult(intent, 0);
        finish();
        //  }
    }


    private void sendNotify() {
        // 第一步：获取NotificationManager
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // 第二步：定义Notification
        Intent intent = new Intent(this, ChosePictureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction("notify_ptu");
        //PendingIntent是待执行的Intent
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.icon)
                .setContentIntent(pi)
                .build();
        // 当用户下来通知栏时候看到的就是RemoteViews中自定义的Notification布局
        RemoteViews contentView = new RemoteViews(this.getPackageName(),
                R.layout.layout_notification);
        contentView.setImageViewResource(R.id.notify_icon, R.mipmap.icon);
        contentView.setImageViewResource(R.id.notify_make_image, R.mipmap.notify_make);
        contentView.setTextViewText(R.id.notify_make_name,
                getResources().getString(R.string.make_expression));

        contentView.setImageViewResource(R.id.notify_latest_image, R.mipmap.notify_latest);
        contentView.setTextViewText(R.id.notify_latest_name,
                getResources().getString(R.string.latest_pic));


        Intent latestIntent = new Intent(this, PtuActivity.class);
        latestIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        latestIntent.setAction("notify_latest");
        //PendingIntent是待执行的Intent
        PendingIntent piLatest = PendingIntent.getActivity(this, 0, latestIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        contentView.setOnClickPendingIntent(R.id.notify_layout_latest, piLatest);
        notification.contentView = contentView;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        //第三步：启动通知栏，第一个参数是一个通知的唯一标识
        nm.notify(0, notification);
        Util.P.le(TAG, "发送通知完成");
    }

    private void testDB1() {
        ProcessUsuallyPicPath ups = new ProcessUsuallyPicPath(this);
        ups.addUsedPath("1111");
        ups.addUsedPath("2222");
        ups.addUsedPath("3333");
        ups.addUsedPath("4444");
        ups.addUsedPath("cccc");
        ups.addUsedPath("dddd");
        ups.addUsedPath("eeee");
        ups.addUsedPath("hhhh");
        ups.addUsedPath("iiii");
        ups.addUsedPath("jjjj");
    }

    private void testDB() {
        MyDatabase mdb = MyDatabase.getInstance(this);
        List<String> paths = new ArrayList<>();
        try {
            mdb.queryAllUsedPicWithTime(paths);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mdb.close();
        }
    }

    private void initview() {

        for (int i = 0; i < fab.length; i++) {
            fab_btn[i] = (FloatingActionButton) findViewById(fab[i]);
            fab_btn[i].setOnClickListener(this);
        }

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "点击了设置", Toast.LENGTH_SHORT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "我的表情包主要显示 保存后的表情", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.fab1:
                Intent intent = new Intent(this, ChosePictureActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.fab2:
                break;
            case R.id.fab3:
                Snackbar.make(v, "自拍相机主要", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        String action = data.getAction();
        if (action != null && action.equals("finish")) {
            setResult(0, new Intent(action));
            finish();
            overridePendingTransition(0, R.anim.go_send_exit);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_STOREGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, ChosePictureActivity.class);
                intent.setAction("test");
                startActivityForResult(intent, 0);
                finish();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * @return 返回是否可以直接使用权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int c = ContextCompat.checkSelfPermission(this, android.Manifest.permission_group.STORAGE);
            if (c != PackageManager.PERMISSION_GRANTED) {
               /* if (!shouldShowRequestPermissionRationale(android.Manifest.permission_group.STORAGE)) {
                    DialogFactory.noTitle(MainActivity.this, "你需要允许此权限",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                                            MY_PERMISSIONS_STOREGE);
                                }
                            });
                    return false;
                } else*/
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission_group.STORAGE}, MY_PERMISSIONS_STOREGE);
                return false;
            }
        }
        return true;
    }
}
