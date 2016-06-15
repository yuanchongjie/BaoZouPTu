package a.baozouptu.control;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import a.baozouptu.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int[] fab = {R.id.fab, R.id.fab1, R.id.fab2, R.id.fab3};
    private FloatingActionButton[] fab_btn = new FloatingActionButton[fab.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///// 第一步：获取NotificationManager
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

///// 第二步：定义Notification
        Intent intent = new Intent(this, ShowPictureActivity.class);
        intent.putExtra("myFlag", "notify");
//PendingIntent是待执行的Intent
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentText("添加文字")
                .setSmallIcon(R.mipmap.icon2).setContentIntent(pi)
                .build();
        notification.flags = Notification.FLAG_NO_CLEAR;

/////第三步：启动通知栏，第一个参数是一个通知的唯一标识
        nm.notify(0, notification);

//关闭通知
//nm.cancel(0);
        Intent intent1 = new Intent(this, ShowPictureActivity.class);
        intent1.putExtra("myFlag", "hahah");
        startActivity(intent1);
        /*setContentView(R.layout.activity_main);

        initToolbar();

        initview();*/

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
                Intent intent = new Intent(this, ShowPictureActivity.class);
                startActivity(intent);
                break;
            case R.id.fab2:
                Snackbar.make(v, "手绘图主要是自己画个简单图保存为表情", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.fab3:
                Snackbar.make(v, "自拍相机主要", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }

    }
}
