package a.baozouptu.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import a.baozouptu.base.MainActivity;

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
        test();
        this.finish();
    }

    private void test() {
        Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("test","test");
        startActivity(intent);
        this.finish();
    }
}

