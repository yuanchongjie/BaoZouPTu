package com.cyl.mytest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class MainActivity extends Activity {

    //浮雕
    EmbossMaskFilter emboss;
    //模糊
    BlurMaskFilter blur;
    TuyaView tuyaView;
    ImageView cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tuyaView = (TuyaView) findViewById(R.id.img);
        cancel = (ImageView) findViewById(R.id.img_1);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuyaView.undo();
            }
        });

        emboss = new EmbossMaskFilter(new float[]
                {1.5f,1.5f,1.5f},0.6f,6 ,4.2f);
        blur = new BlurMaskFilter(8,BlurMaskFilter.Blur.NORMAL);



    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initView() {
//        ptuView = (PtuView) findViewById(R.id.img);
//        LinearLayout lin = new LinearLayout(this);

        DisplayMetrics display = new DisplayMetrics();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//
//        }
//        getWindowManager().getDefaultDisplay().getRealMetrics(display);
        Log.e("ddd0",display.widthPixels+"+==="+display.heightPixels);
//        ptuView = new PtuView(this, display.widthPixels,display.heightPixels);
//        lin.addView(ptuView);
//        setContentView(lin);
        emboss = new EmbossMaskFilter(new float[]
                {1.5f,1.5f,1.5f},0.6f,6 ,4.2f);
        blur = new BlurMaskFilter(8,BlurMaskFilter.Blur.NORMAL);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.red:
               tuyaView.mPaint.setColor(Color.RED);
               item.setChecked(true);
               break;
           case R.id.green:
               tuyaView.mPaint.setColor(Color.GREEN);
               item.setChecked(true);
               break;
           case R.id.blue:
               tuyaView.mPaint.setColor(Color.BLUE);
               item.setChecked(true);
               break;
           case R.id.width_1:
               tuyaView.mPaint.setStrokeWidth(1);
               break;
           case R.id.width_3:
               tuyaView.mPaint.setStrokeWidth(3);
               break;
           case R.id.width_5:
               tuyaView.mPaint.setStrokeWidth(5);
               break;
           case R.id.blur:
               tuyaView.mPaint.setMaskFilter(blur);
               break;
           case R.id.emboss:
               tuyaView.mPaint.setMaskFilter(emboss);
               break;
       }

        return true;
    }
}
