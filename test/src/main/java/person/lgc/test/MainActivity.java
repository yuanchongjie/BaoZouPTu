package person.lgc.test;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    boolean isIn = false;
    boolean isShow=true;
    Fragment fragment;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = new MyFragment();
        fm = getFragmentManager();
        findViewById(R.id.show_pic_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isIn) {
                    fm.beginTransaction().add(R.id.fragment, fragment).commit();
                    isIn=true;
                } else {
                    isIn=false;
                    fm.beginTransaction().remove(fragment).commit();
                }
            }
        });
        findViewById(R.id.hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isIn&&isShow){
                    fm.beginTransaction().hide(fragment).commit();
                    isShow=false;
                }else if(isIn) {
                    fm.beginTransaction().show(fragment).commit();
                    isShow=true;
                }
            }
        });
    }
}