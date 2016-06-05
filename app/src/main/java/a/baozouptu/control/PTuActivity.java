package a.baozouptu.control;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import a.baozouptu.R;
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
    private PtuView pTuView;
    private PtuFrameLayout ptuFrame;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptu);

        initView();
        setViewContent();
        setFragment();

        ptuFrame = (PtuFrameLayout) findViewById(R.id.ptu_frame);
        ptuFrame.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        ptuFrame.getViewTreeObserver().removeOnPreDrawListener(this);
                        int height = ptuFrame.getMeasuredHeight();
                        int width = ptuFrame.getMeasuredWidth();
                        ptuFrame.addView(pTuView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        ptuFrame.initAddFloat(width, height);
                        pTuView.setTouchable(false);
                        return true;
                    }

                });
    }

    private void initView() {
        pTuView = new PtuView(this);
    }

    private void setViewContent() {
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        pTuView.initBitmap(path);
    }

    //
    //观察Fragment的生命周期，
    // （1）是否Activity创建，不管有没有添加，都会执行
    //(2)执行不同的FragmentTransetion事务其反应如何
    //

    private void setFragment() {
        fragMain = new MainFunctionFragment();
        fragText = new AddTextFragment();
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.fragment_function, fragMain);
        ft.commit();
    }

    @Override
    public void changeFragment(String function) {
        switch (function) {
            case "text":
                fragText = new AddTextFragment();
                fm.beginTransaction().add(R.id.fragment_function, fragText)
                        .addToBackStack("main")
                        .commit();
                break;
        }
    }
}
