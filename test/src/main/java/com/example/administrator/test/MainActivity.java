package com.example.administrator.test;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button bb = new Button(getApplicationContext());
        bb.setHeight(100);
        bb.setWidth(100);
        final WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

        /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途请参考SDK文档
         */
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;   //这里是关键，你也可以试试2003
        wmParams.format = 1;
        /**
         *这里的flags也很关键
         *代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;
         *40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
         */
        wmParams.flags = 40;
        wmParams.width = 100;
        wmParams.height = 100;
        wm.addView(bb, wmParams);  //创建View
        Button btn1 = (Button) findViewById(R.id.ptu_view);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   wm.removeViewImmediate(bb);
                TextView tv=new TextView(getApplicationContext());
                tv.setText("6666666");
                tv.setHeight(200);
                tv.setWidth(200);
                wmParams.width=200;
                wmParams.height=200;
                wm.addView(tv,wmParams);
            }
        });
    }

}