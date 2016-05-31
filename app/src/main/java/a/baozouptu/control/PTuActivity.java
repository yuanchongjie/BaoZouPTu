package a.baozouptu.control;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import a.baozouptu.R;
import a.baozouptu.tools.P;
import a.baozouptu.view.PTuView;

public class PTuActivity extends Activity implements MainFunctionFragment.Listen {
    /**
     * 主功能的fragment
     */
    MainFunctionFragment mainFrag;
    FragmentTransaction ft;
    FragmentManager fm;
    private MainFunctionFragment fragMain;
    private AddTextFragment fragText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptu);
        Intent intent = getIntent();
        P.le("PTuActivity.onCreat()", "到达");
        if (intent == null) P.le("PTuActivity.onCreat()", "intent出现空指针");
        String path = intent.getStringExtra("path");

        PTuView pTuView = (PTuView) findViewById(R.id.ptu_view);
        pTuView.initBitmap(path);
        setFragment();
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
