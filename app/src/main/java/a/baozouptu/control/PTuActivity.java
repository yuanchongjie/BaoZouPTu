package a.baozouptu.control;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import a.baozouptu.R;
import a.baozouptu.view.FloatTextView;
import a.baozouptu.view.PtuFrameLayout;
import a.baozouptu.view.PtuView;

public class PTuActivity extends Activity implements MainFunctionFragment.Listen {
    /**
     * 主功能的fragment
     */
    MainFunctionFragment mainFrag;
    FragmentTransaction ft;
    FragmentManager fm;
    private MainFunctionFragment fragMain;
    private AddTextFragment fragText;
    private PtuView ptuView;
    private PtuFrameLayout ptuFrame;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptu);

        initView();
        setViewContent();
        setFragment();
        setOnClick();
    }

    private void initView() {
        ptuFrame = (PtuFrameLayout) findViewById(R.id.ptu_frame);
        ptuView= (PtuView) findViewById(R.id.ptu_view);
    }

    private void setViewContent() {
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        ptuView.initBitmap(path);

    }
    private void setOnClick(){

    }
    //
    //观察Fragment的生命周期，
    // （1）是否Activity创建，不管有没有添加，都会执行
    //(2)执行不同的FragmentTransetion事务其反应如何
    //

    private void setFragment() {
        fragMain = new MainFunctionFragment();
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.fragment_function, fragMain);
        ft.commit();
    }

    @Override
    public void changeFragment(String function) {
        switch (function) {
            case "text":
                if(fragText==null) {
                    fragText = new AddTextFragment();
                }
                fm.beginTransaction().add(R.id.fragment_function, fragText)
                        .addToBackStack("main")
                        .commit();
                Rect rect=ptuView.getBound();
                ptuView.setTouchable(false);
                FloatTextView floatTextView = ptuFrame.initAddFloat(rect.right-rect.left,rect.bottom-rect.top);
                fragText.setFloatView(floatTextView);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ptuFrame.removeViewAt(1);
        ptuView.setTouchable(true);
        super.onBackPressed();
    }
}
